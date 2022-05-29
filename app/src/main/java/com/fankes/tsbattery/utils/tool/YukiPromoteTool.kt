/*
 * TSBattery - A new way to save your battery avoid cancer apps hacker it.
 * Copyright (C) 2019-2022 Fankes Studio(qzmmcn@163.com)
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
 * This file is Created by fankes on 2022/5/30.
 */
package com.fankes.tsbattery.utils.tool

import android.content.Context
import com.fankes.tsbattery.utils.factory.openBrowser
import com.fankes.tsbattery.utils.factory.showDialog
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

/**
 * [YukiHookAPI] 的自动推广工具类
 */
object YukiPromoteTool {

    /** 推广已读存储键值 */
    private val YUKI_PROMOTE_READED = PrefsData("yuki_promote_readed", false)

    /**
     * 显示推广对话框
     * @param context 实例
     */
    fun promote(context: Context) {
        fun saveReaded() = context.modulePrefs.put(YUKI_PROMOTE_READED, value = true)
        if (context.modulePrefs.get(YUKI_PROMOTE_READED).not())
            context.showDialog {
                title = "面向开发者的推广"
                msg = "你想快速拥有一个自己的 Xposed 模块吗，你只需要拥有基础的 Android 开发经验以及使用 Kotlin 编程语言即可。\n\n" +
                        "快来体验 YukiHookAPI，这是一个使用 Kotlin 重新构建的高效 Xposed Hook API，助你的开发变得更轻松。"
                confirmButton(text = "去看看") {
                    context.openBrowser(url = "https://github.com/fankes/YukiHookAPI")
                    saveReaded()
                }
                cancelButton(text = "我不是开发者") { saveReaded() }
                noCancelable()
            }
    }
}