/*
 * Copyright (C) 2021. Fankes Studio(qzmmcn@163.com)
 *
 * This file is part of TSBattery.
 *
 * TSBattery is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TSBattery is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This file is Created by fankes on 2021/11/9.
 */
@file:Suppress("unused")

package com.fankes.tsbattery.application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class TSApplication : Application() {

    companion object {

        /** 全局静态实例 */
        private var context: TSApplication? = null

        /**
         * 调用全局静态实例
         * @return [TSApplication]
         */
        val appContext get() = context ?: error("App is death")
    }

    override fun onCreate() {
        super.onCreate()
        /** 设置静态实例 */
        context = this
        /** 禁止系统夜间模式对自己造成干扰 - 模块要什么夜间模式？😅 (其实是我懒) */
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}