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
 * This file is created by fankes on 2022/1/30.
 */
package com.fankes.tsbattery.ui.activity.base

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.annotation.Keep
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewbinding.ViewBinding
import com.fankes.tsbattery.R
import com.highcapable.betterandroid.ui.component.activity.AppBindingActivity
import com.highcapable.betterandroid.ui.extension.component.base.getColorCompat
import com.highcapable.betterandroid.ui.extension.view.ViewLayoutParams
import com.highcapable.betterandroid.ui.extension.view.animate
import com.highcapable.betterandroid.ui.extension.view.removeSelf
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.proxy.ModuleActivity

@Keep
abstract class BaseActivity<VB : ViewBinding> : AppBindingActivity<VB>(), ModuleActivity {

    private companion object {
        const val SPLASH_SCREEN_ANIMATION_DURATION = 200L
    }

    /** 是否显示启动屏，开启后需要在页面状态确定时调用 [callStartupReady] */
    protected open val showSplashScreen = false

    /** 页面状态是否已经确定 */
    private var isStartupReady = false

    /** 启动遮罩和启动屏动画是否已经结束 */
    private var isStartupFinished = false

    /** 启动结束后才能执行的操作 */
    private val startupActions = mutableListOf<() -> Unit>()

    /** 遮住系统启动屏与页面首帧交接的背景 */
    private var startupCover: View? = null

    override val moduleTheme get() = R.style.Theme_TSBattery

    override fun onCreate(savedInstanceState: Bundle?) {
        // 重建的页面不会再显示启动屏
        isStartupFinished = !showSplashScreen || savedInstanceState != null
        val splashScreen = if (showSplashScreen) installSplashScreen() else null
        delegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
        splashScreen?.let { initSplashScreen(it) }
        // 装载子类
        onCreate()
    }

    /** 回调 [onCreate] 方法 */
    abstract fun onCreate()

    /** 页面状态确定后调用，启动屏会在此之后结束 */
    protected fun callStartupReady() {
        isStartupReady = true
        revealStartupCover()
    }

    /**
     * 启动屏结束后再执行
     * @param action 要执行的操作
     */
    protected fun afterStartup(action: () -> Unit) {
        if (isStartupFinished) action() else startupActions.add(action)
    }

    /**
     * 初始化启动屏，页面状态确定前保持显示
     * @param splashScreen 启动屏实例
     */
    private fun initSplashScreen(splashScreen: SplashScreen) {
        splashScreen.setKeepOnScreenCondition { !isStartupReady }
        splashScreen.setOnExitAnimationListener { provider ->
            // 遮罩已经淡出或页面重建时，系统启动屏来晚了直接移除
            if (isStartupFinished) {
                provider.remove()
                return@setOnExitAnimationListener
            }
            // 回调时系统启动屏已经覆盖窗口，此时才移除交接背景，动画期间不响应点击
            removeStartupCover()
            provider.view.isClickable = true
            val icon = provider.iconView
            val scale = maxOf(provider.view.width.toFloat() / icon.width, provider.view.height.toFloat() / icon.height) * 2f
            icon.animate {
                duration = SPLASH_SCREEN_ANIMATION_DURATION
                interpolator = DecelerateInterpolator()
                scaleX(0.85f)
                scaleY(0.85f)
                withEndAction {
                    icon.animate {
                        duration = SPLASH_SCREEN_ANIMATION_DURATION
                        interpolator = AccelerateInterpolator()
                        scaleX(scale)
                        scaleY(scale)
                    }
                    provider.view.animate {
                        duration = SPLASH_SCREEN_ANIMATION_DURATION
                        alpha(0f)
                        withEndAction {
                            provider.remove()
                            finishStartup()
                        }
                    }
                }
            }
        }
        if (isStartupFinished) return
        val cover = View(this).apply {
            setBackgroundColor(resources.getColorCompat(R.color.colorThemeBackground))
            isClickable = true
        }
        (window.decorView as ViewGroup).addView(cover, ViewLayoutParams(matchParent = true))
        startupCover = cover
    }

    /** 状态确定且窗口获得焦点后淡出遮罩，系统没有回调启动屏时由它结束 */
    private fun revealStartupCover() {
        val cover = startupCover ?: return
        // 淡出已经开始时不再重复执行
        if (!isStartupReady || !cover.hasWindowFocus() || cover.alpha < 1f) return
        cover.animate {
            duration = SPLASH_SCREEN_ANIMATION_DURATION
            alpha(0f)
            withEndAction {
                removeStartupCover()
                finishStartup()
            }
        }
    }

    /** 移除启动遮罩 */
    private fun removeStartupCover() {
        val cover = startupCover ?: return
        cover.animate().cancel()
        cover.removeSelf()
        startupCover = null
    }

    /** 结束启动并执行等待中的操作 */
    private fun finishStartup() {
        isStartupFinished = true
        // 页面销毁后动画仍会走完
        if (isDestroyed) return
        startupActions.forEach { it() }
        startupActions.clear()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) revealStartupCover()
    }

    override fun getClassLoader() = delegate.getClassLoader()

    override fun onConfigurationChanged(newConfig: Configuration) {
        delegate.onConfigurationChanged(newConfig) 
        super.onConfigurationChanged(newConfig)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        delegate.onRestoreInstanceState(savedInstanceState) 
        super.onRestoreInstanceState(savedInstanceState)
    }
}