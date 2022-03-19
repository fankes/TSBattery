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
 * This file is Created by fankes on 2022/1/7.
 */
@file:Suppress("unused", "OPT_IN_USAGE", "EXPERIMENTAL_API_USAGE")

package com.fankes.tsbattery.utils.factory

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.yukihookapi.annotation.DoNotUseField
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.LayoutInflaterClass

/**
 * 构造对话框
 * @param isUseBlackTheme 是否使用深色主题
 * @param it 对话框方法体
 */
fun Context.showDialog(isUseBlackTheme: Boolean = false, it: DialogBuilder.() -> Unit) =
    DialogBuilder(this, isUseBlackTheme).apply(it).show()

/**
 * 对话框构造器
 * @param context 实例
 * @param isUseBlackTheme 是否使用深色主题 - 对 AndroidX 风格无效
 */
class DialogBuilder(val context: Context, private val isUseBlackTheme: Boolean) {

    private var instanceAndroidX: androidx.appcompat.app.AlertDialog.Builder? = null // 实例对象
    private var instanceAndroid: android.app.AlertDialog.Builder? = null // 实例对象

    private var dialogInstance: Dialog? = null // 对话框实例

    @DoNotUseField
    var customLayoutView: View? = null // 自定义布局

    /**
     * 是否需要使用 AndroidX 风格对话框
     * @return [Boolean]
     */
    private val isUsingAndroidX get() = runCatching { context is AppCompatActivity }.getOrNull() ?: false

    init {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX = MaterialAlertDialogBuilder(context) }
        else runCatching {
            instanceAndroid = android.app.AlertDialog.Builder(
                context,
                if (isUseBlackTheme) android.R.style.Theme_Material_Dialog else android.R.style.Theme_Material_Light_Dialog
            )
        }
    }

    /** 设置对话框不可关闭 */
    fun noCancelable() {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setCancelable(false) }
        else runCatching { instanceAndroid?.setCancelable(false) }
    }

    /** 设置对话框标题 */
    var title
        get() = ""
        set(value) {
            if (isUsingAndroidX)
                runCatching { instanceAndroidX?.setTitle(value) }
            else runCatching { instanceAndroid?.setTitle(value) }
        }

    /** 设置对话框消息内容 */
    var msg
        get() = ""
        set(value) {
            if (isUsingAndroidX)
                runCatching { instanceAndroidX?.setMessage(value) }
            else runCatching { instanceAndroid?.setMessage(value) }
        }

    /** 设置进度条对话框消息内容 */
    var progressContent
        get() = ""
        set(value) {
            if (customLayoutView == null)
                customLayoutView = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER or Gravity.START
                    addView(ProgressBar(context))
                    addView(View(context).apply { layoutParams = ViewGroup.LayoutParams(20.dp(context), 5) })
                    addView(TextView(context).apply {
                        tag = "progressContent"
                        text = value
                    })
                    setPadding(20.dp(context), 20.dp(context), 20.dp(context), 20.dp(context))
                }
            else customLayoutView?.findViewWithTag<TextView>("progressContent")?.text = value
        }

    /**
     * 设置对话框自定义布局
     * @return [ViewBinding]
     */
    inline fun <reified T : ViewBinding> bind() =
        T::class.java.method {
            name = "inflate"
            param(LayoutInflaterClass)
        }.get().invoke<T>(LayoutInflater.from(context))?.apply {
            customLayoutView = root
        } ?: error("binding failed")

    /**
     * 设置对话框确定按钮
     * @param text 按钮文本内容
     * @param it 点击事件
     */
    fun confirmButton(text: String = "确定", it: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setPositiveButton(text) { _, _ -> it() } }
        else runCatching { instanceAndroid?.setPositiveButton(text) { _, _ -> it() } }
    }

    /**
     * 设置对话框取消按钮
     * @param text 按钮文本内容
     * @param it 点击事件
     */
    fun cancelButton(text: String = "取消", it: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setNegativeButton(text) { _, _ -> it() } }
        else runCatching { instanceAndroid?.setNegativeButton(text) { _, _ -> it() } }
    }

    /**
     * 设置对话框第三个按钮
     * @param text 按钮文本内容
     * @param it 点击事件
     */
    fun neutralButton(text: String = "更多", it: () -> Unit = {}) {
        if (isUsingAndroidX)
            runCatching { instanceAndroidX?.setNeutralButton(text) { _, _ -> it() } }
        else runCatching { instanceAndroid?.setNeutralButton(text) { _, _ -> it() } }
    }

    /** 取消对话框 */
    fun cancel() = dialogInstance?.cancel()

    /** 显示对话框 */
    internal fun show() {
        if (isUsingAndroidX) runCatching {
            instanceAndroidX?.create()?.apply {
                customLayoutView?.let { setView(it) }
                dialogInstance = this
            }?.show()
        } else runCatching {
            instanceAndroid?.create()?.apply {
                customLayoutView?.let { setView(it) }
                window?.setBackgroundDrawable(
                    GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        if (isUseBlackTheme) intArrayOf(0xFF2D2D2D.toInt(), 0xFF2D2D2D.toInt())
                        else intArrayOf(Color.WHITE, Color.WHITE)
                    ).apply {
                        shape = GradientDrawable.RECTANGLE
                        gradientType = GradientDrawable.LINEAR_GRADIENT
                        cornerRadius = 15.dpFloat(this@DialogBuilder.context)
                    })
                dialogInstance = this
            }?.show()
        }
    }
}