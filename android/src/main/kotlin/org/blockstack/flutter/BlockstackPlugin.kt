package org.blockstack.flutter

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.blockstack.android.sdk.BlockstackSession
import org.blockstack.android.sdk.Executor
import org.blockstack.android.sdk.Scope
import org.blockstack.android.sdk.model.BlockstackConfig
import java.net.URI

class BlockstackPlugin(val registar: Registrar) : MethodCallHandler {

    private lateinit var session: BlockstackSession
    private lateinit var handler: Handler
    private val handlerThread: HandlerThread = HandlerThread("blockstack-flutter")

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "blockstack")
            channel.setMethodCallHandler(BlockstackPlugin(registrar))
        }

        // TODO only store transitKey and the likes in this static variable
        @JvmStatic
        var currentSession: BlockstackSession? = null
        @JvmStatic
        var currentHandler: Handler? = null
        @JvmStatic
        var currentSignInResult: Result? = null
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "createSession") {
            BlockstackSession.doNotVerifyAppLinkConfiguration = true

            val appDomain = (call.argument<String>("appDomain"))
            val redirectPath = (call.argument<String>("redirectPath"))
            val manifestPath = (call.argument<String>("manifestPath"))
            var scopes = call.argument<String>("scopes")
            //var scopeArray = scopes!!.split(",").map { s -> Scope.fromJSName(s) }.toTypedArray()
            var scopeArray = arrayOf(Scope.StoreWrite)
            val config = BlockstackConfig(URI(appDomain), redirectPath!!, manifestPath!!, scopeArray)
            val activity = registar.activity()
            handlerThread.start()
            handler = Handler(handlerThread.looper)

            runOnV8Thread {
                session = BlockstackSession(activity, config, executor = object : Executor {
                    override fun onMainThread(function: (Context) -> Unit) {
                        activity.runOnUiThread {
                            function(activity)
                        }
                    }

                    override fun onV8Thread(function: () -> Unit) {
                        runOnV8Thread(function)
                    }

                    override fun onNetworkThread(function: suspend () -> Unit) {
                        GlobalScope.launch(Dispatchers.IO) {
                            function()
                        }
                    }
                })
                currentSession = session
                currentHandler = handler
                result.success(null)
            }


        } else if (call.method == "redirectToSignIn") {
            currentSignInResult = result
            runOnV8Thread {
                session.redirectUserToSignIn {
                    result.error(it.error, it.error, it.error)
                }
            }
        } else if (call.method == "handlePendingSignIn") {
            val authResponse = call.argument<String>("authResponse")
            runOnV8Thread {
                session.handlePendingSignIn(authResponse!!) {
                    result.success(it.value)
                }
            }
        } else if (call.method == "isUserSignedIn") {
            runOnV8Thread {
                result.success(session.isUserSignedIn())
            }
        } else if (call.method == "loadUserData") {
            runOnV8Thread {
                val userData = session.loadUserData()
                if (userData != null) {
                    result.success(userData.decentralizedID)
                } else {
                    result.error("not logged in ", "not logged in", "not logged in")
                }
            }
        } else {
            result.notImplemented()
        }
    }


    private fun runOnV8Thread(function: () -> Unit) {
        handler.post(function)
    }
}
