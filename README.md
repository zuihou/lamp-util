# lamp 快速开发平台

[![Language](https://img.shields.io/badge/语言-Java17%20%7C%20SpringCloud%20%7C%20Vue3%20%7C%20...-red?style=flat-square&color=42b883)](https://github.com/dromara/lamp-cloud)
[![License](https://img.shields.io/github/license/dromara/lamp-cloud?color=42b883&style=flat-square)](https://github.com/dromara/lamp-cloud/blob/master/LICENSE)
[![Author](https://img.shields.io/badge/作者-zuihou-orange.svg)](https://github.com/zuihou)
[![Star](https://img.shields.io/github/stars/dromara/lamp-cloud?color=42b883&logo=github&style=flat-square)](https://github.com/dromara/lamp-cloud/stargazers)
[![Fork](https://img.shields.io/github/forks/dromara/lamp-cloud?color=42b883&logo=github&style=flat-square)](https://github.com/dromara/lamp-cloud/network/members)
[![Star](https://gitee.com/dromara/lamp-cloud/badge/star.svg?theme=gray)](https://gitee.com/dromara/lamp-cloud/stargazers)
[![Fork](https://gitee.com/dromara/lamp-cloud/badge/fork.svg?theme=gray)](https://gitee.com/dromara/lamp-cloud/members)

# 官网

[https://tangyh.top](https://tangyh.top)

# 分支说明

| 分支        | master        | java17       | 4.x_java17                                                                   |
|-----------|---------------|--------------|------------------------------------------------------------------------------|
| 源码版本      | 3.x           | 3.x          | 4.x                                                                          |
| JDK版本     | jdk8          | jdk17        | jdk17、jdk21                                                                  |
| 是否继续升级新功能 | 不开发新功能,仅修复bug | 不开发新功能,仅修复bug     | 持续开发新功能和bug修复                                                                |
| 是否支持数据源模式 | 是(企业版支持)      | 是(企业版支持)     | 否([企业版](https://tangyh.top/vip/%E6%8E%88%E6%9D%83%E8%B4%B9%E7%94%A8.html)支持) |
| 是否支持字段模式  | 是(开源版企业版均支持)  | 是(开源版企业版均支持) | 否([企业版](https://tangyh.top/vip/%E6%8E%88%E6%9D%83%E8%B4%B9%E7%94%A8.html)支持) |
| 是否支持非租户模式 | 是(开源版企业版均支持)  | 是(开源版企业版均支持) | 是(开源版支持(就是本项目))                                                              |

# lamp 项目组成

`灯灯`(简称灯， 英文名：lamp)，她是一个项目集，由"工具集"、"后端"、"前端"组成，为满足高内聚低耦合设计原则，将一个**大项目**
拆解为以下几个子项目：

[点我了解项目详细介绍](https://tangyh.top)

## 工具集

| 项目             | gitee                                                        | github                                                     | 备注       |
|----------------|--------------------------------------------------------------|------------------------------------------------------------|----------|
| lamp-util      | [lamp-util](https://gitee.com/zuihou111/lamp-util)           | [lamp-util](https://github.com/zuihou/lamp-util)           | 核心工具集    |
| lamp-job       | [lamp-job](https://gitee.com/zuihou111/lamp-job)             | [lamp-job](https://github.com/zuihou/lamp-job)             | 分布式定时调度器 |

## 后端

| 项目         | gitee                                                  | github                                                 | 备注                |
|------------|--------------------------------------------------------|--------------------------------------------------------|-------------------|
| lamp-cloud | [lamp-cloud](https://gitee.com/dromara/lamp-cloud)     | [lamp-cloud](https://github.com/dromara/lamp-cloud)    | SpringCloud(微服务)版 |
| lamp-boot  | [lamp-boot](https://gitee.com/zuihou111/lamp-boot)     | [lamp-boot](https://github.com/zuihou/lamp-boot)       | SpringBoot(单体)版   |

## 前端

| 项目                         | gitee                                                      | github                                                   | 备注            | 演示地址                     |
|----------------------------|------------------------------------------------------------|----------------------------------------------------------|---------------|--------------------------|
| lamp-web                   | [lamp-web](https://gitee.com/zuihou111/lamp-web)           | [lamp-web](https://github.com/zuihou/lamp-web)           | 基于 vben-admin | https://none.tangyh.top  |

## 注意：

- lamp-cloud 和 lamp-boot 依赖 lamp-util
- lamp-job 依赖 lamp-cloud 或 lamp-boot

所以，项目的编译顺序是：lamp-util -> lamp-cloud/lamp-boot -> lamp-job ！


# lamp-util 简介

`lamp-util` 的前身是 `zuihou-commons`，在3.0.0版本之后，改名为lamp-util，它是`lamp`项目的其中一员。

`lamp-util` 是 [lamp-cloud](https://github.com/dromara/lamp-cloud)
和 [lamp-boot](https://github.com/zuihou/lamp-boot) 项目的核心工具包，开发宗旨是打造一套兼顾 SpringBoot 和 SpringCloud
项目的公共工具类。

## lamp-util 亮点功能

- Mvc封装： 通用的 Controller、Service、Mapper、全局异常、全局序列化、反序列化规则
- SpringCloud封装：请求头传递、调用日志、灰度、统一配置编码解码规则等
- 数据回显：优雅解决 跨库表关联字段回显、跨服务字段回显
- 持久层增强：增强MybatisPlus Wrapper操作类、数据权限、自定义类型处理器
- 枚举、字典等字段统一传参、回显格式： 解决前端即要使用编码，有要回显中文名的场景。
- 在线文档：对swagger、knife4j二次封装，实现配置即文档。
- 前后端表单统一校验：还在为前端写一次校验规则，后端写一次校验规则而痛苦不堪？ 本组件将后端配置的jsr校验规则返回给前端，前端通过全局js，实现统一的校验规则。
- 缓存：封装redis缓存、二级缓存等，实现动态启用/禁用redis
- XSS： 对表单参数、json参数进行xss处理
- 统一的操作日志： AOP方式优雅记录操作日志
- 轻量级接口权限
- 快去看源码和文档发现 [更多功能](https://www.kancloud.cn/zuihou/zuihou-admin-cloud) 吧

# 演示地址

- 官网
  - [https://tangyh.top](https://tangyh.top)
- 4.0版本演示地址（账号密码都是   lamp）
  - 数据源模式（vben版）：演示地址：   [https://datasource.tangyh.top](https://datasource.tangyh.top)
  - 数据源模式（soybean版）：演示地址：   [https://soy-datasource.tangyh.top](https://soy-datasource.tangyh.top)
  - 字段模式（vben版）：演示地址：   [https://column.tangyh.top](https://column.tangyh.top)
  - 字段模式（soybean版）：演示地址：   [https://soy-column.tangyh.top](https://soy-column.tangyh.top)
  - 非租户模式：演示地址：   [https://none.tangyh.top](https://none.tangyh.top)

# 企业版

本项目分为开源版、个人学习版和企业商用版，github和gitee上能搜索到的为开源版本，遵循Apache协议。 个人和企业版源码在私有gitlab托管，购买后开通账号。

开源版是因为热爱，企业版是妥协于生活。

开源版和企业版区别请看：[会员版](https://tangyh.top/vip/%E6%8E%88%E6%9D%83%E8%B4%B9%E7%94%A8.html)

# 开源协议

Apache Licence 2.0 Licence是著名的非盈利开源组织Apache采用的协议。该协议和BSD类似，同样鼓励代码共享和尊重原作者的著作权，同样允许代码修改，再发布（作为开源或商业软件）。
需要满足的条件如下：

- 需要给代码的用户一份Apache Licence
- 如果你修改了代码，需要在被修改的文件中说明。
- 在延伸的代码中（修改和有源代码衍生的代码中）需要带有原来代码中的协议，商标，专利声明和其他原来作者规定需要包含的说明。
- 如果再发布的产品中包含一个Notice文件，则在Notice文件中需要带有Apache Licence。你可以在Notice中增加自己的许可，但不可以表现为对Apache
  Licence构成更改。 Apache Licence也是对商业应用友好的许可。使用者也可以在需要的时候修改代码来满足需要并作为开源或商业产品发布/销售。
- 若你借鉴或学习了本项目的源码，请你在你的项目源码和说明文档中显著的表明引用于本项目，并附上本项目的github访问地址。（https://github.com/dromara/lamp-cloud）
