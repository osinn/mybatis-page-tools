package com.gitee.osinn.mybatis.page.plugin.dialect;

import com.gitee.osinn.mybatis.page.plugin.Page;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.ibatis.mapping.BoundSql;

/**
 * 数据库方言
 *
 * @author wency_cai
 */
public interface Dialect {

    /**
     * 第一个分页参数
     */
    String PAGE_PARAMETER_FIRST = "pageNum";

    /**
     * 第二个分页参数
     */
    String PAGE_PARAMETER_SECOND = "pageSize";

    /**
     * 生成 count 查询
     *
     * @return
     */
    SelectItem defaultCountSelectItem();

    /**
     * 生成分页查询 sql
     *
     * @param page     分页对象
     * @param boundSql 绑定 SQL 对象
     * @return 返回最终分页查询SQL
     */
    String getPageSql(Page page, BoundSql boundSql);

    /**
     * 计算MySQL 查询开始行
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    int startRow(int pageNum, int pageSize);

    /**
     * 计算MySQL 查询结束行
     *
     * @param startRow
     * @param pageNum
     * @param pageSize
     * @return
     */
    int endRow(int startRow, int pageNum, int pageSize);

}
