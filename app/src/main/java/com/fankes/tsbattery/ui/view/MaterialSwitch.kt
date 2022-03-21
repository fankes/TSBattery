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
 * This file is Created by fankes on 2022/1/8.
 */
@file:Suppress("SameParameterValue")

package com.fankes.tsbattery.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import com.fankes.tsbattery.utils.drawable.drawabletoolbox.DrawableBuilder
import com.fankes.tsbattery.utils.factory.dp

class MaterialSwitch(context: Context, attrs: AttributeSet?) : SwitchCompat(context, attrs) {

    private fun toColors(selected: Int, pressed: Int, normal: Int): ColorStateList {
        val colors = intArrayOf(selected, pressed, normal)
        val states = arrayOfNulls<IntArray>(3)
        states[0] = intArrayOf(android.R.attr.state_checked)
        states[1] = intArrayOf(android.R.attr.state_pressed)
        states[2] = intArrayOf()
        return ColorStateList(states, colors)
    }

    init {
        trackDrawable = DrawableBuilder()
            .rectangle()
            .rounded()
            .solidColor(0xFF656565.toInt())
            .height(20.dp(context))
            .cornerRadius(15.dp(context))
            .build()
        thumbDrawable = DrawableBuilder()
            .rectangle()
            .rounded()
            .solidColor(Color.WHITE)
            .size(20.dp(context), 20.dp(context))
            .cornerRadius(20.dp(context))
            .strokeWidth(8.dp(context))
            .strokeColor(Color.TRANSPARENT)
            .build()
        trackTintList = toColors(
            0xFF656565.toInt(),
            0xFFCCCCCC.toInt(),
            0xFFCCCCCC.toInt()
        )
        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.END
    }
}