# 《灯灯》中后台快速开发平台

[![AUR](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://github.com/zuihou/lamp-cloud/blob/master/LICENSE)
[![](https://img.shields.io/badge/作者-zuihou-orange.svg)](https://github.com/zuihou)
[![](https://img.shields.io/badge/版本-3.1.2-brightgreen.svg)](https://github.com/zuihou/lamp-cloud)
[![GitHub stars](https://img.shields.io/github/stars/zuihou/lamp-cloud.svg?style=social&label=Stars)](https://github.com/zuihou/lamp-cloud/stargazers)
[![star](https://gitee.com/zuihou111/lamp-cloud/badge/star.svg?theme=white)](https://gitee.com/zuihou111/lamp-cloud/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/zuihou/lamp-cloud.svg?style=social&label=Fork)](https://github.com/zuihou/lamp-cloud/network/members)
[![fork](https://gitee.com/zuihou111/lamp-cloud/badge/fork.svg?theme=white)](https://gitee.com/zuihou111/lamp-cloud/members)

# lamp 项目名字由来

## 叙事版：

在一个夜黑风高的晚上，小孩吵着要出去玩，于是和`程序员老婆`一起带小孩出去放风，路上顺便讨论起项目要换个什么名字，在各自想出的名字都被对方一一否决后，大家陷入了沉思。 走着走着，在一盏路灯下，孩砸盯着路灯打破宁静，喊出：灯灯～
我和媳妇愣了一下，然后对视着一起说：哈哈，这个名字好～

## 解释版：

`灯灯`： 是我小孩学说话时会说的第一个词，也是我在想了很多项目名后，小孩一语点破的一个名字，灯灯象征着光明，给困境的我们带来希望，给加班夜归的程序员们指引前方～

`灯灯`(简称灯， 英文名：lamp)，他是一个项目的统称，包含以下几个子项目

## lamp 项目组成
注意： 请先下载lamp-util 代码并编译（mvn install） 到本地仓库，然后在编译（mvn install）lamp-cloud或lamp-boot项目， 最后编译（mvn install）lamp-job项目！！！

### 工具集 （lamp-util 项目必备，其他2个可选）

| 项目 | gitee | github | 备注 | 
| --- | --- | --- | --- |
| 工具集 | [lamp-util](https://gitee.com/zuihou111/lamp-util) | [lamp-util](https://github.com/zuihou/lamp-util) | 业务无关的工具集，cloud 和 boot 项目都依赖它 |
| 代码生成器 | [lamp-generator](https://gitee.com/zuihou111/lamp-generator) | [lamp-generator](https://github.com/zuihou/lamp-generator) | 给开发人员使用 |
| 定时调度器 | [lamp-job](https://gitee.com/zuihou111/lamp-job) | [lamp-job](https://github.com/zuihou/lamp-job) | 尚未开发 |

### 后端 （下面2个项目功能一致，任选其一即可）

| 项目 | gitee | github | 备注 |
| --- | --- | --- | --- |
| 微服务版(后端) | [lamp-cloud](https://gitee.com/zuihou111/lamp-cloud) | [lamp-cloud](https://github.com/zuihou/lamp-cloud) | SpringCloud 版 |
| 单体版(后端) | [lamp-boot](https://gitee.com/zuihou111/lamp-boot) | [lamp-boot](https://github.com/zuihou/lamp-boot) | SpringBoot 版(和 lamp-cloud 功能基本一致) |

### 前端 （下面3个项目功能一致，任选其一即可）

| 项目 | gitee | github | 备注 | 演示地址 |
| --- | --- | --- | --- | --- |
| 中后台管理系统 | [lamp-web](https://gitee.com/zuihou111/lamp-web) | [lamp-web](https://github.com/zuihou/lamp-web) | （全部功能已完成）基于 vue-admin-element | http://tangyh.top:10000 |
| 中后台管理系统 | [lamp-web-beautiful](https://gitee.com/zuihou111/lamp-web-beautiful) | [lamp-web-beautiful](https://github.com/zuihou/lamp-web-beautiful) | （正在开发...）基于 vue-admin-beautiful | http://tangyh.top:180 |
| 中后台管理系统 (强烈推荐！👏👏👏) | [lamp-web-beautiful](https://gitee.com/zuihou111/lamp-web-plus) | [lamp-web-beautiful](https://github.com/zuihou/lamp-web-plus) | （正在开发...）基于 vue-vben-admin （vue 3 + vite 2） | http://tangyh.top:3100 |

# lamp-util 简介

`lamp-util` 的前身是 `zuihou-commons`，在3.0.0版本之后，改名为lamp-util，它是`lamp`项目的其中一员。
`lamp-util` 是 [lamp-cloud](https://github.com/zuihou/lamp-cloud) 和 [lamp-boot](https://github.com/zuihou/lamp-boot)
项目的核心工具包，开发宗旨是打造一套兼顾 SpringBoot 和 SpringCloud 项目的公共工具类。

## 结构介绍

![lamp-util 功能介绍.png](docs/images/lamp-util功能介绍.png)

## lamp-util 亮点功能

- Mvc封装： 通用的 Controller、Service、Mapper、全局异常、全局序列化、反序列化规则
- SpringCloud封装：请求头传递、调用日志、灰度、统一配置编码解码规则等
- 关联数据注入：优雅解决 跨库表关联字段回显、跨服务字段回显
- 持久层增强：增强MybatisPlus Wrapper操作类、数据权限、自定义类型处理器
- 枚举、字典等字段统一传参、回显格式： 解决前端即要使用编码，有要回显中文名的场景。
- 在线文档：对swagger、knife4j二次封装，实现配置即文档。
- 前后端表单统一校验：还在为前端写一次校验规则，后端写一次校验规则而痛苦不堪？ 本组件将后端配置的jsr校验规则返回给前端，前端通过全局js，实现统一的校验规则。
- 缓存：封装redis缓存、二级缓存等，实现动态启用/禁用redis
- XSS： 对表单参数、json参数进行xss处理
- 统一的操作日志： AOP方式优雅记录操作日志
- 轻量级接口权限
- 快去看源码和文档发现 [更多功能](https://www.kancloud.cn/zuihou/zuihou-admin-cloud) 吧

# 如果觉得对您有任何一点帮助，请点右上角 "Star" 支持一下吧，谢谢！

[点我查看详细文档](https://www.kancloud.cn/zuihou/zuihou-admin-cloud) 

    ps: gitee捐献 或者 二维码打赏(本页最下方)： 45元及以上 并 备注邮箱，可得开发文档一份(支持后续更新)
    打赏或者捐献后直接加群：1039545140 并备注打赏时填写的邮箱，可以持续的获取最新的文档。 

# 会员版
本项目分为开源版、会员版，github和gitee上能搜索到的为开源版本，遵循Apache协议。 会员版源码在私有gitlab托管，购买后开通账号。

会员版和会员版区别请看：[会员版](会员版.md)

# lamp 会员版项目演示地址 

- (已完成) 基于 [FEBS-Cloud-Web](https://gitee.com/mrbirdd/FEBS-Cloud-Web) 改造的前端项目演示地址： http://tangyh.top:10000/lamp-web/
- (正在开发中...)基于 [vue-admin-beautiful](https://github.com/chuzhixin/vue-admin-beautiful)改造的前端项目
  演示地址： http://tangyh.top:180
- (正在开发中...)基于 ant-design [vue-vben-admin](https://github.com/anncwb/vue-vben-admin) 开发的前端项目 演示地址：http://tangyh.top:3100
- 以下内置账号仅限于内置的0000租户
- 平台管理员： lamp_pt/lamp (内置给公司内部运营人员使用)
- 超级管理员： lamp/lamp
- 普通管理员： general/lamp
- 普通账号： normal/lamp

# 友情链接 & 特别鸣谢
* SaaS型微服务快速开发平台：[https://github.com/zuihou/lamp-cloud](https://github.com/zuihou/lamp-cloud)
* SaaS型单体快速开发平台：[https://github.com/zuihou/lamp-boot](https://github.com/zuihou/lamp-boot)
* MyBatis-Plus：[https://mybatis.plus/](https://mybatis.plus/)
* knife4j：[http://doc.xiaominfo.com/](http://doc.xiaominfo.com/)
* hutool：[https://hutool.cn/](https://hutool.cn/)
* xxl-job：[http://www.xuxueli.com/xxl-job/](http://www.xuxueli.com/xxl-job/)
* kkfileview：[https://kkfileview.keking.cn](https://kkfileview.keking.cn)
* vue-vben-admin(vue3.x + vite2.x)： [https://github.com/anncwb/vue-vben-admin](https://github.com/anncwb/vue-vben-admin)
  lamp-web-plus 基于本项目改造
* FEBS Cloud Web： [https://gitee.com/mrbirdd/FEBS-Cloud-Web](https://gitee.com/mrbirdd/FEBS-Cloud-Web)
  lamp-web 基于本项目改造， 感谢 [wuyouzhuguli](https://github.com/wuyouzhuguli)
* Cloud-Platform： [https://gitee.com/geek_qi/cloud-platform](https://gitee.com/geek_qi/cloud-platform)
  作者学习时接触到的第一个微服务项目
* [vue-admin-beautiful](https://github.com/chuzhixin/vue-admin-beautiful) ： 前端框架
