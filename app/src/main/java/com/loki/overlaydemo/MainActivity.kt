package com.loki.overlaydemo

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.loki.overlaydemo.ui.theme.OverlayDemoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OverlayDemoTheme {

                val coroutineScope = rememberCoroutineScope()
                var isDialogVisible by remember { mutableStateOf(false) }

                LaunchedEffect(key1 = Unit) {
                    isDialogVisible = !Settings.canDrawOverlays(this@MainActivity)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        if (isDialogVisible) {
                            PermissionDialog(
                                onRequest = {
                                    isDialogVisible = false
                                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).also {
                                        startActivity(it)
                                    }
                                },
                                content = "Request permission to overlay over other apps"
                            )
                        }

                        Button(
                            onClick = {
                                if (Settings.canDrawOverlays(this@MainActivity)) {
                                    coroutineScope.launch {
                                        delay(10 * 1000L)
                                        startService(
                                            Intent(
                                                this@MainActivity,
                                                ForegroundService::class.java
                                            )
                                        )
                                    }
                                } else {
                                    Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).also {
                                        startActivity(it)
                                    }
                                }
                            }
                        ) {
                            Text(text = "Open Layout")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionDialog(
    onRequest: () -> Unit,
    content: String
) {
    Dialog(
        onDismissRequest = {}, properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .size(200.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = content)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRequest) {
                Text(text = "Request")
            }
        }
    }
}