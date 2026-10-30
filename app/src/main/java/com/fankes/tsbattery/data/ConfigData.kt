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
@file:Suppress("StaticFieldLeak")

package com.fankes.tsbattery.data

import android.annotation.SuppressLint
import android.content.Context
import android.widget.CompoundButton
import com.highcapable.yukihookapi.hook.factory.preferences
import com.highcapable.yukihookapi.hook.xposed.preference.YukiHookPreferences

/**
 * 全局配置存储控制类
 */
object ConfigData {

    /** QQ、TIM 保守模式*/
    const val ENABLE_QQ_TIM_PROTECT_MODE = "enable_qq_tim_protect_mode"

    /** 自动关闭 QQ、TIM 的 CoreService */
    const val ENABLE_KILL_QQ_TIM_CORESERVICE = "enable_kill_qq_tim_core_service"

    /** 自动关闭 QQ、TIM 的 CoreService$KernelService */
    const val ENABLE_KILLE_QQ_TIM_CORESERVICE_CHILD = "enable_kill_qq_tim_core_service_child"

    /** 停用全部省电功能 (停用模块) */
    const val DISABLE_ALL_HOOK = "disable_all_hook"

    /** 启动时展示项目推广 */
    const val SHOW_PROJECT_PROMOTION = "show_project_promotion"

    /** 当前的 [YukiHookPreferences] */
    @SuppressLint("StaticFieldLeak")
    private var preferences: YukiHookPreferences? = null

    /** 当前的模块自身的 [YukiHookPreferences]，在宿主中只读 */
    @SuppressLint("StaticFieldLeak")
    private var modulePreferences: YukiHookPreferences? = null

    /**
     * 读取 [YukiHookPreferences]
     * @param key 键值名称
     * @param value 键值内容
     * @param native 是否读取宿主自身的存储，为 false 时读取模块自身的存储
     * @return [Boolean]
     */
    private fun getBoolean(key: String, value: Boolean = false, native: Boolean = true) =
        (if (native) preferences else modulePreferences)?.getBoolean(key, value) ?: value

    /**
     * 存入 [YukiHookPreferences]
     * @param key 键值名称
     * @param value 键值内容
     * @param native 是否存入宿主自身的存储，为 false 时存入模块自身的存储 (仅能在模块中使用)
     */
    private fun putBoolean(key: String, value: Boolean = false, native: Boolean = true) =
        (if (native) preferences else modulePreferences)?.edit { putBoolean(key, value) }

    /**
     * 初始化 [YukiHookPreferences]
     * @param context 实例
     */
    fun init(context: Context) {
        preferences = context.preferences(name = "tsbattery_config").native()
        modulePreferences = context.preferences(name = "tsbattery_config")
    }

    /**
     * 绑定到 [CompoundButton] 自动设置选中状态
     * @param key 键值名称
     * @param onChange 当改变时回调
     */
    fun CompoundButton.bind(key: String, onChange: (Boolean) -> Unit = {}) {
        isChecked = getBoolean(key)
        setOnCheckedChangeListener { button, isChecked ->
            if (button.isPressed) {
                putBoolean(key, isChecked)
                onChange(isChecked)
            }
        }
    }

    /**
     * 是否启用 QQ、TIM 保守模式
     * @return [Boolean]
     */
    var isEnableQQTimProtectMode
        get() = getBoolean(ENABLE_QQ_TIM_PROTECT_MODE)
        set(value) {
            putBoolean(ENABLE_QQ_TIM_PROTECT_MODE, value)
        }

    /**
     * 是否启用自动关闭 QQ、TIM 的 CoreService
     * @return [Boolean]
     */
    var isEnableKillQQTimCoreService
        get() = getBoolean(ENABLE_KILL_QQ_TIM_CORESERVICE)
        set(value) {
            putBoolean(ENABLE_KILL_QQ_TIM_CORESERVICE, value)
        }

    /**
     * 是否启用自动关闭 QQ、TIM 的 CoreService$KernelService
     * @return [Boolean]
     */
    var isEnableKillQQTimCoreServiceChild
        get() = getBoolean(ENABLE_KILLE_QQ_TIM_CORESERVICE_CHILD)
        set(value) {
            putBoolean(ENABLE_KILLE_QQ_TIM_CORESERVICE_CHILD, value)
        }

    /**
     * 是否停用全部省电功能 (停用模块)
     * @return [Boolean]
     */
    var isDisableAllHook
        get() = getBoolean(DISABLE_ALL_HOOK)
        set(value) {
            putBoolean(DISABLE_ALL_HOOK, value)
        }

    /**
     * 是否在启动时展示项目推广
     *
     * 此项保存在模块自身的存储中，宿主中的配置界面需要读取它，所以不使用 native
     * @return [Boolean]
     */
    var isShowProjectPromotion
        get() = getBoolean(SHOW_PROJECT_PROMOTION, value = true, native = false)
        set(value) {
            putBoolean(SHOW_PROJECT_PROMOTION, value, native = false)
        }
}