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

import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable

class RotateDrawableBuilder : DrawableWrapperBuilder<RotateDrawableBuilder>() {

    private var pivotX: Float = 0.5f
    private var pivotY: Float = 0.5f
    private var fromDegrees: Float = 0f
    private var toDegrees: Float = 360f

    fun pivotX(x: Float) = apply { pivotX = x }
    fun pivotY(y: Float) = apply { pivotY = y }
    fun fromDegrees(degree: Float) = apply { fromDegrees = degree }
    fun toDegrees(degree: Float) = apply { toDegrees = degree }

    override fun build(): Drawable {
        val rotateDrawable = RotateDrawable()
        drawable?.let {
            setDrawable(rotateDrawable, it)
            apply {
                setPivotX(rotateDrawable, pivotX)
                setPivotY(rotateDrawable, pivotY)
                setFromDegrees(rotateDrawable, fromDegrees)
                setToDegrees(rotateDrawable, toDegrees)
            }
        }
        return rotateDrawable
    }
}