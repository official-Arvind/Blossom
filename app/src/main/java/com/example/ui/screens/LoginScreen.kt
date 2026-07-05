package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.datastore.preferences.core.edit
import com.example.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCompletingLogin by remember { mutableStateOf(false) }
    var webView: WebView? = null
    var visitorDataFromWeb by remember { mutableStateOf("") }
    var dataSyncIdFromWeb by remember { mutableStateOf("") }

    fun completeLogin(onClose: () -> Unit) {
        if (isCompletingLogin) return
        isCompletingLogin = true
        coroutineScope.launch {
            val currentCookie = CookieManager.getInstance().getCookie("https://music.youtube.com").orEmpty()
            if (currentCookie.isBlank()) {
                isCompletingLogin = false
                onClose()
                return@launch
            }
            val savedVisitorData = visitorDataFromWeb
            val savedDataSyncId = dataSyncIdFromWeb
            
            // For now, since we haven't linked YouTube.kt yet, we will just save to dataStore
            withContext(Dispatchers.IO) {
                context.dataStore.edit { settings ->
                    settings[InnerTubeCookieKey] = currentCookie
                    settings[VisitorDataKey] = savedVisitorData
                    settings[DataSyncIdKey] = savedDataSyncId
                }
            }
            withContext(Dispatchers.Main) {
                onLoginSuccess()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Login") },
            navigationIcon = {
                IconButton(onClick = { completeLogin(onBack) }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            }
        )
        
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { webViewContext ->
                WebView(webViewContext).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView, url: String?) {
                            loadUrl("javascript:Android.onRetrieveVisitorData(window.yt.config_.VISITOR_DATA)")
                            loadUrl("javascript:Android.onRetrieveDataSyncId(window.yt.config_.DATASYNC_ID)")
                            
                            if (url?.contains("music.youtube.com") == true &&
                                !isCompletingLogin &&
                                CookieManager.getInstance().getCookie("https://music.youtube.com").orEmpty().isNotBlank()
                            ) {
                                completeLogin(onBack)
                            }
                        }
                    }
                    settings.apply {
                        javaScriptEnabled = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                    }
                    addJavascriptInterface(
                        object {
                            @JavascriptInterface
                            fun onRetrieveVisitorData(newVisitorData: String?) {
                                if (newVisitorData != null) visitorDataFromWeb = newVisitorData
                            }

                            @JavascriptInterface
                            fun onRetrieveDataSyncId(newDataSyncId: String?) {
                                if (newDataSyncId != null) dataSyncIdFromWeb = newDataSyncId.substringBefore("||")
                            }
                        },
                        "Android"
                    )
                    webView = this
                    loadUrl("https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fmusic.youtube.com")
                }
            }
        )
    }

    BackHandler {
        val currentWebView = webView
        if (currentWebView?.canGoBack() == true) {
            currentWebView.goBack()
        } else {
            completeLogin(onBack)
        }
    }
}
