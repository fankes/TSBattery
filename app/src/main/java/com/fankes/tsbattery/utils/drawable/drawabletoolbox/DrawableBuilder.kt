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
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.fankes.tsbattery.utils.drawable.drawabletoolbox

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.StateSet
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class DrawableBuilder {

    private var properties = DrawableProperties()
    private var order: AtomicInteger = AtomicInteger(1)
    private var transformsMap = TreeMap<Int, (Drawable) -> Drawable>()
    private var baseDrawable: Drawable? = null

    fun batch(properties: DrawableProperties) = apply { this.properties = properties.copy() }
    fun baseDrawable(drawable: Drawable) = apply { baseDrawable = drawable }

    // <shape>
    fun shape(shape: Int) = apply { properties.shape = shape }

    fun rectangle() = apply { shape(GradientDrawable.RECTANGLE) }
    fun oval() = apply { shape(GradientDrawable.OVAL) }
    fun line() = apply { shape(GradientDrawable.LINE) }
    fun ring() = apply { shape(GradientDrawable.RING) }
    fun innerRadius(innerRadius: Int) = apply { properties.innerRadius = innerRadius }
    fun innerRadiusRatio(innerRadiusRatio: Float) =
        apply { properties.innerRadiusRatio = innerRadiusRatio }

    fun thickness(thickness: Int) = apply { properties.thickness = thickness }
    fun thicknessRatio(thicknessRatio: Float) = apply { properties.thicknessRatio = thicknessRatio }

    fun useLevelForRing(use: Boolean = true) = apply { properties.useLevelForRing = use }

    // <corner>
    fun cornerRadius(cornerRadius: Int) = apply { properties.cornerRadius = cornerRadius }

    fun topLeftRadius(topLeftRadius: Int) = apply { properties.topLeftRadius = topLeftRadius }
    fun topRightRadius(topRightRadius: Int) = apply { properties.topRightRadius = topRightRadius }
    fun bottomRightRadius(bottomRightRadius: Int) =
        apply { properties.bottomRightRadius = bottomRightRadius }

    fun bottomLeftRadius(bottomLeftRadius: Int) =
        apply { properties.bottomLeftRadius = bottomLeftRadius }

    fun rounded() = apply { cornerRadius(Int.MAX_VALUE) }
    fun cornerRadii(
        topLeftRadius: Int,
        topRightRadius: Int,
        bottomRightRadius: Int,
        bottomLeftRadius: Int
    ) = apply {
        topLeftRadius(topLeftRadius); topRightRadius(topRightRadius); bottomRightRadius(
        bottomRightRadius
    ); bottomLeftRadius(bottomLeftRadius)
    }

    // <gradient>

    fun gradient(useGradient: Boolean = true) = apply { properties.useGradient = useGradient }

    fun gradientType(type: Int) = apply { properties.type = type }
    fun linearGradient() = apply { gradientType(GradientDrawable.LINEAR_GRADIENT) }
    fun radialGradient() = apply { gradientType(GradientDrawable.RADIAL_GRADIENT) }
    fun sweepGradient() = apply { gradientType(GradientDrawable.SWEEP_GRADIENT) }
    fun angle(angle: Int) = apply { properties.angle = angle }
    fun centerX(centerX: Float) = apply { properties.centerX = centerX }
    fun centerY(centerY: Float) = apply { properties.centerY = centerY }
    fun center(centerX: Float, centerY: Float) = apply { centerX(centerX); centerY(centerY) }

    fun useCenterColor(useCenterColor: Boolean = true) =
        apply { properties.useCenterColor = useCenterColor }

    fun startColor(startColor: Int) = apply { properties.startColor = startColor }
    fun centerColor(centerColor: Int) = apply {
        properties.centerColor = centerColor
        useCenterColor(true)
    }

    fun endColor(endColor: Int) = apply { properties.endColor = endColor }
    fun gradientColors(startColor: Int, endColor: Int, centerColor: Int?) = apply {
        startColor(startColor); endColor(endColor)
        useCenterColor(centerColor != null)
        centerColor?.let {
            centerColor(it)
        }
    }

    fun gradientRadiusType(gradientRadiusType: Int) =
        apply { properties.gradientRadiusType = gradientRadiusType }

    fun gradientRadius(gradientRadius: Float) = apply { properties.gradientRadius = gradientRadius }
    fun gradientRadius(radius: Float, type: Int) =
        apply { gradientRadius(radius); gradientRadiusType(type) }

    fun gradientRadiusInPixel(radius: Float) =
        apply { gradientRadius(radius); gradientRadiusType(DrawableProperties.RADIUS_TYPE_PIXELS) }

    fun gradientRadiusInFraction(radius: Float) =
        apply { gradientRadius(radius); gradientRadiusType(DrawableProperties.RADIUS_TYPE_FRACTION) }

    fun useLevelForGradient(use: Boolean) = apply { properties.useLevelForGradient = use }
    fun useLevelForGradient() = apply { useLevelForGradient(true) }

    // <size>
    fun width(width: Int) = apply { properties.width = width }

    fun height(height: Int) = apply { properties.height = height }
    fun size(width: Int, height: Int) = apply { width(width); height(height) }
    fun size(size: Int) = apply { width(size).height(size) }

    // <solid>
    fun solidColor(solidColor: Int) = apply { properties.solidColor = solidColor }

    private var solidColorPressed: Int? = null
    fun solidColorPressed(color: Int?) = apply { solidColorPressed = color }
    private var solidColorPressedWhenRippleUnsupported: Int? = null
    fun solidColorPressedWhenRippleUnsupported(color: Int?) =
        apply { solidColorPressedWhenRippleUnsupported = color }

    private var solidColorDisabled: Int? = null
    fun solidColorDisabled(color: Int?) = apply { solidColorDisabled = color }
    private var solidColorSelected: Int? = null
    fun solidColorSelected(color: Int?) = apply { solidColorSelected = color }
    fun solidColorStateList(colorStateList: ColorStateList) =
        apply { properties.solidColorStateList = colorStateList }

    // <stroke>
    fun strokeWidth(strokeWidth: Int) = apply { properties.strokeWidth = strokeWidth }

    fun strokeColor(strokeColor: Int) = apply { properties.strokeColor = strokeColor }
    private var strokeColorPressed: Int? = null
    fun strokeColorPressed(color: Int?) = apply { strokeColorPressed = color }
    private var strokeColorDisabled: Int? = null
    fun strokeColorDisabled(color: Int?) = apply { strokeColorDisabled = color }
    private var strokeColorSelected: Int? = null
    fun strokeColorSelected(color: Int?) = apply { strokeColorSelected = color }
    fun strokeColorStateList(colorStateList: ColorStateList) =
        apply { properties.strokeColorStateList = colorStateList }

    fun dashWidth(dashWidth: Int) = apply { properties.dashWidth = dashWidth }
    fun dashGap(dashGap: Int) = apply { properties.dashGap = dashGap }
    fun hairlineBordered() = apply { strokeWidth(1) }
    fun shortDashed() = apply { dashWidth(12).dashGap(12) }
    fun mediumDashed() = apply { dashWidth(24).dashGap(24) }
    fun longDashed() = apply { dashWidth(36).dashGap(36) }
    fun dashed() = apply { mediumDashed() }

    // <rotate>
    private var rotateOrder = 0


    fun rotate(boolean: Boolean = true) = apply {
        properties.useRotate = boolean
        rotateOrder = if (boolean) {
            order.getAndIncrement()
        } else {
            0
        }
    }

    fun pivotX(pivotX: Float) = apply { properties.pivotX = pivotX }
    fun pivotY(pivotY: Float) = apply { properties.pivotY = pivotY }
    fun pivot(pivotX: Float, pivotY: Float) = apply { pivotX(pivotX).pivotY(pivotY) }
    fun fromDegrees(degrees: Float) = apply { properties.fromDegrees = degrees }
    fun toDegrees(degrees: Float) = apply { properties.toDegrees = degrees }
    fun degrees(fromDegrees: Float, toDegrees: Float) =
        apply { fromDegrees(fromDegrees).toDegrees(toDegrees) }

    fun degrees(degrees: Float) = apply { fromDegrees(degrees).toDegrees(degrees) }
    fun rotate(fromDegrees: Float, toDegrees: Float) =
        apply { rotate().fromDegrees(fromDegrees).toDegrees(toDegrees) }

    fun rotate(degrees: Float) = apply { rotate().degrees(degrees) }

    // <scale>
    private var scaleOrder = 0


    fun scale(boolean: Boolean = true) = apply {
        properties.useScale = boolean
        scaleOrder = if (boolean) {
            order.getAndIncrement()
        } else {
            0
        }
    }

    fun scaleLevel(level: Int) = apply { properties.scaleLevel = level }
    fun scaleGravity(gravity: Int) = apply { properties.scaleGravity = gravity }
    fun scaleWidth(scale: Float) = apply { properties.scaleWidth = scale }
    fun scaleHeight(scale: Float) = apply { properties.scaleHeight = scale }
    fun scale(scale: Float) = apply { scale().scaleWidth(scale).scaleHeight(scale) }
    fun scale(scaleWidth: Float, scaleHeight: Float) =
        apply { scale().scaleWidth(scaleWidth).scaleHeight(scaleHeight) }

    // flip

    fun flip(boolean: Boolean = true) = apply { properties.useFlip = boolean }

    fun orientation(orientation: Int) = apply { properties.orientation = orientation }
    fun flipVertical() = apply { flip().orientation(FlipDrawable.ORIENTATION_VERTICAL) }

    // <ripple>

    fun ripple(boolean: Boolean = true) = apply { properties.useRipple = boolean }

    fun rippleColor(color: Int) = apply { properties.rippleColor = color }
    fun rippleColorStateList(colorStateList: ColorStateList) =
        apply { properties.rippleColorStateList = colorStateList }

    fun rippleRadius(radius: Int) = apply { properties.rippleRadius = radius }

    fun build(): Drawable {
        if (baseDrawable != null) {
            return wrap(baseDrawable!!)
        }

        var drawable: Drawable

        // fall back when ripple is unavailable on devices with API < 21
        if (shouldFallbackRipple()) {
            if (solidColorPressedWhenRippleUnsupported != null) {
                solidColorPressed(solidColorPressedWhenRippleUnsupported)
            } else {
                solidColorPressed(properties.rippleColor)
            }
        }

        if (needStateListDrawable()) {
            drawable = StateListDrawableBuilder()
                .pressed(buildPressedDrawable())
                .disabled(buildDisabledDrawable())
                .selected(buildSelectedDrawable())
                .normal(buildNormalDrawable())
                .build()
        } else {
            drawable = GradientDrawable()
            setupGradientDrawable(drawable)
        }
        drawable = wrap(drawable)
        return drawable
    }

    private fun getSolidColorStateList(): ColorStateList {
        if (properties.solidColorStateList != null) {
            return properties.solidColorStateList!!
        }

        val states = mutableListOf<IntArray>()
        val colors = mutableListOf<Int>()

        solidColorPressed?.let {
            states.add(intArrayOf(android.R.attr.state_pressed))
            colors.add(it)
        }
        solidColorDisabled?.let {
            states.add(intArrayOf(-android.R.attr.state_enabled))
            colors.add(it)
        }
        solidColorSelected?.let {
            states.add(intArrayOf(android.R.attr.state_selected))
            colors.add(it)
        }
        states.add(StateSet.WILD_CARD)
        colors.add(properties.solidColor)

        return ColorStateList(states.toTypedArray(), colors.toIntArray())
    }

    private fun getStrokeColorStateList(): ColorStateList {
        if (properties.strokeColorStateList != null) {
            return properties.strokeColorStateList!!
        }

        val states = mutableListOf<IntArray>()
        val colors = mutableListOf<Int>()

        strokeColorPressed?.let {
            states.add(intArrayOf(android.R.attr.state_pressed))
            colors.add(it)
        }
        strokeColorDisabled?.let {
            states.add(intArrayOf(-android.R.attr.state_enabled))
            colors.add(it)
        }
        strokeColorSelected?.let {
            states.add(intArrayOf(android.R.attr.state_selected))
            colors.add(it)
        }
        states.add(StateSet.WILD_CARD)
        colors.add(properties.strokeColor)

        return ColorStateList(states.toTypedArray(), colors.toIntArray())
    }

    private fun buildPressedDrawable(): Drawable? {
        if (solidColorPressed == null && strokeColorPressed == null) return null

        val pressedDrawable = GradientDrawable()
        setupGradientDrawable(pressedDrawable)
        solidColorPressed?.let {
            pressedDrawable.setColor(it)
        }
        strokeColorPressed?.let {
            setStrokeColor(pressedDrawable, it)
        }
        return pressedDrawable
    }

    private fun buildDisabledDrawable(): Drawable? {
        if (solidColorDisabled == null && strokeColorDisabled == null) return null

        val disabledDrawable = GradientDrawable()
        setupGradientDrawable(disabledDrawable)
        solidColorDisabled?.let {
            disabledDrawable.setColor(it)
        }
        strokeColorDisabled?.let {
            setStrokeColor(disabledDrawable, it)
        }
        return disabledDrawable
    }

    private fun buildSelectedDrawable(): Drawable? {
        if (solidColorSelected == null && strokeColorSelected == null) return null

        val selectedDrawable = GradientDrawable()
        setupGradientDrawable(selectedDrawable)
        solidColorSelected?.let {
            selectedDrawable.setColor(it)
        }
        strokeColorSelected?.let {
            setStrokeColor(selectedDrawable, it)
        }
        return selectedDrawable
    }

    private fun buildNormalDrawable(): Drawable {
        val pressedDrawable = GradientDrawable()
        setupGradientDrawable(pressedDrawable)
        return pressedDrawable
    }

    private fun setupGradientDrawable(drawable: GradientDrawable) {
        properties.apply {
            drawable.shape = shape
            if (shape == GradientDrawable.RING) {
                setInnerRadius(drawable, innerRadius)
                setInnerRadiusRatio(drawable, innerRadiusRatio)
                setThickness(drawable, thickness)
                setThicknessRatio(drawable, thicknessRatio)
                setUseLevelForShape(drawable, useLevelForRing)
            }
            drawable.cornerRadii = getCornerRadii()
            if (useGradient) {
                drawable.gradientType = type
                setGradientRadiusType(drawable, gradientRadiusType)
                setGradientRadius(drawable, gradientRadius)
                drawable.setGradientCenter(centerX, centerY)
                setOrientation(drawable, getOrientation())
                setColors(drawable, getColors())
                drawable.useLevel = useLevelForGradient
            } else {
                drawable.color = getSolidColorStateList()
            }
            drawable.setSize(width, height)
            drawable.setStroke(
                strokeWidth,
                getStrokeColorStateList(),
                dashWidth.toFloat(),
                dashGap.toFloat()
            )
        }
    }

    private fun needStateListDrawable(): Boolean {
        return (hasStrokeColorStateList() || (!properties.useGradient && hasSolidColorStateList()))
    }

    private fun needRotateDrawable(): Boolean {
        return properties.useRotate &&
                !(properties.pivotX == 0.5f && properties.pivotY == 0.5f
                        && properties.fromDegrees == 0f && properties.toDegrees == 0f)
    }

    private fun needScaleDrawable(): Boolean {
        return properties.useScale
    }

    private fun wrap(drawable: Drawable): Drawable {
        var wrappedDrawable = drawable

        if (rotateOrder > 0) {
            transformsMap[rotateOrder] = ::wrapRotateIfNeeded
        }
        if (scaleOrder > 0) {
            transformsMap[scaleOrder] = ::wrapScaleIfNeeded
        }

        for (action in transformsMap.values) {
            wrappedDrawable = action.invoke(wrappedDrawable)
        }

        if (properties.useFlip) {
            wrappedDrawable = FlipDrawableBuilder()
                .drawable(wrappedDrawable)
                .orientation(properties.orientation)
                .build()
        }

        if (isRippleSupported() && properties.useRipple) {
            wrappedDrawable = RippleDrawableBuilder()
                .drawable(wrappedDrawable)
                .color(properties.rippleColor)
                .colorStateList(properties.rippleColorStateList)
                .radius(properties.rippleRadius)
                .build()
        }

        return wrappedDrawable
    }

    private fun shouldFallbackRipple(): Boolean {
        return properties.useRipple && !isRippleSupported()
    }

    private fun isRippleSupported() = true

    private fun wrapRotateIfNeeded(drawable: Drawable): Drawable {
        if (!needRotateDrawable()) return drawable

        with(properties) {
            return RotateDrawableBuilder()
                .drawable(drawable)
                .pivotX(pivotX)
                .pivotY(pivotY)
                .fromDegrees(fromDegrees)
                .toDegrees(toDegrees)
                .build()
        }
    }

    private fun wrapScaleIfNeeded(drawable: Drawable): Drawable {
        if (!needScaleDrawable()) return drawable

        with(properties) {
            return ScaleDrawableBuilder()
                .drawable(drawable)
                .level(scaleLevel)
                .scaleGravity(scaleGravity)
                .scaleWidth(scaleWidth)
                .scaleHeight(scaleHeight)
                .build()
        }
    }

    private fun hasSolidColorStateList(): Boolean {
        return solidColorPressed != null || solidColorDisabled != null || solidColorSelected != null
    }

    private fun hasStrokeColorStateList(): Boolean {
        return strokeColorPressed != null || strokeColorDisabled != null || strokeColorSelected != null
    }
}