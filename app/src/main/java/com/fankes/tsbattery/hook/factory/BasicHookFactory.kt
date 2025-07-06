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
 * This file is created by fankes on 2022/9/29.
 */
package com.fankes.tsbattery.hook.factory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.appcompat.app.AppCompatDelegate
import com.fankes.tsbattery.const.JumpEvent
import com.fankes.tsbattery.const.PackageName
import com.fankes.tsbattery.hook.entity.QQTIMHooker.toClass
import com.fankes.tsbattery.ui.activity.parasitic.ConfigActivity
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.param.PackageParam
import kotlin.system.exitProcess

/** QQ、TIM 存在的类 */
private const val MobileQQClass = "mqq.app.MobileQQ"

/** QQ、TIM 存在的类 */
private val ThemeUtilClass = VariousClass("${PackageName.QQ}.theme.ThemeUtil", "${PackageName.QQ}.vas.theme.api.ThemeUtil")

/**
 * QQ、TIM 主题是否为夜间模式
 * @return [Boolean]
 */
fun Context.isQQNightMode(): Boolean {
    val sMobileQQ = MobileQQClass.toClass(classLoader).resolve()
        .optional()
        .firstFieldOrNull {
            name = "sMobileQQ"
            superclass()
        }?.get()
    val mAppRuntime = sMobileQQ?.asResolver()
        ?.optional()
        ?.firstFieldOrNull {
            name = "mAppRuntime"
            superclass()
        }?.get()
    val currentThemeId = ThemeUtilClass.load(classLoader).resolve()
        .optional()
        .firstMethodOrNull {
            name = "getUserCurrentThemeId"
            parameterCount = 1
        }?.invokeQuietly<String>(mAppRuntime)
    return currentThemeId?.let { it.endsWith("1103") || it.endsWith("2920") } == true
}

/** 启动模块设置 [Activity] */
fun Context.startModuleSettings() {
    /** 为 QQ、TIM 适配夜间模式 */
    if (packageName == PackageName.QQ || packageName == PackageName.TIM)
        AppCompatDelegate.setDefaultNightMode(if (isQQNightMode()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    startActivity(Intent(this, ConfigActivity::class.java))
}

/**
 * 跳转模块设置 [Activity]
 * @param isFinish 执行完成是否自动关闭当前活动
 */
fun Activity.jumpToModuleSettings(isFinish: Boolean = true) {
    if (intent.hasExtra(JumpEvent.OPEN_MODULE_SETTING)) {
        /** 宿主版本不匹配的时候自动结束宿主进程 */
        if (intent.getLongExtra(JumpEvent.OPEN_MODULE_SETTING, 0) != YukiHookAPI.Status.compiledTimestamp)
            exitProcess(status = 0)
        intent.removeExtra(JumpEvent.OPEN_MODULE_SETTING)
        startModuleSettings()
        if (isFinish) finish()
    }
}

/** Hook 系统电源锁 */
fun PackageParam.hookSystemWakeLock() {
    PowerManager.WakeLock::class.resolve().apply {
        firstMethod {
            name = "acquireLocked"
            emptyParameters()
        }.hook().intercept()
        firstMethod {
            name = "release"
            parameters(Int::class)
        }.hook().intercept()
    }
}