/*
 * TSBattery - A new way to save your battery avoid cancer apps hacker it.
 * Copyright (C) 2017-2023 Fankes Studio(qzmmcn@163.com)
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
 * This file is created by fankes on 2022/9/28.
 */
@file:Suppress("SetTextI18n", "DEPRECATION")

package com.fankes.tsbattery.ui.activity.parasitic

import android.content.ComponentName
import android.content.Intent
import android.content.res.Resources
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.fankes.projectpromote.ProjectPromote
import com.fankes.tsbattery.const.ModuleVersion
import com.fankes.tsbattery.const.PackageName
import com.fankes.tsbattery.data.ConfigData
import com.fankes.tsbattery.data.ConfigData.bind
import com.fankes.tsbattery.databinding.ActivityConfigBinding
import com.fankes.tsbattery.hook.HookEntry
import com.fankes.tsbattery.hook.entity.QQTIMHooker
import com.fankes.tsbattery.ui.activity.MainActivity
import com.fankes.tsbattery.ui.activity.base.BaseActivity
import com.fankes.tsbattery.utils.factory.appIconOf
import com.fankes.tsbattery.utils.factory.appNameOf
import com.fankes.tsbattery.utils.factory.appVersionCode
import com.fankes.tsbattery.utils.factory.appVersionName
import com.fankes.tsbattery.utils.factory.showDialog
import com.fankes.tsbattery.utils.factory.snake
import com.fankes.tsbattery.utils.tool.GithubReleaseTool
import com.fankes.tsbattery.wrapper.BuildConfigWrapper
import com.highcapable.yukihookapi.YukiHookAPI
import kotlin.system.exitProcess

class ConfigActivity : BaseActivity<ActivityConfigBinding>() {

    override fun onCreate() {
        /** 检查更新 */
        GithubReleaseTool.checkingForUpdate(context = this, ModuleVersion.NAME) { version, function ->
            binding.updateVersionText.apply {
                text = "点击更新 $version"
                isVisible = true
                setOnClickListener { function() }
            }
        }
        binding.titleBackIcon.setOnClickListener { finish() }
        binding.titleModuleIcon.setOnClickListener {
            showDialog {
                title = "打开模块主界面"
                msg = "点击确定后将打开模块主界面，如果未安装模块本体将尝试打开寄生界面。"
                confirmButton {
                    runCatching {
                        startActivity(Intent().apply {
                            component = ComponentName(BuildConfigWrapper.APPLICATION_ID, MainActivity::class.java.name)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.onFailure {
                        runCatching {
                            startActivity(Intent(this@ConfigActivity, MainActivity::class.java))
                        }.onFailure { snake(msg = "打开失败，请确认你已安装模块 APP 或在模块更新后重启过$appName\n$it") }
                    }
                }
                cancelButton()
            }
        }
        binding.titleNameText.text = "TSBattery 设置 (${appName.trim()})"
        binding.appIcon.setImageDrawable(appIconOf())
        binding.appName.text = appName.trim()
        binding.appVersion.text = "$appVersionName($appVersionCode)"
        binding.moduleVersion.text = ModuleVersion.toString()
        binding.activeModeIcon.isVisible = HookEntry.isHookClientSupport
        binding.inactiveModeIcon.isGone = HookEntry.isHookClientSupport
        binding.unsupportItem.isGone = HookEntry.isHookClientSupport
        binding.executorInfoText.text = "${YukiHookAPI.Status.Executor.name} API ${YukiHookAPI.Status.Executor.apiLevel}"
        binding.needRestartTipText.replaceToAppName()
        binding.needRestartTipText.setOnClickListener {
            showDialog {
                title = "需要重新启动"
                msg = "你必须重新启动${appName}才能使当前更改生效，现在重新启动吗？"
                confirmButton {
                    cancel()
                    finish()
                    exitProcess(status = 0)
                }
                cancelButton(text = "稍后再说") {
                    cancel()
                    it.isVisible = false
                }
            }
        }
        /** 刷新当前模式文本 */
        fun refreshCurrentModeText() {
            binding.currentModeText.text = when {
                ConfigData.isDisableAllHook -> "模块已停用"
                packageName == PackageName.WECHAT -> "基础省电模式"
                ConfigData.isEnableQQTimProtectMode -> "保守模式"
                else -> "完全模式"
            }
        }
        refreshCurrentModeText()
        /** 刷新配置条目显示隐藏状态 */
        fun refreshConfigItems() {
            binding.itemQqTimConfig.isVisible = packageName != PackageName.WECHAT && ConfigData.isDisableAllHook.not()
        }
        refreshConfigItems()
        binding.infoTipText.replaceToAppName()
        binding.qqTimProtectTipText.replaceToAppName()
        binding.disableAllHookSwitch.bind(ConfigData.DISABLE_ALL_HOOK) { refreshConfigItems(); refreshCurrentModeText(); showNeedRestartTip() }
        binding.qqTimProtectModeSwitch.bind(ConfigData.ENABLE_QQ_TIM_PROTECT_MODE) { refreshCurrentModeText(); showNeedRestartTip() }
        binding.qqTimCoreServiceSwitch.bind(ConfigData.ENABLE_KILL_QQ_TIM_CORESERVICE) { showNeedRestartTip() }
        binding.qqTimCoreServiceChildSwitch.bind(ConfigData.ENABLE_KILLE_QQ_TIM_CORESERVICE_CHILD) { showNeedRestartTip() }
        /** 推广、恰饭 */
        ProjectPromote.show(activity = this, ModuleVersion.toString())
    }

    /** 显示需要重新启动提示 */
    private fun showNeedRestartTip() {
        binding.needRestartTipText.isVisible = true
    }

    /** 替换占位符到当前 APP 名称 */
    private fun TextView.replaceToAppName() {
        text = text.toString().replace("{APP_NAME}", appName)
    }

    /** 重新设置 DPI 防止 QQ、TIM 修改它 */
    override fun getResources(): Resources? = super.getResources().apply {
        if (packageName == PackageName.QQ || packageName == PackageName.TIM)
            QQTIMHooker.baseConfiguration?.also {
                updateConfiguration(configuration.apply {
                    fontScale = it.fontScale
                    densityDpi = it.densityDpi
                }, displayMetrics)
            }
    }

    /**
     * 获取当前 APP 名称
     * @return [String]
     */
    private val appName by lazy { appNameOf().let { if (packageName == PackageName.WECHAT) it else " $it " } }
}