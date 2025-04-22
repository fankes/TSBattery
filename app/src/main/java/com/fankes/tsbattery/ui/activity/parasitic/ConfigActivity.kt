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
 * This file is created by fankes on 2022/9/28.
 */
@file:Suppress("SetTextI18n", "DEPRECATION")

package com.fankes.tsbattery.ui.activity.parasitic

import android.animation.LayoutTransition
import android.content.res.Resources
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.core.view.updateMargins
import androidx.core.view.updateMarginsRelative
import androidx.core.view.updatePadding
import com.fankes.projectpromote.ProjectPromote
import com.fankes.tsbattery.R
import com.fankes.tsbattery.const.ModuleVersion
import com.fankes.tsbattery.const.PackageName
import com.fankes.tsbattery.data.ConfigData
import com.fankes.tsbattery.data.ConfigData.bind
import com.fankes.tsbattery.hook.HookEntry
import com.fankes.tsbattery.hook.entity.QQTIMHooker
import com.fankes.tsbattery.ui.activity.MainActivity
import com.fankes.tsbattery.ui.activity.base.BaseActivity2
import com.fankes.tsbattery.utils.factory.appIconOf
import com.fankes.tsbattery.utils.factory.appNameOf
import com.fankes.tsbattery.utils.factory.appVersionCode
import com.fankes.tsbattery.utils.factory.appVersionName
import com.fankes.tsbattery.utils.factory.showDialog
import com.fankes.tsbattery.utils.factory.snake
import com.fankes.tsbattery.utils.tool.GithubReleaseTool
import com.fankes.tsbattery.wrapper.BuildConfigWrapper
import com.highcapable.betterandroid.ui.extension.component.base.getThemeAttrsDrawable
import com.highcapable.betterandroid.ui.extension.component.startActivity
import com.highcapable.betterandroid.ui.extension.graphics.toNormalColorStateList
import com.highcapable.betterandroid.ui.extension.view.textColor
import com.highcapable.betterandroid.ui.extension.view.tooltipTextCompat
import com.highcapable.betterandroid.ui.extension.view.updateMargins
import com.highcapable.betterandroid.ui.extension.view.updatePadding
import com.highcapable.betterandroid.ui.extension.view.updateTypeface
import com.highcapable.hikage.extension.setContentView
import com.highcapable.hikage.widget.android.widget.ImageView
import com.highcapable.hikage.widget.android.widget.LinearLayout
import com.highcapable.hikage.widget.android.widget.TextView
import com.highcapable.hikage.widget.androidx.cardview.widget.CardView
import com.highcapable.hikage.widget.androidx.core.widget.NestedScrollView
import com.highcapable.hikage.widget.com.fankes.tsbattery.ui.widget.MaterialSwitch
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.classOf
import kotlin.system.exitProcess
import android.R as Android_R

class ConfigActivity : BaseActivity2() {

    private lateinit var updateVersionText: TextView
    private lateinit var needRestartTipText: TextView
    private lateinit var currentModeText: TextView
    private lateinit var itemQQTimConfig: LinearLayout

    override fun onCreate() {
        setContentView {
            LinearLayout(
                lparams = LayoutParams(matchParent = true),
                init = {
                    orientation = LinearLayout.VERTICAL
                    setBackgroundResource(R.color.colorThemeBackground)
                }
            ) {
                LinearLayout(
                    lparams = LayoutParams(widthMatchParent = true),
                    init = {
                        gravity = Gravity.CENTER or Gravity.START
                        setPadding(15.dp)
                    }
                ) {
                    ImageView(
                        lparams = LayoutParams(20.dp, 20.dp) {
                            marginStart = 10.dp
                            marginEnd = 20.dp
                        }
                    ) {
                        imageTintList = stateColorResource(R.color.colorTextGray)
                        tooltipTextCompat = "返回"
                        setImageResource(R.drawable.ic_back)
                        background = getThemeAttrsDrawable(Android_R.attr.selectableItemBackgroundBorderless)
                        setOnClickListener { finish() }
                    }
                    TextView(
                        lparams = LayoutParams(width = 0) {
                            marginEnd = 2.dp
                            weight = 1f
                        }
                    ) {
                        isSingleLine = true
                        textSize = 19f
                        textColor = colorResource(R.color.colorTextGray)
                        text = "TSBattery 设置 (${appName.trim()})"
                        updateTypeface(Typeface.BOLD)
                    }
                    ImageView(
                        lparams = LayoutParams(23.dp, 23.dp) {
                            marginEnd = 10.dp
                        }
                    ) {
                        setPadding(1.dp)
                        imageTintList = stateColorResource(R.color.colorTextGray)
                        tooltipTextCompat = "打开模块主界面"
                        setImageResource(R.drawable.ic_icon)
                        background = getThemeAttrsDrawable(Android_R.attr.selectableItemBackgroundBorderless)
                        setOnClickListener {
                            showDialog {
                                title = "打开模块主界面"
                                msg = "点击确定后将打开模块主界面，如果未安装模块本体将尝试打开寄生界面。"
                                confirmButton {
                                    runCatching {
                                        startActivity(
                                            packageName = BuildConfigWrapper.APPLICATION_ID,
                                            activityClass = classOf<MainActivity>().name
                                        )
                                    }.onFailure {
                                        runCatching {
                                            startActivity<MainActivity>()
                                        }.onFailure { snake(msg = "打开失败，请确认你已安装模块 APP 或在模块更新后重启过$appName\n$it") }
                                    }
                                }
                                cancelButton()
                            }
                        }
                    }
                }
                NestedScrollView(
                    lparams = LayoutParams(matchParent = true),
                    init = {
                        setFadingEdgeLength(10.dp)
                        isVerticalFadingEdgeEnabled = true
                        isVerticalScrollBarEnabled = false
                        isHorizontalScrollBarEnabled = false
                    }
                ) {
                    LinearLayout(
                        lparams = LayoutParams(widthMatchParent = true),
                        init = {
                            orientation = LinearLayout.VERTICAL
                            layoutTransition = LayoutTransition()
                            updatePadding(bottom = 15.dp)
                        }
                    ) {
                        repeat(2) { index ->
                            TextView(
                                lparams = LayoutParams(widthMatchParent = true) {
                                    updateMargins(horizontal = 15.dp)
                                    updateMargins(bottom = 5.dp)
                                }
                            ) {
                                setBackgroundResource(R.drawable.bg_orange_round)
                                setPadding(5.dp)
                                gravity = Gravity.CENTER
                                textSize = 13f
                                textColor = colorResource(R.color.white)
                                text = when (index) {
                                    0 -> "点击更新 %1"
                                    else -> "新的设置需要重新启动${appName}才能生效"
                                }
                                isVisible = false
                                when (index) {
                                    0 -> updateVersionText = this
                                    else -> {
                                        needRestartTipText = this
                                        setOnClickListener {
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
                                    }
                                }
                            }
                        }
                        LinearLayout(
                            lparams = LayoutParams(widthMatchParent = true) {
                                updateMargins(horizontal = 15.dp)
                                updateMargins(top = 5.dp)
                            },
                            init = {
                                setBackgroundResource(R.drawable.bg_permotion_round)
                                setPadding(15.dp)
                                gravity = Gravity.CENTER
                            }
                        ) {
                            ImageView(
                                lparams = LayoutParams(45.dp, 45.dp) {
                                    marginEnd = 15.dp
                                }
                            ) {
                                setImageDrawable(appIconOf())
                            }
                            LinearLayout(
                                lparams = LayoutParams(widthMatchParent = true),
                                init = {
                                    orientation = LinearLayout.VERTICAL
                                }
                            ) {
                                LinearLayout(
                                    lparams = LayoutParams(widthMatchParent = true) {
                                        bottomMargin = 5.dp
                                        gravity = Gravity.CENTER or Gravity.START
                                    }
                                ) {
                                    TextView(
                                        lparams = LayoutParams {
                                            marginEnd = 7.dp
                                        }
                                    ) {
                                        isSingleLine = true
                                        textColor = colorResource(R.color.colorTextGray)
                                        textSize = 15f
                                        text = appName.trim()
                                    }
                                    TextView {
                                        isSingleLine = true
                                        textColor = colorResource(R.color.colorTextGray)
                                        textSize = 12f
                                        text = "$appVersionName($appVersionCode)"
                                        alpha = 0.85f
                                    }
                                    TextView(
                                        lparams = LayoutParams {
                                            updateMargins(horizontal = 6.dp)
                                        }
                                    ) {
                                        isSingleLine = true
                                        textColor = colorResource(R.color.colorTextGray)
                                        textSize = 12f
                                        text = "|"
                                        alpha = 0.85f
                                    }
                                    TextView {
                                        isSingleLine = true
                                        textColor = colorResource(R.color.colorTextGray)
                                        textSize = 12f
                                        text = "模块版本：$ModuleVersion"
                                        alpha = 0.85f
                                    }
                                }
                                LinearLayout(
                                    lparams = LayoutParams(widthMatchParent = true),
                                    init = {
                                        gravity = Gravity.CENTER or Gravity.START
                                    }
                                ) {
                                    CardView(
                                        lparams = LayoutParams(13.dp, 13.dp) {
                                            marginEnd = 6.dp
                                        },
                                        init = {
                                            setCardBackgroundColor(colorResource(R.color.colorThemeBackground))
                                            radius = 50f.dp
                                            elevation = 0f
                                        }
                                    ) {
                                        ImageView(
                                            lparams = LayoutParams(13.dp, 13.dp)
                                        ) {
                                            setImageResource(if (HookEntry.isHookClientSupport)
                                                R.drawable.ic_success
                                            else R.drawable.ic_error)
                                            imageTintList = (if (HookEntry.isHookClientSupport)
                                                0xFF26A69A
                                            else 0xFFFF7043).toInt().toNormalColorStateList()
                                        }
                                    }
                                    if (!HookEntry.isHookClientSupport)
                                        LinearLayout(
                                            lparams = LayoutParams {
                                                marginEnd = 6.dp
                                            },
                                            init = {
                                                gravity = Gravity.CENTER or Gravity.START
                                            }
                                        ) {
                                            TextView {
                                                isSingleLine = true
                                                textColor = colorResource(R.color.colorTextGray)
                                                textSize = 12f
                                                alpha = 0.85f
                                                text = "未适配"
                                            }
                                            TextView(
                                                lparams = LayoutParams {
                                                    updateMarginsRelative(start = 6.dp)
                                                }
                                            ) {
                                                isSingleLine = true
                                                textColor = colorResource(R.color.colorTextGray)
                                                textSize = 12f
                                                text = "|"
                                                alpha = 0.85f
                                            }
                                        }
                                    TextView {
                                        isSingleLine = true
                                        textColor = colorResource(R.color.colorTextGray)
                                        textSize = 12f
                                        alpha = 0.85f
                                        currentModeText = this
                                    }
                                    TextView(
                                        lparams = LayoutParams {
                                            updateMarginsRelative(start = 6.dp)
                                        }
                                    ) {
                                        isSingleLine = true
                                        textColor = colorResource(R.color.colorTextGray)
                                        textSize = 12f
                                        text = "|"
                                        alpha = 0.85f
                                    }
                                    TextView(
                                        lparams = LayoutParams {
                                            updateMarginsRelative(start = 6.dp)
                                        }
                                    ) {
                                        isSingleLine = true
                                        textColor = colorResource(R.color.colorTextGray)
                                        textSize = 12f
                                        text = "${YukiHookAPI.Status.Executor.name} API ${YukiHookAPI.Status.Executor.apiLevel}"
                                        alpha = 0.85f
                                    }
                                }
                            }
                        }
                        TextView(
                            lparams = LayoutParams(widthMatchParent = true) {
                                updateMargins(horizontal = 15.dp)
                                updateMargins(top = 15.dp)
                            }
                        ) {
                            setBackgroundResource(R.drawable.bg_permotion_round)
                            setPadding(15.dp)
                            textSize = 12f
                            textColor = colorResource(R.color.colorTextDark)
                            setLineSpacing(10f.dp, 1f)
                            text = "模块只对挂后台锁屏情况下有省电效果，请不要将过多的群提醒，消息通知打开，这样子在使用过程时照样会极其耗电。\n" +
                                "持续常驻使用${appName}依然会耗电，任何软件都是如此，模块是无法帮你做到前台不耗电的。"
                        }
                        LinearLayout(
                            lparams = LayoutParams(widthMatchParent = true) {
                                updateMargins(horizontal = 15.dp)
                                updateMargins(top = 15.dp)
                            },
                            init = {
                                orientation = LinearLayout.VERTICAL
                            }
                        ) {
                            LinearLayout(
                                lparams = LayoutParams(widthMatchParent = true),
                                init = {
                                    orientation = LinearLayout.VERTICAL
                                    gravity = Gravity.CENTER
                                    setBackgroundResource(R.drawable.bg_permotion_round)
                                    updatePadding(horizontal = 15.dp)
                                    updatePadding(top = 10.dp, bottom = 15.dp)
                                }
                            ) {
                                MaterialSwitch(
                                    lparams = LayoutParams(widthMatchParent = true, height = 35.dp) {
                                        bottomMargin = 5.dp
                                    }
                                ) {
                                    text = "停用省电策略"
                                    textColor = colorResource(R.color.colorTextGray)
                                    textSize = 15f
                                    bind(ConfigData.DISABLE_ALL_HOOK) {
                                        refreshConfigItems()
                                        refreshCurrentModeText()
                                        showNeedRestartTip()
                                    }
                                }
                                TextView(
                                    lparams = LayoutParams(widthMatchParent = true)
                                ) {
                                    isSingleLine = true
                                    textColor = colorResource(R.color.colorTextGray)
                                    textSize = 12f
                                    alpha = 0.6f
                                    setLineSpacing(6f.dp, 1f)
                                    text = "选择停用后模块将关闭所有省电功能，模块停止使用。"
                                }
                            }
                        }
                        LinearLayout(
                            lparams = LayoutParams(widthMatchParent = true) {
                                updateMargins(horizontal = 15.dp)
                                updateMargins(top = 15.dp)
                            },
                            init = {
                                orientation = LinearLayout.VERTICAL
                                itemQQTimConfig = this
                            }
                        ) {
                            LinearLayout(
                                lparams = LayoutParams(widthMatchParent = true),
                                init = {
                                    orientation = LinearLayout.VERTICAL
                                    gravity = Gravity.CENTER
                                    setBackgroundResource(R.drawable.bg_permotion_round)
                                    updatePadding(horizontal = 15.dp)
                                    updatePadding(top = 10.dp, bottom = 15.dp)
                                }
                            ) {
                                MaterialSwitch(
                                    lparams = LayoutParams(widthMatchParent = true, height = 35.dp) {
                                        bottomMargin = 5.dp
                                    }
                                ) {
                                    text = "启用保守模式"
                                    textColor = colorResource(R.color.colorTextGray)
                                    textSize = 15f
                                    bind(ConfigData.ENABLE_QQ_TIM_PROTECT_MODE) {
                                        refreshCurrentModeText()
                                        showNeedRestartTip()
                                    }
                                }
                                TextView(
                                    lparams = LayoutParams(widthMatchParent = true)
                                ) {
                                    isSingleLine = true
                                    textColor = colorResource(R.color.colorTextGray)
                                    textSize = 12f
                                    alpha = 0.6f
                                    setLineSpacing(6f.dp, 1f)
                                    text = "此选项默认关闭，默认情况下模块将会干掉${appName}自身的电源锁控制类，开启后模块将只对系统电源锁生效，" +
                                        "如果你的${appName}视频通话等设置发生了故障，可以尝试开启这个功能。"
                                }
                            }
                            LinearLayout(
                                lparams = LayoutParams(widthMatchParent = true) {
                                    topMargin = 15.dp
                                },
                                init = {
                                    orientation = LinearLayout.VERTICAL
                                    gravity = Gravity.CENTER
                                    setBackgroundResource(R.drawable.bg_permotion_round)
                                    updatePadding(horizontal = 15.dp)
                                    updatePadding(top = 10.dp, bottom = 15.dp)
                                }
                            ) {
                                MaterialSwitch(
                                    lparams = LayoutParams(widthMatchParent = true, height = 35.dp) {
                                        bottomMargin = 5.dp
                                    }
                                ) {
                                    text = "关闭 CoreService"
                                    textColor = colorResource(R.color.colorTextGray)
                                    textSize = 15f
                                    bind(ConfigData.ENABLE_KILL_QQ_TIM_CORESERVICE) { showNeedRestartTip() }
                                }
                                TextView(
                                    lparams = LayoutParams(widthMatchParent = true) {
                                        bottomMargin = 10.dp
                                    }
                                ) {
                                    isSingleLine = true
                                    textColor = colorResource(R.color.colorTextGray)
                                    textSize = 12f
                                    alpha = 0.6f
                                    setLineSpacing(6f.dp, 1f)
                                    text = "关闭后可能会影响消息接收与视频通话，但是会达到省电效果，如果你的系统拥有推送服务 (HMS) 或 (MIPUSH) 可以尝试关闭。"
                                }
                                MaterialSwitch(
                                    lparams = LayoutParams(widthMatchParent = true, height = 35.dp) {
                                        bottomMargin = 5.dp
                                    }
                                ) {
                                    text = "关闭 CoreService\$KernelService"
                                    textColor = colorResource(R.color.colorTextGray)
                                    textSize = 15f
                                    bind(ConfigData.ENABLE_KILLE_QQ_TIM_CORESERVICE_CHILD) { showNeedRestartTip() }
                                }
                                TextView(
                                    lparams = LayoutParams(widthMatchParent = true)
                                ) {
                                    isSingleLine = true
                                    textColor = colorResource(R.color.colorTextGray)
                                    textSize = 12f
                                    alpha = 0.6f
                                    setLineSpacing(6f.dp, 1f)
                                    text = "这是一个辅助子服务，理论主服务关闭后子服务同样不会被启动，建议在保证消息接收的前提下可以尝试关闭子服务。"
                                }
                            }
                        }
                    }
                }
            }
        }
        /** 检查更新 */
        GithubReleaseTool.checkingForUpdate(context = this, ModuleVersion.NAME) { version, function ->
            updateVersionText.apply {
                text = "点击更新 $version"
                isVisible = true
                setOnClickListener { function() }
            }
        }
        refreshCurrentModeText()
        refreshConfigItems()
        /** 推广、恰饭 */
        ProjectPromote.show(activity = this, ModuleVersion.toString())
    }

    /** 显示需要重新启动提示 */
    private fun showNeedRestartTip() {
        needRestartTipText.isVisible = true
    }

    /** 刷新配置条目显示隐藏状态 */
    private fun refreshConfigItems() {
        itemQQTimConfig.isVisible = packageName != PackageName.WECHAT && ConfigData.isDisableAllHook.not()
    }

    /** 刷新当前模式文本 */
    private fun refreshCurrentModeText() {
        currentModeText.text = when {
            ConfigData.isDisableAllHook -> "模块已停用"
            packageName == PackageName.WECHAT -> "基础省电模式"
            ConfigData.isEnableQQTimProtectMode -> "保守模式"
            else -> "完全模式"
        }
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