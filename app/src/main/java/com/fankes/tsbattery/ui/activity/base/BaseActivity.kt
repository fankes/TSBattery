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
 * This file is created by fankes on 2022/1/30.
 */
package com.fankes.tsbattery.ui.activity.base

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.viewbinding.ViewBinding
import com.fankes.tsbattery.R
import com.fankes.tsbattery.utils.factory.isNotSystemInDarkMode
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.genericSuperclassTypeArguments
import com.highcapable.kavaref.extension.toClassOrNull
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.proxy.ModuleActivity

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(), ModuleActivity {

    override val moduleTheme get() = R.style.Theme_TSBattery

    /** 获取绑定布局对象 */
    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
        val bindingClass = javaClass.genericSuperclassTypeArguments().firstOrNull()?.toClassOrNull()
        binding = bindingClass?.resolve()?.optional()?.firstMethodOrNull {
            name = "inflate"
            parameters(LayoutInflater::class)
        }?.invoke<VB>(layoutInflater) ?: error("binding failed")
        if (Build.VERSION.SDK_INT >= 35) binding.root.fitsSystemWindows = true
        setContentView(binding.root)
        /** 隐藏系统的标题栏 */
        supportActionBar?.hide()
        /** 初始化沉浸状态栏 */
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = isNotSystemInDarkMode
            isAppearanceLightNavigationBars = isNotSystemInDarkMode
        }
        @Suppress("DEPRECATION")
        ResourcesCompat.getColor(resources, R.color.colorThemeBackground, null).also {
            window?.statusBarColor = it
            window?.navigationBarColor = it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) window?.navigationBarDividerColor = it
        }
        /** 装载子类 */
        onCreate()
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