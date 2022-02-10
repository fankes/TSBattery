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

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.StateSet

class StateListDrawableBuilder {

    private var pressed: Drawable? = null
    private var disabled: Drawable? = null
    private var selected: Drawable? = null
    private var normal: Drawable = ColorDrawable(Color.TRANSPARENT)

    fun pressed(pressed: Drawable?) = apply { this.pressed = pressed }
    fun disabled(disabled: Drawable?) = apply { this.disabled = disabled }
    fun selected(selected: Drawable?) = apply { this.selected = selected }
    fun normal(normal: Drawable) = apply { this.normal = normal }

    fun build(): StateListDrawable {
        val stateListDrawable = StateListDrawable()
        setupStateListDrawable(stateListDrawable)
        return stateListDrawable
    }

    private fun setupStateListDrawable(stateListDrawable: StateListDrawable) {
        pressed?.let {
            stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), it)
        }
        disabled?.let {
            stateListDrawable.addState(intArrayOf(-android.R.attr.state_enabled), it)
        }
        selected?.let {
            stateListDrawable.addState(intArrayOf(android.R.attr.state_selected), it)
        }
        stateListDrawable.addState(StateSet.WILD_CARD, normal)
    }
}