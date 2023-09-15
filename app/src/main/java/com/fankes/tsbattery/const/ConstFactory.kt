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
 * This file is Created by fankes on 2022/9/29.
 */
@file:Suppress("MemberVisibilityCanBePrivate")

package com.fankes.tsbattery.const

import com.fankes.tsbattery.generated.AppProperties
import com.fankes.tsbattery.wrapper.BuildConfigWrapper

/**
 * 包名常量定义类
 */
object PackageName {

    /** QQ */
    const val QQ = "com.tencent.mobileqq"

    /** TIM */
    const val TIM = "com.tencent.tim"

    /** 微信 */
    const val WECHAT = "com.tencent.mm"
}

/**
 * 跳转常量定义类
 */
object JumpEvent {

    /** 启动模块设置 */
    const val OPEN_MODULE_SETTING = "tsbattery_open_module_settings"
}

/**
 * 模块版本常量定义类
 */
object ModuleVersion {

    /** 当前 GitHub 提交的 ID (CI 自动构建) */
    const val GITHUB_COMMIT_ID = AppProperties.GITHUB_CI_COMMIT_ID

    /** 版本名称 */
    const val NAME = BuildConfigWrapper.VERSION_NAME

    /** 版本号 */
    const val CODE = BuildConfigWrapper.VERSION_CODE

    /** 是否为 CI 自动构建版本 */
    val isCiMode = GITHUB_COMMIT_ID.isNotBlank()

    /** 当前版本名称后缀 */
    val suffix = GITHUB_COMMIT_ID.let { if (it.isNotBlank()) "-$it" else "" }

    override fun toString() = "$NAME$suffix($CODE)"
}