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
@file:Suppress("DEPRECATION", "CanvasSize")

package com.fankes.tsbattery.utils.drawable.drawabletoolbox

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Rect
import android.graphics.drawable.Drawable

class FlipDrawable(
    private var drawable: Drawable,
    private var orientation: Int = ORIENTATION_HORIZONTAL
) : Drawable() {

    companion object {
        const val ORIENTATION_HORIZONTAL = 0
        const val ORIENTATION_VERTICAL = 1
    }

    override fun draw(canvas: Canvas) {
        val saveCount = canvas.save()
        if (orientation == ORIENTATION_VERTICAL) {
            canvas.scale(1f, -1f, (canvas.width / 2).toFloat(), (canvas.height / 2).toFloat())
        } else {
            canvas.scale(-1f, 1f, (canvas.width / 2).toFloat(), (canvas.height / 2).toFloat())
        }
        drawable.bounds = Rect(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        canvas.restoreToCount(saveCount)
    }

    override fun onLevelChange(level: Int): Boolean {
        drawable.level = level
        invalidateSelf()
        return true
    }

    override fun getIntrinsicWidth(): Int {
        return drawable.intrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return drawable.intrinsicHeight
    }

    override fun setAlpha(alpha: Int) {
        drawable.alpha = alpha
    }

    override fun getOpacity(): Int {
        return drawable.opacity
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        drawable.colorFilter = colorFilter
    }
}