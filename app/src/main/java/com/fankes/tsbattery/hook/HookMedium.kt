/*
 * TSBattery - A new way to save your battery avoid cancer apps hacker it.
 * Copyright (C) 2019-2022 Fankes Studio(qzmmcn@163.com)
 * https://github.com/fankes/TSBattery
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
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
 * This file is Created by fankes on 2021/11/9.
 */
@file:Suppress("DEPRECATION", "SetWorldReadable")

package com.fankes.tsbattery.hook

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.Keep
import com.fankes.tsbattery.application.TSApplication.Companion.appContext
import com.fankes.tsbattery.application.TSApplication.Companion.isMineStarted
import com.fankes.tsbattery.ui.MainActivity
import com.fankes.tsbattery.utils.FileUtils
import com.fankes.tsbattery.utils.XPrefUtils
import java.io.File

@Keep
object HookMedium {

    const val ENABLE_HIDE_ICON = "_hide_icon"
    const val ENABLE_RUN_INFO = "_tip_run_info"
    const val ENABLE_QQTIM_WHITE_MODE = "_qqtim_white_mode"
    const val ENABLE_QQTIM_CORESERVICE_BAN = "_qqtim_core_service_ban"
    const val ENABLE_QQTIM_CORESERVICE_CHILD_BAN = "_qqtim_core_service_child_ban"
    const val DISABLE_WECHAT_HOOK = "_disable_wechat_hook"
    const val ENABLE_MODULE_VERSION = "_module_version"

    const val SELF_PACKAGE_NAME = "com.fankes.tsbattery"
    const val QQ_PACKAGE_NAME = "com.tencent.mobileqq"
    const val TIM_PACKAGE_NAME = "com.tencent.tim"
    const val WECHAT_PACKAGE_NAME = "com.tencent.mm"

    /**
     * 判断模块是否激活
     * 在 [HookMain] 中 Hook 掉此方法
     * @return [Boolean] 激活状态
     */
    fun isHooked(): Boolean {
        Log.d("TSBattery", "isHooked: true")
        return isExpModuleActive()
    }

    /**
     * 太极激活判断方式
     * @return [Boolean] 是否激活
     */
    private fun isExpModuleActive(): Boolean {
        var isExp = false
        MainActivity.instance?.also {
            try {
                val uri = Uri.parse("content://me.weishu.exposed.CP/")
                var result: Bundle? = null
                try {
                    result = it.contentResolver.call(uri, "active", null, null)
                } catch (_: RuntimeException) {
                    // TaiChi is killed, try invoke
                    try {
                        val intent = Intent("me.weishu.exp.ACTION_ACTIVE")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        it.startActivity(intent)
                    } catch (_: Throwable) {
                        return false
                    }
                }
                if (result == null) result = it.contentResolver.call(uri, "active", null, null)
                if (result == null) return false
                isExp = result.getBoolean("active", false)
            } catch (_: Throwable) {
            }
        }
        return isExp
    }

    /**
     * 获取保存的值
     * @param key 名称
     * @param default 默认值
     * @return [Boolean] 保存的值
     */
    fun getBoolean(key: String, default: Boolean = false) =
        if (isMineStarted)
            appContext.getSharedPreferences(
                appContext.packageName + "_preferences",
                Context.MODE_PRIVATE
            ).getBoolean(key, default)
        else XPrefUtils.getBoolean(key, default)

    /**
     * 获取保存的值
     * @param key 名称
     * @param default 默认值
     * @return [String] 保存的值
     */
    fun getString(key: String, default: String = "unknown") =
        if (isMineStarted)
            appContext.getSharedPreferences(
                appContext.packageName + "_preferences",
                Context.MODE_PRIVATE
            ).getString(key, default)
        else XPrefUtils.getString(key, default)

    /**
     * 保存值
     * @param key 名称
     * @param bool 值
     */
    fun putBoolean(key: String, bool: Boolean) {
        appContext.getSharedPreferences(
            appContext.packageName + "_preferences",
            Context.MODE_PRIVATE
        ).edit().putBoolean(key, bool).apply()
        setWorldReadable(appContext)
        /** 延迟继续设置强制允许 SP 可读可写 */
        Handler().postDelayed({ setWorldReadable(appContext) }, 500)
        Handler().postDelayed({ setWorldReadable(appContext) }, 1000)
        Handler().postDelayed({ setWorldReadable(appContext) }, 1500)
    }

    /**
     * 保存值
     * @param key 名称
     * @param value 值
     */
    fun putString(key: String, value: String) {
        appContext.getSharedPreferences(
            appContext.packageName + "_preferences",
            Context.MODE_PRIVATE
        ).edit().putString(key, value).apply()
        setWorldReadable(appContext)
        /** 延迟继续设置强制允许 SP 可读可写 */
        Handler().postDelayed({ setWorldReadable(appContext) }, 500)
        Handler().postDelayed({ setWorldReadable(appContext) }, 1000)
        Handler().postDelayed({ setWorldReadable(appContext) }, 1500)
    }

    /**
     * 强制设置 Sp 存储为全局可读可写
     * 以供模块使用
     * @param context 实例
     */
    fun setWorldReadable(context: Context) {
        try {
            if (FileUtils.getDefaultPrefFile(context).exists()) {
                for (file in arrayOf<File>(
                    FileUtils.getDataDir(context),
                    FileUtils.getPrefDir(context),
                    FileUtils.getDefaultPrefFile(context)
                )) {
                    file.setReadable(true, false)
                    file.setExecutable(true, false)
                }
            }
        } catch (_: Exception) {
            Toast.makeText(context, "无法写入模块设置，请检查权限\n如果此提示一直显示，请不要双开模块", Toast.LENGTH_SHORT).show()
        }
    }
}