package com.dev4life.imagelabeling.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev4life.imagelabeling.R

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun CurationDialogPreview() {
    CurationDialog(5, 100)
}

@Composable
fun CurationDialog(counts: Int, total: Int, onCancel: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background.copy(
            alpha = 0.975f
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()

            ) {
                Image(
                    painter = painterResource(id = R.drawable.curating_svg),
                    contentDescription = "Curating svg",
                    modifier = Modifier.fillMaxWidth(fraction = 0.75f)
                )

                Text(
                    text = "Curating photos",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier.padding(top = 24.dp),
                    color = colorResource(R.color.black)
                )

                val percent = try {
                    counts * 100 / total
                } catch (e: Exception) {
                    0
                }
                val textValue = if (percent > 0) "$percent%" else "0%"

                Text(
                    text = "$counts of $total Photos ($textValue)",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    modifier = Modifier.padding(top = 16.dp),
                    color = colorResource(R.color.black)
                )

                Button(
                    onClick = { onCancel() },
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "This could take a while",
                    fontSize = 14.sp,
                    color = colorResource(R.color.black).copy(alpha = 0.7f)
                )

                Text(
                    text = "Feel free to cancel to see the photos that are curated already",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp),
                    color = colorResource(R.color.black).copy(alpha = 0.6f)
                )
            }
        }
    }
}