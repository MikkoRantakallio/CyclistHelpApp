package com.example.cyclisthelp

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat

class AppViewModel : ViewModel() {

    val CallForHelps: MutableState<Map<String, CallForHelp>> = mutableStateOf(mapOf())

    init {
        val query = com.parse.ParseQuery.getQuery<com.parse.ParseObject>("CallForHelp")
        query.orderByDescending("createdAt")
        query.findInBackground { cfhs, e ->
            if (e == null) {
                for (parseCFH in cfhs) {
                    val cfh = CallForHelp(
                        objectId = parseCFH.objectId,
                        icon = parseCFH.getString("Icon")!!,
                        title = parseCFH.getString("Title")!!,
                        description = parseCFH.getString("Description")!!,
                        status = parseCFH.getString("Status")!!,
                        createdAt = parseCFH.createdAt,

                    )
                    this.CallForHelps.value += (cfh.objectId!! to cfh)
                }
            } else {
                println("Error: ${e.message}")
            }
        }
    }

    companion object {
        @Volatile
        private var instance: AppViewModel? = null

        fun getInstance(): AppViewModel {
            return instance ?: synchronized(this) {
                instance ?: AppViewModel().also { instance = it }
            }
        }
    }
}