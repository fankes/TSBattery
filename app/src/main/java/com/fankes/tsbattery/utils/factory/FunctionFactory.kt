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
@file:Suppress("unused", "DiscouragedApi", "InternalInsetResource")

package com.fankes.tsbattery.utils.factory

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PackageInfoFlags
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.content.pm.PackageInfoCompat
import com.fankes.tsbattery.BuildConfig
import com.google.android.material.snackbar.Snackbar
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication.Companion.appContext

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
 * 得到 APP 安装包信息 (兼容)
 * @param packageName APP 包名
 * @param flag [PackageInfoFlags]
 * @return [PackageInfo] or null
 */
private fun Context.getPackageInfoCompat(packageName: String, flag: Number = 0) = runCatching {
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= 33)
        packageManager?.getPackageInfo(packageName, PackageInfoFlags.of(flag.toLong()))
    else packageManager?.getPackageInfo(packageName, flag.toInt())
}.getOrNull()

/**
 * 得到 APP 版本号 (兼容 [PackageInfo.getLongVersionCode])
 * @return [Int]
 */
private val PackageInfo.versionCodeCompat get() = PackageInfoCompat.getLongVersionCode(this)

/**
 * 判断 APP 是否安装
 * @param packageName APP 包名
 * @return [Boolean]
 */
fun Context.isInstall(packageName: String) = getPackageInfoCompat(packageName)?.let { true } ?: false

/**
 * 得到 APP 版本信息
 * @return [String]
 */
val Context.appVersionName get() = getPackageInfoCompat(packageName)?.versionName ?: ""

/**
 * 得到 APP 版本号
 * @return [Int]
 */
val Context.appVersionCode get() = getPackageInfoCompat(packageName)?.versionCodeCompat

/**
 * 得到 APP 版本信息与版本号
 * @param packageName APP 包名 - 默认为当前 APP
 * @return [String]
 */
fun Context.appVersionBrandOf(packageName: String = getPackageName()) =
    getPackageInfoCompat(packageName)?.let { "${it.versionName}(${it.versionCodeCompat})" } ?: ""

/**
 * 得到 APP 名称
 * @param packageName APP 包名 - 默认为当前 APP
 * @return [String]
 */
fun Context.appNameOf(packageName: String = getPackageName()) =
    getPackageInfoCompat(packageName)?.applicationInfo?.loadLabel(packageManager)?.toString() ?: ""

/**
 * 得到 APP 图标
 * @param packageName APP 包名 - 默认为当前 APP
 * @return [Drawable] or null
 */
fun Context.appIconOf(packageName: String = getPackageName()) = getPackageInfoCompat(packageName)?.applicationInfo?.loadIcon(packageManager)

/**
 * 网络连接是否正常
 * @return [Boolean] 网络是否连接
 */
val Context.isNetWorkSuccess
    get() = safeOfFalse {
        @Suppress("DEPRECATION")
        getSystemService<ConnectivityManager>()?.activeNetworkInfo != null
    }

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
 * 获取绝对状态栏高度
 * @return [Int]
 */
val Context.absoluteStatusBarHeight
    get() = safeOfNan {
        resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"))
    }

/**
 * 弹出 [Toast]
 * @param msg 提示内容
 */
fun toast(msg: String) = appContext.toast(msg)

/**
 * 弹出 [Toast]
 * @param msg 提示内容
 */
fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

/**
 * 弹出 [Snackbar]
 * @param msg 提示内容
 * @param actionText 按钮文本 - 不写默认取消按钮
 * @param callback 按钮事件回调
 */
fun Context.snake(msg: String, actionText: String = "", callback: () -> Unit = {}) =
    Snackbar.make((this as Activity).findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).apply {
        if (actionText.isBlank()) return@apply
        setActionTextColor(if (isSystemInDarkMode) Color.BLACK else Color.WHITE)
        setAction(actionText) { callback() }
    }.show()

/**
 * 跳转 APP 自身设置界面
 * @param packageName 包名
 */
fun Context.openSelfSetting(packageName: String = appContext.packageName) = runCatching {
    if (isInstall(packageName))
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

/**
 * 隐藏或显示启动器图标
 *
 * - 你可能需要 LSPosed 的最新版本以开启高版本系统中隐藏 APP 桌面图标功能
 * @param isShow 是否显示
 */
fun Context.hideOrShowLauncherIcon(isShow: Boolean) {
    packageManager?.setComponentEnabledSetting(
        ComponentName(packageName, "${BuildConfig.APPLICATION_ID}.Home"),
        if (isShow) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
    )
}

/**
 * 获取启动器图标状态
 * @return [Boolean] 是否显示
 */
val Context.isLauncherIconShowing
    get() = packageManager?.getComponentEnabledSetting(
        ComponentName(packageName, "${BuildConfig.APPLICATION_ID}.Home")
    ) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED