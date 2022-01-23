/**
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
 * This file is Created by fankes on 2022/1/7.
 */
@file:Suppress("unused")

package com.fankes.tsbattery.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable


/**
 * 构造对话框
 * @param it 对话框方法体
 */
fun Context.showDialog(it: DialogBuilder.() -> Unit) = DialogBuilder(this).apply(it).show()

/**
 * 对话框构造器
 * @param context 实例
 */
class DialogBuilder(private val context: Context) {

    private var instance: AlertDialog.Builder? = null // 实例对象

    init {
        instance = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog)
    }

    /** 设置对话框不可关闭 */
    fun noCancelable() = instance?.setCancelable(false)

    /** 设置对话框标题 */
    var title
        get() = ""
        set(value) {
            instance?.setTitle(value)
        }

    /** 设置对话框消息内容 */
    var msg
        get() = ""
        set(value) {
            instance?.setMessage(value)
        }

    /**
     * 设置对话框确定按钮
     * @param text 按钮文本内容
     * @param it 点击事件
     */
    fun confirmButton(text: String = "确定", it: () -> Unit = {}) =
        instance?.setPositiveButton(text) { _, _ -> it() }

    /**
     * 设置对话框取消按钮
     * @param text 按钮文本内容
     * @param it 点击事件
     */
    fun cancelButton(text: String = "取消", it: () -> Unit = {}) =
        instance?.setNegativeButton(text) { _, _ -> it() }

    /**
     * 设置对话框第三个按钮
     * @param text 按钮文本内容
     * @param it 点击事件
     */
    fun neutralButton(text: String = "更多", it: () -> Unit = {}) =
        instance?.setNeutralButton(text) { _, _ -> it() }

    /** 显示对话框 */
    internal fun show() = instance?.create()?.apply {
        window?.setBackgroundDrawable(GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.WHITE, Color.WHITE)
        ).apply {
            shape = GradientDrawable.RECTANGLE
            gradientType = GradientDrawable.LINEAR_GRADIENT
            cornerRadius = 15.dp(this@DialogBuilder.context)
        })
    }?.show()
}