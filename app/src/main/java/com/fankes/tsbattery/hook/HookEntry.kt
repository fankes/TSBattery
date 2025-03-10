/*
 * TSBattery - A new way to save your battery avoid cancer apps hacker it.
 * Copyright (C) 2017 Fankes Studio(qzmmcn@163.com)
 * https://github.com/fankes/TSBattery
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 *
 * This file is created by fankes on 2022/2/15.
 */
package com.fankes.tsbattery.hook

import com.fankes.tsbattery.const.PackageName
import com.fankes.tsbattery.hook.entity.QQTIMHooker
import com.fankes.tsbattery.hook.entity.WeChatHooker
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit {

    /** 是否完全支持当前版本 */
    var isHookClientSupport = true

    override fun onInit() = configs {
        debugLog { tag = "TSBattery" }
        isDebug = false
        isEnableDataChannel = false
    }

    override fun onHook() = encase {
        loadApp(PackageName.QQ, PackageName.TIM) { loadHooker(QQTIMHooker) }
        loadApp(PackageName.WECHAT, WeChatHooker)
    }
}