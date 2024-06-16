/*
 * TSBattery - A new way to save your battery avoid cancer apps hacker it.
 * Copyright (C) 2017-2024 Fankes Studio(qzmmcn@163.com)
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
 * This file is created by fankes on 2023/10/8.
 */
@file:Suppress("MemberVisibilityCanBePrivate")

package com.fankes.tsbattery.hook.helper

import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import org.luckypray.dexkit.DexKitBridge

/**
 * DexKit 工具类
 */
object DexKitHelper {

    /** 是否已装载 */
    private var isLoaded = false

    /** 装载 */
    fun load() {
        if (isLoaded) return
        runCatching {
            System.loadLibrary("dexkit")
            isLoaded = true
        }.onFailure { YLog.error("Load DexKit failed!", it) }
    }

    /**
     * 创建 [DexKitBridge]
     * @param param 当前实例
     * @param initiate 方法体
     */
    fun create(param: PackageParam, initiate: DexKitBridge.() -> Unit) {
        load()
        runCatching { DexKitBridge.create(param.appInfo.sourceDir).use { initiate(it) } }
    }
}