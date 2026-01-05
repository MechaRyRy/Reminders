package com.meowmakers.reminders

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.meowmakers.reminders.notification.ServiceBinding
import com.meowmakers.reminders.ui.theme.RemindersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            checkPermissionsAndStart()
            RemindersTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // TODO: RYRY: Create a composable version of the alert dialog.
    // Move this permission stuff to a separate class.
    private val backgroundLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startService()
        } else {
            Logger.d(this::class.java, "Background location denied")
        }
    }

    private val foregroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val foregroundLocationGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val notificationGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] == true

        if (foregroundLocationGranted && notificationGranted) {
            checkAndRequestBackgroundLocation()
        }
    }

    private fun checkPermissionsAndStart() {
        val permissionsNeeded = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        foregroundPermissionLauncher.launch(permissionsNeeded.toTypedArray())
    }

    private fun checkAndRequestBackgroundLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val hasBackgroundLocation = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasBackgroundLocation) {
                startService()
            } else {
                showBackgroundExplanationDialog()
            }
        } else {
            startService()
        }
    }

    private fun startService() = ServiceBinding(this).startAndBindService()

    private fun showBackgroundExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Allow Background Location")
            .setMessage("This app needs location access even when closed to trigger your reminders.")
            .setPositiveButton("Settings") { _, _ ->
                backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            .show()
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RemindersTheme {
        Greeting("Android")
    }
}