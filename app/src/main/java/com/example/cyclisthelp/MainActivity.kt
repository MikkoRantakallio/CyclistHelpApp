package com.example.cyclisthelp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.recreate

import com.example.cyclisthelp.ui.theme.CyclistHelpTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.logging.Handler

class MainActivity : ComponentActivity() {

    private val viewModel = AppViewModel.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this as Context

        setContent {
            MainActivityContent(
                viewModel = viewModel,
                onCfhListItemClick = {
                    val intent = Intent(context, FormActivity::class.java)
                    intent.putExtra("objectId", it.objectId)
                    context.startActivity(intent)                },
                onAddClick = {
                    val intent = Intent(context, FormActivity::class.java)
                    context.startActivity(intent)                },
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityContent(
    viewModel: AppViewModel,
    onCfhListItemClick: (cfh: CallForHelp) -> Unit,
    onAddClick: () -> Unit,
) {
    val cfhs = viewModel.CallForHelps.value
    MaterialTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Help requests") }) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { onAddClick() },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                    text = { Text("Add") }
                )
            },
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {
                NoteList(cfhs, onCfhListItemClick = { onCfhListItemClick(it) })
            }
        }
    }
}

@Composable
fun CfhListItem(cfh: CallForHelp, onCfhListItemClick: (cfh: CallForHelp) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onCfhListItemClick(cfh) })
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val imageModifier = Modifier
            .size(50.dp)

        when (cfh.status) {
            "Open" ->
                Image(painter = painterResource(R.drawable.open),
                    contentDescription = stringResource(id = R.string.open_description),
                    modifier = imageModifier
                    )

            "Ongoing" ->
                Image(
                    painter = painterResource(R.drawable.ongoing),
                    contentDescription = stringResource(id = R.string.ongoing_description),
                    modifier = imageModifier
                )

            "Closed" ->
                Image(
                    painter = painterResource(R.drawable.closed),
                    contentDescription = stringResource(id = R.string.closed_description),
                    modifier = imageModifier
                )
        }

//        Text(text = cfh.icon, fontSize = 32.sp, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = cfh.title, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = SimpleDateFormat("dd.MM.YYYY HH:mm").format(cfh.createdAt),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = cfh.description,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tähän vois tulla vaikka karttalinkki",
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun NoteList(cfhs: Map<String, CallForHelp>, onCfhListItemClick: (cfh: CallForHelp) -> Unit) {
    LazyColumn {
        items(cfhs.entries.toList()) { (_, cfh) ->
            CfhListItem(cfh = cfh, onCfhListItemClick = onCfhListItemClick)
        }
    }
}
