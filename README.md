# zuihou-commons

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/zuihou/zuihou-admin-cloud/blob/master/LICENSE)
[![](https://img.shields.io/badge/Author-zuihou-orange.svg)](https://github.com/zuihou/zuihou-admin-cloud)
[![](https://img.shields.io/badge/version-1.0-brightgreen.svg)](https://github.com/zuihou/zuihou-admin-cloud)
[![GitHub stars](https://img.shields.io/github/stars/zuihou/zuihou-admin-cloud.svg?style=social&label=Stars)](https://github.com/zuihou/zuihou-admin-cloud/stargazers)
[![star](https://gitee.com/zuihou111/zuihou-admin-cloud/badge/star.svg?theme=white)](https://gitee.com/zuihou111/zuihou-admin-cloud/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/zuihou/zuihou-admin-cloud.svg?style=social&label=Fork)](https://github.com/zuihou/zuihou-admin-cloud/network/members)
[![fork](https://gitee.com/zuihou111/zuihou-admin-cloud/badge/fork.svg?theme=white)](https://gitee.com/zuihou111/zuihou-admin-cloud/members)

## 简介：
`zuihou-commons` 是 `zuihou-admin-cloud` 和 `zuihou-admin-boot` 项目的核心工具包，开发宗旨是打造一套兼顾 SpringBoot 和 SpringCloud 项目的公共工具类。

zuihou-admin-cloud 基于`SpringCloud(Hoxton.SR1)`  + `SpringBoot(2.2.4.RELEASE)` 的 SaaS型微服务脚手架，具备用户管理、资源权限管理、网关统一鉴权、Xss防跨站攻击、自动代码生成、多存储系统、分布式事务、分布式定时任务等多个模块，支持多业务系统并行开发，
支持多服务并行开发，可以作为后端服务的开发脚手架。代码简洁，架构清晰，非常适合学习使用。核心技术采用Nacos、Fegin、Ribbon、Zuul、Hystrix、JWT Token、Mybatis、SpringBoot、Seata、Nacos、Sentinel、
RabbitMQ、FastDFS等主要框架和中间件。

希望能努力打造一套从 `SaaS基础框架` - `分布式微服务架构` - `持续集成` - `系统监测` 的解决方案。`本项目旨在实现基础能力，不涉及具体业务。`

## 如果觉得对您有任何一点帮助，请点右上角 "Star" 支持一下吧，谢谢！

## 详细文档: https://www.kancloud.cn/zuihou/zuihou-admin-cloud

    ps: gitee捐献 或者 二维码打赏（本页最下方）： 45元及以上 并 备注邮箱，可得开发文档一份（支持后续更新）
    打赏或者捐献后直接加群：1039545140 并备注打赏时填写的邮箱，可以持续的获取最新的文档。 

## 收费版
本项目分为开源版和收费版，github和gitee上能搜索到的为开源版本，遵循Apache协议。 收费版源码在私有gitlab托管，购买后开通账号。

收费版和开源版区别请看：[收费版](收费版.md)


## 项目代码地址


| 项目 | gitee | github | 备注 |
|---|---|---|---|
| zuihou-commons | https://gitee.com/zuihou111/zuihou-commons  | https://github.com/zuihou/zuihou-commons | 核心工具类：boot和cloud 项目的公共抽象 |
| zuihou-admin-cloud | https://gitee.com/zuihou111/zuihou-admin-cloud | https://github.com/zuihou/zuihou-admin-cloud | 微服务项目 |
| zuihou-admin-boot | https://gitee.com/zuihou111/zuihou-admin-boot | https://github.com/zuihou/zuihou-admin-boot | 单体项目：功能跟cloud版一样 |
| zuihou-ui | https://gitee.com/zuihou111/zuihou-ui | https://github.com/zuihou/zuihou-ui | 租户后台：租户使用 |
| zuihou-admin-ui | https://gitee.com/zuihou111/zuihou-admin-ui | https://github.com/zuihou/zuihou-admin-ui | 开发&运营后台：内部使用 |
| zuihou-generator  | https://gitee.com/zuihou111/zuihou-generator | https://github.com/zuihou/zuihou-generator | 代码生成器：开发使用 |

## zuihou-admin-cloud 演示地址 （服务器没法备案，只能加上端口~~~）

| 项目 | 演示地址 | 管理员账号 | 普通账号 | 
|---|---|---|---|
| 租户后台 | http://tangyh.top:10000/zuihou-ui/ | zuihou/zuihou | test/zuiou |
| 开发&运营后台 | http://tangyh.top:180/zuihou-admin-ui/ | demoAdmin/zuihou | 无 |


## 如何导入本项目
1. 将zuihou-commons/zuihou-dependencies/pom.xml导入IDEA 
2. 编译 mvn install
3. 将zuihou-commons/pom.xml导入IDEA
4. 编译 mvn clean install -Dmaven.javadoc.skip=true -Dgpg.skip=true -Dmaven.source.skip=true


## 如何编译 zuihou-commons ?
```

# 跳过 生成javadoc
mvn clean install -Dmaven.javadoc.skip=true -Dgpg.skip=true
# 跳过 生成源代码
mvn clean install -Dmaven.source.skip=true
# 跳过 发布jar到中央仓库
clean install -Dgpg.skip -f pom.xml

# 同时跳过 生成javadoc、生成源代码、发布jar到中央仓库， 只编译源码到本地仓库
mvn clean install  -Dmaven.javadoc.skip=true -Dgpg.skip=true -Dmaven.source.skip=true -Dgpg.skip -f pom.xml

# 编译 同时生成源代码和javadoc和发布  （默认情况大家都会报错）
mvn clean install


```

## 如何解决 IDEA 2019 控制台生成javadoc时乱码
```
# mac
IntelliJ IDEA -> Preferences  -> Build, Execution, Deployment -> Build Tools ->  Maven -> Runner 
在 Environment variables: 加入  JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8

# window
File -> Settings -> Build, Execution, Deployment-> Build Tools ->  Maven -> Runner 
在VM Options： 加入  -Dfile.encoding=GBK

# 还不行就在命令行执行 mvn -version  看看 mvn 的编码是什么，改成一样的即可。
# 改了还不行，就度娘吧
```

