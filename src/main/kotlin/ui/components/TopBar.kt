package ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBar(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("Kotlin Script Runner", fontSize = 18.sp)
        Text(
            text = if (isDark) "🌙 Dark" else "🌞 Light",
            fontSize = 14.sp,
            modifier = Modifier.clickable { onToggleTheme() },
        )
    }
}
