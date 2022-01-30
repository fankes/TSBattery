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
 * This file is Created by fankes on 2022/1/8.
 */
package com.fankes.tsbattery.utils.drawable.drawabletoolbox

import android.graphics.Path
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.PathShape

class PathShapeDrawableBuilder {

    private var path: Path? = null
    private var pathStandardWidth: Float = 100f
    private var pathStandardHeight: Float = 100f
    private var width: Int = -1
    private var height: Int = -1

    fun path(path: Path, pathStandardWidth: Float, pathStandardHeight: Float) = apply {
        this.path = path
        this.pathStandardWidth = pathStandardWidth
        this.pathStandardHeight = pathStandardHeight
    }

    fun width(width: Int) = apply { this.width = width }
    fun height(height: Int) = apply { this.height = height }
    fun size(size: Int) = apply { width(size).height(size) }

    fun build(custom: ((shapeDrawable: ShapeDrawable) -> Unit)? = null): ShapeDrawable {
        val shapeDrawable = ShapeDrawable()
        if (path == null || width <= 0 || height <= 0) {
            return shapeDrawable
        }
        val pathShape = PathShape(path!!, pathStandardWidth, pathStandardHeight)

        shapeDrawable.shape = pathShape
        shapeDrawable.intrinsicWidth = width
        shapeDrawable.intrinsicHeight = height
        if (custom != null) {
            custom(shapeDrawable)
        }
        return shapeDrawable
    }
}
