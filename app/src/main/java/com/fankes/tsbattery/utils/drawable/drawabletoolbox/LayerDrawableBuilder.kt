/*
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
 * This file is Created by fankes on 2022/1/8.
 */
package com.fankes.tsbattery.utils.drawable.drawabletoolbox

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.Gravity
import androidx.annotation.RequiresApi
import java.util.*

class LayerDrawableBuilder {

    companion object {
        const val DIMEN_UNDEFINED = Int.MIN_VALUE
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private var paddingMode = LayerDrawable.PADDING_MODE_NEST
    private var paddingLeft = 0
    private var paddingTop = 0
    private var paddingRight = 0
    private var paddingBottom = 0
    private var paddingStart = 0
    private var paddingEnd = 0
    private val layers = ArrayList<Layer>()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun paddingMode(mode: Int) = apply { paddingMode = mode }
    fun paddingLeft(padding: Int) = apply { paddingLeft = padding }
    fun paddingTop(padding: Int) = apply { paddingTop = padding }
    fun paddingRight(padding: Int) = apply { paddingRight = padding }
    fun paddingBottom(padding: Int) = apply { paddingBottom = padding }

    @RequiresApi(Build.VERSION_CODES.M)
    fun paddingStart(padding: Int) = apply { paddingStart = padding }

    @RequiresApi(Build.VERSION_CODES.M)
    fun paddingEnd(padding: Int) = apply { paddingEnd = padding }
    fun padding(padding: Int) = apply {
        paddingLeft(padding).paddingTop(padding).paddingRight(padding).paddingBottom(padding)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun paddingRelative(padding: Int) = apply {
        paddingStart(padding).paddingTop(padding).paddingEnd(padding).paddingBottom(padding)
    }

    fun add(drawable: Drawable) = apply { layers.add(Layer(drawable)) }

    fun width(width: Int) = apply { layers.last().width = width }
    fun height(height: Int) = apply { layers.last().height = height }

    fun insetLeft(inset: Int) = apply { layers.last().insetLeft = inset }
    fun insetTop(inset: Int) = apply { layers.last().insetTop = inset }
    fun insetRight(inset: Int) = apply { layers.last().insetRight = inset }
    fun insetBottom(inset: Int) = apply { layers.last().insetBottom = inset }

    @RequiresApi(Build.VERSION_CODES.M)
    fun insetStart(inset: Int) = apply { layers.last().insetStart = inset }

    @RequiresApi(Build.VERSION_CODES.M)
    fun insetEnd(inset: Int) = apply { layers.last().insetEnd = inset }
    fun inset(inset: Int) =
        apply { insetLeft(inset).insetTop(inset).insetRight(inset).insetBottom(inset) }

    fun inset(insetLeft: Int, insetTop: Int, insetRight: Int, insetBottom: Int) = apply {
        insetLeft(insetLeft).insetTop(insetTop).insetRight(insetRight).insetBottom(insetBottom)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun insetRelative(inset: Int) =
        apply { insetStart(inset).insetTop(inset).insetEnd(inset).insetBottom(inset) }

    @RequiresApi(Build.VERSION_CODES.M)
    fun insetRelative(insetStart: Int, insetTop: Int, insetEnd: Int, insetBottom: Int) = apply {
        insetStart(insetStart).insetTop(insetTop).insetEnd(insetEnd).insetBottom(insetBottom)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun gravity(gravity: Int) = apply { layers.last().gravity = gravity }

    fun modify(
        index: Int, drawable: Drawable,
        width: Int = DIMEN_UNDEFINED,
        height: Int = DIMEN_UNDEFINED,
        insetLeft: Int = DIMEN_UNDEFINED,
        insetTop: Int = DIMEN_UNDEFINED,
        insetRight: Int = DIMEN_UNDEFINED,
        insetBottom: Int = DIMEN_UNDEFINED,
        insetStart: Int = DIMEN_UNDEFINED,
        insetEnd: Int = DIMEN_UNDEFINED
    ) =
        apply {
            val layer = layers[index]
            layer.drawable = drawable
            if (width != DIMEN_UNDEFINED) layer.width = width
            if (height != DIMEN_UNDEFINED) layer.height = height
            if (insetLeft != DIMEN_UNDEFINED) layer.insetLeft = insetLeft
            if (insetTop != DIMEN_UNDEFINED) layer.insetTop = insetTop
            if (insetRight != DIMEN_UNDEFINED) layer.insetRight = insetRight
            if (insetBottom != DIMEN_UNDEFINED) layer.insetBottom = insetBottom
            if (insetStart != DIMEN_UNDEFINED) layer.insetStart = insetStart
            if (insetEnd != DIMEN_UNDEFINED) layer.insetEnd = insetEnd
        }

    fun build(): LayerDrawable {
        val layerDrawable = LayerDrawable(layers.map { it -> it.drawable }.toTypedArray())
        for (i in 0 until layers.size) {
            val layer = layers[i]
            layerDrawable.setLayerInset(
                i,
                layer.insetLeft,
                layer.insetTop,
                layer.insetRight,
                layer.insetBottom
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (layer.insetStart != DIMEN_UNDEFINED || layer.insetEnd != DIMEN_UNDEFINED) {
                    layerDrawable.setLayerInsetRelative(
                        i,
                        layer.insetStart,
                        layer.insetTop,
                        layer.insetEnd,
                        layer.insetBottom
                    )
                }
            }
            layerDrawable.setId(i, i)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                layerDrawable.setLayerGravity(i, layer.gravity)
                layerDrawable.setLayerInsetStart(i, layer.insetStart)
                layerDrawable.setLayerInsetEnd(i, layer.insetEnd)
            }
        }
        layerDrawable.paddingMode = paddingMode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawable.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            if (paddingStart != DIMEN_UNDEFINED || paddingEnd != DIMEN_UNDEFINED) {
                layerDrawable.setPaddingRelative(
                    paddingStart,
                    paddingTop,
                    paddingEnd,
                    paddingBottom
                )
            }
        }

        return layerDrawable
    }

    internal class Layer(var drawable: Drawable) {
        var gravity: Int = Gravity.NO_GRAVITY
        var width: Int = -1
        var height: Int = -1
        var insetLeft: Int = 0
        var insetTop: Int = 0
        var insetRight: Int = 0
        var insetBottom: Int = 0
        var insetStart: Int = DIMEN_UNDEFINED
        var insetEnd: Int = DIMEN_UNDEFINED
    }
}