# TSBattery

[![GitHub license](https://img.shields.io/github/license/fankes/TSBattery?color=blue)](https://github.com/fankes/TSBattery/blob/master/LICENSE)
[![GitHub CI](https://img.shields.io/github/actions/workflow/status/fankes/TSBattery/commit_ci.yml?label=CI%20builds)](https://github.com/fankes/TSBattery/actions/workflows/commit_ci.yml)
[![GitHub release](https://img.shields.io/github/v/release/fankes/TSBattery?display_name=release&logo=github&color=green)](https://github.com/fankes/TSBattery/releases)
![GitHub all releases](https://img.shields.io/github/downloads/fankes/TSBattery/total?label=downloads)
![GitHub all releases](https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.fankes.tsbattery/total?label=LSPosed%20downloads&labelColor=F48FB1)

[![Telegram CI](https://img.shields.io/badge/CI%20builds-Telegram-blue.svg?logo=telegram)](https://t.me/TSBATTERY_CI)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram)](https://t.me/XiaofangInternet)
[![QQ](https://img.shields.io/badge/discussion-QQ-blue.svg?logo=tencent-qq&logoColor=red)](https://qm.qq.com/cgi-bin/qm/qr?k=dp2h5YhWiga9WWb_Oh7kSHmx01X8I8ii&jump_from=webapi&authKey=Za5CaFP0lk7+Zgsk2KpoBD7sSaYbeXbsDgFjiWelOeH4VSionpxFJ7V0qQBSqvFM)
[![QQ 频道](https://img.shields.io/badge/discussion-QQ%20频道-blue.svg?logo=tencent-qq&logoColor=red)](https://pd.qq.com/s/44gcy28h)

![Banner](https://github.com/fankes/TSBattery/blob/master/img-src/banner.png?raw=true)

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

## 发行渠道说明

- [Automatic Build on Commit](https://github.com/fankes/TSBattery/actions/workflows/commit_ci.yml)

上述更新为代码 `commit` 后自动触发，具体更新内容可点击上方的文字前往 **GitHub Actions** 进行查看，本更新由开源的流程自动编译发布，
**不保证其稳定性**， 所发布的版本**仅供测试**，且不会特殊说明甚至可能会变更版本号或保持与当前稳定版相同的版本号。

如果你需要直接下载 CI 自动构建打包的安装包，请点击顶部的 `CI builds | Telegram` 标签加入 Telegram CI 自动构建频道。

- [Release](https://github.com/fankes/TSBattery/releases)
- [Xposed-Modules-Repo](https://github.com/Xposed-Modules-Repo/com.fankes.tsbattery/releases)
- [蓝奏云 **密码：tsbt**](https://fankes.lanzouy.com/b02zfz3sj)

上述更新为手动发布的稳定版，具体更新内容可点击上方的文字前往指定的发布页面查看，稳定版的更新将会同时发布到上述地址中，同步更新。

本模块发布地址仅限于上述所列出的地址，从其他非正规渠道下载到的版本或对您造成任何影响均与我们无关。

## 请勿用于非法用途

本模块完全开源免费，如果好用你可以打赏支持开发，但是请不要用于非法用途。

## 项目推广

如果你正在寻找一个可以自动管理 Gradle 项目依赖的 Gradle 插件，你可以了解一下 [SweetDependency](https://github.com/HighCapable/SweetDependency) 项目。

如果你正在寻找一个可以自动生成属性键值的 Gradle 插件，你可以了解一下 [SweetProperty](https://github.com/HighCapable/SweetProperty) 项目。

本项目同样使用了 **SweetDependency** 和 **SweetProperty**。

## 捐赠支持

工作不易，无意外情况此项目将继续维护下去，提供更多可能，欢迎打赏。

<img src="https://github.com/fankes/fankes/blob/main/img-src/payment_code.jpg?raw=true" width = "500" alt="Payment Code"/>

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=fankes/TSBattery&type=Date)

## 许可证

- [AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.html)

```
Copyright (C) 2017-2023 Fankes Studio(qzmmcn@163.com)

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

Powered by [YukiHookAPI](https://github.com/fankes/YukiHookAPI)

版权所有 © 2017-2023 Fankes Studio(qzmmcn@163.com)