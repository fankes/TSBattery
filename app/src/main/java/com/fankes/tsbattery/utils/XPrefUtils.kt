/*
 * Copyright (C) 2022. Fankes Studio(qzmmcn@163.com)
 *
 * This file is part of TSBattery.
 *
 * TSBattery is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TSBattery is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This file is Created by zpp0196 on 2018/4/11.
 */
package com.fankes.tsbattery.utils

import de.robv.android.xposed.XSharedPreferences

object XPrefUtils {

    fun getBoolean(key: String, default: Boolean = false) = pref.getBoolean(key, default)

    fun getString(key: String, default: String = "unknown") = pref.getString(key, default)

    private val pref: XSharedPreferences
        get() {
            val preferences = XSharedPreferences("com.fankes.tsbattery")
            preferences.makeWorldReadable()
            preferences.reload()
            return preferences
        }
}