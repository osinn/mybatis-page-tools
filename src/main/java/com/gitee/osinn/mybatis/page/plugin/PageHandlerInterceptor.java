package com.gitee.osinn.mybatis.page.plugin;

import com.gitee.osinn.mybatis.page.plugin.dialect.MySqlDialect;
import com.gitee.osinn.mybatis.page.plugin.utils.CollectionUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分页插件处理查询
 *
 * @author wency_cai
 */
public class PageHandlerInterceptor extends MySqlDialect {

    protected static final Map<String, MappedStatement> COUNT_MS_CACHE = new ConcurrentHashMap<>();

    public Object executorPage(Invocation invocation) throws InvocationTargetException, IllegalAccessException, SQLException, JSQLParserException {
        Object target = invocation.getTarget();
        if (target instanceof Executor) {
            Object[] args = invocation.getArgs();
            MappedStatement mappedStatement = (MappedStatement) args[0];

            boolean isUpdate = args.length == 2;
            if (!isUpdate && mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
                Executor executor = (Executor) invocation.getTarget();
                CacheKey cacheKey;
                BoundSql boundSql;
                Object parameter = args[1];
                RowBounds rowBounds = (RowBounds) args[2];
                ResultHandler resultHandler = (ResultHandler) args[3];
                //由于逻辑关系，只会进入一次
                if (args.length == 4) {
                    //4 个参数时
                    boundSql = mappedStatement.getBoundSql(parameter);
                    cacheKey = executor.createCacheKey(mappedStatement, parameter, rowBounds, boundSql);
                } else {
                    //6 个参数时
                    cacheKey = (CacheKey) args[4];
                    boundSql = (BoundSql) args[5];
                }
                Page page = findPage(parameter).orElse(null);
                if (page != null) {
                    // 执行统计
                    Long total = this.executorCount(boundSql,
                            mappedStatement,
                            executor,
                            parameter,
                            rowBounds,
                            resultHandler);

                    page.setTotal(total);

                    if (page.getPages() < page.getPageNum()) {
                        page.setList(Collections.emptyList());
                        return page.getList();
                    }

                    // 执行分页查询
                    List<Object> query = executorPageQuery(boundSql,
                            mappedStatement,
                            executor,
                            parameter,
                            rowBounds,
                            resultHandler,
                            cacheKey,
                            page);

                    page.setList(query);
                    return query;
                } else {
                    return executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, boundSql);
                }
            } else {
                return invocation.proceed();
            }
        } else {
            return invocation.proceed();
        }
    }


    /**
     * 查找分页参数
     *
     * @param parameterObject 参数对象
     * @return 分页参数
     */
    public Optional<Page> findPage(Object parameterObject) {
        if (parameterObject != null) {
            if (parameterObject instanceof Map) {
                Map<?, ?> parameterMap = (Map<?, ?>) parameterObject;
                for (Map.Entry entry : parameterMap.entrySet()) {
                    if (entry.getValue() != null && entry.getValue() instanceof Page) {
                        return Optional.of((Page) entry.getValue());
                    }
                }
            } else if (parameterObject instanceof Page) {
                return Optional.of((Page) parameterObject);
            }
        }
        return Optional.empty();
    }

    private Long executorCount(BoundSql boundSql,
                               MappedStatement mappedStatement,
                               Executor executor,
                               Object parameter,
                               RowBounds rowBounds,
                               ResultHandler resultHandler) throws JSQLParserException, SQLException {

        Select select = (Select) CCJSqlParserUtil.parse(boundSql.getSql());
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        plainSelect.setSelectItems(Collections.singletonList(super.defaultCountSelectItem()));

        MappedStatement countMs = buildAutoCountMappedStatement(mappedStatement);

        BoundSql countSql = new BoundSql(
                mappedStatement.getConfiguration(),
                select.toString(),
                boundSql.getParameterMappings(),
                boundSql.getParameterObject());
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            String prop = parameterMapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                countSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }

        //创建 count 查询的缓存 key
        CacheKey countKey = executor.createCacheKey(countMs, parameter, RowBounds.DEFAULT, countSql);
        List<Object> result = executor.query(countMs, parameter, rowBounds, resultHandler, countKey, countSql);
        long total = 0;
        if (CollectionUtils.isNotEmpty(result)) {
            // 个别数据库 count 没数据不会返回 0
            Object o = result.get(0);
            if (o != null) {
                total = Long.parseLong(o.toString());
            }
        }

        return total;
    }

    private List<Object> executorPageQuery(BoundSql boundSql,
                                           MappedStatement mappedStatement,
                                           Executor executor,
                                           Object parameter,
                                           RowBounds rowBounds,
                                           ResultHandler resultHandler,
                                           CacheKey cacheKey,
                                           Page page) throws SQLException {

        parameter = this.processParameterObject(mappedStatement, parameter, boundSql, cacheKey, page);


        String pageSql = getPageSql(page, boundSql);

        BoundSql pageBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql, boundSql.getParameterMappings(), parameter);

        return executor.query(mappedStatement, parameter, rowBounds, resultHandler, cacheKey, pageBoundSql);
    }

    private Object processParameterObject(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql, CacheKey pageKey, Page page) {
        Map<String, Object> paramMap = null;
        if (parameterObject == null) {
            paramMap = new HashMap<>();
        } else if (parameterObject instanceof Map) {
            //解决不可变Map的情况
            paramMap = new HashMap<>();
            paramMap.putAll((Map) parameterObject);
        } else {
            paramMap = new HashMap<>();
        }
        return processPageParameter(mappedStatement, paramMap, page, boundSql, pageKey);
    }

    public Object processPageParameter(MappedStatement mappedStatement, Map<String, Object> paramMap, Page page, BoundSql boundSql, CacheKey pageKey) {
        paramMap.put(PAGE_PARAMETER_FIRST, startRow(page.getPageNum(), page.getPageSize()));
        paramMap.put(PAGE_PARAMETER_SECOND, page.getPageSize());
        //处理pageKey
        pageKey.update(startRow(page.getPageNum(), page.getPageSize()));
        pageKey.update(page.getPageSize());
        //处理参数配置
        if (boundSql.getParameterMappings() != null) {
            List<ParameterMapping> newParameterMappings = new ArrayList<>(boundSql.getParameterMappings());
            if (super.startRow(page.getPageNum(), page.getPageNum()) != 0) {
                newParameterMappings.add(new ParameterMapping.Builder(mappedStatement.getConfiguration(), PAGE_PARAMETER_FIRST, int.class).build());
            }
            newParameterMappings.add(new ParameterMapping.Builder(mappedStatement.getConfiguration(), PAGE_PARAMETER_SECOND, int.class).build());

            MetaObject metaObject = SystemMetaObject.forObject(boundSql);
            metaObject.setValue("parameterMappings", newParameterMappings);
        }
        return paramMap;
    }

    /**
     * 构建 MappedStatement
     *
     * @param ms MappedStatement
     * @return MappedStatement
     */
    private MappedStatement buildAutoCountMappedStatement(MappedStatement ms) {
        final String countId = ms.getId() + "_pageCount";
        final Configuration configuration = ms.getConfiguration();
        return CollectionUtils.computeIfAbsent(COUNT_MS_CACHE, countId, key -> {
            MappedStatement.Builder builder = new MappedStatement.Builder(configuration, key, ms.getSqlSource(), ms.getSqlCommandType());
            builder.resource(ms.getResource());
            builder.fetchSize(ms.getFetchSize());
            builder.statementType(ms.getStatementType());
            builder.timeout(ms.getTimeout());
            builder.parameterMap(ms.getParameterMap());
            builder.resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, "mybatis-page-tools", Long.class, Collections.emptyList()).build()));
            builder.resultSetType(ms.getResultSetType());
            builder.cache(ms.getCache());
            builder.flushCacheRequired(ms.isFlushCacheRequired());
            builder.useCache(ms.isUseCache());
            return builder.build();
        });
    }
}
