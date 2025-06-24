# TSBattery

[![GitHub license](https://img.shields.io/github/license/fankes/TSBattery?color=blue&style=flat-square)](https://github.com/fankes/TSBattery/blob/master/LICENSE)
[![GitHub CI](https://img.shields.io/github/actions/workflow/status/fankes/TSBattery/commit_ci.yml?label=CI%20builds&style=flat-square)](https://github.com/fankes/TSBattery/actions/workflows/commit_ci.yml)
[![GitHub release](https://img.shields.io/github/v/release/fankes/TSBattery?display_name=release&logo=github&color=green&style=flat-square)](https://github.com/fankes/TSBattery/releases)
![GitHub all releases](https://img.shields.io/github/downloads/fankes/TSBattery/total?label=downloads&style=flat-square)
![GitHub all releases](https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.fankes.tsbattery/total?label=LSPosed%20downloads&labelColor=F48FB1&style=flat-square)

[![Telegram CI](https://img.shields.io/badge/CI%20builds-Telegram-blue.svg?logo=telegram&style=flat-square)](https://t.me/TSBattery_CI)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram&style=flat-square)](https://t.me/XiaofangInternet)
[![QQ](https://img.shields.io/badge/discussion-QQ-blue.svg?logo=tencent-qq&logoColor=red&style=flat-square)](https://qm.qq.com/cgi-bin/qm/qr?k=dp2h5YhWiga9WWb_Oh7kSHmx01X8I8ii&jump_from=webapi&authKey=Za5CaFP0lk7+Zgsk2KpoBD7sSaYbeXbsDgFjiWelOeH4VSionpxFJ7V0qQBSqvFM)
[![QQ 频道](https://img.shields.io/badge/discussion-QQ%20频道-blue.svg?logo=tencent-qq&logoColor=red&style=flat-square)](https://pd.qq.com/s/44gcy28h)

<img src="app/src/main/ic_launcher-playstore.png" width = "100" height = "100" alt="LOGO"/>

A new way to save your battery avoid cancer apps hacker it.

TSBattery 是一个旨在使 QQ、TIM、微信 变得更省电的开源 Xposed 模块。

## For Non-Chinese Users

This Xposed Module is for use by specific apps for users in mainland China, you should not need it.

## 适配说明

- 解锁 BootLoader 并安装 **KernelSU**、**Magisk** 的设备建议使用 [LSPosed](https://github.com/LSPosed/LSPosed)

- **太极 (无极)** 支持性不是很好，建议使用 [LSPatch](https://github.com/LSPosed/LSPatch)

- 支持一些第三方 Xposed 框架，但是不保证其稳定性

- 支持一些第三方免 Root 框架例如**应用转生**、**SandVXposed**，但是不推荐使用，可能会造成封号风险

- 如果在微信设置界面右上角你无法找到 **TSBattery**
  的图标，请尝试同时激活 [WeXposed (微X模块)](https://github.com/Xposed-Modules-Repo/com.fkzhang.wechatxposed)

## 发行渠道

| <img src="https://avatars.githubusercontent.com/in/15368?s=64&v=4" width = "30" height = "30" alt="LOGO"/> | [GitHub CI](https://github.com/fankes/TSBattery/actions/workflows/commit_ci.yml) | CI 自动构建 (测试版) |
|------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|---------------|

| <img src="https://github.com/peter-iakovlev/Telegram/blob/public/Icon.png?raw=true" width = "30" height = "30" alt="LOGO"/> | [Telegram CI 频道](https://t.me/TSBattery_CI) | CI 自动构建 (测试版) |
|-----------------------------------------------------------------------------------------------------------------------------|---------------------------------------------|---------------|

| <img src="https://avatars.githubusercontent.com/in/15368?s=64&v=4" width = "30" height = "30" alt="LOGO"/> | [GitHub Releases](https://github.com/fankes/TSBattery/releases) | 正式版 (稳定版) |
|------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------|-----------|

| <img src="https://avatars.githubusercontent.com/u/78217009?s=200&v=4?raw=true" width = "30" height = "30" alt="LOGO"/> | [Xposed-Modules-Repo](https://github.com/Xposed-Modules-Repo/com.fankes.tsbattery/releases) | 正式版 (稳定版) |
|------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------|-----------|

| <img src="https://github.com/fankes/fankes/assets/37344460/82113d3c-aa7b-4dd1-95c7-cda650065c12" width = "30" height = "30" alt="LOGO"/> | [123 云盘 **(密码：tsbt)**](https://www.123pan.com/s/5SlUVv-N8DBh.html) | 正式版 (稳定版) |
|------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------|-----------|

本模块发布地址仅限于上述所列出的地址，从其他非正规渠道下载到的版本或对您造成任何影响均与我们无关。

## 注意事项

<h3>1.&nbsp;本软件免费、由兴趣驱动开发，仅供学习交流使用。如果你是从其他非官方渠道付费获得本软件，可能已遭遇欺诈，欢迎向我们举报可疑行为。</h3>

<h3>2.&nbsp;本软件采用 <strong>GNU Affero General Public License (AGPL 3.0)</strong> 许可证。根据该许可证的要求：</h3>

- 任何衍生作品必须采用相同的 AGPL 许可证
- 分发本软件或其修改版本时，必须提供完整的源代码
- 必须保留原始的版权声明及许可证信息
- 不得额外施加限制来限制他人对本软件的自由使用

<h3>3.&nbsp;我们鼓励在遵守 AGPL 3.0 条款的前提下进行自由传播和改进，但请尊重作者署名权，勿冒用原作者名义。</h3>

## 项目推广

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
    <h2>嘿，还请君留步！👋</h2>
    <h3>这里有 Android 开发工具、UI 设计、Gradle 插件、Xposed 模块和实用软件等相关项目。</h3>
    <h3>如果下方的项目能为你提供帮助，不妨为我点个 star 吧！</h3>
    <h3>所有项目免费、开源，遵循对应开源许可协议。</h3>
    <h1><a href="https://github.com/fankes/fankes/blob/main/project-promote/README-zh-CN.md">→ 查看更多关于我的项目，请点击这里 ←</a></h1>
</div>

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=fankes/TSBattery&type=Date)

## 许可证

- [AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.html)

```
Copyright (C) 2017 Fankes Studio(qzmmcn@163.com)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

Powered by [YukiHookAPI](https://github.com/HighCapable/YukiHookAPI)

版权所有 © 2017 Fankes Studio(qzmmcn@163.com)