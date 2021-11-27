# TSBattery
![Eclipse Marketplace](https://img.shields.io/badge/build-passing-brightgreen)
![Eclipse Marketplace](https://img.shields.io/badge/license-GPL3.0-blue)
![Eclipse Marketplace](https://img.shields.io/badge/version-v2.4-green)
<br/><br/>
TSBattery a new way to save your battery avoid cancer apps hacker it.<br/>
TSBattery 是一个旨在使 QQ、TIM 变得更省电的开源 Xposed 模块
# 开始使用
点击下载最新版本
<a href='https://github.com/fankes/TSBattery/releases'>![Eclipse Marketplace](https://img.shields.io/badge/download-v2.4-green)</a>
<br/><br/>
⚠️适配说明：此模块支持原生 Xposed、Lsposed(作用域 QQ、TIM 如果不起作用勾选系统框架)、EdXposed(不推荐)、太极无极(阴和阳)、Pine(梦境模块)
# 禁止任何商业用途
本模块完全开源免费，如果好用你可以打赏支持开发，严禁未经许可进行二改贩卖，违者必惩必究。
# 开始贡献
欢迎为此项目进行新版本的适配代码贡献！<br/>
## 代码规范：
### 1.全部提交代码必须使用 IDE(Android Studio 或 IDEA) 进行格式化，未经格式化的代码将拒绝合并提交请求
### 2.代码必须使用 4 spaces 缩进格式化
### 3.代码注释规范：
1.第一种注释方式：可使用在方法名或顶级变量名上
```kotlin
/** 注释内容 */
fun a() {
}

/**
 * 注释名称
 * @param test 方法名称
 * @return 返回值名称
 */
fun a(test: String) {
}
```
2.第二种注释方式：仅可使用在变量后方
```kotlin
val a = "" // 变量注释
```
⚠️注意：只允许两个 // 后方要有空格
### 4.调试性质或大批量注释代码，禁止提交
### 5.类名和方法名仅能由开发者进行修改和提交，禁止随意修改项目名称、方法名称以及类名
### 6.禁止随意更新项目依赖以及增加新的依赖，有问题请提前提交到 issues 进行说明
### 7.禁止更新项目版本号，版本号交由开发者合并代码并发布 release 版本
### 8.代码语言要求，请统一使用 Kotlin，除特殊情况外，不接受其他语言的提交