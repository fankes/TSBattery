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
 * This file is created by fankes on 2025/4/21.
 */
@file:Suppress("DEPRECATION")

package com.fankes.tsbattery.ui.activity.base

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import com.fankes.tsbattery.R
import com.fankes.tsbattery.utils.factory.isNotSystemInDarkMode
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.proxy.ModuleActivity
import android.R as Android_R

abstract class BaseActivity2 : AppCompatActivity(), ModuleActivity {

    override val moduleTheme get() = R.style.Theme_TSBattery

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
        /** 隐藏系统的标题栏 */
        supportActionBar?.hide()
        /** 初始化沉浸状态栏 */
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = isNotSystemInDarkMode
            isAppearanceLightNavigationBars = isNotSystemInDarkMode
        }
        ResourcesCompat.getColor(resources, R.color.colorThemeBackground, null).also {
            window?.statusBarColor = it
            window?.navigationBarColor = it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) window?.navigationBarDividerColor = it
        }
        /** 装载子类 */
        onCreate()
        if (Build.VERSION.SDK_INT >= 35) findViewById<FrameLayout>(Android_R.id.content).getChildAt(0)?.fitsSystemWindows = true
    }

    /** 回调 [onCreate] 方法 */
    abstract fun onCreate()

    override fun getClassLoader() = delegate.getClassLoader()

    override fun onConfigurationChanged(newConfig: Configuration) {
        delegate.onConfigurationChanged(newConfig)
        super.onConfigurationChanged(newConfig)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        delegate.onRestoreInstanceState(savedInstanceState)
        super.onRestoreInstanceState(savedInstanceState)
    }
}