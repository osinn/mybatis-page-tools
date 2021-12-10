package com.gitee.osinn.mybatis.page.plugin;

import com.gitee.osinn.mybatis.page.plugin.entity.UserEntity;
import com.gitee.osinn.mybatis.page.plugin.mapper.UserMapper;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TenantApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MybatisMultiDemoApplicationTest {

    @Resource
    private UserMapper userMapper;

    @Test
    public void testMybatisPage1() {
        Page page = new Page();
        page.setPageNum(1);
        page.setPageSize(2);
        List<UserEntity> userEntities = userMapper.testMybatisPage(page, "root");
//        UserEntity userEntity = userMapper.selectTestWhere(1L);
        System.out.println("统计数量：" + page.getTotal());
        System.out.println(userEntities.toString());
//
//        UserEntity userEntity = userMapper.selectTestWhere(11L);
//        System.out.println(userEntity);
//
//        List<UserEntity> userEntities = userMapper.testPage(0,10);
//        System.out.println(userEntities);
    }

    @Test
    public void testMybatis2Page() {
        Page page = new Page();
        page.setPageNum(1);
        page.setPageSize(2);
        List<UserEntity> userEntities = userMapper.testMybatis2Page(page);
        System.out.println(userEntities);
    }

    @Test
    public void testMybatisPage2() {
        List<UserEntity> userEntities = userMapper.testPage(0,10);
        System.out.println(userEntities);
    }

    @Test
    public void testMybatisPage3() {
        Page page = new Page();
        page.setPageNum(1);
        page.setPageSize(2);
        List<UserEntity> userEntities = userMapper.testMybatis3Page(page, "root", "123");
        System.out.println("统计数量：" + page.getTotal());
        System.out.println(userEntities.toString());

    }
    @Test
    public void testMybatis4Page() {
        Page page = new Page();
        page.setPageNum(1);
        page.setPageSize(2);
        List<UserEntity> userEntities = userMapper.testMybatis4Page(page);
        System.out.println("统计数量：" + page.getTotal());
        System.out.println(userEntities.toString());

    }

    @Test
    public void testMybatis5Page() {
        Page page = new Page();
        page.setPageNum(1);
        page.setPageSize(2);
        List<UserEntity> userEntities = userMapper.testMybatis5Page(page);
        System.out.println("统计数量：" + page.getTotal());
        System.out.println(userEntities.toString());
    }

    @Test
    public void testMybatis6Page() {
        Page<UserEntity> page = new Page<>();
        page.setPageNum(1);
        page.setPageSize(2);
        List<UserEntity> userEntities = userMapper.testMybatis6Page(page);
        System.out.println("统计数量：" + page.getTotal());
        System.out.println(userEntities.toString());
        System.out.println(page.getList());
    }

}
