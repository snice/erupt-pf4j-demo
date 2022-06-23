# erupt-pf4j-demo

## 基于pf4j实现erupt体系中插件开发

## 依赖

* [spring-pf4j](https://github.com/snice/spring-pf4j)
* [erupt v1.10.9+](https://github.com/erupts/erupt)

## 特性

    开发过程中，可以避免 修改erupt后重启，直接在管理端 重启下插件即可
    生产环境，通过jar或zip，动态加载erupt，不需要重新部署

* 支持@Erupt
* 支持@TplAction



## 目录结构

```js
├── README.md
├── erupt-pf4j       ---> erupt-pf4j模块
│   ├── pom.xml
│   ├── src
├── example          ---> erupt应用示例
│   ├── pom.xml
│   ├── src
├── plugins          ---> 开发模式下的插件（目录结构不可变）
│   ├── disabled.txt
│   ├── plugin1
│   ├── plugin2
│   └── pom.xml
└── pom.xml

```