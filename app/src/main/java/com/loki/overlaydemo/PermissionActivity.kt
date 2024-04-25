package com.loki.overlaydemo

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loki.overlaydemo.ForegroundService.Companion.INTENT_COMMAND_START
import com.loki.overlaydemo.ui.theme.OverlayDemoTheme
import com.loki.overlaydemo.util.drawOverOtherAppEnabled
import com.loki.overlaydemo.util.startFloatingService

class PermissionActivity : ComponentActivity() {

    private fun showDialog(title: String, message: String) {
        with(AlertDialog.Builder(this)) {
            setTitle(title)
            setMessage(message)
            setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private val overlayIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        if (drawOverOtherAppEnabled()) {
            startFloatingService(INTENT_COMMAND_START)
            finish()
        } else {
            showDialog(
                "Overlay Permission",
                "Permission was not enabled"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            OverlayDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {

                        Text(
                            text = "Permission Required",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                        )

                        Text(
                            text = "To enable floating window, please enable the permission",
                            modifier = Modifier.padding(16.dp, 4.dp)
                        )

                        TextButton(
                            onClick = {
                                resultLauncher.launch(overlayIntent)
                            },
                            modifier = Modifier.padding(16.dp, 8.dp)
                        ) {
                            Text(text = "Open")
                        }
                    }
                }
            }
        }
    }
}