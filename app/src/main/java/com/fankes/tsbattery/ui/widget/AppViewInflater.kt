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
 * This file is created by fankes on 2026/11/15.
 */
@file:Suppress("unused")

package com.fankes.tsbattery.ui.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.annotation.Keep
import com.fankes.tsbattery.R
import com.google.android.material.theme.MaterialComponentsViewInflater
import com.highcapable.betterandroid.ui.extension.component.base.getColorCompat
import com.highcapable.betterandroid.ui.extension.view.textColor

/**
 * 使用应用配色创建原生 Tooltip，其他控件沿用 Material 实现
 */
@Keep
class AppViewInflater : MaterialComponentsViewInflater() {

    override fun createTextView(context: Context, attrs: AttributeSet) = super.createTextView(context, attrs).apply {
        val textAppearance = attrs.getAttributeResourceValue(
            "http://schemas.android.com/apk/res/android",
            "textAppearance", 0
        )
        // 原生 Tooltip 的颜色属性未公开，按运行时样式名称识别后直接设置 View 颜色
        if (textAppearance != 0 && resources.getResourceName(textAppearance) == "android:style/TextAppearance.Tooltip") {
            textColor = context.getColorCompat(R.color.colorSnackbarText)
            backgroundTintList = ColorStateList.valueOf(context.getColorCompat(R.color.colorSnackbarBackground))
        }
    }
}