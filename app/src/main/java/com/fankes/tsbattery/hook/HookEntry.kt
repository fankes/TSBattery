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
 * This file is Created by fankes on 2022/2/15.
 */
@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.fankes.tsbattery.hook

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import com.fankes.tsbattery.BuildConfig
import com.fankes.tsbattery.data.DataConst
import com.fankes.tsbattery.hook.HookConst.QQ_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookConst.TIM_PACKAGE_NAME
import com.fankes.tsbattery.hook.HookConst.WECHAT_PACKAGE_NAME
import com.fankes.tsbattery.ui.activity.MainActivity
import com.fankes.tsbattery.utils.factory.dp
import com.fankes.tsbattery.utils.factory.showDialog
import com.fankes.tsbattery.utils.factory.versionCode
import com.fankes.tsbattery.utils.factory.versionName
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.*
import com.highcapable.yukihookapi.hook.log.loggerD
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.type.android.*
import com.highcapable.yukihookapi.hook.type.java.*
import com.highcapable.yukihookapi.hook.xposed.proxy.YukiHookXposedInitProxy

@InjectYukiHookWithXposed
class HookEntry : YukiHookXposedInitProxy {

    companion object {

        /** QQ、TIM 存在的类 */
        private const val SplashActivityClass = "$QQ_PACKAGE_NAME.activity.SplashActivity"

        /** QQ、TIM 存在的类 */
        private const val CoreServiceClass = "$QQ_PACKAGE_NAME.app.CoreService"

        /** QQ、TIM 存在的类 */
        private const val CoreService_KernelServiceClass = "$QQ_PACKAGE_NAME.app.CoreService\$KernelService"

        /** QQ、TIM 新版本存在的类 */
        private const val FormSimpleItemClass = "$QQ_PACKAGE_NAME.widget.FormSimpleItem"

        /** QQ、TIM 旧版本存在的类 */
        private const val FormCommonSingleLineItemClass = "$QQ_PACKAGE_NAME.widget.FormCommonSingleLineItem"

        /** 微信存在的类 */
        private const val LauncherUIClass = "$WECHAT_PACKAGE_NAME.ui.LauncherUI"

        /** 根据多个版本存的不同的类 */
        private val BaseChatPieClass = VariousClass(
            "$QQ_PACKAGE_NAME.activity.aio.core.BaseChatPie",
            "$QQ_PACKAGE_NAME.activity.BaseChatPie"
        )
    }

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
     * @param version QQ 版本
     */
    private fun PackageParam.hookQQBaseChatPie(version: String) {
        when (version) {
            "8.2.11" -> {
                interceptBaseChatPie(methodName = "bE")
                interceptBaseChatPie(methodName = "aV")
            }
            "8.8.17" -> {
                interceptBaseChatPie(methodName = "bd")
                interceptBaseChatPie(methodName = "be")
            }
            "8.8.23" -> {
                interceptBaseChatPie(methodName = "bf")
                interceptBaseChatPie(methodName = "bg")
            }
            /** 8.8.35 贡献者：StarWishsama */
            "8.8.35", "8.8.38" -> {
                interceptBaseChatPie(methodName = "bi")
                interceptBaseChatPie(methodName = "bj")
            }
            /** 贡献者：JiZhi-Error */
            "8.8.50" -> {
                interceptBaseChatPie(methodName = "bj")
                interceptBaseChatPie(methodName = "bk")
            }
            "8.8.55", "8.8.68", "8.8.80" -> {
                interceptBaseChatPie(methodName = "bk")
                interceptBaseChatPie(methodName = "bl")
            }
            "8.8.83", "8.8.85" -> {
                interceptBaseChatPie(methodName = "bl")
                interceptBaseChatPie(methodName = "bm")
            }
            else -> loggerD(msg = "$version not supported!")
        }
    }

    /**
     * 拦截 [BaseChatPieClass] 的目标方法体封装
     * @param methodName 方法名
     */
    private fun PackageParam.interceptBaseChatPie(methodName: String) =
        BaseChatPieClass.hook {
            injectMember {
                method {
                    name = methodName
                    returnType = UnitType
                }
                intercept()
            }
        }

    /** Hook 系统电源锁 */
    private fun PackageParam.hookSystemWakeLock() =
        PowerManager_WakeLockClass.hook {
            injectMember {
                method {
                    name = "acquireLocked"
                    returnType = UnitType
                }
                intercept()
            }
        }

    /** 增加通知栏文本显示守护状态 */
    private fun PackageParam.hookNotification() =
        Notification_BuilderClass.hook {
            injectMember {
                method {
                    name = "setContentText"
                    param(CharSequenceType)
                }
                beforeHook {
                    if (prefs.get(DataConst.ENABLE_NOTIFY_TIP))
                        when (firstArgs<CharSequence>()) {
                            "QQ正在后台运行" ->
                                args().set("QQ正在后台运行 - TSBattery 守护中")
                            "TIM正在后台运行" ->
                                args().set("TIM正在后台运行 - TSBattery 守护中")
                        }
                }
            }
        }

    /**
     * 提示模块运行信息 QQ、TIM、微信
     * @param isQQTIM 是否为 QQ、TIM
     */
    private fun PackageParam.hookModuleRunningInfo(isQQTIM: Boolean) =
        when {
            isQQTIM -> SplashActivityClass.hook {
                /**
                 * Hook 启动界面的第一个 [Activity]
                 * QQ 和 TIM 都是一样的类
                 * 在里面加入提示运行信息的对话框测试模块是否激活
                 */
                injectMember {
                    method {
                        name = "doOnCreate"
                        param(BundleClass)
                    }
                    afterHook {
                        if (prefs.get(DataConst.ENABLE_RUN_INFO))
                            instance<Activity>().apply {
                                showDialog {
                                    title = "TSBattery 已激活"
                                    msg = "[提示模块运行信息功能已打开]\n\n" +
                                            "模块工作看起来一切正常，请自行测试是否能达到省电效果。\n\n" +
                                            "已生效模块版本：${prefs.get(DataConst.ENABLE_MODULE_VERSION)}\n" +
                                            "当前模式：${if (prefs.get(DataConst.ENABLE_QQTIM_WHITE_MODE)) "保守模式" else "完全模式"}" +
                                            "\n\n包名：${packageName}\n版本：$versionName($versionCode)" +
                                            "\n\n模块只对挂后台锁屏情况下有省电效果，" +
                                            "请不要将过多的群提醒，消息通知打开，这样子在使用过程时照样会极其耗电。\n\n" +
                                            "如果你不想看到此提示。请在模块设置中关闭“提示模块运行信息”，此设置默认关闭。\n\n" +
                                            "持续常驻使用 QQ、TIM 依然会耗电，任何软件都是如此，" +
                                            "模块是无法帮你做到前台不耗电的。\n\n" +
                                            "开发者 酷安 @星夜不荟\n未经允许禁止转载、修改或复制我的劳动成果。"
                                    confirmButton(text = "我知道了")
                                    noCancelable()
                                }
                            }
                    }
                }
            }
            else -> LauncherUIClass.hook {
                /**
                 * Hook 启动界面的第一个 [Activity]
                 * 在里面加入提示运行信息的对话框测试模块是否激活
                 */
                injectMember {
                    method {
                        name = "onCreate"
                        param(BundleClass)
                    }
                    afterHook {
                        if (prefs.get(DataConst.ENABLE_RUN_INFO))
                            instance<Activity>().apply {
                                showDialog(isUseBlackTheme = true) {
                                    title = "TSBattery 已激活"
                                    msg = "[提示模块运行信息功能已打开]\n\n" +
                                            "模块工作看起来一切正常，请自行测试是否能达到省电效果。\n\n" +
                                            "已生效模块版本：${prefs.get(DataConst.ENABLE_MODULE_VERSION)}\n" +
                                            "当前模式：基础省电" +
                                            "\n\n包名：${packageName}\n版本：$versionName($versionCode)" +
                                            "\n\n当前只支持微信的基础省电，即系统电源锁，后续会继续适配微信相关的省电功能(在新建文件夹了)。\n\n" +
                                            "如果你不想看到此提示。请在模块设置中关闭“提示模块运行信息”，此设置默认关闭。\n\n" +
                                            "持续常驻使用微信依然会耗电，任何软件都是如此，" +
                                            "模块是无法帮你做到前台不耗电的。\n\n" +
                                            "开发者 酷安 @星夜不荟\n未经允许禁止转载、修改或复制我的劳动成果。"
                                    confirmButton(text = "我知道了")
                                    noCancelable()
                                }
                            }
                    }
                }
            }
        }

    /**
     * Hook CoreService QQ、TIM
     * @param isQQ 是否为 QQ - 单独处理
     */
    private fun PackageParam.hookCoreService(isQQ: Boolean) {
        CoreServiceClass.hook {
            if (isQQ) {
                injectMember {
                    method { name = "startTempService" }
                    intercept()
                }
                injectMember {
                    method {
                        name = "startCoreService"
                        param(BooleanType)
                    }
                    intercept()
                }
                injectMember {
                    method {
                        name = "onStartCommand"
                        param(IntentClass, IntType, IntType)
                    }
                    replaceTo(any = 2)
                }
            }
            injectMember {
                method { name = "onCreate" }
                afterHook {
                    if (prefs.get(DataConst.ENABLE_QQTIM_CORESERVICE_BAN))
                        instance<Service>().apply {
                            stopForeground(true)
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
                    if (prefs.get(DataConst.ENABLE_QQTIM_CORESERVICE_CHILD_BAN))
                        instance<Service>().apply {
                            stopForeground(true)
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
            }
        }
    }

    /**
     * 将激活状态插入到设置页面
     * @param isQQ 是否为 QQ - 单独处理
     */
    private fun PackageParam.hookQQSettingsSettingActivity(isQQ: Boolean) =
        findClass(name = "$QQ_PACKAGE_NAME.activity.QQSettingSettingActivity").hook {
            injectMember {
                method {
                    name = "doOnCreate"
                    param(BundleClass)
                    afterHook {
                        /** 是否启用 Hook */
                        if (prefs.get(DataConst.ENABLE_SETTING_TIP).not()) return@afterHook

                        /** 当前的顶级 Item 实例 */
                        var formItemRefRoot: View? = null

                        /**
                         * 使用循环筛选
                         * @param target 目标变量名称
                         * @return [View] or null
                         */
                        fun match(target: String) = runCatching {
                            field {
                                name = target
                                type = FormSimpleItemClass.clazz
                            }.ignoredError().get(instance).cast() ?: field {
                                name = target
                                type = FormCommonSingleLineItemClass.clazz
                            }.ignoredError().get(instance).cast<View?>()
                        }.getOrNull()
                        /** 循环出当前设置界面存在的顶级 Item */
                        arrayOf(
                            "a", "b", "c", "d", "e", "f", "g",
                            "h", "i", "j", "k", "l", "m", "n",
                            "o", "p", "q", "r", "s", "t", "u",
                            "v", "w", "x", "y", "z", "A", "B"
                        ).forEach { match(it)?.also { e -> formItemRefRoot = e } }
                        /** 创建一个新的 Item */
                        FormSimpleItemClass.clazz.constructor { param(ContextClass) }.get().newInstance<View>(instance)?.also {
                            it.javaClass.apply {
                                method {
                                    name = "setLeftText"
                                    param(CharSequenceType)
                                }.get(it).call("TSBattery")
                                method {
                                    name = "setRightText"
                                    param(CharSequenceType)
                                }.get(it).call(prefs.get(DataConst.ENABLE_MODULE_VERSION))
                                method {
                                    name = "setBgType"
                                    param(IntType)
                                }.get(it).call(if (isQQ) 0 else 2)
                            }
                            it.setOnClickListener {
                                instance<Activity>().apply {
                                    showDialog {
                                        title = "TSBattery 守护中"
                                        msg = "已生效模块版本：${prefs.get(DataConst.ENABLE_MODULE_VERSION)}\n" +
                                                "当前模式：${if (prefs.get(DataConst.ENABLE_QQTIM_WHITE_MODE)) "保守模式" else "完全模式"}" +
                                                "\n\n包名：${packageName}\n版本：$versionName($versionCode)" +
                                                "\n\n模块只对挂后台锁屏情况下有省电效果，" +
                                                "请不要将过多的群提醒，消息通知打开，这样子在使用过程时照样会极其耗电。\n\n" +
                                                "持续常驻使用 QQ、TIM 依然会耗电，任何软件都是如此，" +
                                                "模块是无法帮你做到前台不耗电的。\n\n" +
                                                "开发者 酷安 @星夜不荟\n未经允许禁止转载、修改或复制我的劳动成果。"
                                        confirmButton(text = "打开模块设置") {
                                            runCatching {
                                                startActivity(Intent().apply {
                                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                                    component = ComponentName(
                                                        BuildConfig.APPLICATION_ID,
                                                        MainActivity::class.java.name
                                                    )
                                                })
                                            }.onFailure { Toast.makeText(context, "启动失败", Toast.LENGTH_SHORT).show() }
                                        }
                                        cancelButton(text = "关闭")
                                    }
                                }
                            }
                        }?.apply {
                            var listGroup = formItemRefRoot?.parent as? ViewGroup?
                            val lparam = (if (listGroup?.childCount == 1) {
                                listGroup = listGroup.parent as? ViewGroup
                                (formItemRefRoot?.parent as? View?)?.layoutParams
                            } else formItemRefRoot?.layoutParams) ?: ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                            /** 设为圆角和间距 */
                            if (isQQ) (lparam as? MarginLayoutParams?)?.setMargins(0, 15.dp(context), 0, 0)
                            /** 将 Item 添加到设置界面 */
                            listGroup?.also { if (isQQ) it.addView(this, lparam) else it.addView(this, 0, lparam) }
                        }
                    }
                }
            }
        }

    override fun onInit() = configs {
        debugTag = "TSBattery"
        isDebug = false
        isEnableModulePrefsCache = false
    }

    override fun onHook() = encase {
        loadApp(QQ_PACKAGE_NAME) {
            hookSystemWakeLock()
            hookNotification()
            hookCoreService(isQQ = true)
            hookModuleRunningInfo(isQQTIM = true)
            hookQQSettingsSettingActivity(isQQ = true)
            if (prefs.get(DataConst.ENABLE_QQTIM_WHITE_MODE)) return@loadApp
            /** 通过在 [SplashActivityClass] 里取到应用的版本号 */
            SplashActivityClass.hook {
                injectMember {
                    method {
                        name = "doOnCreate"
                        param(BundleClass)
                    }
                    afterHook { hookQQBaseChatPie(instance<Activity>().versionName) }
                }
            }
            /**
             * 干掉消息收发功能的电源锁
             * 每个版本的差异暂未做排查
             * 旧版本理论上没有这个类
             */
            findClass(name = "$QQ_PACKAGE_NAME.msf.service.y").hook {
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
            findClass(name = "$QQ_PACKAGE_NAME.activity.QQLSUnlockActivity").hook {
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
            findClass("$QQ_PACKAGE_NAME.activity.QQLSActivity\$14", "ktq").hook {
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
                        param(("com.tencent.qapmsdk.qqbattery.monitor.WakeLockMonitor\$WakeLockEntity").clazz, IntType)
                    }
                    intercept()
                }
                injectMember {
                    method {
                        name = "afterHookedMethod"
                        param(("com.tencent.qapmsdk.qqbattery.monitor.MethodHookParam").clazz)
                    }
                    intercept()
                }
                injectMember {
                    method {
                        name = "beforeHookedMethod"
                        param(("com.tencent.qapmsdk.qqbattery.monitor.MethodHookParam").clazz)
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
        loadApp(TIM_PACKAGE_NAME) {
            hookSystemWakeLock()
            hookNotification()
            hookCoreService(isQQ = false)
            hookModuleRunningInfo(isQQTIM = true)
            hookQQSettingsSettingActivity(isQQ = false)
        }
        loadApp(WECHAT_PACKAGE_NAME) {
            if (prefs.get(DataConst.DISABLE_WECHAT_HOOK)) return@loadApp
            hookSystemWakeLock()
            hookModuleRunningInfo(isQQTIM = false)
            loggerD(msg = "ウイチャット：それが機能するかどうかはわかりませんでした")
        }
    }
}