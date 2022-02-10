/*
 * TSBattery - A new way to save your battery avoid cancer apps hacker it.
 * Copyright (C) 2019-2022 Fankes Studio(qzmmcn@163.com)
 * https://github.com/fankes/TSBattery
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
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
import android.graphics.drawable.ScaleDrawable
import android.view.Gravity

class ScaleDrawableBuilder : DrawableWrapperBuilder<ScaleDrawableBuilder>() {

    private var level: Int = 10000
    private var scaleGravity = Gravity.CENTER
    private var scaleWidth: Float = 0f
    private var scaleHeight: Float = 0f

    fun level(level: Int) = apply { this.level = level }
    fun scaleGravity(gravity: Int) = apply { this.scaleGravity = gravity }
    fun scaleWidth(scale: Float) = apply { this.scaleWidth = scale }
    fun scaleHeight(scale: Float) = apply { this.scaleHeight = scale }

    override fun build(): Drawable {
        val scaleDrawable = ScaleDrawable(drawable, scaleGravity, scaleWidth, scaleHeight)
        scaleDrawable.level = level
        return scaleDrawable
    }
}