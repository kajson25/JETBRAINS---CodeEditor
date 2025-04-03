@file:Suppress("ktlint:standard:no-wildcard-imports")

package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.helpers.ScriptLang
import java.io.File

@Suppress("ktlint:standard:function-naming")
@Composable
fun ScriptSidebar(
    onScriptSelected: (String) -> Unit,
    onSaveScript: (String) -> Unit,
    lang: ScriptLang,
) {
    val scriptDir = File("scripts")
    if (!scriptDir.exists()) scriptDir.mkdir()

    var scriptFiles by remember { mutableStateOf(scriptDir.listFiles()?.filter { it.extension in listOf("kts", "swift") } ?: emptyList()) }
    var showSaveInput by remember { mutableStateOf(false) }
    var scriptName by remember { mutableStateOf("") }

    Column(modifier = Modifier.width(200.dp).background(Color(0xFFDDDDDD)).padding(8.dp)) {
        Text("📁 Scripts", modifier = Modifier.padding(bottom = 8.dp))

        scriptFiles.forEach { file ->
            Text(
                text = file.name,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onScriptSelected(file.readText()) }
                        .padding(4.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        if (showSaveInput) {
            Column {
                Text("Save as:")
                androidx.compose.material.OutlinedTextField(
                    value = scriptName,
                    onValueChange = { scriptName = it },
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(onClick = {
                    if (scriptName.isNotBlank()) {
                        val ext = if (lang == ScriptLang.KOTLIN) "kts" else "swift"
                        val target = File(scriptDir, "$scriptName.$ext")
                        onSaveScript(target.absolutePath)
                        showSaveInput = false
                        scriptName = ""
                        scriptFiles = scriptDir.listFiles()?.filter { it.extension in listOf("kts", "swift") } ?: emptyList()
                    }
                }) {
                    Text("Save")
                }
            }
        } else {
            Button(onClick = { showSaveInput = true }) {
                Text("Save Current")
            }
        }
    }
}
