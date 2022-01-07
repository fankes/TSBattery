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
 * This file is Created by fankes on 2021/11/9.
 */

package com.fankes.tsbattery.hook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import com.fankes.tsbattery.ui.MainActivity

@Keep
object HookMedium {

    const val ENABLE_HIDE_ICON = "_hide_icon"
    const val ENABLE_RUN_INFO = "_tip_run_info"
    const val ENABLE_WHITE_MODE = "_white_mode"
    const val ENABLE_MODULE_VERSION = "_module_version"

    const val SELF_PACKAGE_NAME = "com.fankes.tsbattery"
    const val QQ_PACKAGE_NAME = "com.tencent.mobileqq"
    const val TIM_PACKAGE_NAME = "com.tencent.tim"
    const val WECHAT_PACKAGE_NAME = "com.tencent.mm"

    /**
     * 判断模块是否激活
     * 在 [HookMain] 中 Hook 掉此方法
     * @return 激活状态
     */
    fun isHooked(): Boolean {
        Log.d("TSBattery", "isHooked: true")
        return isExpModuleActive()
    }

    /**
     * 新增太极判断方式
     * @return 是否激活
     */
    private fun isExpModuleActive(): Boolean {
        var isExp = false
        MainActivity.instance?.also {
            try {
                val uri = Uri.parse("content://me.weishu.exposed.CP/")
                var result: Bundle? = null
                try {
                    result = it.contentResolver.call(uri, "active", null, null)
                } catch (e: RuntimeException) {
                    // TaiChi is killed, try invoke
                    try {
                        val intent = Intent("me.weishu.exp.ACTION_ACTIVE")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        it.startActivity(intent)
                    } catch (e1: Throwable) {
                        return false
                    }
                }
                if (result == null) result = it.contentResolver.call(uri, "active", null, null)
                if (result == null) return false
                isExp = result.getBoolean("active", false)
            } catch (ignored: Throwable) {
            }
        }
        return isExp
    }
}