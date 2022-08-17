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
import android.view.HapticFeedbackConstants
import androidx.core.view.isVisible
import com.fankes.tsbattery.BuildConfig
import com.fankes.tsbattery.R
import com.fankes.tsbattery.data.DataConst
import com.fankes.tsbattery.databinding.ActivityMainBinding
import com.fankes.tsbattery.hook.HookConst.QQ_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookConst.TIM_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookConst.WECHAT_PACKAGE_NAME
import com.fankes.tsbattery.ui.activity.base.BaseActivity
import com.fankes.tsbattery.utils.factory.*
import com.fankes.tsbattery.utils.tool.GithubReleaseTool
import com.fankes.tsbattery.utils.tool.YukiPromoteTool
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class MainActivity : BaseActivity<ActivityMainBinding>() {

    companion object {

        private const val moduleVersion = BuildConfig.VERSION_NAME
        private val qqSupportVersions = arrayOf(
            "8.0.0", "8.0.5", "8.0.7", "8.1.0", "8.1.3", "8.1.5", "8.1.8",
            "8.2.0", "8.2.6", "8.2.7", "8.2.8", "8.2.11", "8.3.0", "8.3.5",
            "8.3.6", "8.3.9", "8.4.1", "8.4.5", "8.4.8", "8.4.10", "8.4.17",
            "8.4.18", "8.5.0", "8.5.5", "8.6.0", "8.6.5", "8.7.0", "8.7.5",
            "8.7.8", "8.8.0", "8.8.3", "8.8.5", "8.8.11", "8.8.12", "8.8.17",
            "8.8.20", "8.8.23", "8.8.28", "8.8.33", "8.8.35", "8.8.38", "8.8.50",
            "8.8.55", "8.8.68", "8.8.80", "8.8.83", "8.8.85", "8.8.88", "8.8.90",
            "8.8.93", "8.8.95", "8.8.98", "8.9.0", "8.9.1", "8.9.2", "8.9.3", "8.9.5"
        )
        private val qqSupportVersion by lazy {
            if (qqSupportVersions.isNotEmpty()) {
                var value = ""
                qqSupportVersions.forEach { value += "$it、" }
                "${value.trim().let { it.substring(0, it.lastIndex) }}\n\n其余版本请自行测试是否有效。"
            } else "empty"
        }
        private const val timSupportVersion = "2+、3+ (并未完全测试每个版本)"
        private const val wechatSupportVersion = "全版本仅支持基础省电，更多功能依然画饼"

        /** 预发布的版本标识 */
        private const val pendingFlag = ""
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
        if (YukiHookAPI.Status.isModuleActive) {
            binding.mainLinStatus.setBackgroundResource(R.drawable.bg_green_round)
            binding.mainImgStatus.setImageResource(R.mipmap.ic_success)
            binding.mainTextStatus.text = "模块已激活"
            binding.mainTextApiWay.isVisible = true
            refreshActivateExecutor()
            /** 写入激活的模块版本 */
            modulePrefs.put(DataConst.ENABLE_MODULE_VERSION, moduleVersion)
            /** 推广、恰饭 */
            YukiPromoteTool.promote(context = this)
        } else
            showDialog {
                title = "模块没有激活"
                msg = "检测到模块没有激活，模块需要 Xposed 环境依赖，" +
                        "同时需要系统拥有 Root 权限(太极阴可以免 Root)，" +
                        "请自行查看本页面使用帮助与说明第三条。\n" +
                        "太极和第三方 Xposed 激活后" +
                        "可能不会提示激活，若想验证是否激活请打开“提示模块运行信息”自行检查，" +
                        "或观察 QQ、TIM 的常驻通知是否有“TSBattery 守护中”字样”。\n\n" +
                        "如果生效就代表模块运行正常，若你在未 Root 情况下激活模块，这里的激活状态只是一个显示意义上的存在。\n" +
                        "太极(无极)在 MIUI 设备上会提示打开授权，请进行允许，然后再次打开本模块查看激活状态。"
                confirmButton(text = "我知道了")
                noCancelable()
            }
        /** 推荐使用 LSPosed */
        if (YukiHookAPI.Status.isTaiChiModuleActive)
            showDialog {
                title = "兼容性提示"
                msg = "若你的设备已 Root，推荐使用 LSPosed 激活模块，太极可能会出现模块设置无法保存的问题。"
                confirmButton(text = "我知道了")
            }
        /** 检测应用转生 - 如果模块已激活就不再检测 */
        if (("com.bug.xposed").isInstall && YukiHookAPI.Status.isModuleActive.not())
            showDialog {
                title = "环境异常"
                msg = "检测到“应用转生”已被安装，为了保证模块的安全和稳定，请卸载更换其他 Hook 框架后才能继续使用。"
                confirmButton(text = "退出") { finish() }
                noCancelable()
            }
        /** 设置安装状态 */
        binding.mainTextQqVer.text = if (QQ_PACKAGE_NAME.isInstall) version(QQ_PACKAGE_NAME) else "未安装"
        binding.mainTextTimVer.text = if (TIM_PACKAGE_NAME.isInstall) version(TIM_PACKAGE_NAME) else "未安装"
        binding.mainTextWechatVer.text = if (WECHAT_PACKAGE_NAME.isInstall) version(WECHAT_PACKAGE_NAME) else "未安装"
        /** 设置文本 */
        binding.mainTextVersion.text = "模块版本：$moduleVersion $pendingFlag"
        binding.mainQqItem.setOnClickListener {
            showDialog {
                title = "兼容的 QQ 版本"
                msg = qqSupportVersion
                confirmButton(text = "我知道了")
            }
            /** 振动提醒 */
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        binding.mainTimItem.setOnClickListener {
            showDialog {
                title = "兼容的 TIM 版本"
                msg = timSupportVersion
                confirmButton(text = "我知道了")
            }
            /** 振动提醒 */
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        binding.mainWechatItem.setOnClickListener {
            showDialog {
                title = "兼容的微信版本"
                msg = wechatSupportVersion
                confirmButton(text = "我知道了")
            }
            /** 振动提醒 */
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        /** 获取 Sp 存储的信息 */
        binding.qqtimProtectModeSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_QQTIM_WHITE_MODE)
        binding.qqTimCoreServiceSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_QQTIM_CORESERVICE_BAN)
        binding.qqTimCoreServiceKnSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_QQTIM_CORESERVICE_CHILD_BAN)
        binding.wechatDisableHookSwitch.isChecked = modulePrefs.get(DataConst.DISABLE_WECHAT_HOOK)
        binding.hideIconInLauncherSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_HIDE_ICON)
        binding.notifyModuleInfoSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_RUN_INFO)
        binding.notifyNotifyTipSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_NOTIFY_TIP)
        binding.settingModuleTipSwitch.isChecked = modulePrefs.get(DataConst.ENABLE_SETTING_TIP)
        binding.qqtimProtectModeSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.ENABLE_QQTIM_WHITE_MODE, b)
            snake(msg = "修改需要重启 QQ 以生效")
        }
        binding.qqTimCoreServiceSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.ENABLE_QQTIM_CORESERVICE_BAN, b)
        }
        binding.qqTimCoreServiceKnSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.ENABLE_QQTIM_CORESERVICE_CHILD_BAN, b)
        }
        binding.wechatDisableHookSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.DISABLE_WECHAT_HOOK, b)
            snake(msg = "修改需要重启微信以生效")
        }
        binding.hideIconInLauncherSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.ENABLE_HIDE_ICON, b)
            packageManager.setComponentEnabledSetting(
                ComponentName(this@MainActivity, "com.fankes.tsbattery.Home"),
                if (b) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
        binding.notifyModuleInfoSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.ENABLE_RUN_INFO, b)
        }
        binding.notifyNotifyTipSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.ENABLE_NOTIFY_TIP, b)
        }
        binding.settingModuleTipSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            modulePrefs.put(DataConst.ENABLE_SETTING_TIP, b)
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

    /** 刷新模块激活使用的方式 */
    private fun refreshActivateExecutor() {
        when {
            YukiHookAPI.Status.executorVersion > 0 ->
                binding.mainTextApiWay.text =
                    "Activated by ${YukiHookAPI.Status.executorName} API ${YukiHookAPI.Status.executorVersion}"
            YukiHookAPI.Status.isTaiChiModuleActive -> binding.mainTextApiWay.text = "Activated by TaiChi"
            else -> binding.mainTextApiWay.text = "Activated by anonymous"
        }
    }
}
