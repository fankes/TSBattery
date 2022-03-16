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

package com.fankes.tsbattery.ui

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.isGone
import com.fankes.tsbattery.BuildConfig
import com.fankes.tsbattery.R
import com.fankes.tsbattery.hook.HookConst.DISABLE_WECHAT_HOOK
import com.fankes.tsbattery.hook.HookConst.ENABLE_HIDE_ICON
import com.fankes.tsbattery.hook.HookConst.ENABLE_MODULE_VERSION
import com.fankes.tsbattery.hook.HookConst.ENABLE_QQTIM_CORESERVICE_BAN
import com.fankes.tsbattery.hook.HookConst.ENABLE_QQTIM_CORESERVICE_CHILD_BAN
import com.fankes.tsbattery.hook.HookConst.ENABLE_QQTIM_WHITE_MODE
import com.fankes.tsbattery.hook.HookConst.ENABLE_RUN_INFO
import com.fankes.tsbattery.hook.HookConst.QQ_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookConst.TIM_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookConst.WECHAT_PACKAGE_NAME
import com.fankes.tsbattery.utils.factory.isInstall
import com.fankes.tsbattery.utils.factory.isNotSystemInDarkMode
import com.fankes.tsbattery.utils.factory.openSelfSetting
import com.fankes.tsbattery.utils.factory.showDialog
import com.gyf.immersionbar.ktx.immersionBar
import com.highcapable.yukihookapi.hook.factory.isTaiChiModuleActive
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.highcapable.yukihookapi.hook.xposed.YukiHookModuleStatus

class MainActivity : AppCompatActivity() {

    companion object {

        private const val moduleVersion = BuildConfig.VERSION_NAME
        private const val qqSupportVersion =
            "8.2.11(Play)、8.8.17、8.8.23、8.8.35、8.8.38、8.8.50、8.8.55、8.8.68、8.8.80 (8.2.11、8.5.5~8.8.80)"
        private const val timSupportVersion = "2+、3+ (并未完全测试每个版本)"
        private const val wechatSupportVersion = "全版本仅支持基础省电，更多功能依然画饼"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /** 隐藏系统的标题栏 */
        supportActionBar?.hide()
        /** 初始化沉浸状态栏 */
        immersionBar {
            statusBarColor(R.color.colorThemeBackground)
            autoDarkModeEnable(true)
            statusBarDarkFont(isNotSystemInDarkMode)
            navigationBarColor(R.color.colorThemeBackground)
            navigationBarDarkIcon(isNotSystemInDarkMode)
            fitsSystemWindows(true)
        }
        /** 判断 Hook 状态 */
        if (isHooked()) {
            findViewById<LinearLayout>(R.id.main_lin_status).setBackgroundResource(R.drawable.bg_green_round)
            findViewById<ImageFilterView>(R.id.main_img_status).setImageResource(R.mipmap.ic_success)
            findViewById<TextView>(R.id.main_text_status).text = "模块已激活"
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
        findViewById<View>(R.id.main_text_qq_noinstall).isGone = QQ_PACKAGE_NAME.isInstall
        findViewById<View>(R.id.main_text_tim_noinstall).isGone = TIM_PACKAGE_NAME.isInstall
        findViewById<View>(R.id.main_text_wechat_noinstall).isGone = WECHAT_PACKAGE_NAME.isInstall
        /** 设置文本 */
        findViewById<TextView>(R.id.main_text_version).text = "模块版本：$moduleVersion"
        findViewById<TextView>(R.id.main_text_support_qq).apply {
            text = qqSupportVersion
            setOnClickListener {
                showDialog {
                    title = "兼容的 QQ 版本"
                    msg = qqSupportVersion
                    confirmButton(text = "我知道了")
                }
            }
        }
        findViewById<TextView>(R.id.main_text_support_tim).apply {
            text = timSupportVersion
            setOnClickListener {
                showDialog {
                    title = "兼容的 TIM 版本"
                    msg = timSupportVersion
                    confirmButton(text = "我知道了")
                }
            }
        }
        findViewById<TextView>(R.id.main_text_support_wechat).apply {
            text = wechatSupportVersion
            setOnClickListener {
                showDialog {
                    title = "兼容的微信版本"
                    msg = wechatSupportVersion
                    confirmButton(text = "我知道了")
                }
            }
        }
        /** 初始化 View */
        val qqTimProtectModeSwitch = findViewById<SwitchCompat>(R.id.qqtim_protect_mode_switch)
        val qqTimCoreServiceSwitch = findViewById<SwitchCompat>(R.id.shut_core_sv_qqtim_switch)
        val qqTimCoreServiceKnSwitch = findViewById<SwitchCompat>(R.id.shut_core_sv_kn_qqtim_switch)
        val wechatDisableHookSwitch = findViewById<SwitchCompat>(R.id.disable_wechat_sv_switch)
        val hideIconInLauncherSwitch = findViewById<SwitchCompat>(R.id.hide_icon_in_launcher_switch)
        val notifyModuleInfoSwitch = findViewById<SwitchCompat>(R.id.notify_module_info_switch)
        /** 获取 Sp 存储的信息 */
        qqTimProtectModeSwitch.isChecked = modulePrefs.getBoolean(ENABLE_QQTIM_WHITE_MODE)
        qqTimCoreServiceSwitch.isChecked = modulePrefs.getBoolean(ENABLE_QQTIM_CORESERVICE_BAN)
        qqTimCoreServiceKnSwitch.isChecked = modulePrefs.getBoolean(ENABLE_QQTIM_CORESERVICE_CHILD_BAN)
        wechatDisableHookSwitch.isChecked = modulePrefs.getBoolean(DISABLE_WECHAT_HOOK)
        hideIconInLauncherSwitch.isChecked = modulePrefs.getBoolean(ENABLE_HIDE_ICON)
        notifyModuleInfoSwitch.isChecked = modulePrefs.getBoolean(ENABLE_RUN_INFO)
        qqTimProtectModeSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(ENABLE_QQTIM_WHITE_MODE, b)
        }
        qqTimCoreServiceSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(ENABLE_QQTIM_CORESERVICE_BAN, b)
        }
        qqTimCoreServiceKnSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(ENABLE_QQTIM_CORESERVICE_CHILD_BAN, b)
        }
        wechatDisableHookSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(DISABLE_WECHAT_HOOK, b)
        }
        hideIconInLauncherSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(ENABLE_HIDE_ICON, b)
            packageManager.setComponentEnabledSetting(
                ComponentName(this@MainActivity, "com.fankes.tsbattery.Home"),
                if (b) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
        notifyModuleInfoSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            modulePrefs.putBoolean(ENABLE_RUN_INFO, b)
        }
        /** 快捷操作 QQ */
        findViewById<View>(R.id.quick_qq_button).setOnClickListener { openSelfSetting(QQ_PACKAGE_NAME) }
        /** 快捷操作 TIM */
        findViewById<View>(R.id.quick_tim_button).setOnClickListener { openSelfSetting(TIM_PACKAGE_NAME) }
        /** 快捷操作微信 */
        findViewById<View>(R.id.quick_wechat_button).setOnClickListener { openSelfSetting(WECHAT_PACKAGE_NAME) }
        /** 恰饭！ */
        findViewById<View>(R.id.link_with_follow_me).setOnClickListener {
            runCatching {
                startActivity(Intent().apply {
                    setPackage("com.coolapk.market")
                    action = "android.intent.action.VIEW"
                    data = Uri.parse("https://www.coolapk.com/u/876977")
                    /** 防止顶栈一样重叠在自己的 APP 中 */
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.onFailure {
                Toast.makeText(this, "你可能没有安装酷安", Toast.LENGTH_SHORT).show()
            }
        }
        /** 项目地址按钮点击事件 */
        findViewById<View>(R.id.title_github_icon).setOnClickListener {
            runCatching {
                startActivity(Intent().apply {
                    action = "android.intent.action.VIEW"
                    data = Uri.parse("https://github.com/fankes/TSBattery")
                    /** 防止顶栈一样重叠在自己的 APP 中 */
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.onFailure {
                Toast.makeText(this, "无法启动系统默认浏览器", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 判断模块是否激活
     * @return [Boolean] 激活状态
     */
    private fun isHooked() = YukiHookModuleStatus.isActive() || isTaiChiModuleActive
}