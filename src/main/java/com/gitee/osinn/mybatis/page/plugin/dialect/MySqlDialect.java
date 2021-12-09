package com.gitee.osinn.mybatis.page.plugin.dialect;

import com.gitee.osinn.mybatis.page.plugin.Page;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.ibatis.mapping.BoundSql;

/**
 * MySQL 数据库方言
 *
 * @author wency_cai
 */
public class MySqlDialect implements Dialect {

    /**
     * 获取jsqlparser中count的SelectItem
     */
    @Override
    public SelectItem defaultCountSelectItem() {
        Function function = new Function();
        function.setName("COUNT");
        function.setAllColumns(true);
        return new SelectExpressionItem(function).withAlias(new Alias("total"));
    }

    @Override
    public String getPageSql(Page page, BoundSql boundSql) {
        String sql = boundSql.getSql();
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        if (this.startRow(page.getPageNum(), page.getPageSize()) == 0) {
            sqlBuilder.append("\n LIMIT ? ");
        } else {
            sqlBuilder.append("\n LIMIT ?, ? ");
        }
        return sqlBuilder.toString();
    }

    @Override
    public int startRow(int pageNum, int pageSize) {
        return pageNum > 0 ? (pageNum - 1) * pageSize : 0;
    }

    @Override
    public int endRow(int startRow, int pageNum, int pageSize) {
        return startRow + pageSize * (pageNum > 0 ? 1 : 0);
    }
}
