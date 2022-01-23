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
 * This file is Created by fankes on 2021/9/4.
 */
@file:Suppress("DEPRECATION", "SameParameterValue")

package com.fankes.tsbattery.hook

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.Keep
import com.fankes.tsbattery.hook.HookMedium.QQ_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookMedium.SELF_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookMedium.TIM_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookMedium.WECHAT_PACKAGE_NAME
import com.fankes.tsbattery.utils.XPrefUtils
import com.fankes.tsbattery.utils.showDialog
import com.fankes.tsbattery.utils.versionCode
import com.fankes.tsbattery.utils.versionName
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.*

@Keep
class HookMain : IXposedHookLoadPackage {

    companion object {

        private const val BASE_CHAT_PIE = "activity.aio.core.BaseChatPie"
    }

    /** 仅作用于替换的 Hook 方法体 */
    private val replaceToNull = object : XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam?): Any? {
            return null
        }
    }

    /** 仅作用于替换的 Hook 方法体 */
    private val replaceToTrue = object : XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam?): Any {
            return true
        }
    }

    /**
     * 干掉目标方法体封装
     * @param clazz 类名缩写
     * @param name 方法名
     */
    private fun XC_LoadPackage.LoadPackageParam.replaceToNull(clazz: String, name: String) {
        XposedHelpers.findAndHookMethod(
            "$QQ_PACKAGE_NAME.$clazz",
            classLoader,
            name,
            replaceToNull
        )
    }

    /**
     * 忽略异常运行
     * @param it 正常回调
     */
    private fun runWithoutError(error: String, it: () -> Unit) {
        try {
            it()
        } catch (e: Error) {
            logE("hookFailed: $error", e)
        } catch (e: Exception) {
            logE("hookFailed: $error", e)
        } catch (e: Throwable) {
            logE("hookFailed: $error", e)
        }
    }

    /**
     * 这个类 QQ 的 BaseChatPie 是控制聊天界面的
     * 里面有两个随机混淆的方法 ⬇️
     * remainScreenOn、cancelRemainScreenOn
     * 这两个方法一个是挂起电源锁常驻亮屏
     * 一个是停止常驻亮屏
     * 不由分说每个版本混淆的方法名都会变
     * 所以说每个版本重新适配 - 也可以提交分支帮我适配
     * ⚠️ Hook 错了方法会造成闪退！
     * @param version QQ 版本
     */
    private fun XC_LoadPackage.LoadPackageParam.hookQQBaseChatPie(version: String) {
        when (version) {
            "8.8.17" -> {
                replaceToNull(BASE_CHAT_PIE, "bd")
                replaceToNull(BASE_CHAT_PIE, "be")
            }
            "8.8.23" -> {
                replaceToNull(BASE_CHAT_PIE, "bf")
                replaceToNull(BASE_CHAT_PIE, "bg")
            }
            /** 8.8.35 贡献者：StarWishsama */
            "8.8.35", "8.8.38" -> {
                replaceToNull(BASE_CHAT_PIE, "bi")
                replaceToNull(BASE_CHAT_PIE, "bj")
            }
            /** 贡献者：JiZhi-Error */
            "8.8.50" -> {
                replaceToNull(BASE_CHAT_PIE, "bj")
                replaceToNull(BASE_CHAT_PIE, "bk")
            }
            "8.8.55" -> {
                replaceToNull(BASE_CHAT_PIE, "bk")
                replaceToNull(BASE_CHAT_PIE, "bl")
            }
            else -> logD("$version not supported!")
        }
    }

    /**
     * Print the log
     * @param content
     */
    private fun logD(content: String) {
        XposedBridge.log("[TSBattery][D]>$content")
        Log.d("TSBattery", content)
    }

    /**
     * Print the log
     * @param content
     */
    private fun logE(content: String, e: Throwable? = null) {
        XposedBridge.log("[TSBattery][E]>$content")
        XposedBridge.log(e)
        Log.e("TSBattery", content, e)
    }

    /** Hook 系统电源锁 */
    private fun XC_LoadPackage.LoadPackageParam.hookSystemWakeLock() {
        runWithoutError("wakeLock acquire()") {
            XposedHelpers.findAndHookMethod(
                "android.os.PowerManager\$WakeLock",
                classLoader,
                "acquire",
                replaceToNull
            )
        }
        runWithoutError("hook wakeLock acquire(time)") {
            XposedHelpers.findAndHookMethod(
                "android.os.PowerManager\$WakeLock",
                classLoader,
                "acquire",
                Long::class.java,
                replaceToNull
            )
        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam == null) return
        when (lpparam.packageName) {
            /** Hook 自身 */
            SELF_PACKAGE_NAME ->
                XposedHelpers.findAndHookMethod(
                    "$SELF_PACKAGE_NAME.hook.HookMedium",
                    lpparam.classLoader,
                    "isHooked",
                    replaceToTrue
                )
            /** 经过测试 QQ 与 TIM 这两个是一个模子里面的东西，所以他们的类名也基本上是一样的 */
            QQ_PACKAGE_NAME, TIM_PACKAGE_NAME -> {
                lpparam.hookSystemWakeLock()
                /** 增加通知栏文本显示守护状态 */
                runWithoutError("Notification") {
                    XposedHelpers.findAndHookMethod(
                        "android.app.Notification\$Builder",
                        lpparam.classLoader,
                        "setContentText",
                        CharSequence::class.java,
                        object : XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam?) {
                                when (param?.args?.get(0) as? CharSequence?) {
                                    "QQ正在后台运行" ->
                                        param.args?.set(0, "QQ正在后台运行 - TSBattery 守护中")
                                    "TIM正在后台运行" ->
                                        param.args?.set(0, "TIM正在后台运行 - TSBattery 守护中")
                                }
                            }
                        })
                }
                /** 判断是否开启提示模块运行信息 */
                if (XPrefUtils.getBoolean(HookMedium.ENABLE_RUN_INFO))
                    runWithoutError("SplashActivity") {
                        /**
                         * Hook 启动界面的第一个 [Activity]
                         * QQ 和 TIM 都是一样的类
                         * 在里面加入提示运行信息的对话框测试模块是否激活
                         */
                        XposedHelpers.findAndHookMethod(
                            "$QQ_PACKAGE_NAME.activity.SplashActivity",
                            lpparam.classLoader,
                            "doOnCreate",
                            Bundle::class.java,
                            object : XC_MethodHook() {

                                override fun afterHookedMethod(param: MethodHookParam?) {
                                    (param?.thisObject as? Activity?)?.apply {
                                        showDialog {
                                            title = "TSBattery 已激活"
                                            msg = "[提示模块运行信息功能已打开]\n\n" +
                                                    "模块工作看起来一切正常，请自行测试是否能达到省电效果。\n\n" +
                                                    "已生效模块版本：${XPrefUtils.getString(HookMedium.ENABLE_MODULE_VERSION)}\n" +
                                                    "当前模式：${if (XPrefUtils.getBoolean(HookMedium.ENABLE_WHITE_MODE)) "保守模式" else "完全模式"}" +
                                                    "\n\n包名：${packageName}\n版本：$versionName($versionCode)" +
                                                    "\n\n模块只对挂后台锁屏情况下有省电效果，请不要将过多的群提醒，消息通知打开，这样子在使用过程时照样会极其耗电。\n\n" +
                                                    "如果你不想看到此提示。请在模块设置中关闭“提示模块运行信息”，此设置默认关闭。\n\n" +
                                                    "持续常驻使用 QQ 依然会耗电，任何软件都是如此，模块无法帮你做到前台不耗电，永远记住这一点。\n\n" +
                                                    "开发者 酷安 @星夜不荟\n未经允许禁止转载、修改或复制我的劳动成果。"
                                            confirmButton("我知道了")
                                            noCancelable()
                                        }
                                    }
                                }
                            })
                    }
                /** 关闭保守模式后不再仅仅作用于系统电源锁 */
                if (!XPrefUtils.getBoolean(HookMedium.ENABLE_WHITE_MODE)) {
                    runWithoutError("BaseChatPie(first time)") {
                        /** 通过在 SplashActivity 里取到应用的版本号 */
                        XposedHelpers.findAndHookMethod(
                            "$QQ_PACKAGE_NAME.activity.SplashActivity",
                            lpparam.classLoader,
                            "doOnCreate",
                            Bundle::class.java,
                            object : XC_MethodHook() {

                                override fun beforeHookedMethod(param: MethodHookParam?) {
                                    val self = param?.thisObject as? Activity ?: return
                                    val name = self.packageName
                                    val version = self.versionName
                                    /** 这个地方我们只处理 QQ */
                                    runWithoutError("BaseChatPie") {
                                        if (name == QQ_PACKAGE_NAME)
                                            lpparam.hookQQBaseChatPie(version)
                                    }
                                }
                            })
                    }
                    runWithoutError("WakerLock") {
                        /**
                         * 一个不知道是什么作用的电源锁
                         * 同样直接干掉
                         */
                        XposedHelpers.findAndHookMethod(
                            "com.tencent.mars.ilink.comm.WakerLock",
                            lpparam.classLoader,
                            "lock", Long::class.java,
                            replaceToNull
                        )
                    }
                    runWithoutError("QQLSActivity") {
                        /**
                         * Hook 掉一个一像素保活 [Activity] 真的我怎么都想不到讯哥的程序员做出这种事情
                         * 这个东西经过测试会在锁屏的时候吊起来，解锁的时候自动 finish()，无限耍流氓耗电
                         */
                        XposedHelpers.findAndHookMethod(
                            "$QQ_PACKAGE_NAME.activity.QQLSUnlockActivity",
                            lpparam.classLoader,
                            "onCreate", Bundle::class.java,
                            object : XC_MethodHook() {

                                private var origDevice = ""

                                override fun beforeHookedMethod(param: MethodHookParam?) {
                                    /** 由于在 onCreate 里有一行判断只要型号是 xiaomi 的设备就开电源锁，所以说这里临时替换成菊花厂 */
                                    origDevice = Build.MANUFACTURER
                                    if (Build.MANUFACTURER.toLowerCase(Locale.ROOT) == "xiaomi")
                                        XposedHelpers.setStaticObjectField(
                                            Build::class.java,
                                            "MANUFACTURER",
                                            "HUAWEI"
                                        )
                                }

                                override fun afterHookedMethod(param: MethodHookParam?) {
                                    (param?.thisObject as? Activity)?.finish()
                                    /** 这里再把型号替换回去 - 不影响应用变量等 Xposed 模块修改的型号 */
                                    XposedHelpers.setStaticObjectField(
                                        Build::class.java,
                                        "MANUFACTURER",
                                        origDevice
                                    )
                                }
                            }
                        )
                        /**
                         * 这个东西同上，不知道是啥时候调用
                         * 反正也是一个一像素保活的 [Activity]
                         * 讯哥的程序员真的有你的
                         */
                        XposedHelpers.findAndHookMethod(
                            "$QQ_PACKAGE_NAME.activity.QQLSActivity\$14",
                            lpparam.classLoader,
                            "run",
                            replaceToNull
                        )
                    }
                    runWithoutError("WakerLockMonitor") {
                        /**
                         * 这个是毒瘤核心类
                         * WakeLockMonitor
                         * 这个名字真的起的特别诗情画意
                         * 带给用户的却是 shit 一样的体验
                         * 里面有各种使用 Handler 和 Timer 的各种耗时常驻后台耗电办法持续接收消息
                         * 直接循环全部方法全部干掉
                         */
                        lpparam.classLoader.loadClass("com.tencent.qapmsdk.qqbattery.monitor.WakeLockMonitor")
                            .apply {
                                val lockClazz =
                                    lpparam.classLoader.loadClass("com.tencent.qapmsdk.qqbattery.monitor.WakeLockMonitor\$WakeLockEntity")
                                val hookClazz =
                                    lpparam.classLoader.loadClass("com.tencent.qapmsdk.qqbattery.monitor.MethodHookParam")
                                val onHook = getDeclaredMethod(
                                    "onHook",
                                    String::class.java,
                                    Any::class.java,
                                    java.lang.reflect.Array.newInstance(
                                        Any::class.java,
                                        0
                                    ).javaClass,
                                    Any::class.java
                                ).apply { isAccessible = true }
                                val doReport =
                                    getDeclaredMethod(
                                        "doReport",
                                        lockClazz,
                                        Int::class.java
                                    ).apply {
                                        isAccessible = true
                                    }
                                val afterHookedMethod =
                                    getDeclaredMethod(
                                        "afterHookedMethod",
                                        hookClazz
                                    ).apply { isAccessible = true }
                                val beforeHookedMethod =
                                    getDeclaredMethod("beforeHookedMethod", hookClazz).apply {
                                        isAccessible = true
                                    }
                                val onAppBackground =
                                    getDeclaredMethod("onAppBackground").apply {
                                        isAccessible = true
                                    }
                                val onOtherProcReport =
                                    getDeclaredMethod(
                                        "onOtherProcReport",
                                        Bundle::class.java
                                    ).apply { isAccessible = true }
                                val onProcessRun30Min =
                                    getDeclaredMethod("onProcessRun30Min").apply {
                                        isAccessible = true
                                    }
                                val onProcessBG5Min =
                                    getDeclaredMethod("onProcessBG5Min").apply {
                                        isAccessible = true
                                    }
                                val writeReport =
                                    getDeclaredMethod(
                                        "writeReport",
                                        Boolean::class.java
                                    ).apply { isAccessible = true }
                                XposedBridge.hookMethod(onHook, replaceToNull)
                                XposedBridge.hookMethod(doReport, replaceToNull)
                                XposedBridge.hookMethod(afterHookedMethod, replaceToNull)
                                XposedBridge.hookMethod(beforeHookedMethod, replaceToNull)
                                XposedBridge.hookMethod(onAppBackground, replaceToNull)
                                XposedBridge.hookMethod(onOtherProcReport, replaceToNull)
                                XposedBridge.hookMethod(onProcessRun30Min, replaceToNull)
                                XposedBridge.hookMethod(onProcessBG5Min, replaceToNull)
                                XposedBridge.hookMethod(writeReport, replaceToNull)
                            }
                    }
                    logD("hook Completed!")
                }
            }
            /** 微信 */
            WECHAT_PACKAGE_NAME -> {
                lpparam.hookSystemWakeLock()
                /** 判断是否开启提示模块运行信息 */
                if (XPrefUtils.getBoolean(HookMedium.ENABLE_RUN_INFO))
                    runWithoutError("LauncherUI") {
                        /**
                         * Hook 启动界面的第一个 [Activity]
                         * 在里面加入提示运行信息的对话框测试模块是否激活
                         */
                        XposedHelpers.findAndHookMethod(
                            "$WECHAT_PACKAGE_NAME.ui.LauncherUI",
                            lpparam.classLoader,
                            "onCreate",
                            Bundle::class.java,
                            object : XC_MethodHook() {

                                override fun afterHookedMethod(param: MethodHookParam?) {
                                    (param?.thisObject as? Activity?)?.apply {
                                        showDialog {
                                            title = "TSBattery 已激活"
                                            msg = "[提示模块运行信息功能已打开]\n\n" +
                                                    "模块工作看起来一切正常，请自行测试是否能达到省电效果。\n\n" +
                                                    "已生效模块版本：${XPrefUtils.getString(HookMedium.ENABLE_MODULE_VERSION)}\n" +
                                                    "当前模式：基础省电" +
                                                    "\n\n包名：${packageName}\n版本：$versionName($versionCode)" +
                                                    "\n\n当前只支持微信的基础省电，即系统电源锁，后续会继续适配微信相关的省电功能(在新建文件夹了)。\n\n" +
                                                    "如果你不想看到此提示。请在模块设置中关闭“提示模块运行信息”，此设置默认关闭。\n\n" +
                                                    "持续常驻使用微信依然会耗电，任何软件都是如此，模块无法帮你做到前台不耗电，永远记住这一点。\n\n" +
                                                    "开发者 酷安 @星夜不荟\n未经允许禁止转载、修改或复制我的劳动成果。"
                                            confirmButton("我知道了")
                                            noCancelable()
                                        }
                                    }
                                }
                            })
                    }
                // TODO 新建文件夹
                logD("それが機能するかどうかはわかりません")
            }
        }
    }
}