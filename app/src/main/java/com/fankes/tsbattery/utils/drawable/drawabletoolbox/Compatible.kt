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
@file:Suppress("SameParameterValue")

package com.fankes.tsbattery.utils.drawable.drawabletoolbox

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.RotateDrawable
import android.os.Build
import java.lang.reflect.Field
import java.lang.reflect.Method

private val gradientState = resolveGradientState()

private fun resolveGradientState(): Class<*> {
    val classes = GradientDrawable::class.java.declaredClasses
    for (singleClass in classes) {
        if (singleClass.simpleName == "GradientState") return singleClass
    }
    throw RuntimeException("GradientState could not be found in thisAny GradientDrawable implementation")
}

private val rotateState = resolveRotateState()

private fun resolveRotateState(): Class<*> {
    val classes = RotateDrawable::class.java.declaredClasses
    for (singleClass in classes) {
        if (singleClass.simpleName == "RotateState") return singleClass
    }
    throw RuntimeException("RotateState could not be found in thisAny RotateDrawable implementation")
}

@Throws(SecurityException::class, NoSuchFieldException::class)
private fun resolveField(source: Class<*>, fieldName: String): Field {
    val field = source.getDeclaredField(fieldName)
    field.isAccessible = true
    return field
}

@Throws(SecurityException::class, NoSuchMethodException::class)
private fun resolveMethod(
    source: Class<*>,
    methodName: String,
    vararg parameterTypes: Class<*>
): Method {
    val method = source.getDeclaredMethod(methodName, *parameterTypes)
    method.isAccessible = true
    return method
}

fun setInnerRadius(drawable: GradientDrawable, value: Int) {
    try {
        val innerRadius = resolveField(gradientState, "mInnerRadius")
        innerRadius.setInt(drawable.constantState, value)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}

fun setInnerRadiusRatio(drawable: GradientDrawable, value: Float) {
    try {
        val innerRadius = resolveField(gradientState, "mInnerRadiusRatio")
        innerRadius.setFloat(drawable.constantState, value)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}

fun setThickness(drawable: GradientDrawable, value: Int) {
    try {
        val innerRadius = resolveField(gradientState, "mThickness")
        innerRadius.setInt(drawable.constantState, value)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}

fun setThicknessRatio(drawable: GradientDrawable, value: Float) {
    try {
        val innerRadius = resolveField(gradientState, "mThicknessRatio")
        innerRadius.setFloat(drawable.constantState, value)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}

fun setUseLevelForShape(drawable: GradientDrawable, value: Boolean) {
    try {
        val useLevelForShape = resolveField(gradientState, "mUseLevelForShape")
        useLevelForShape.setBoolean(drawable.constantState, value)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}

@SuppressLint("ObsoleteSdkInt")
fun setOrientation(drawable: GradientDrawable, value: GradientDrawable.Orientation) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        drawable.orientation = value
    } else {
        try {
            val orientation = resolveField(gradientState, "mOrientation")
            orientation.set(drawable.constantState, value)
            val rectIdDirty = resolveField(GradientDrawable::class.java, "mRectIsDirty")
            rectIdDirty.setBoolean(drawable, true)
            drawable.invalidateSelf()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}

@SuppressLint("ObsoleteSdkInt")
fun setColors(drawable: GradientDrawable, value: IntArray) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        drawable.colors = value
    } else {
        try {
            val colors = resolveField(gradientState, "mColors")
            colors.set(drawable.constantState, value)
            drawable.invalidateSelf()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}

fun setGradientRadiusType(drawable: GradientDrawable, value: Int) {
    try {
        val type = resolveField(gradientState, "mGradientRadiusType")
        type.setInt(drawable.constantState, value)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}

fun setGradientRadius(drawable: GradientDrawable, value: Float) {
    try {
        val gradientRadius = resolveField(gradientState, "mGradientRadius")
        gradientRadius.setFloat(drawable.constantState, value)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}

fun setStrokeColor(drawable: GradientDrawable, value: Int) {
    try {
        val type = resolveField(gradientState, "mStrokeColor")
        type.setInt(drawable.constantState, value)
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}

fun setDrawable(rotateDrawable: RotateDrawable, drawable: Drawable) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        rotateDrawable.drawable = drawable
    } else {
        try {
            val drawableField = resolveField(rotateState, "mDrawable")
            val stateField = resolveField(RotateDrawable::class.java, "mState")
            drawableField.set(stateField.get(rotateDrawable), drawable)
            drawable.callback = rotateDrawable
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}

fun setPivotX(rotateDrawable: RotateDrawable, value: Float) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        rotateDrawable.pivotX = value
    } else {
        try {
            val pivotXField = resolveField(rotateState, "mPivotX")
            pivotXField.setFloat(rotateDrawable.constantState, value)
            val pivotXRelField = resolveField(rotateState, "mPivotXRel")
            pivotXRelField.setBoolean(rotateDrawable.constantState, true)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}

fun setPivotY(rotateDrawable: RotateDrawable, value: Float) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        rotateDrawable.pivotY = value
    } else {
        try {
            val pivotYField = resolveField(rotateState, "mPivotY")
            pivotYField.setFloat(rotateDrawable.constantState, value)
            val pivotYRelField = resolveField(rotateState, "mPivotYRel")
            pivotYRelField.setBoolean(rotateDrawable.constantState, true)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}

fun setFromDegrees(rotateDrawable: RotateDrawable, value: Float) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        rotateDrawable.fromDegrees = value
    } else {
        try {
            val fromDegreesField = resolveField(rotateState, "mFromDegrees")
            fromDegreesField.setFloat(rotateDrawable.constantState, value)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}

fun setToDegrees(rotateDrawable: RotateDrawable, value: Float) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        rotateDrawable.toDegrees = value
    } else {
        try {
            val toDegreesField = resolveField(rotateState, "mToDegrees")
            toDegreesField.setFloat(rotateDrawable.constantState, value)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}

fun setRadius(rippleDrawable: RippleDrawable, value: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        rippleDrawable.radius = value
    } else {
        try {
            val setRadiusMethod =
                resolveMethod(RippleDrawable::class.java, "setMaxRadius", Int::class.java)
            setRadiusMethod.invoke(rippleDrawable, value)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}