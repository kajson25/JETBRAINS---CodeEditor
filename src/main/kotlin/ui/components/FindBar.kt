package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FindBar(
    query: String,
    totalMatches: Int,
    currentMatchIndex: Int,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEEEEE))
                .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
            modifier =
                Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .background(Color.White)
                    .padding(6.dp),
        )

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("🔽", modifier = Modifier.clickable { onNext() }.padding(horizontal = 4.dp))
            Text("🔼", modifier = Modifier.clickable { onPrev() }.padding(horizontal = 4.dp))
            Text("${if (totalMatches == 0) "0" else currentMatchIndex + 1}/$totalMatches", fontSize = 13.sp)
            Text("❌", modifier = Modifier.clickable { onClose() }.padding(start = 8.dp))
        }
    }
}
