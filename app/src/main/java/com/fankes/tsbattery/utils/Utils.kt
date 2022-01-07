/**
 * Copyright (C) 2021. Fankes Studio(qzmmcn@163.com)
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
 * This file is Created by fankes on 2022/1/7.
 */
@file:Suppress("DEPRECATION")

package com.fankes.tsbattery.utils

import android.content.Context
import android.content.pm.PackageInfo

/** 得到安装包信息 */
val Context.packageInfo get() = packageManager?.getPackageInfo(packageName, 0) ?: PackageInfo()

/** 得到版本信息 */
val Context.versionName get() = packageInfo.versionName ?: ""

/** 得到版本号 */
val Context.versionCode get() = packageInfo.versionCode

