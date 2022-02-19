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
 * This file is Created by fankes on 2022/1/7.
 */
@file:Suppress("DEPRECATION")

package com.fankes.tsbattery.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.fankes.tsbattery.application.TSApplication.Companion.appContext

/**
 * 系统深色模式是否开启
 * @return [Boolean] 是否开启
 */
val isSystemInDarkMode
    get() = (appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/**
 * 系统深色模式是否没开启
 * @return [Boolean] 是否开启
 */
inline val isNotSystemInDarkMode get() = !isSystemInDarkMode

/**
 * 得到安装包信息
 * @return [PackageInfo]
 */
val Context.packageInfo get() = packageManager?.getPackageInfo(packageName, 0) ?: PackageInfo()

/**
 * 判断应用是否安装
 * @return [Boolean]
 */
val String.isInstall
    get() =
        try {
            appContext.packageManager.getPackageInfo(
                this,
                PackageManager.GET_UNINSTALLED_PACKAGES
            )
            true
        } catch (e: Exception) {
            false
        }

/**
 * 得到版本信息
 * @return [String]
 */
val Context.versionName get() = packageInfo.versionName ?: ""

/**
 * 得到版本号
 * @return [Int]
 */
val Context.versionCode get() = packageInfo.versionCode

/**
 * dp 转换为 px
 * @return [Int]
 */
val Number.dp get() = (toFloat() * appContext.resources.displayMetrics.density).toInt()

/**
 * dp 转换为 px
 * @param context 使用的实例
 * @return [Float]
 */
fun Number.dp(context: Context) = toFloat() * context.resources.displayMetrics.density

/**
 * 跳转 APP 自身设置界面
 * @param packageName 包名
 */
fun Context.openSelfSetting(packageName: String) {
    try {
        if (packageName.isInstall)
            startActivity(Intent().apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
            })
        else Toast.makeText(this, "你没有安装此应用", Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
        Toast.makeText(this, "启动 $packageName 应用信息失败", Toast.LENGTH_SHORT).show()
    }
}

