/*
 * TSBattery - A new way to save your battery avoid cancer apps hacker it.
 * Copyright (C) 2017 Fankes Studio(qzmmcn@163.com)
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
 * This file is created by fankes on 2022/9/29.
 */
@file:Suppress("ConstPropertyName")

package com.fankes.tsbattery.hook.entity

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ServiceCompat
import androidx.fragment.app.Fragment
import com.fankes.tsbattery.R
import com.fankes.tsbattery.const.ModuleVersion
import com.fankes.tsbattery.const.PackageName
import com.fankes.tsbattery.data.ConfigData
import com.fankes.tsbattery.hook.HookEntry
import com.fankes.tsbattery.hook.factory.hookSystemWakeLock
import com.fankes.tsbattery.hook.factory.isQQNightMode
import com.fankes.tsbattery.hook.factory.jumpToModuleSettings
import com.fankes.tsbattery.hook.factory.startModuleSettings
import com.fankes.tsbattery.hook.helper.DexKitHelper
import com.fankes.tsbattery.utils.factory.appVersionName
import com.fankes.tsbattery.utils.factory.dp
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.ArrayClass
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.kavaref.extension.classOf
import com.highcapable.kavaref.extension.createInstanceAsTypeOrNull
import com.highcapable.kavaref.extension.createInstanceOrNull
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.registerModuleAppActivities
import com.highcapable.yukihookapi.hook.log.YLog
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Hook QQ、TIM
 */
object QQTIMHooker : YukiBaseHooker() {

    /** QQ、TIM 存在的类 */
    const val JumpActivityClassName = "${PackageName.QQ}.activity.JumpActivity"

    /** QQ、TIM 存在的类 */
    private val JumpActivityClass by lazyClassOrNull(JumpActivityClassName)

    /** QQ、TIM 存在的类 (NT 版本不再存在) */
    private val QQSettingSettingActivityClass by lazyClassOrNull("${PackageName.QQ}.activity.QQSettingSettingActivity")

    /** QQ 新版存在的类 (Pad 模式 - NT 版本不再存在) */
    private val QQSettingSettingFragmentClass by lazyClassOrNull("${PackageName.QQ}.fragment.QQSettingSettingFragment")

    /** QQ、TIM 存在的类 (NT 版本不再存在) */
    private val AboutActivityClass by lazyClassOrNull("${PackageName.QQ}.activity.AboutActivity")

    /** QQ 新版本存在的类 */
    private val GeneralSettingActivityClass by lazyClassOrNull("${PackageName.QQ}.activity.GeneralSettingActivity")

    /** QQ 新版本 (NT) 存在的类 */
    private val MainSettingFragmentClass by lazyClassOrNull("${PackageName.QQ}.setting.main.MainSettingFragment")

    /** QQ 新版本 (NT) 存在的类 */
    private val MainSettingConfigProviderClass by lazyClassOrNull("${PackageName.QQ}.setting.main.MainSettingConfigProvider")

    /** QQ、TIM 新版本存在的类 */
    private val FormSimpleItemClass by lazyClassOrNull("${PackageName.QQ}.widget.FormSimpleItem")

    /** QQ、TIM 旧版本存在的类 */
    private val FormCommonSingleLineItemClass by lazyClassOrNull("${PackageName.QQ}.widget.FormCommonSingleLineItem")

    /** QQ、TIM 存在的类 */
    private val CoreServiceClass by lazyClassOrNull("${PackageName.QQ}.app.CoreService")

    /** QQ、TIM 存在的类 */
    private val CoreService_KernelServiceClass by lazyClassOrNull("${PackageName.QQ}.app.CoreService\$KernelService")

    /** 根据多个版本存的不同的类 */
    private val BaseChatPieClass by lazyClassOrNull(
        VariousClass(
            "${PackageName.QQ}.activity.aio.core.BaseChatPie",
            "${PackageName.QQ}.activity.BaseChatPie"
        )
    )

    /** 是否存在 [BaseChatPieClass] */
    private val hasBaseChatPieClass by lazy { BaseChatPieClass != null }

    /**
     * DexKit 搜索结果数据实现类
     */
    private object DexKitData {
        var BaseChatPie_RemainScreenOnMethod: Method? = null
        var BaseChatPie_CancelRemainScreenOnMethod: Method? = null
        var SimpleItemProcessorClass: Class<*>? = null
        var SimpleItemProcessorClass_OnClickMethod: Method? = null
    }

    /** 一个内部进程的名称 (与 X5 浏览器内核有关) */
    private val privilegedProcessName = "$packageName:privileged_process"

    /** 默认的 [Configuration] */
    var baseConfiguration: Configuration? = null

    /**
     * 当前是否为 QQ
     * @return [Boolean]
     */
    private val isQQ get() = packageName == PackageName.QQ

    /**
     * 当前是否为 QQ 的 NT 版本
     *
     * 在 QQ NT 中 [AboutActivityClass] 已被移除 - 以此作为判断条件
     * @return [Boolean]
     */
    private val isQQNTVersion get() = AboutActivityClass == null

    /** 当前宿主的版本 */
    private var hostVersionName = "<unknown>"

    /**
     * 通过 [Activity] or [Fragment] 实例得到上下文
     * @return [Activity] or null
     */
    private fun Any.compatToActivity() = if (this !is Activity)
        resolve().optional().firstMethodOrNull { name = "getActivity"; superclass() }?.invoke()
    else this

    /** 使用 DexKit 进行搜索 */
    private fun searchUsingDexKit() {
        val classLoader = appClassLoader ?: return
        DexKitHelper.create(this) {
            BaseChatPieClass?.name?.also { baseChatPieClassName ->
                DexKitData.BaseChatPie_RemainScreenOnMethod =
                    findMethod {
                        matcher {
                            declaredClass(baseChatPieClassName)
                            usingStrings("remainScreenOn")
                            paramCount = 0
                            returnType = Void.TYPE.name
                        }
                    }.singleOrNull()?.getMethodInstance(classLoader)
                DexKitData.BaseChatPie_CancelRemainScreenOnMethod =
                    findMethod {
                        matcher {
                            declaredClass(baseChatPieClassName)
                            usingStrings("cancelRemainScreenOn")
                            paramCount = 0
                            returnType = Void.TYPE.name
                        }
                    }.singleOrNull()?.getMethodInstance(classLoader)
            }
            val kotlinFunction0 = "kotlin.jvm.functions.Function0"
            findClass {
                searchPackages("${PackageName.QQ}.setting.processor")
                matcher {
                    methods {
                        add {
                            name = "<init>"
                            paramTypes(classOf<Context>().name, classOf<Int>().name, classOf<CharSequence>().name, classOf<Int>().name)
                        }
                        add {
                            paramTypes(kotlinFunction0)
                            returnType = Void.TYPE.name
                        }
                    }
                    fields { count(6..Int.MAX_VALUE) }
                }
            }.singleOrNull()?.name?.also { className ->
                DexKitData.SimpleItemProcessorClass = className.toClass()
                DexKitData.SimpleItemProcessorClass_OnClickMethod =
                    findMethod {
                        matcher {
                            declaredClass = className
                            paramTypes(kotlinFunction0)
                            returnType = Void.TYPE.name
                            usingNumbers(2)
                        }
                    }.singleOrNull()?.getMethodInstance(classLoader)
            }
        }
    }

    /**
     * 这个类 QQ 的 BaseChatPie 是控制聊天界面的
     *
     * 里面有两个随机混淆的方法 ⬇
     *
     * remainScreenOn、cancelRemainScreenOn
     *
     * 这两个方法一个是挂起电源锁常驻亮屏 - 一个是停止常驻亮屏
     *
     * - 在 QQ NT 版本中完全移除了 BaseChatPie 类 - 所以不再处理
     */
    private fun hookQQBaseChatPie() {
        if (hasBaseChatPieClass.not()) {
            HookEntry.isHookClientSupport = true
            return YLog.debug("Start for QQ NT version,.")
        }
        /**
         * 打印警告信息
         * @param index 序号
         */
        fun warn(index: Int) {
            HookEntry.isHookClientSupport = false
            YLog.warn("$hostVersionName [$index] not support!")
        }
        DexKitData.BaseChatPie_RemainScreenOnMethod?.hook()?.intercept() ?: warn(index = 0)
        DexKitData.BaseChatPie_CancelRemainScreenOnMethod?.hook()?.intercept() ?: warn(index = 1)
    }

    /** Hook CoreService QQ、TIM */
    private fun hookCoreService() {
        CoreServiceClass?.resolve()?.optional()?.apply {
            if (isQQ) {
                firstMethodOrNull {
                    name = "startTempService"
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "startCoreService"
                    parameters(Boolean::class)
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "onStartCommand"
                    parameters(Intent::class, Int::class, Int::class)
                }?.hook()?.replaceTo(any = 2)
            }
            firstMethodOrNull {
                name = "onCreate"
            }?.hook()?.after {
                if (ConfigData.isEnableKillQQTimCoreService)
                    instance<Service>().apply {
                        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                        stopSelf()
                        YLog.debug("Shutdown CoreService OK!")
                    }
            }
        }
        CoreService_KernelServiceClass?.resolve()?.optional()?.apply {
            firstMethodOrNull {
                name = "onCreate"
            }?.hook()?.after {
                if (ConfigData.isEnableKillQQTimCoreServiceChild)
                    instance<Service>().apply {
                        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                        stopSelf()
                        YLog.debug("Shutdown CoreService\$KernelService OK!")
                    }
            }
            firstMethodOrNull {
                name = "onStartCommand"
                parameters(Intent::class, Int::class, Int::class)
            }?.hook()?.replaceTo(any = 2)
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
        "${PackageName.QQ}.msf.service.y".toClassOrNull()
            ?.resolve()
            ?.optional(silent = true)
            ?.firstMethodOrNull {
                name = "a"
                parameters(String::class, Long::class)
                returnType = Void.TYPE
            }?.hook()?.intercept()
        /**
         * 干掉自动上传服务的电源锁
         * 每个版本的差异暂未做排查
         */
        "com.tencent.upload.impl.UploadServiceImpl".toClassOrNull()
            ?.resolve()
            ?.optional(silent = true)
            ?.firstMethodOrNull {
                name = "acquireWakeLockIfNot"
            }?.hook()?.intercept()
        /**
         * Hook 掉一个一像素保活 Activity 真的我怎么都想不到讯哥的程序员做出这种事情
         * 这个东西经过测试会在锁屏的时候吊起来，解锁的时候自动 finish()，无限耍流氓耗电
         * 2022/1/25 后期查证：锁屏界面消息快速回复窗口的解锁后拉起保活界面，也是毒瘤
         */
        "${PackageName.QQ}.activity.QQLSUnlockActivity".toClassOrNull()
            ?.resolve()
            ?.optional(silent = true)
            ?.firstMethodOrNull {
                name = "onCreate"
                parameters(Bundle::class)
            }?.hook {
                var origDevice = ""
                before {
                    /** 由于在 onCreate 里有一行判断只要型号是 xiaomi 的设备就开电源锁，所以说这里临时替换成菊花厂 */
                    origDevice = Build.MANUFACTURER
                    if (Build.MANUFACTURER.lowercase() == "xiaomi")
                        Build::class.resolve().firstField { name = "MANUFACTURER" }.set("HUAWEI")
                }
                after {
                    instance<Activity>().finish()
                    /** 这里再把型号替换回去 - 不影响应用变量等 Xposed 模块修改的型号 */
                    Build::class.resolve().firstField { name = "MANUFACTURER" }.set(origDevice)
                }
            }
        /**
         * 这个东西同上
         * 反正也是一个一像素保活的 Activity
         * 讯哥的程序员真的有你的
         * 2022/1/25 后期查证：锁屏界面消息快速回复窗口
         */
        VariousClass("${PackageName.QQ}.activity.QQLSActivity\$14", "ktq").toClassOrNull()
            ?.resolve()
            ?.optional(silent = true)
            ?.firstMethodOrNull {
                name = "run"
            }?.hook()?.intercept()
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
        "com.tencent.qapmsdk.qqbattery.monitor.WakeLockMonitor".toClassOrNull()
            ?.resolve()
            ?.optional(silent = true)
            ?.apply {
                firstMethodOrNull {
                    name = "onHook"
                    parameters(String::class, Any::class, ArrayClass(Any::class), Any::class)
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "doReport"
                    parameters("com.tencent.qapmsdk.qqbattery.monitor.WakeLockMonitor\$WakeLockEntity", Int::class)
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "afterHookedMethod"
                    parameters("com.tencent.qapmsdk.qqbattery.monitor.MethodHookParam")
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "beforeHookedMethod"
                    parameters("com.tencent.qapmsdk.qqbattery.monitor.MethodHookParam")
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "onAppBackground"
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "onOtherProcReport"
                    parameters(Bundle::class)
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "onProcessRun30Min"
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "onProcessBG5Min"
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "writeReport"
                    parameters(Boolean::class)
                }?.hook()?.intercept()
            }
        /**
         * 这个是毒瘤核心操作类
         * 功能同上、全部拦截
         * 👮🏻 经过排查 Play 版本也没这个类...... Emmmm 不想说啥了
         * ✅ 备注：8.9.x 版本已经基本移除了这个功能，没有再发现存在这个类
         */
        "com.tencent.qapmsdk.qqbattery.QQBatteryMonitor".toClassOrNull()
            ?.resolve()
            ?.optional(silent = true)
            ?.apply {
                firstMethodOrNull {
                    name = "start"
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "stop"
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "handleMessage"
                    parameters(Message::class)
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "startMonitorInner"
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "onAppBackground"
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "onAppForeground"
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "setLogWhite"
                    parameterCount = 2
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "setCmdWhite"
                    parameterCount = 2
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "onWriteLog"
                    parameters(String::class, String::class)
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "onCmdRequest"
                    parameters(String::class)
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "addData"
                    parameterCount = 4
                }?.hook()?.intercept()
                firstMethodOrNull {
                    name = "onGpsScan"
                    parameterCount = 2
                }?.hook()?.intercept()
            }
    }

    /** Hook QQ 的设置界面添加模块设置入口 (新版) */
    private fun hookQQSettingsUi() {
        if (MainSettingFragmentClass == null) return YLog.error("Could not found main setting class, hook aborted")
        val kotlinUnit = "kotlin.Unit"
        val simpleItemProcessorClass = DexKitData.SimpleItemProcessorClass ?: return YLog.error("Could not found processor class, hook aborted")

        /**
         * 创建入口点条目
         * @param context 当前实例
         * @return [Any]
         */
        fun createTSEntryItem(context: Context): Any {
            /** 为了使用图标资源 ID - 这里需要重新注入模块资源防止不生效 */
            context.injectModuleAppResources()
            val iconResId = if (context.isQQNightMode()) R.mipmap.ic_tsbattery_entry_night else R.mipmap.ic_tsbattery_entry_day
            return simpleItemProcessorClass.createInstanceOrNull(context, R.id.tsbattery_qq_entry_item_id, "TSBattery", iconResId)?.also { item ->
                val onClickMethod = DexKitData.SimpleItemProcessorClass_OnClickMethod ?: error("Could not found processor method")
                val proxyOnClick = Proxy.newProxyInstance(appClassLoader, arrayOf(onClickMethod.parameterTypes[0])) { any, method, args ->
                    if (method.name == "invoke") {
                        context.startModuleSettings()
                        kotlinUnit.toClass().resolve().firstField { name = "INSTANCE" }.get()
                    } else method.invoke(any, args)
                }; onClickMethod.invoke(item, proxyOnClick)
            } ?: error("Could not create TSBattery entry item")
        }
        MainSettingConfigProviderClass?.resolve()?.optional()?.firstMethodOrNull {
            parameters(Context::class)
            returnType = List::class
        }?.hook()?.after {
            val context = args().first().cast<Context>() ?: return@after
            val processor = result<MutableList<Any?>>() ?: return@after
            processor.add(
                1,
                processor[0]?.javaClass?.createInstanceOrNull(
                    arrayListOf<Any>().apply { 
                        add(createTSEntryItem(context))
                    }.toList(), "", ""
                )
            )
        }
    }

    /**
     * Hook QQ 的设置界面添加模块设置入口 (旧版)
     * @param instance 当前设置界面实例
     */
    private fun hookQQSettingsUiLegacy(instance: Any?) {
        /** 当前的顶级 Item 实例 */
        val formItemRefRoot = instance?.resolve()?.optional()?.lastFieldOrNull {
            type { it == FormSimpleItemClass || it == FormCommonSingleLineItemClass }
        }?.get<View>()

        /** 创建一个新的 Item */
        val item = FormSimpleItemClass?.createInstanceAsTypeOrNull<View>(instance?.compatToActivity())
        item?.resolve()?.optional()?.apply {
            firstMethodOrNull {
                name = "setLeftText"
                parameters(CharSequence::class)
            }?.invoke("TSBattery")
            firstMethodOrNull {
                name = "setRightText"
                parameters(CharSequence::class)
            }?.invoke(ModuleVersion.toString())
            firstMethodOrNull {
                name = "setBgType"
                parameters(Int::class)
            }?.invoke(if (isQQ) 0 else 2)
        }
        item ?: return
        item.setOnClickListener { it.context.startModuleSettings() }
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

    override fun onHook() {
        searchUsingDexKit()
        onAppLifecycle(isOnFailureThrowToApp = false) {
            attachBaseContext { baseContext, hasCalledSuper ->
                if (hasCalledSuper.not()) baseConfiguration = baseContext.resources.configuration
            }
            onCreate {
                hostVersionName = appVersionName
                /** 不注入此进程防止部分系统发生 X5 浏览器内核崩溃问题 */
                if (processName.startsWith(privilegedProcessName)) return@onCreate
                ConfigData.init(context = this)
                registerModuleAppActivities(when {
                    isQQNTVersion -> GeneralSettingActivityClass
                    else -> AboutActivityClass
                })
                if (ConfigData.isDisableAllHook) return@onCreate
                hookSystemWakeLock()
                hookQQBaseChatPie()
                hookCoreService()
                hookQQDisgusting()
                YLog.info("All processes are completed for \"${processName.takeIf { it != packageName } ?: packageName}\"")
            }
        }
        /** 仅注入主进程 */
        withProcess(mainProcessName) {
            /** Hook 跳转事件 */
            JumpActivityClass?.resolve()?.optional()?.firstMethodOrNull {
                name = "doOnCreate"
                parameters(Bundle::class)
            }?.hook()?.after { instance<Activity>().jumpToModuleSettings() }
            /** Hook 设置界面入口点 */
            if (isQQNTVersion) hookQQSettingsUi()
            else {
                /** 将条目注入设置界面 (Activity) */
                QQSettingSettingActivityClass?.resolve()?.optional()?.firstMethodOrNull {
                    name = "doOnCreate"
                    parameters(Bundle::class)
                }?.hook()?.after { hookQQSettingsUiLegacy(instance) }
                /** 将条目注入设置界面 (Fragment) */
                QQSettingSettingFragmentClass?.resolve()?.optional()?.firstMethodOrNull {
                    name = "doOnCreateView"
                    parameterCount = 3
                }?.hook()?.after { hookQQSettingsUiLegacy(instance) }
            }
        }
    }
}