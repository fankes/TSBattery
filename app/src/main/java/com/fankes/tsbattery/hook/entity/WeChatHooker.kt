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
@file:Suppress("ConstPropertyName")

package com.fankes.tsbattery.hook.entity

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.fankes.tsbattery.R
import com.fankes.tsbattery.const.PackageName
import com.fankes.tsbattery.data.ConfigData
import com.fankes.tsbattery.hook.factory.hookSystemWakeLock
import com.fankes.tsbattery.hook.factory.jumpToModuleSettings
import com.fankes.tsbattery.hook.factory.startModuleSettings
import com.fankes.tsbattery.utils.factory.absoluteStatusBarHeight
import com.fankes.tsbattery.utils.factory.appVersionCode
import com.fankes.tsbattery.utils.factory.appVersionName
import com.fankes.tsbattery.utils.factory.dp
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.processName
import com.highcapable.yukihookapi.hook.factory.registerModuleAppActivities
import com.highcapable.yukihookapi.hook.log.YLog

/**
 * Hook 微信
 *
 * 具体功能还在画饼
 */
object WeChatHooker : YukiBaseHooker() {

    /** 微信存在的类 - 未测试每个版本是否都存在 */
    const val LauncherUIClassName = "${PackageName.WECHAT}.ui.LauncherUI"

    /** 微信存在的类 - 未测试每个版本是否都存在 */
    private val LauncherUIClass by lazyClassOrNull("${PackageName.WECHAT}.ui.LauncherUI")

    /** 微信存在的类 - 未测试每个版本是否都存在 */
    private val EmptyActivityClass by lazyClassOrNull("${PackageName.WECHAT}.ui.EmptyActivity")

    /** 微信存在的类 - 未测试每个版本是否都存在 */
    private val WelabMainUIClass by lazyClassOrNull("${PackageName.WECHAT}.plugin.welab.ui.WelabMainUI")

    /** 微信存在的类 - 未测试每个版本是否都存在 */
    private val SettingsUIClass by lazyClassOrNull("${PackageName.WECHAT}.plugin.setting.ui.setting.SettingsUI")

    /** TSBattery 图标 TAG 名称 */
    private const val TSBARRERY_ICON_TAG = "tsbattery_icon"

    /**
     * 创建 TSBattery 图标
     * @param context 当前实例
     * @return [LinearLayout]
     */
    private fun createPreferenceIcon(context: Context) = LinearLayout(context).apply {
        tag = TSBARRERY_ICON_TAG
        gravity = Gravity.END or Gravity.BOTTOM
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        addView(ImageView(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(20.dp(context), 20.dp(context)).apply {
                topMargin = context.absoluteStatusBarHeight + 15.dp(context)
                rightMargin = 20.dp(context)
            }
            setColorFilter(ResourcesCompat.getColor(resources, R.color.colorTextGray, null))
            setImageResource(R.drawable.ic_icon)
            if (Build.VERSION.SDK_INT >= 26) tooltipText = "TSBattery 设置"
            setOnClickListener { context.startModuleSettings() }
        })
    }

    override fun onHook() {
        onAppLifecycle {
            onCreate {
                ConfigData.init(context = this)
                registerModuleAppActivities(when {
                    EmptyActivityClass != null -> EmptyActivityClass
                    WelabMainUIClass != null -> WelabMainUIClass
                    else -> error("Inject WeChat Activity Proxy failed, unsupport version $appVersionName($appVersionCode)")
                })
                if (ConfigData.isDisableAllHook) return@onCreate
                hookSystemWakeLock()
                YLog.info("All processes are completed for \"${processName.takeIf { it != packageName } ?: packageName}\"")
            }
        }
        /** 仅注入主进程 */
        withProcess(mainProcessName) {
            /** Hook 跳转事件 */
            LauncherUIClass?.resolve()?.optional()?.firstMethodOrNull {
                name = "onResume"
                emptyParameters()
            }?.hook()?.after { instance<Activity>().jumpToModuleSettings(isFinish = false) }
            /** 向设置界面右上角添加按钮 */
            SettingsUIClass?.resolve()?.optional()?.firstMethodOrNull {
                name = "onResume"
                emptyParameters()
            }?.hook()?.after {
                SettingsUIClass?.resolve()?.optional()?.firstMethodOrNull {
                    name = "get_fragment"
                    emptyParameters()
                    superclass()
                }?.of(instance)?.invoke()?.asResolver()?.optional()?.firstMethodOrNull {
                    name = "getView"
                    emptyParameters()
                    returnType = View::class
                    superclass()
                }?.invoke<ViewGroup?>()?.also {
                    it.context?.injectModuleAppResources()
                    runCatching { it.getChildAt(0) as? ViewGroup? }.getOrNull()?.also { rootView ->
                        if (rootView.findViewWithTag<View>(TSBARRERY_ICON_TAG) == null)
                            rootView.addView(createPreferenceIcon(it.context))
                    }
                }
            }
        }
    }
}