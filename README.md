# SpringBoot Demo

本Demo基于SpringBoot 2.0.5构建，包含了Controller、Service、Repository的样例代码，同时包含了Controller、Service的单元测试样例。

## 使用方法
该Demo已集成Gradle，使用方法如下：
1. 拉取项目

    ```
    git clone http://git.inspur.com/framework/spring-boot-demo.git
    ```
2. 拉取子项目

    ```
    git submodule init
    git submodule update
    ```
3. 初始化项目

    Eclipse:
    
    ```
    gradlew clean cleanEclipse eclipse
    ```
    
    Idea: 暂不支持通过脚本初始化，可手工配置。注意：`需要将src/test/java和src/test/resources两个目录加入classpath`
4. 启动项目

    运行`com.inspur.cloud.demo.DemoApplication`中的main方法
    
## 项目配置

- SpringBoot配置：src/test/resources/application.yaml，配置方法可参考[Spring Boot Reference Guide - Appendix A. Common application properties](https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#common-application-properties)
- Gradle依赖配置：build.gradle
- 仓库配置及项目版本配置：gradle.properties
- 项目别名配置：settings.gradle


## 单元测试

Demo中使用了Spring基于JUnit实现的单元测试框架，样例详见`src/test/java`下的代码

单元测试开发应注意以下几点：
- Controller层和Service层单元测试应分开编写，不应该使用一个单元测试同时覆盖Controler和Service的逻辑。
- Controller层单元测试应使用Mockito模拟Service层逻辑，确保单元测试范围的原子性。
- 涉及到调用外系统的业务功能，需要使用Mockito模拟外系统的处理逻辑，保证单元测试可独立运行，不依赖第三方服务。
- 测试数据库使用H2，以免对外部数据库进行依赖，使用方法详见示例代码。
- 单元测试覆盖率至少达到70%以上。

## 项目数据初始化及变更

项目使用Liquibase自动初始化项目数据（包括表结构和字典表数据），无需人工干预，保证各环境部署应用的数据结构一致性和完整性。

Liquibase使用方法详见：[liquibase管理数据库变更](http://10.10.7.5:8090/pages/viewpage.action?pageId=10620419)

## 开发规范
- [后端-Java开发框架](http://10.10.7.5:8090/pages/viewpage.action?pageId=7766339)
- [后端-Java编码规范](http://10.10.7.5:8090/pages/viewpage.action?pageId=7769796)
- [后端-Java国际化（springboot改造）](http://10.10.7.5:8090/pages/viewpage.action?pageId=15136807)

## 其他依赖

- 错误码管理
    build.gradle
    ```
    compile('com.inspur.cloudframework:common-utils:0.2.1-SNAPSHOT')
    ```
    - `文档待补`
    - [后端-API错误信息规范](http://10.10.7.5:8090/pages/viewpage.action?pageId=7774167)
    - [公有云API错误代码表](http://10.10.7.5:8090/pages/viewpage.action?pageId=7775510)

- IAM认证及权限控制
    build.gradle
    ```
    compile('com.inspur.iam:iam-adapter-java:0.0.1-SNAPSHOT')
    ```
    
    `文档待补`
- BSS接口SDK
    build.gradle
    ```
    com.inspur.cloudframework:cloud-service-bss:0.0.1-SNAPSHOT
    ```
    `文档待补`