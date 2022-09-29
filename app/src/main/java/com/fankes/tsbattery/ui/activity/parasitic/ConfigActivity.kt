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
 * This file is Created by fankes on 2022/9/28.
 */
@file:Suppress("SetTextI18n")

package com.fankes.tsbattery.ui.activity.parasitic

import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.fankes.tsbattery.BuildConfig
import com.fankes.tsbattery.const.PackageName
import com.fankes.tsbattery.data.ConfigData
import com.fankes.tsbattery.data.ConfigData.bind
import com.fankes.tsbattery.databinding.ActivityConfigBinding
import com.fankes.tsbattery.hook.HookEntry
import com.fankes.tsbattery.ui.activity.base.BaseActivity
import com.fankes.tsbattery.utils.factory.*
import com.fankes.tsbattery.utils.tool.GithubReleaseTool
import kotlin.system.exitProcess

class ConfigActivity : BaseActivity<ActivityConfigBinding>() {

    override fun onCreate() {
        /** 检查更新 */
        GithubReleaseTool.checkingForUpdate(context = this, BuildConfig.VERSION_NAME) { version, function ->
            binding.updateVersionText.apply {
                text = "点击更新 $version"
                isVisible = true
                setOnClickListener { function() }
            }
        }
        binding.titleBackIcon.setOnClickListener { finish() }
        binding.titleNameText.text = "TSBattery 设置 (${appName.trim()})"
        binding.appIcon.setImageDrawable(findAppIcon())
        binding.appName.text = appName.trim()
        binding.appVersion.text = "${versionName}($versionCode)"
        binding.moduleVersion.text = "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
        binding.activeModeIcon.isVisible = HookEntry.isHookClientSupport
        binding.inactiveModeIcon.isGone = HookEntry.isHookClientSupport
        binding.unsupportItem.isGone = HookEntry.isHookClientSupport
        /** 刷新当前模式文本 */
        fun refreshCurrentModeText() {
            binding.currentModeText.text = when {
                ConfigData.isDisableAllHook -> "模块已停用"
                packageName == PackageName.WECHAT -> "仅限基础省电模式"
                ConfigData.isEnableQQTimProtectMode -> "已启用保守模式"
                else -> "已启用完全模式"
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
        binding.disableAllHookSwitch.bind(ConfigData.DISABLE_ALL_HOOK) { refreshConfigItems(); refreshCurrentModeText(); showRestartDialog() }
        binding.qqTimProtectModeSwitch.bind(ConfigData.ENABLE_QQ_TIM_PROTECT_MODE) { refreshCurrentModeText(); showRestartDialog() }
        binding.qqTimCoreServiceSwitch.bind(ConfigData.ENABLE_KILL_QQ_TIM_CORESERVICE) { showRestartDialog() }
        binding.qqTimCoreServiceChildSwitch.bind(ConfigData.ENABLE_KILLE_QQ_TIM_CORESERVICE_CHILD) { showRestartDialog() }
    }

    /** 显示重新启动对话框 */
    private fun showRestartDialog() {
        showDialog {
            title = "需要重新启动"
            msg = "你必须重新启动${appName}才能使当前更改生效，现在重新启动吗？"
            confirmButton {
                cancel()
                finish()
                exitProcess(status = 0)
            }
            cancelButton(text = "稍后再说")
        }
    }

    /** 替换占位符到当前 APP 名称 */
    private fun TextView.replaceToAppName() {
        text = text.toString().replace(oldValue = "{APP_NAME}", appName)
    }

    /**
     * 获取当前 APP 名称
     * @return [String]
     */
    private val appName by lazy { findAppName().let { if (packageName == PackageName.WECHAT) it else " $it " } }
}