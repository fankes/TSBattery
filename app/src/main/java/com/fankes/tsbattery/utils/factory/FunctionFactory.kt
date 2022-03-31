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
@file:Suppress("DEPRECATION", "unused")

package com.fankes.tsbattery.utils.factory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.getSystemService
import com.fankes.tsbattery.application.TSApplication.Companion.appContext
import com.google.android.material.snackbar.Snackbar

/**
 * 系统深色模式是否开启
 * @return [Boolean] 是否开启
 */
val isSystemInDarkMode get() = appContext.isSystemInDarkMode

/**
 * 系统深色模式是否没开启
 * @return [Boolean] 是否开启
 */
inline val isNotSystemInDarkMode get() = !isSystemInDarkMode

/**
 * 系统深色模式是否开启
 * @return [Boolean] 是否开启
 */
val Context.isSystemInDarkMode get() = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/**
 * 系统深色模式是否没开启
 * @return [Boolean] 是否开启
 */
inline val Context.isNotSystemInDarkMode get() = !isSystemInDarkMode

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
    get() = try {
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
 * 得到版本信息与版本号
 * @param packageName 包名
 * @return [String]
 */
fun Context.version(packageName: String) = safeOfNothing {
    packageManager?.getPackageInfo(packageName, 0)?.let {
        "${it.versionName}(${it.versionCode})"
    } ?: ""
}

/**
 * 网络连接是否正常
 * @return [Boolean] 网络是否连接
 */
val isNetWorkSuccess
    get() = safeOfFalse { appContext.getSystemService<ConnectivityManager>()?.activeNetworkInfo != null }

/**
 * dp 转换为 pxInt
 * @param context 使用的实例
 * @return [Int]
 */
fun Number.dp(context: Context) = dpFloat(context).toInt()

/**
 * dp 转换为 pxFloat
 * @param context 使用的实例
 * @return [Float]
 */
fun Number.dpFloat(context: Context) = toFloat() * context.resources.displayMetrics.density

/**
 * 弹出 [Toast]
 * @param msg 提示内容
 */
fun toast(msg: String) = Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show()

/**
 * 弹出 [Snackbar]
 * @param msg 提示内容
 * @param actionText 按钮文本 - 不写默认取消按钮
 * @param it 按钮事件回调
 */
fun Context.snake(msg: String, actionText: String = "", it: () -> Unit = {}) =
    Snackbar.make((this as Activity).findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).apply {
        if (actionText.isBlank()) return@apply
        setActionTextColor(if (isSystemInDarkMode) Color.BLACK else Color.WHITE)
        setAction(actionText) { it() }
    }.show()

/**
 * 跳转 APP 自身设置界面
 * @param packageName 包名
 */
fun Context.openSelfSetting(packageName: String = appContext.packageName) = runCatching {
    if (packageName.isInstall)
        startActivity(Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        })
    else snake(msg = "你没有安装此应用")
}.onFailure { toast(msg = "启动 $packageName 应用信息失败") }

/**
 * 启动系统浏览器
 * @param url 网址
 * @param packageName 指定包名 - 可不填
 */
fun Context.openBrowser(url: String, packageName: String = "") = runCatching {
    startActivity(Intent().apply {
        if (packageName.isNotBlank()) setPackage(packageName)
        action = Intent.ACTION_VIEW
        data = Uri.parse(url)
        /** 防止顶栈一样重叠在自己的 APP 中 */
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
}.onFailure {
    if (packageName.isNotBlank()) snake(msg = "启动 $packageName 失败")
    else snake(msg = "启动系统浏览器失败")
}

