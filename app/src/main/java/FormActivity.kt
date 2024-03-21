package com.example.cyclisthelp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.util.Date

class FormActivity : ComponentActivity() {
    private val viewModel = AppViewModel.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val objectId = intent.getStringExtra("objectId")
        val cfh = if (objectId !== null) viewModel.CallForHelps.value[objectId] else null

        setContent {
            FormActivityContent(
                cfh,
                onAddClick = { icon: String, title: String, description: String, status: String ->
                    if (cfh !== null) return@FormActivityContent
                    val newCFH = CallForHelp(
                        icon = icon,
                        title = title,
                        description = description,
                        status = status,
                        createdAt = java.util.Calendar.getInstance().time
                    )
                    newCFH.addToParse {
                        viewModel.CallForHelps.value += (it to newCFH)
                        finish()
                    }
                },
                onSaveClick = { icon: String, title: String, description: String, status: String ->
                    if (cfh === null) return@FormActivityContent
                    val updatedCFH = cfh.copy()
                    updatedCFH.icon = icon
                    updatedCFH.title = title
                    updatedCFH.description = description
                    updatedCFH.status = status
                    updatedCFH.updateToParse {
                        viewModel.CallForHelps.value += (it to updatedCFH)
                        finish()
                    }
                },
                onDeleteClick = {
                    if (cfh === null) return@FormActivityContent
                    viewModel.CallForHelps.value = viewModel.CallForHelps.value.filter {
                        it.value.objectId != cfh.objectId
                    }
                    cfh.deleteFromParse {
                        finish()
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormActivityContent(
    callforhelp: CallForHelp?,
    onAddClick: (icon: String, title: String, description: String, status: String) -> Unit,
    onSaveClick: (icon: String, title: String, description: String, status: String) -> Unit,
    onDeleteClick: () -> Unit,
) {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(callforhelp?.let { "Edit row" } ?: ("Add row")) })
            },
            floatingActionButton = {
                if (callforhelp !== null) {
                    ExtendedFloatingActionButton(
                        onClick = { onDeleteClick() },
                        icon = { Icon(Icons.Filled.Delete, "Delete") },
                        text = { Text("Delete") },
                    )
                }
            },
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                CfhForm(callforhelp = callforhelp, onSave = { icon, title, description, status ->
                    if (callforhelp === null) {
                        onAddClick(icon, title, description, status)
                    } else {
                        onSaveClick(icon, title, description, status)
                    }
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CfhForm(
    callforhelp: CallForHelp?,
    onSave: (icon: String, title: String, description: String, status: String) -> Unit
) {
    var title by remember { mutableStateOf(TextFieldValue(callforhelp?.title ?: "")) }
    var description by remember { mutableStateOf(TextFieldValue(callforhelp?.description ?: "")) }

    val radioOptions = listOf("Open","Ongoing","Closed")
    var status by remember { mutableStateOf(radioOptions[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

//        Spacer(modifier = Modifier.height(16.dp).padding(0.dp))
        radioOptions.forEach { caseStatus ->

            Row(verticalAlignment=Alignment.CenterVertically) {
                RadioButton(
                    selected = (caseStatus == status),
                    onClick = {
                        status = caseStatus
                    }
                )
                Text(
                    text = caseStatus,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 3.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            label = { Text(text = "Title") },
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            label = { Text(text = "Content") },
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {

                var icon: String ="C"

                if (status === "Open"){
                    icon = "O"
                }
                if (status === "Ongoing"){
                    icon = "G"
                }
                if (status === "Closed"){
                    icon = "C"
                }

                onSave(icon, title.text, description.text, status )
            },
            Modifier.fillMaxWidth()
        ) {
            Text(text = "Save")
        }
    }
}