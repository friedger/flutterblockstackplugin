package blockstack.blockstack

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import org.blockstack.android.sdk.BlockstackSession
import org.blockstack.android.sdk.Scope
import org.blockstack.android.sdk.model.BlockstackConfig
import java.net.URI

class BlockstackPlugin (val registar: Registrar): MethodCallHandler {

  private lateinit var session: BlockstackSession

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "blockstack")
      channel.setMethodCallHandler(BlockstackPlugin(registrar))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    }else if (call.method == "createSession") {
      val appDomain = (call.argument<String>("appDomain"))
      val redirectPath = (call.argument<String>("redirectPath"))
      val manifestPath = (call.argument<String>("manifestPath"))
      val scopes = arrayOf("store_write") //Scope.StoreWrite.name
      //val scopes = (call.argument<Array<String>>("scopes"))
      Log.d("BlockstackPlugin", "$appDomain $redirectPath")
      val config = BlockstackConfig(URI(appDomain), redirectPath!!, manifestPath!!, scopes!!.map { s -> Scope.fromJSName(s) }.toTypedArray())
      session = BlockstackSession(registar.context(), config)
      result.success(null)
    } else {
      result.notImplemented()
    }
  }
}
