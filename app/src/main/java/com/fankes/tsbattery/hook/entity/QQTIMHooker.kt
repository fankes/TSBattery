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
 * This file is Created by fankes on 2022/9/29.
 */
package com.fankes.tsbattery.hook.entity

import android.app.Activity
import android.app.Service
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ServiceCompat
import androidx.fragment.app.Fragment
import com.fankes.tsbattery.BuildConfig
import com.fankes.tsbattery.const.PackageName
import com.fankes.tsbattery.data.ConfigData
import com.fankes.tsbattery.hook.HookEntry
import com.fankes.tsbattery.hook.factory.hookSystemWakeLock
import com.fankes.tsbattery.hook.factory.jumpToModuleSettings
import com.fankes.tsbattery.hook.factory.startModuleSettings
import com.fankes.tsbattery.utils.factory.appVersionName
import com.fankes.tsbattery.utils.factory.dp
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.*
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import com.highcapable.yukihookapi.hook.log.loggerW
import com.highcapable.yukihookapi.hook.type.android.*
import com.highcapable.yukihookapi.hook.type.java.*

/**
 * Hook QQ、TIM
 */
object QQTIMHooker : YukiBaseHooker() {

    /** QQ、TIM 存在的类 */
    const val JumpActivityClass = "${PackageName.QQ}.activity.JumpActivity"

    /** QQ、TIM 存在的类 */
    private const val QQSettingSettingActivityClass = "${PackageName.QQ}.activity.QQSettingSettingActivity"

    /** QQ 新版存在的类 (Pad 模式) */
    private const val QQSettingSettingFragmentClass = "${PackageName.QQ}.fragment.QQSettingSettingFragment"

    /** QQ、TIM 存在的类 */
    private const val AboutActivityClass = "${PackageName.QQ}.activity.AboutActivity"

    /** QQ、TIM 新版本存在的类 */
    private const val FormSimpleItemClass = "${PackageName.QQ}.widget.FormSimpleItem"

    /** QQ、TIM 旧版本存在的类 */
    private const val FormCommonSingleLineItemClass = "${PackageName.QQ}.widget.FormCommonSingleLineItem"

    /** QQ、TIM 存在的类 */
    private const val CoreServiceClass = "${PackageName.QQ}.app.CoreService"

    /** QQ、TIM 存在的类 */
    private const val CoreService_KernelServiceClass = "${PackageName.QQ}.app.CoreService\$KernelService"

    /** 根据多个版本存的不同的类 */
    private val BaseChatPieClass =
        VariousClass("${PackageName.QQ}.activity.aio.core.BaseChatPie", "${PackageName.QQ}.activity.BaseChatPie")

    /** 默认的 [Configuration] */
    var baseConfiguration: Configuration? = null

    /**
     * 当前是否为 QQ
     * @return [Boolean]
     */
    private val isQQ get() = packageName == PackageName.QQ

    /** 当前宿主的版本 */
    private var hostVersionName = "<unknown>"

    /**
     * 通过 [Activity] or [Fragment] 实例得到上下文
     * @return [Activity] or null
     */
    private fun Any.compatToActivity() = if (this is Activity) this else current().method { name = "getActivity"; superClass() }.invoke()

    /**
     * 这个类 QQ 的 BaseChatPie 是控制聊天界面的
     *
     * 里面有两个随机混淆的方法 ⬇
     *
     * remainScreenOn、cancelRemainScreenOn
     *
     * 这两个方法一个是挂起电源锁常驻亮屏
     *
     * 一个是停止常驻亮屏
     *
     * 不由分说每个版本混淆的方法名都会变
     *
     * 所以说每个版本重新适配 - 也可以提交分支帮我适配
     *
     * - ❗Hook 错了方法会造成闪退！
     */
    private fun hookQQBaseChatPie() {
        if (isQQ) when (hostVersionName) {
            "8.0.0" -> {
                hookBaseChatPie(methodName = "bq")
                hookBaseChatPie(methodName = "aL")
            }
            "8.0.5", "8.0.7" -> {
                hookBaseChatPie(methodName = "bw")
                hookBaseChatPie(methodName = "aQ")
            }
            "8.1.0", "8.1.3" -> {
                hookBaseChatPie(methodName = "bE")
                hookBaseChatPie(methodName = "aT")
            }
            "8.1.5" -> {
                hookBaseChatPie(methodName = "bF")
                hookBaseChatPie(methodName = "aT")
            }
            "8.1.8", "8.2.0", "8.2.6" -> {
                hookBaseChatPie(methodName = "bC")
                hookBaseChatPie(methodName = "aT")
            }
            "8.2.7", "8.2.8", "8.2.11", "8.3.0" -> {
                hookBaseChatPie(methodName = "bE")
                hookBaseChatPie(methodName = "aV")
            }
            "8.3.5" -> {
                hookBaseChatPie(methodName = "bR")
                hookBaseChatPie(methodName = "aX")
            }
            "8.3.6" -> {
                hookBaseChatPie(methodName = "cp")
                hookBaseChatPie(methodName = "aX")
            }
            "8.3.9" -> {
                hookBaseChatPie(methodName = "cj")
                hookBaseChatPie(methodName = "aT")
            }
            "8.4.1", "8.4.5" -> {
                hookBaseChatPie(methodName = "ck")
                hookBaseChatPie(methodName = "aT")
            }
            "8.4.8", "8.4.10", "8.4.17", "8.4.18", "8.5.0" -> {
                hookBaseChatPie(methodName = "remainScreenOn")
                hookBaseChatPie(methodName = "cancelRemainScreenOn")
            }
            "8.5.5" -> {
                hookBaseChatPie(methodName = "bT")
                hookBaseChatPie(methodName = "aN")
            }
            "8.6.0", "8.6.5", "8.7.0", "8.7.5", "8.7.8", "8.8.0", "8.8.3", "8.8.5" -> {
                hookBaseChatPie(methodName = "ag")
                hookBaseChatPie(methodName = "ah")
            }
            "8.8.11", "8.8.12" -> {
                hookBaseChatPie(methodName = "bc")
                hookBaseChatPie(methodName = "bd")
            }
            "8.8.17", "8.8.20" -> {
                hookBaseChatPie(methodName = "bd")
                hookBaseChatPie(methodName = "be")
            }
            "8.8.23", "8.8.28" -> {
                hookBaseChatPie(methodName = "bf")
                hookBaseChatPie(methodName = "bg")
            }
            "8.8.33" -> {
                hookBaseChatPie(methodName = "bg")
                hookBaseChatPie(methodName = "bh")
            }
            "8.8.35", "8.8.38" -> {
                hookBaseChatPie(methodName = "bi")
                hookBaseChatPie(methodName = "bj")
            }
            "8.8.50" -> {
                hookBaseChatPie(methodName = "bj")
                hookBaseChatPie(methodName = "bk")
            }
            "8.8.55", "8.8.68", "8.8.80" -> {
                hookBaseChatPie(methodName = "bk")
                hookBaseChatPie(methodName = "bl")
            }
            "8.8.83", "8.8.85", "8.8.88", "8.8.90" -> {
                hookBaseChatPie(methodName = "bl")
                hookBaseChatPie(methodName = "bm")
            }
            "8.8.93", "8.8.95" -> {
                hookBaseChatPie(methodName = "J3")
                hookBaseChatPie(methodName = "S")
            }
            "8.8.98" -> {
                hookBaseChatPie(methodName = "M3")
                hookBaseChatPie(methodName = "S")
            }
            "8.9.0", "8.9.1", "8.9.2" -> {
                hookBaseChatPie(methodName = "N3")
                hookBaseChatPie(methodName = "S")
            }
            "8.9.3", "8.9.5" -> {
                hookBaseChatPie(methodName = "H3")
                hookBaseChatPie(methodName = "P")
            }
            "8.9.8", "8.9.10" -> {
                hookBaseChatPie(methodName = "H3")
                hookBaseChatPie(methodName = "N")
            }
            "8.9.13" -> {
                hookBaseChatPie(methodName = "y3")
                hookBaseChatPie(methodName = "H")
            }
            "8.9.15", "8.9.18", "8.9.19", "8.9.20" -> {
                hookBaseChatPie(methodName = "w3")
                hookBaseChatPie(methodName = "H")
            }
            "8.9.23", "8.9.25" -> {
                hookBaseChatPie(methodName = "z3")
                hookBaseChatPie(methodName = "H")
            }
            else -> {
                HookEntry.isHookClientSupport = false
                loggerW(msg = "$hostVersionName not supported!")
            }
        }
    }

    /**
     * 拦截 [BaseChatPieClass] 的目标方法体封装
     * @param methodName 方法名
     */
    private fun hookBaseChatPie(methodName: String) {
        BaseChatPieClass.hook {
            injectMember {
                method {
                    name = methodName
                    emptyParam()
                    returnType = UnitType
                }
                intercept()
            }
        }
    }

    /** Hook CoreService QQ、TIM */
    private fun hookCoreService() {
        CoreServiceClass.hook {
            if (isQQ) {
                injectMember {
                    method { name = "startTempService" }
                    intercept()
                }.ignoredNoSuchMemberFailure()
                injectMember {
                    method {
                        name = "startCoreService"
                        param(BooleanType)
                    }
                    intercept()
                }.ignoredNoSuchMemberFailure()
                injectMember {
                    method {
                        name = "onStartCommand"
                        param(IntentClass, IntType, IntType)
                    }
                    replaceTo(any = 2)
                }.ignoredNoSuchMemberFailure()
            }
            injectMember {
                method { name = "onCreate" }
                afterHook {
                    if (ConfigData.isEnableKillQQTimCoreService)
                        instance<Service>().apply {
                            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                            stopSelf()
                            loggerD(msg = "Shutdown CoreService OK!")
                        }
                }
            }
        }
        CoreService_KernelServiceClass.hook {
            injectMember {
                method { name = "onCreate" }
                afterHook {
                    if (ConfigData.isEnableKillQQTimCoreServiceChild)
                        instance<Service>().apply {
                            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                            stopSelf()
                            loggerD(msg = "Shutdown CoreService\$KernelService OK!")
                        }
                }
            }
            injectMember {
                method {
                    name = "onStartCommand"
                    param(IntentClass, IntType, IntType)
                }
                replaceTo(any = 2)
            }.ignoredNoSuchMemberFailure()
        }
    }

    /** Hook QQ 不省电的功能 */
    private fun hookQQDisgusting() {
        if (isQQ.not()) return
        /**
         * 干掉消息收发功能的电源锁
         * 每个版本的差异暂未做排查
         * 旧版本理论上没有这个类
         */
        findClass(name = "${PackageName.QQ}.msf.service.y").hook {
            injectMember {
                method {
                    name = "a"
                    param(StringType, LongType)
                    returnType = UnitType
                }
                intercept()
            }.onAllFailure { loggerE(msg = "Hook MsfService Failed $it") }
        }.ignoredHookClassNotFoundFailure()
        /**
         * 干掉自动上传服务的电源锁
         * 每个版本的差异暂未做排查
         */
        findClass(name = "com.tencent.upload.impl.UploadServiceImpl").hook {
            injectMember {
                method { name = "acquireWakeLockIfNot" }
                intercept()
            }.onAllFailure { loggerE(msg = "Hook UploadServiceImpl Failed $it") }
        }.ignoredHookClassNotFoundFailure()
        /**
         * Hook 掉一个一像素保活 [Activity] 真的我怎么都想不到讯哥的程序员做出这种事情
         * 这个东西经过测试会在锁屏的时候吊起来，解锁的时候自动 finish()，无限耍流氓耗电
         * 2022/1/25 后期查证：锁屏界面消息快速回复窗口的解锁后拉起保活界面，也是毒瘤
         */
        findClass(name = "${PackageName.QQ}.activity.QQLSUnlockActivity").hook {
            injectMember {
                method {
                    name = "onCreate"
                    param(BundleClass)
                }
                var origDevice = ""
                beforeHook {
                    /** 由于在 onCreate 里有一行判断只要型号是 xiaomi 的设备就开电源锁，所以说这里临时替换成菊花厂 */
                    origDevice = Build.MANUFACTURER
                    if (Build.MANUFACTURER.lowercase() == "xiaomi")
                        BuildClass.field { name = "MANUFACTURER" }.get().set("HUAWEI")
                }
                afterHook {
                    instance<Activity>().finish()
                    /** 这里再把型号替换回去 - 不影响应用变量等 Xposed 模块修改的型号 */
                    BuildClass.field { name = "MANUFACTURER" }.get().set(origDevice)
                }
            }
        }
        /**
         * 这个东西同上
         * 反正也是一个一像素保活的 [Activity]
         * 讯哥的程序员真的有你的
         * 2022/1/25 后期查证：锁屏界面消息快速回复窗口
         */
        findClass("${PackageName.QQ}.activity.QQLSActivity\$14", "ktq").hook {
            injectMember {
                method { name = "run" }
                intercept()
            }.ignoredAllFailure()
        }.ignoredHookClassNotFoundFailure()
        /**
         * 这个是毒瘤核心类
         * WakeLockMonitor
         * 这个名字真的起的特别诗情画意
         * 带给用户的却是 shit 一样的体验
         * 里面有各种使用 Handler 和 Timer 的各种耗时常驻后台耗电办法持续接收消息
         * 直接循环全部方法全部干掉
         * 👮🏻 经过排查 Play 版本没这个类...... Emmmm 不想说啥了
         * ✅ 备注：8.9.x 版本已经基本移除了这个功能，没有再发现存在这个类
         */
        findClass(name = "com.tencent.qapmsdk.qqbattery.monitor.WakeLockMonitor").hook {
            injectMember {
                method {
                    name = "onHook"
                    param(StringType, AnyType, AnyArrayClass, AnyType)
                }
                intercept()
            }
            injectMember {
                method {
                    name = "doReport"
                    param("com.tencent.qapmsdk.qqbattery.monitor.WakeLockMonitor\$WakeLockEntity", IntType)
                }
                intercept()
            }
            injectMember {
                method {
                    name = "afterHookedMethod"
                    param("com.tencent.qapmsdk.qqbattery.monitor.MethodHookParam")
                }
                intercept()
            }
            injectMember {
                method {
                    name = "beforeHookedMethod"
                    param("com.tencent.qapmsdk.qqbattery.monitor.MethodHookParam")
                }
                intercept()
            }
            injectMember {
                method { name = "onAppBackground" }
                intercept()
            }
            injectMember {
                method {
                    name = "onOtherProcReport"
                    param(BundleClass)
                }
                intercept()
            }
            injectMember {
                method { name = "onProcessRun30Min" }
                intercept()
            }
            injectMember {
                method { name = "onProcessBG5Min" }
                intercept()
            }
            injectMember {
                method {
                    name = "writeReport"
                    param(BooleanType)
                }
                intercept()
            }
        }.ignoredHookClassNotFoundFailure()
        /**
         * 这个是毒瘤核心操作类
         * 功能同上、全部拦截
         * 👮🏻 经过排查 Play 版本也没这个类...... Emmmm 不想说啥了
         * ✅ 备注：8.9.x 版本已经基本移除了这个功能，没有再发现存在这个类
         */
        findClass(name = "com.tencent.qapmsdk.qqbattery.QQBatteryMonitor").hook {
            injectMember {
                method { name = "start" }
                intercept()
            }
            injectMember {
                method { name = "stop" }
                intercept()
            }
            injectMember {
                method {
                    name = "handleMessage"
                    param(MessageClass)
                }
                replaceToTrue()
            }
            injectMember {
                method { name = "startMonitorInner" }
                intercept()
            }
            injectMember {
                method { name = "onAppBackground" }
                intercept()
            }
            injectMember {
                method { name = "onAppForeground" }
                intercept()
            }
            injectMember {
                method {
                    name = "setLogWhite"
                    paramCount = 2
                }
                intercept()
            }
            injectMember {
                method {
                    name = "setCmdWhite"
                    paramCount = 2
                }
                intercept()
            }
            injectMember {
                method {
                    name = "onWriteLog"
                    param(StringType, StringType)
                }
                intercept()
            }
            injectMember {
                method {
                    name = "onCmdRequest"
                    param(StringType)
                }
                intercept()
            }
            injectMember {
                method {
                    name = "addData"
                    paramCount = 4
                }
                intercept()
            }
            injectMember {
                method {
                    name = "onGpsScan"
                    paramCount = 2
                }
                intercept()
            }
        }.ignoredHookClassNotFoundFailure()
    }

    /**
     * Hook QQ 的设置界面添加模块设置入口
     * @param instance 当前设置界面实例
     * @param instanceClass 当前设置界面 [Class] 实例
     */
    private fun hookQQSettingsUI(instance: Any?, instanceClass: Class<*>) {
        /** 当前的顶级 Item 实例 */
        val formItemRefRoot = instanceClass.field {
            // TODO: 这里的 "type" 在之后换成新用法
            type(FormSimpleItemClass).index(num = 1)
        }.ignored().get(instance).cast() ?: instanceClass.field {
            // TODO: 这里的 "type" 在之后换成新用法
            type(FormCommonSingleLineItemClass).index(num = 1)
        }.ignored().get(instance).cast<View?>()
        /** 创建一个新的 Item */
        FormSimpleItemClass.toClassOrNull()?.buildOf<View>(instance?.compatToActivity()) { param(ContextClass) }?.current {
            method {
                name = "setLeftText"
                param(CharSequenceType)
            }.call("TSBattery")
            method {
                name = "setRightText"
                param(CharSequenceType)
            }.call("${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})")
            method {
                name = "setBgType"
                param(IntType)
            }.call(if (isQQ) 0 else 2)
        }?.apply { setOnClickListener { context.startModuleSettings() } }?.also { item ->
            var listGroup = formItemRefRoot?.parent as? ViewGroup?
            val lparam = (if (listGroup?.childCount == 1) {
                listGroup = listGroup.parent as? ViewGroup
                (formItemRefRoot?.parent as? View?)?.layoutParams
            } else formItemRefRoot?.layoutParams)
                ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            /** 设置圆角和间距 */
            if (isQQ) (lparam as? ViewGroup.MarginLayoutParams?)?.setMargins(0, 15.dp(item.context), 0, 0)
            /** 将 Item 添加到设置界面 */
            listGroup?.also { if (isQQ) it.addView(item, lparam) else it.addView(item, 0, lparam) }
        }
    }

    override fun onHook() {
        onAppLifecycle {
            attachBaseContext { baseContext, hasCalledSuper ->
                if (hasCalledSuper.not()) baseConfiguration = baseContext.resources.configuration
            }
            onCreate {
                hostVersionName = appVersionName
                ConfigData.init(context = this)
                registerModuleAppActivities(AboutActivityClass)
                if (ConfigData.isDisableAllHook) return@onCreate
                hookSystemWakeLock()
                hookQQBaseChatPie()
                hookCoreService()
                hookQQDisgusting()
                loggerI(msg = "All processes are completed for \"${processName.takeIf { it != packageName } ?: packageName}\"")
            }
        }
        /** 仅注入主进程 */
        withProcess(mainProcessName) {
            /** Hook 跳转事件 */
            JumpActivityClass.hook {
                injectMember {
                    method {
                        name = "doOnCreate"
                        param(BundleClass)
                    }
                    afterHook { instance<Activity>().jumpToModuleSettings() }
                }
            }
            /** 将条目注入设置界面 (Activity) */
            QQSettingSettingActivityClass.hook {
                injectMember {
                    method {
                        name = "doOnCreate"
                        param(BundleClass)
                    }
                    afterHook { hookQQSettingsUI(instance, instanceClass) }
                }
            }
            /** 将条目注入设置界面 (Fragment) */
            QQSettingSettingFragmentClass.hook {
                injectMember {
                    method {
                        name = "doOnCreateView"
                        paramCount = 3
                    }
                    afterHook { hookQQSettingsUI(instance, instanceClass) }
                }
            }
        }
    }
}