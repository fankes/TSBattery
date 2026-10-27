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
 * This file is created by fankes on 2021/9/4.
 */
@file:Suppress("SetTextI18n", "LocalVariableName", "SameParameterValue")

package com.fankes.tsbattery.ui.activity

import android.app.Dialog
import android.content.ComponentName
import android.content.Intent
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fankes.projectpromote.ProjectPromote
import com.fankes.tsbattery.R
import com.fankes.tsbattery.const.JumpEvent
import com.fankes.tsbattery.const.ModuleVersion
import com.fankes.tsbattery.const.PackageName
import com.fankes.tsbattery.databinding.ActivityMainBinding
import com.fankes.tsbattery.databinding.DiaRestartAppBinding
import com.fankes.tsbattery.databinding.PageAboutBinding
import com.fankes.tsbattery.databinding.PageConfigBinding
import com.fankes.tsbattery.databinding.PageHomeBinding
import com.fankes.tsbattery.hook.entity.QQTIMHooker
import com.fankes.tsbattery.hook.entity.WeChatHooker
import com.fankes.tsbattery.ui.activity.base.BaseActivity
import com.fankes.tsbattery.utils.factory.appVersionBrandOf
import com.fankes.tsbattery.utils.factory.hideOrShowLauncherIcon
import com.fankes.tsbattery.utils.factory.isInstall
import com.fankes.tsbattery.utils.factory.isLauncherIconShowing
import com.fankes.tsbattery.utils.factory.openBrowser
import com.fankes.tsbattery.utils.factory.openSelfSetting
import com.fankes.tsbattery.utils.factory.showDialog
import com.fankes.tsbattery.utils.factory.snake
import com.fankes.tsbattery.utils.factory.toast
import com.fankes.tsbattery.utils.tool.GithubReleaseTool
import com.fankes.tsbattery.wrapper.BuildConfigWrapper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.betterandroid.ui.extension.component.runDelayed
import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.hook.factory.applyModuleTheme
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import kotlinx.coroutines.Job
import kotlin.concurrent.thread

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private companion object {

        const val QQ_SUPPORT_VERSION = "理论支持 8.0.0+ 及以上版本。"
        const val TIM_SUPPORT_VERSION = "2+、3+ (并未完全测试每个版本)。"
        const val WECHAT_SUPPORT_VERSION = "全版本仅支持基础省电，更多功能依然画饼。"

        const val GITHUB_PROFILE_URL = "https://github.com/fankes"
        const val GITHUB_REPO_URL = "https://github.com/fankes/TSBattery"
        const val TELEGRAM_CI_URL = "https://t.me/TSBattery_CI"
        const val TELEGRAM_CHANNEL_URL = "https://t.me/XiaofangDaily"
        const val TELEGRAM_GROUP_URL = "https://t.me/XiaofangInternet"
        const val QQ_GROUP_URL = "https://qm.qq.com/cgi-bin/qm/qr?k=dp2h5YhWiga9WWb_Oh7kSHmx01X8I8ii&jump_from=webapi&authKey=Za5CaFP0lk7+Zgsk2KpoBD7sSaYbeXbsDgFjiWelOeH4VSionpxFJ7V0qQBSqvFM"
        const val QQ_CHANNEL_URL = "https://pd.qq.com/s/44gcy28h"
        const val COOLAPK_PROFILE_URL = "https://www.coolapk.com/u/876977"
        const val COOLAPK_PACKAGE_NAME = "com.coolapk.market"
        const val AFDIAN_URL = "https://afdian.com/a/fankes"
        const val YUKIHOOK_URL = "https://github.com/HighCapable/YukiHook"
        const val KAVAREF_URL = "https://github.com/HighCapable/KavaRef"
    }

    private val homeBinding by lazy { PageHomeBinding.inflate(layoutInflater, binding.mainViewPager, false) }
    private val configBinding by lazy { PageConfigBinding.inflate(layoutInflater, binding.mainViewPager, false) }
    private val aboutBinding by lazy { PageAboutBinding.inflate(layoutInflater, binding.mainViewPager, false) }

    override fun onCreate() {
        initPager()
        initHomeInfo()
        initListeners()
        if (YukiHook.Status.runtime == YukiHookRuntime.LIBXPOSED) {
            var unavailableJob: Job? = null
            YukiHook.Status.observeFrameworkService(lifecycle) { available ->
                // 第一次回调可能是 false，稍后连上会再回调 true，所以 false 先等一会
                unavailableJob?.cancel()
                if (available) onCreate(available = true)
                else unavailableJob = runDelayed(500) { onCreate(available = false) }
            }
        } else onCreate(available = true)
    }

    private fun onCreate(available: Boolean) {
        // 判断 Hook 状态
        if (available && YukiHook.Status.isModuleActive) {
            homeBinding.mainLinStatus.setCardBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorStatusActiveContainer, null))
            homeBinding.mainImgStatus.setImageResource(R.drawable.ic_status_success)
            homeBinding.mainTextStatus.text = "已激活"
            homeBinding.mainTextApiWay.isVisible = true
            refreshActivateExecutor()
            // 推广、恰饭
            ProjectPromote.show(activity = this, ModuleVersion.toString())
        } else
            showDialog {
                title = "模块没有激活"
                msg = "检测到模块没有激活，若你正在使用免 Root 框架，你可以忽略此提示。"
                confirmButton(text = "我知道了")
                noCancelable()
            }
    }

    /** 初始化主页的安装状态、版本文本、CI 自动构建标识和检查更新，它们不依赖模块是否激活 */
    private fun initHomeInfo() {
        // 设置安装状态
        homeBinding.mainTextQqVer.text = PackageName.QQ.takeIf { isInstall(it) }?.let { appVersionBrandOf(it) } ?: "未安装"
        homeBinding.mainTextTimVer.text = PackageName.TIM.takeIf { isInstall(it) }?.let { appVersionBrandOf(it) } ?: "未安装"
        homeBinding.mainTextWechatVer.text = PackageName.WECHAT.takeIf { isInstall(it) }?.let { appVersionBrandOf(it) } ?: "未安装"
        // 设置文本
        homeBinding.mainTextVersion.text = "${ModuleVersion.NAME} (${ModuleVersion.CODE})"
        // 设置 CI 自动构建标识
        if (ModuleVersion.isCiMode)
            homeBinding.mainTextReleaseVersion.apply {
                text = "CI ${ModuleVersion.GITHUB_COMMIT_ID}"
                isVisible = true
                setOnClickListener {
                    showDialog {
                        title = "CI 自动构建说明"
                        msg = """
                          你正在使用的是 CI 自动构建版本，Commit ID 为 ${ModuleVersion.GITHUB_COMMIT_ID}。
                          
                          它是由代码提交后自动触发并构建、自动编译发布的，并未经任何稳定性测试，使用风险自负。
                        """.trimIndent()
                        confirmButton(text = "我知道了")
                        noCancelable()
                    }
                }
            }
        // 检查更新
        GithubReleaseTool.checkingForUpdate(context = this, ModuleVersion.NAME) { version, function ->
            homeBinding.mainTextReleaseVersion.apply {
                text = "点击更新 $version"
                isVisible = true
                setOnClickListener { function() }
            }
        }
    }

    /** 初始化各个按钮的点击事件，它们不依赖模块是否激活 */
    private fun initListeners() {
        homeBinding.mainQqItem.setOnClickListener {
            showDialog {
                title = "兼容的 QQ 版本"
                msg = QQ_SUPPORT_VERSION
                confirmButton(text = "我知道了")
            }
            // 振动提醒
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        homeBinding.mainTimItem.setOnClickListener {
            showDialog {
                title = "兼容的 TIM 版本"
                msg = TIM_SUPPORT_VERSION
                confirmButton(text = "我知道了")
            }
            // 振动提醒
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        homeBinding.mainWechatItem.setOnClickListener {
            showDialog {
                title = "兼容的微信版本"
                msg = WECHAT_SUPPORT_VERSION
                confirmButton(text = "我知道了")
            }
            // 振动提醒
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        // 快捷操作 QQ
        homeBinding.quickQqButton.setOnClickListener { startModuleSettings(PackageName.QQ) }
        // 快捷操作 TIM
        homeBinding.quickTimButton.setOnClickListener { startModuleSettings(PackageName.TIM) }
        // 快捷操作微信
        homeBinding.quickWechatButton.setOnClickListener { startModuleSettings(PackageName.WECHAT) }
        // 重启按钮
        binding.titleRestartIcon.setOnClickListener { showRestartAppDialog() }
        // 项目地址
        aboutBinding.aboutProfileItem.setOnClickListener { openBrowser(url = GITHUB_PROFILE_URL) }
        aboutBinding.aboutRepoItem.setOnClickListener { openBrowser(url = GITHUB_REPO_URL) }
        // 联系方式
        aboutBinding.linkTelegramCi.setOnClickListener { openBrowser(url = TELEGRAM_CI_URL) }
        aboutBinding.linkTelegramChannel.setOnClickListener { openBrowser(url = TELEGRAM_CHANNEL_URL) }
        aboutBinding.linkTelegramGroup.setOnClickListener { openBrowser(url = TELEGRAM_GROUP_URL) }
        aboutBinding.linkQqGroup.setOnClickListener {
            openBrowser(url = QQ_GROUP_URL)
        }
        aboutBinding.linkQqChannel.setOnClickListener { openBrowser(url = QQ_CHANNEL_URL) }
        aboutBinding.linkFollowMe.setOnClickListener {
            openBrowser(url = COOLAPK_PROFILE_URL, packageName = COOLAPK_PACKAGE_NAME)
        }
        // 恰饭！
        aboutBinding.linkSupportMe.setOnClickListener { openBrowser(url = AFDIAN_URL) }
        // 基于项目构建
        aboutBinding.linkYukihook.setOnClickListener { openBrowser(url = YUKIHOOK_URL) }
        aboutBinding.linkKavaref.setOnClickListener { openBrowser(url = KAVAREF_URL) }
        // 设置桌面图标显示隐藏
        homeBinding.hideIconInLauncherSwitch.isChecked = isLauncherIconShowing.not()
        homeBinding.hideIconInLauncherSwitch.setOnCheckedChangeListener { btn, b ->
            if (btn.isPressed.not()) return@setOnCheckedChangeListener
            hideOrShowLauncherIcon(b)
        }
        // 判断当前启动模式
        if (packageName != BuildConfigWrapper.APPLICATION_ID) {
            homeBinding.quickActionItem.isVisible = false
            homeBinding.displaySettingItem.isVisible = false
        }
    }

    /** 初始化底栏和页面 (主页 | 配置 | 关于) */
    private fun initPager() {
        val pages = listOf(homeBinding.root, configBinding.root, aboutBinding.root)
        val navIds = listOf(R.id.nav_home, R.id.nav_config, R.id.nav_about)
        binding.mainViewPager.adapter = object : PagerAdapter() {
            override fun getCount() = pages.size
            override fun isViewFromObject(view: View, `object`: Any) = view === `object`
            override fun instantiateItem(container: ViewGroup, position: Int) = pages[position].also { container.addView(it) }
            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)
        }
        binding.mainViewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                binding.mainBottomNav.selectedItemId = navIds[position]
            }
        })
        binding.mainBottomNav.setOnItemSelectedListener {
            binding.mainViewPager.currentItem = navIds.indexOf(it.itemId)
            true
        }
        // 重复点击当前页面的底栏图标时平滑回到顶部
        binding.mainBottomNav.setOnItemReselectedListener { pages[navIds.indexOf(it.itemId)].smoothScrollTo(0, 0) }
    }

    /** 显示重启应用对话框 */
    private fun showRestartAppDialog() {
        val dialogBinding = DiaRestartAppBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(applyModuleTheme(R.style.Theme_TSBattery))
            .setTitle("重启应用")
            .setView(dialogBinding.root)
            .setNegativeButton("取消", null)
            .show()
        dialogBinding.restartQqButton.setOnClickListener { restartApp(PackageName.QQ, "QQ", dialog) }
        dialogBinding.restartTimButton.setOnClickListener { restartApp(PackageName.TIM, "TIM", dialog) }
        dialogBinding.restartWechatButton.setOnClickListener { restartApp(PackageName.WECHAT, "微信", dialog) }
    }

    /**
     * 使用 Root 强行停止指定应用，失败则打开应用信息界面
     * @param packageName 包名
     * @param name 应用名称
     * @param dialog 需要关闭的对话框
     */
    private fun restartApp(packageName: String, name: String, dialog: Dialog) {
        dialog.dismiss()
        if (isInstall(packageName).not()) return snake(msg = "你没有安装此应用")
        thread {
            val isSuccess = runCatching {
                ProcessBuilder("su", "-c", "am force-stop $packageName").start().waitFor() == 0
            }.getOrDefault(false)
            runOnUiThread {
                if (isSuccess) snake(msg = "已结束 $name，请手动重新打开")
                else {
                    toast("获取 Root 权限失败，请授予 Root 权限，或在应用信息中手动强行停止 $name")
                    openSelfSetting(packageName)
                }
            }
        }
    }

    /**
     * 启动模块设置界面
     * @param packageName 包名
     */
    private fun startModuleSettings(packageName: String) {
        if (isInstall(packageName)) runCatching {
            startActivity(Intent().apply {
                component = ComponentName(
                    packageName,
                    if (packageName != PackageName.WECHAT) QQTIMHooker.JumpActivityClassName else WeChatHooker.LauncherUIClassName
                )
                putExtra(JumpEvent.OPEN_MODULE_SETTING, YukiHook.Status.compiledTimestamp)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }.onFailure { snake(msg = "启动模块设置失败\n$it") } else snake(msg = "你没有安装此应用")
    }

    /** 刷新模块激活使用的方式 */
    private fun refreshActivateExecutor() {
        if (YukiHook.Status.Executor.apiVersion > 0)
            homeBinding.mainTextApiWay.text = "Activated by ${YukiHook.Status.Executor.name} API ${YukiHook.Status.Executor.apiVersion}"
        else homeBinding.mainTextApiWay.text = "Activated by ${YukiHook.Status.Executor.name}"
    }
}