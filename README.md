# mybatis-page-tools-spring-boot-starter
> MyBatis 分页插件，开箱即用

# 测试数据库
- [x] MySQL

# 快速开始
- 在已经集成Mybatis项目中引入以下依赖

```
<dependency>
    <groupId>com.gitee.osinn</groupId>
    <artifactId>mybatis-page-tools-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```

### `application.yml`配置Mybatis 分页插件参数
```
mybatis:
  page:
    config:
       # 启用分页插件
      enable: true
```

# 演示
```
Page page = new Page();
page.setPageNum(2);
page.setPageSize(2);
List<UserEntity> userEntities = userMapper.testMybatisPage(page);

<select id="testMybatisPage" resultType="com.gitee.osinn.mybatis.page.plugin.entity.UserEntity">
    SELECT * FROM user where 1=1
</select>
    
# 执行结果
==>  Preparing: SELECT COUNT(*) AS total FROM user WHERE 1 = 1
==> Parameters: 
<==    Columns: TOTAL
<==        Row: 3
<==      Total: 1
==>  Preparing: SELECT * FROM user where 1=1 LIMIT ?, ?
==> Parameters: 2(Integer), 2(Integer)
<==    Columns: ID, USERNAME, PASSWORD, TENANT_ID
<==        Row: 3, 张三, 123, 1
<==      Total: 1
```