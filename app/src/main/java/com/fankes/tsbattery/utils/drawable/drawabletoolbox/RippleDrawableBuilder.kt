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
package com.fankes.tsbattery.utils.drawable.drawabletoolbox

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.*
import android.util.StateSet

class RippleDrawableBuilder : DrawableWrapperBuilder<RippleDrawableBuilder>() {

    private var color: Int = Constants.DEFAULT_COLOR
    private var colorStateList: ColorStateList? = null
    private var radius: Int = -1

    fun color(color: Int) = apply { this.color = color }
    fun colorStateList(colorStateList: ColorStateList?) =
        apply { this.colorStateList = colorStateList }

    fun radius(radius: Int) = apply { this.radius = radius }

    override fun build(): Drawable {
        var drawable = this.drawable!!
        val colorStateList = this.colorStateList ?: ColorStateList(
            arrayOf(StateSet.WILD_CARD),
            intArrayOf(color)
        )

        var mask = if (drawable is DrawableContainer) drawable.getCurrent() else drawable
        if (mask is ShapeDrawable) {
            val state = mask.getConstantState()
            if (state != null) {
                val temp = state.newDrawable().mutate() as ShapeDrawable
                temp.paint.color = Color.BLACK
                mask = temp
            }
        } else if (mask is GradientDrawable) {
            val state = mask.getConstantState()
            if (state != null) {
                val temp = state.newDrawable().mutate() as GradientDrawable
                temp.setColor(Color.BLACK)
                mask = temp
            }
        } else {
            mask = ColorDrawable(Color.BLACK)
        }

        val rippleDrawable = RippleDrawable(colorStateList, drawable, mask)
        setRadius(rippleDrawable, radius)
        rippleDrawable.invalidateSelf()
        drawable = rippleDrawable
        return drawable
    }
}