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
import ui.helpers.ScriptLang

@Composable
fun TopBar(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    lang: ScriptLang,
    onToggleLang: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("Kotlin Script Runner", fontSize = 18.sp)
        Row {
            Text(
                text = if (lang == ScriptLang.KOTLIN) "🟦 Kotlin" else "🟥 Swift",
                modifier = Modifier.clickable { onToggleLang() }.padding(horizontal = 8.dp),
            )
            Text(
                text = if (isDark) "🌙" else "🌞",
                modifier = Modifier.clickable { onToggleTheme() },
            )
        }
    }
}
