/*
 * TSBattery - A new way to save your battery avoid cancer apps hacker it.
 * Copyright (C) 2017-2024 Fankes Studio(qzmmcn@163.com)
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
 * This file is created by fankes on 2022/1/7.
 */
@file:Suppress("unused")

package com.fankes.tsbattery.utils.factory

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.fankes.tsbattery.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.highcapable.yukihookapi.hook.factory.applyModuleTheme

/**
 * 构造对话框
 * @param initiate 对话框方法体
 */
inline fun Context.showDialog(initiate: DialogBuilder.() -> Unit) = DialogBuilder(context = this).apply(initiate).show()

/**
 * 对话框构造器
 * @param context 实例
 */
class DialogBuilder(val context: Context) {

    /** 实例对象 */
    private var instance: AlertDialog.Builder? = null

    /** 对话框实例 */
    private var dialogInstance: Dialog? = null

    /** 自定义布局 */
    private var customLayoutView: View? = null

    init {
        instance = MaterialAlertDialogBuilder(context.applyModuleTheme(R.style.Theme_TSBattery))
    }

    /** 设置对话框不可关闭 */
    fun noCancelable() {
        instance?.setCancelable(false)
    }

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

    /** 设置进度条对话框消息内容 */
    var progressContent
        get() = ""
        set(value) {
            if (customLayoutView == null)
                customLayoutView = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER or Gravity.START
                    addView(CircularProgressIndicator(context).apply {
                        isIndeterminate = true
                        trackCornerRadius = 10.dp(context)
                    })
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
     * 设置对话框确定按钮
     * @param text 按钮文本内容
     * @param callback 点击事件
     */
    fun confirmButton(text: String = "确定", callback: () -> Unit = {}) {
        instance?.setPositiveButton(text) { _, _ -> callback() }
    }

    /**
     * 设置对话框取消按钮
     * @param text 按钮文本内容
     * @param callback 点击事件
     */
    fun cancelButton(text: String = "取消", callback: () -> Unit = {}) {
        instance?.setNegativeButton(text) { _, _ -> callback() }
    }

    /**
     * 设置对话框第三个按钮
     * @param text 按钮文本内容
     * @param callback 点击事件
     */
    fun neutralButton(text: String = "更多", callback: () -> Unit = {}) {
        instance?.setNeutralButton(text) { _, _ -> callback() }
    }

    /** 取消对话框 */
    fun cancel() = dialogInstance?.cancel()

    /** 显示对话框 */
    fun show() = runInSafe {
        instance?.create()?.apply {
            customLayoutView?.let { setView(it) }
            dialogInstance = this
        }?.show()
    }
}