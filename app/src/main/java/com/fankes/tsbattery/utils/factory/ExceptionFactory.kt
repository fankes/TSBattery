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
 * This file is Created by fankes on 2022/3/13.
 */
@file:Suppress("unused")

package com.fankes.tsbattery.utils.factory

import com.highcapable.yukihookapi.hook.log.loggerE

/**
 * 忽略异常返回值
 * @param result 回调 - 如果异常为空
 * @return [T] 发生异常时返回设定值否则返回正常值
 */
inline fun <T> safeOfNull(result: () -> T): T? = safeOf(default = null, result)

/**
 * 忽略异常返回值
 * @param result 回调 - 如果异常为 false
 * @return [Boolean] 发生异常时返回设定值否则返回正常值
 */
inline fun safeOfFalse(result: () -> Boolean) = safeOf(default = false, result)

/**
 * 忽略异常返回值
 * @param result 回调 - 如果异常为 true
 * @return [Boolean] 发生异常时返回设定值否则返回正常值
 */
inline fun safeOfTrue(result: () -> Boolean) = safeOf(default = true, result)

/**
 * 忽略异常返回值
 * @param result 回调 - 如果异常为 false
 * @return [String] 发生异常时返回设定值否则返回正常值
 */
inline fun safeOfNothing(result: () -> String) = safeOf(default = "", result)

/**
 * 忽略异常返回值
 * @param result 回调 - 如果异常为 false
 * @return [Int] 发生异常时返回设定值否则返回正常值
 */
inline fun safeOfNan(result: () -> Int) = safeOf(default = 0, result)

/**
 * 忽略异常返回值
 * @param default 异常返回值
 * @param result 正常回调值
 * @return [T] 发生异常时返回设定值否则返回正常值
 */
inline fun <T> safeOf(default: T, result: () -> T) = try {
    result()
} catch (_: Throwable) {
    default
}

/**
 * 忽略异常运行
 * @param msg 出错输出的消息 - 默认为空
 * @param block 正常回调
 */
inline fun <T> T.runInSafe(msg: String = "", block: () -> Unit) {
    runCatching(block).onFailure { if (msg.isNotBlank()) loggerE(msg = msg, e = it) }
}