package com.example.cyclisthelp

import com.parse.ParseObject
import com.parse.ParseQuery
import java.util.Date

data class CallForHelp(
    var objectId: String? = null,
    var icon: String,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var title: String,
    var description: String,
    var status: String,
    var createdAt: Date?
)

{
    fun addToParse(callback: (objectId: String) -> Unit) {
        if (objectId !== null) throw Exception("Help request is already saved to Parse!")

        val parseCFH = ParseObject("CallForHelp")
        parseCFH.put("Icon", icon)
        parseCFH.put("Title", title)
        parseCFH.put("Description", description)
        parseCFH.put("Status", status)

        parseCFH.saveInBackground {
            if (it !== null) throw Exception("Error: ${it.message}")
            objectId = parseCFH.objectId
            callback(parseCFH.objectId)
        }
    }

    fun updateToParse(callback: (objectId: String) -> Unit) {
        if (objectId === null) throw Exception("Help request hasn't been saved to Parse yet!")

        val query = ParseQuery.getQuery<ParseObject>("CallForHelp")
        val parseCFH = query.get(objectId)
        parseCFH.put("Icon", icon)
        parseCFH.put("Title", title)
        parseCFH.put("Description", description)
        parseCFH.put("Status", status)

        parseCFH.saveInBackground {
            if (it !== null) throw Exception("Error: ${it.message}")
            callback(parseCFH.objectId)
        }
    }

    fun deleteFromParse(callback: () -> Unit) {
        if (objectId === null) throw Exception("Call for help hasn't been saved to Parse yet!")

        val query = ParseQuery.getQuery<ParseObject>("CallForHelp")
        val parseCFH = query.get(objectId)
        parseCFH.deleteInBackground {
            if (it !== null) throw Exception("Error: ${it.message}")
            callback()
        }
    }

    companion object {
        fun generateObjectId(): String {
            val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            return (1..10).map { chars.random() }.joinToString("")
        }
    }
}