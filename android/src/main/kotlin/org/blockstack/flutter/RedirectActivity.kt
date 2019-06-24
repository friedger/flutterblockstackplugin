package org.blockstack.flutter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class RedirectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.action == Intent.ACTION_VIEW) {
            handleAuthResponse(intent)
        }
    }

    private fun handleAuthResponse(intent: Intent) {
        val response = intent.data
        if (response != null) {
            val authResponse = response.getQueryParameter("authResponse")
            if (authResponse!= null) {

                BlockstackPlugin.currentHandler!!.post {
                    if (BlockstackPlugin.currentSession != null) {
                        BlockstackPlugin.currentSession!!.handlePendingSignIn(authResponse) {
                            if (it.hasValue) {
                                // The user is now signed in!
                                runOnUiThread {
                                    Log.d("RedirectActivity", "user logged in")
                                    if (BlockstackPlugin.currentSignInResult != null) {
                                        BlockstackPlugin.currentSignInResult!!.success(it.value)
                                    }
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}