# lamp 快速开发平台

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/zuihou/lamp-cloud/blob/master/LICENSE)
[![](https://img.shields.io/badge/作者-zuihou-orange.svg)](https://github.com/zuihou)
[![](https://img.shields.io/badge/版本-3.2.0-brightgreen.svg)](https://github.com/zuihou/lamp-cloud)
[![GitHub stars](https://img.shields.io/github/stars/zuihou/lamp-cloud.svg?style=social&label=Stars)](https://github.com/zuihou/lamp-cloud/stargazers)
[![star](https://gitee.com/zuihou111/lamp-cloud/badge/star.svg?theme=white)](https://gitee.com/zuihou111/lamp-cloud/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/zuihou/lamp-cloud.svg?style=social&label=Fork)](https://github.com/zuihou/lamp-cloud/network/members)
[![fork](https://gitee.com/zuihou111/lamp-cloud/badge/fork.svg?theme=white)](https://gitee.com/zuihou111/lamp-cloud/members)

# lamp 项目名字由来
`灯灯`(简称灯， 英文名：lamp)，他是一个项目的统称，由"工具集"、"后端"、"前端"组成，包含以下几个子项目

[点我了解项目详细介绍](简介.md)

## 工具集

| 项目 | gitee | github | 备注 |
| --- | --- | --- | --- |
| lamp-util | https://gitee.com/zuihou111/lamp-util | https://github.com/zuihou/lamp-util | 核心工具集 |
| lamp-generator | https://gitee.com/zuihou111/lamp-generator | https://github.com/zuihou/lamp-generator | 代码生成器 |
| lamp-job | https://gitee.com/zuihou111/lamp-job | https://github.com/zuihou/lamp-job | 分布式定时调度器 |

## 后端

| 项目 | gitee | github | 备注 |
| --- | --- | --- | --- |
| lamp-cloud | https://gitee.com/zuihou111/lamp-cloud | https://github.com/zuihou/lamp-cloud | SpringCloud(微服务)版 |
| lamp-boot | https://gitee.com/zuihou111/lamp-boot | https://github.com/zuihou/lamp-boot | SpringBoot(单体)版 |

## 前端

| 项目 | gitee | github | 备注 | 演示地址 |
| --- | --- | --- | --- | --- |
| lamp-web | https://gitee.com/zuihou111/lamp-web | https://github.com/zuihou/lamp-web | 基于 vue-admin-element (element-ui) | http://tangyh.top:10000 |
| lamp-web-plus(强烈推荐！👏👏👏) | https://gitee.com/zuihou111/lamp-web-plus | https://github.com/zuihou/lamp-web-plus | 基于 vue-vben-admin （vue 3 + ant design vue 2） | http://tangyh.top:3100 |
| lamp-web-beautiful(暂时停更) | https://gitee.com/zuihou111/lamp-web-beautiful | https://github.com/zuihou/lamp-web-beautiful | 基于 vue-admin-beautiful | http://tangyh.top:180 |

# lamp-util 简介

`lamp-util` 的前身是 `zuihou-commons`，在3.0.0版本之后，改名为lamp-util，它是`lamp`项目的其中一员。
`lamp-util` 是基于`jdk11`的， [lamp-cloud](https://github.com/zuihou/lamp-cloud)
和 [lamp-boot](https://github.com/zuihou/lamp-boot) 项目的核心工具包，开发宗旨是打造一套兼顾 SpringBoot 和 SpringCloud 项目的公共工具类。

# 快速上手
- 入门到精通，参考文档： https://www.kancloud.cn/zuihou/zuihou-admin-cloud
- 发现bug和建议，请提交issue： https://github.com/zuihou/lamp-cloud/issues
- 常见问题，请参考Discussions： https://github.com/zuihou/lamp-cloud/discussions

## 结构介绍

![lamp-util 功能介绍.png](docs/images/lamp-util功能介绍.png)

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

# lamp 会员版项目演示地址

- lamp-web-plus演示地址： http://tangyh.top:3100
- lamp-web演示地址： http://tangyh.top:10000/lamp-web
- lamp-web-beautiful演示地址： http://tangyh.top:180
- 以下内置账号仅限于内置的0000租户
- 平台管理员： lamp_pt/lamp (内置给公司内部运营人员使用)
- 超级管理员： lamp/lamp
- 普通管理员： general/lamp
- 普通账号： normal/lamp

# 会员版

本项目分为开源版和会员版，github和gitee上能搜索到的为开源版本，遵循Apache协议。 会员版源码在私有gitlab托管，购买后开通账号。

会员版和开源版区别请看：[会员版](会员版.md)

