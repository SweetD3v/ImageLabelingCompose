package com.dev4life.imagelabeling.ui.components

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SettingsDialogPreview() {
    ShowSettingsDialog(LocalContext.current)
}

@Composable
fun ShowSettingsDialog(context: Context) {
    val shouldShowDialog = remember { mutableStateOf(true) }

    LaunchedEffect(key1 = shouldShowDialog.value) {
        if (!shouldShowDialog.value) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts(
                "package", context.packageName,
                null
            )
            intent.data = uri
            context.startActivity(intent)
        }
    }

    if (shouldShowDialog.value) {
        CustomAlertDialog(shouldShowDialog = shouldShowDialog)
    }
}

@Composable
fun CustomAlertDialog(shouldShowDialog: MutableState<Boolean>) {
    if (shouldShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
            },
            title = { Text(text = "Permissions Required") },
            text = { Text(text = "Please give storage permissions for this app to function properly") },
            confirmButton = {
                Button(
                    onClick = {
                        shouldShowDialog.value = false
                    },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Settings",
                    )
                }
            }
        )
    }
}