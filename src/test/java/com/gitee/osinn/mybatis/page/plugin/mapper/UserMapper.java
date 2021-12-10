package com.gitee.osinn.mybatis.page.plugin.mapper;

import com.gitee.osinn.mybatis.page.plugin.Page;
import com.gitee.osinn.mybatis.page.plugin.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {

    List<UserEntity> selectAll();

    List<UserEntity> testMybatis2Page(Page page);

    List<UserEntity> testMybatisPage(Page page, @Param("username") String username);

    List<UserEntity> testMybatis3Page(Page page, @Param("username") String username, @Param("password")  String password);

    List<UserEntity> testMybatis4Page(@Param("page") Page page);

    @Select("SELECT * FROM user")
    List<UserEntity> testMybatis5Page(@Param("page") Page page);


    @Select("SELECT * FROM user")
    List<UserEntity> testMybatis6Page(@Param("page") Page<UserEntity> page);

    List<UserEntity> testPage(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
}
