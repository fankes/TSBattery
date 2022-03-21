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
 * This file is Created by fankes on 2021/9/4.
 */
@file:Suppress("SetTextI18n", "LocalVariableName", "SameParameterValue")

package com.fankes.tsbattery.ui.activity

import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.fankes.tsbattery.BuildConfig
import com.fankes.tsbattery.R
import com.fankes.tsbattery.databinding.ActivityMainBinding
import com.fankes.tsbattery.hook.HookConst.DISABLE_WECHAT_HOOK
import com.fankes.tsbattery.hook.HookConst.ENABLE_HIDE_ICON
import com.fankes.tsbattery.hook.HookConst.ENABLE_MODULE_VERSION
import com.fankes.tsbattery.hook.HookConst.ENABLE_NOTIFY_TIP
import com.fankes.tsbattery.hook.HookConst.ENABLE_QQTIM_CORESERVICE_BAN
import com.fankes.tsbattery.hook.HookConst.ENABLE_QQTIM_CORESERVICE_CHILD_BAN
import com.fankes.tsbattery.hook.HookConst.ENABLE_QQTIM_WHITE_MODE
import com.fankes.tsbattery.hook.HookConst.ENABLE_RUN_INFO
import com.fankes.tsbattery.hook.HookConst.QQ_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookConst.TIM_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookConst.WECHAT_PACKAGE_NAME
import com.fankes.tsbattery.ui.activity.base.BaseActivity
import com.fankes.tsbattery.utils.factory.isInstall
import com.fankes.tsbattery.utils.factory.openBrowser
import com.fankes.tsbattery.utils.factory.openSelfSetting
import com.fankes.tsbattery.utils.factory.showDialog
import com.fankes.tsbattery.utils.tool.GithubReleaseTool
import com.highcapable.yukihookapi.hook.factory.isModuleActive
import com.highcapable.yukihookapi.hook.factory.isTaiChiModuleActive
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {

        private const val moduleVersion = BuildConfig.VERSION_NAME
        private const val qqSupportVersion =
            "8.2.11(Play)、8.8.17、8.8.23、8.8.35、8.8.38、8.8.50、8.8.55、8.8.68、8.8.80、8.8.83 (8.2.11、8.5.5~8.8.83)"
        private const val timSupportVersion = "2+、3+ (并未完全测试每个版本)"
        private const val wechatSupportVersion = "全版本仅支持基础省电，更多功能依然画饼"

        /** 预发布的版本标识 */
        private const val pendingFlag = "[pending]"
    }

    override fun onCreate() {
        /** 检查更新 */
        GithubReleaseTool.checkingForUpdate(context = this, moduleVersion) { version, function ->
            binding.mainTextReleaseVersion.apply {
                text = "点击更新 $version"
                isVisible = true
                setOnClickListener { function() }
            }
        }
        /** 判断 Hook 状态 */
        if (isModuleActive) {
            binding.mainLinStatus.setBackgroundResource(R.drawable.bg_green_round)
            binding.mainImgStatus.setImageResource(R.mipmap.ic_success)
            binding.mainTextStatus.text = "模块已激活"
            /** 写入激活的模块版本 */
            modulePrefs.putString(ENABLE_MODULE_VERSION, moduleVersion)
        } else
            showDialog {
                title = "模块没有激活"
                msg = "检测到模块没有激活，模块需要 Xposed 环境依赖，" +
                        "同时需要系统拥有 Root 权限(太极阴可以免 Root)，" +
                        "请自行查看本页面使用帮助与说明第三条。\n" +
                        "太极和第三方 Xposed 激活后" +
                        "可能不会提示激活，若想验证是否激活请打开“提示模块运行信息”自行检查，" +
                        "或观察 QQ、TIM 的常驻通知是否有“TSBattery 守护中”字样”，" +
                        "如果生效就代表模块运行正常，这里的激活状态只是一个显示意义上的存在。\n" +
                        "太极(无极)在 MIUI 设备上会提示打开授权，请进行允许，然后再次打开本应用查看激活状态。"
                confirmButton(text = "我知道了")
                noCancelable()
            }
        /** 推荐使用 LSPosed */
        if (isTaiChiModuleActive)
            showDialog {
                title = "兼容性提示"
                msg = "若你的设备已 Root，推荐使用 LSPosed 激活模块，太极可能会出现模块设置无法保存的问题。"
                confirmButton(text = "我知道了")
            }
        /** 检测应用转生 */
        if (("com.bug.xposed").isInstall)
            showDialog {
                title = "环境异常"
                msg = "检测到“应用转生”已被安装，为了保证模块的安全和稳定，请卸载更换其他 Hook 框架后才能继续使用。"
                confirmButton(text = "退出") { finish() }
                noCancelable()
            }
        /** 设置安装状态 */
        binding.mainTextQqNoinstall.isGone = QQ_PACKAGE_NAME.isInstall
        binding.mainTextTimNoinstall.isGone = TIM_PACKAGE_NAME.isInstall
        binding.mainTextWechatNoinstall.isGone = WECHAT_PACKAGE_NAME.isInstall
        /** 设置文本 */
        binding.mainTextVersion.text = "模块版本：$moduleVersion $pendingFlag"
        binding.mainTextSupportQq.apply {
            text = qqSupportVersion
            setOnClickListener {
                showDialog {
                    title = "兼容的 QQ 版本"
                    msg = qqSupportVersion
                    confirmButton(text = "我知道了")
                }
            }
        }
        binding.mainTextSupportTim.apply {
            text = timSupportVersion
            setOnClickListener {
                showDialog {
                    title = "兼容的 TIM 版本"
                    msg = timSupportVersion
                    confirmButton(text = "我知道了")
                }
            }
        }
        binding.mainTextSupportWechat.apply {
            text = wechatSupportVersion
            setOnClickListener {
                showDialog {
                    title = "兼容的微信版本"
                    msg = wechatSupportVersion
                    confirmButton(text = "我知道了")
                }
            }
        }
        /** 获取 Sp 存储的信息 */
        binding.qqtimProtectModeSwitch.isChecked = modulePrefs.getBoolean(ENABLE_QQTIM_WHITE_MODE)
        binding.qqTimCoreServiceSwitch.isChecked = modulePrefs.getBoolean(ENABLE_QQTIM_CORESERVICE_BAN)
        binding.qqTimCoreServiceKnSwitch.isChecked = modulePrefs.getBoolean(ENABLE_QQTIM_CORESERVICE_CHILD_BAN)
        binding.wechatDisableHookSwitch.isChecked = modulePrefs.getBoolean(DISABLE_WECHAT_HOOK)
        binding.hideIconInLauncherSwitch.isChecked = modulePrefs.getBoolean(ENABLE_HIDE_ICON)
        binding.notifyModuleInfoSwitch.isChecked = modulePrefs.getBoolean(ENABLE_RUN_INFO)
        binding.notifyNotifyTipSwitch.isChecked = modulePrefs.getBoolean(ENABLE_NOTIFY_TIP, default = true)
        binding.qqtimProtectModeSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(ENABLE_QQTIM_WHITE_MODE, b)
        }
        binding.qqTimCoreServiceSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(ENABLE_QQTIM_CORESERVICE_BAN, b)
        }
        binding.qqTimCoreServiceKnSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(ENABLE_QQTIM_CORESERVICE_CHILD_BAN, b)
        }
        binding.wechatDisableHookSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(DISABLE_WECHAT_HOOK, b)
        }
        binding.hideIconInLauncherSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(ENABLE_HIDE_ICON, b)
            packageManager.setComponentEnabledSetting(
                ComponentName(this@MainActivity, "com.fankes.tsbattery.Home"),
                if (b) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
        binding.notifyModuleInfoSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(ENABLE_RUN_INFO, b)
        }
        binding.notifyNotifyTipSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(ENABLE_NOTIFY_TIP, b)
        }
        /** 快捷操作 QQ */
        binding.quickQqButton.setOnClickListener { openSelfSetting(QQ_PACKAGE_NAME) }
        /** 快捷操作 TIM */
        binding.quickTimButton.setOnClickListener { openSelfSetting(TIM_PACKAGE_NAME) }
        /** 快捷操作微信 */
        binding.quickWechatButton.setOnClickListener { openSelfSetting(WECHAT_PACKAGE_NAME) }
        /** 项目地址按钮点击事件 */
        binding.titleGithubIcon.setOnClickListener { openBrowser(url = "https://github.com/fankes/TSBattery") }
        /** 恰饭！ */
        binding.linkWithFollowMe.setOnClickListener {
            openBrowser(url = "https://www.coolapk.com/u/876977", packageName = "com.coolapk.market")
        }
    }
}