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
 * This file is Created by fankes on 2022/3/28.
 */
package com.fankes.tsbattery.data

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

object DataConst {

    val ENABLE_HIDE_ICON = PrefsData("_hide_icon", false)
    val ENABLE_RUN_INFO = PrefsData("_tip_run_info", false)
    val ENABLE_NOTIFY_TIP = PrefsData("_tip_in_notify", true)
    val ENABLE_SETTING_TIP = PrefsData("_tip_in_setting", true)
    val ENABLE_QQTIM_WHITE_MODE = PrefsData("_qqtim_white_mode", false)
    val ENABLE_QQTIM_CORESERVICE_BAN = PrefsData("_qqtim_core_service_ban", false)
    val ENABLE_QQTIM_CORESERVICE_CHILD_BAN = PrefsData("_qqtim_core_service_child_ban", false)
    val DISABLE_WECHAT_HOOK = PrefsData("_disable_wechat_hook", false)
}