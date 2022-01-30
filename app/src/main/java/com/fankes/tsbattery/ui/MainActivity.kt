/**
 * Copyright (C) 2021. Fankes Studio(qzmmcn@163.com)
 *
 * This file is part of TSBattery.
 *
 * TSBattery is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TSBattery is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This file is Created by fankes on 2021/9/4.
 */
@file:Suppress(
    "DEPRECATION", "SetTextI18n", "SetWorldReadable", "WorldReadableFiles",
    "LocalVariableName", "SameParameterValue"
)

package com.fankes.tsbattery.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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
import com.fankes.tsbattery.hook.HookMedium
import com.fankes.tsbattery.hook.HookMedium.QQ_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookMedium.TIM_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookMedium.WECHAT_PACKAGE_NAME
import com.fankes.tsbattery.utils.*
import com.gyf.immersionbar.ktx.immersionBar
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {

        private const val moduleVersion = BuildConfig.VERSION_NAME
        private const val qqSupportVersion =
            "8.2.11(Play)、8.8.17、8.8.23、8.8.35、8.8.38、8.8.50、8.8.55、8.8.68 (8.2.11、8.5.5~8.8.68)"
        private const val timSupportVersion = "2+、3+ (并未完全测试每个版本)"
        private const val wechatSupportVersion = "全版本仅支持基础省电，更多功能依然画饼"

        /** 声明当前实例 */
        var instance: MainActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** 设置自身实例 */
        instance = this
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
            findViewById<LinearLayout>(R.id.main_lin_status).setBackgroundResource(R.drawable.green_round)
            findViewById<ImageFilterView>(R.id.main_img_status).setImageResource(R.mipmap.succcess)
            findViewById<TextView>(R.id.main_text_status).text = "模块已激活"
            /** 写入激活的模块版本 */
            putString(HookMedium.ENABLE_MODULE_VERSION, moduleVersion)
        } else
            showDialog {
                title = "模块没有激活"
                msg = "检测到模块没有激活，模块需要 Xposed 环境依赖，" +
                        "同时需要系统拥有 Root 权限(太极阴可以免 Root)，" +
                        "请自行查看本页面使用帮助与说明第三条。\n" +
                        "太极、应用转生、梦境(Pine)和第三方 Xposed 激活后" +
                        "可能不会提示激活，若想验证是否激活请打开“提示模块运行信息”自行检查，" +
                        "如果生效就代表模块运行正常，这里的激活状态只是一个显示意义上的存在。\n" +
                        "太极(无极)在 MIUI 设备上会提示打开授权，请进行允许，然后再次打开本应用查看激活状态。"
                confirmButton(text = "我知道了")
                noCancelable()
            }
        /** 设置安装状态 */
        findViewById<View>(R.id.main_text_qq_noinstall).isGone = QQ_PACKAGE_NAME.isInstall
        findViewById<View>(R.id.main_text_tim_noinstall).isGone = TIM_PACKAGE_NAME.isInstall
        findViewById<View>(R.id.main_text_wechat_noinstall).isGone = WECHAT_PACKAGE_NAME.isInstall
        /** 设置文本 */
        findViewById<TextView>(R.id.main_text_version).text = "当前版本：$moduleVersion"
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
        qqTimProtectModeSwitch.isChecked = getBoolean(HookMedium.ENABLE_QQTIM_WHITE_MODE)
        qqTimCoreServiceSwitch.isChecked = getBoolean(HookMedium.ENABLE_QQTIM_CORESERVICE_BAN)
        qqTimCoreServiceKnSwitch.isChecked = getBoolean(HookMedium.ENABLE_QQTIM_CORESERVICE_CHILD_BAN)
        wechatDisableHookSwitch.isChecked = getBoolean(HookMedium.DISABLE_WECHAT_HOOK)
        hideIconInLauncherSwitch.isChecked = getBoolean(HookMedium.ENABLE_HIDE_ICON)
        notifyModuleInfoSwitch.isChecked = getBoolean(HookMedium.ENABLE_RUN_INFO)
        qqTimProtectModeSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            putBoolean(HookMedium.ENABLE_QQTIM_WHITE_MODE, b)
        }
        qqTimCoreServiceSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            putBoolean(HookMedium.ENABLE_QQTIM_CORESERVICE_BAN, b)
        }
        qqTimCoreServiceKnSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            putBoolean(HookMedium.ENABLE_QQTIM_CORESERVICE_CHILD_BAN, b)
        }
        wechatDisableHookSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            putBoolean(HookMedium.DISABLE_WECHAT_HOOK, b)
        }
        hideIconInLauncherSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            putBoolean(HookMedium.ENABLE_HIDE_ICON, b)
            packageManager.setComponentEnabledSetting(
                ComponentName(this@MainActivity, "com.fankes.tsbattery.Home"),
                if (b) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
        notifyModuleInfoSwitch.setOnCheckedChangeListener { btn, b ->
            if (!btn.isPressed) return@setOnCheckedChangeListener
            putBoolean(HookMedium.ENABLE_RUN_INFO, b)
        }
        /** 快捷操作 QQ */
        findViewById<View>(R.id.quick_qq_button).setOnClickListener { openSelfSetting(QQ_PACKAGE_NAME) }
        /** 快捷操作 TIM */
        findViewById<View>(R.id.quick_tim_button).setOnClickListener { openSelfSetting(TIM_PACKAGE_NAME) }
        /** 快捷操作微信 */
        findViewById<View>(R.id.quick_wechat_button).setOnClickListener { openSelfSetting(WECHAT_PACKAGE_NAME) }
        /** 恰饭！ */
        findViewById<View>(R.id.link_with_follow_me).setOnClickListener {
            try {
                startActivity(Intent().apply {
                    setPackage("com.coolapk.market")
                    action = "android.intent.action.VIEW"
                    data = Uri.parse("https://www.coolapk.com/u/876977")
                    /** 防止顶栈一样重叠在自己的 APP 中 */
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            } catch (e: Exception) {
                Toast.makeText(this, "你可能没有安装酷安", Toast.LENGTH_SHORT).show()
            }
        }
        /** 项目地址点击事件 */
        findViewById<View>(R.id.link_with_project_address).setOnClickListener {
            try {
                startActivity(Intent().apply {
                    action = "android.intent.action.VIEW"
                    data = Uri.parse("https://github.com/fankes/TSBattery")
                    /** 防止顶栈一样重叠在自己的 APP 中 */
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            } catch (e: Exception) {
                Toast.makeText(this, "无法启动系统默认浏览器", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 判断模块是否激活
     * @return [Boolean] 激活状态
     */
    private fun isHooked() = HookMedium.isHooked()

    override fun onResume() {
        super.onResume()
        setWorldReadable()
    }

    override fun onRestart() {
        super.onRestart()
        setWorldReadable()
    }

    override fun onPause() {
        super.onPause()
        setWorldReadable()
    }

    /**
     * 获取保存的值
     * @param key 名称
     * @return [Boolean] 保存的值
     */
    private fun getBoolean(key: String) =
        getSharedPreferences(
            packageName + "_preferences",
            Context.MODE_PRIVATE
        ).getBoolean(key, false)

    /**
     * 保存值
     * @param key 名称
     * @param bool 值
     */
    private fun putBoolean(key: String, bool: Boolean) {
        getSharedPreferences(
            packageName + "_preferences",
            Context.MODE_PRIVATE
        ).edit().putBoolean(key, bool).apply()
        setWorldReadable()
        /** 延迟继续设置强制允许 SP 可读可写 */
        Handler().postDelayed({ setWorldReadable() }, 500)
        Handler().postDelayed({ setWorldReadable() }, 1000)
        Handler().postDelayed({ setWorldReadable() }, 1500)
    }

    /**
     * 保存值
     * @param key 名称
     * @param value 值
     */
    private fun putString(key: String, value: String) {
        getSharedPreferences(
            packageName + "_preferences",
            Context.MODE_PRIVATE
        ).edit().putString(key, value).apply()
        setWorldReadable()
        /** 延迟继续设置强制允许 SP 可读可写 */
        Handler().postDelayed({ setWorldReadable() }, 500)
        Handler().postDelayed({ setWorldReadable() }, 1000)
        Handler().postDelayed({ setWorldReadable() }, 1500)
    }

    /**
     * 强制设置 Sp 存储为全局可读可写
     * 以供模块使用
     */
    private fun setWorldReadable() {
        try {
            if (FileUtils.getDefaultPrefFile(this).exists()) {
                for (file in arrayOf<File>(
                    FileUtils.getDataDir(this),
                    FileUtils.getPrefDir(this),
                    FileUtils.getDefaultPrefFile(this)
                )) {
                    file.setReadable(true, false)
                    file.setExecutable(true, false)
                }
            }
        } catch (_: Exception) {
            Toast.makeText(this, "无法写入模块设置，请检查权限\n如果此提示一直显示，请不要双开模块", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        setWorldReadable()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        /** 销毁实例防止内存泄漏 */
        instance = null
    }
}