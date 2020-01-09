package com.jlbennett.trackmapstat.database

import android.net.Uri
import android.provider.BaseColumns

class RunContract {
    companion object{
        const val AUTHORITY = "com.jlbennett.trackmapstat.database.RunContentProvider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/${RunEntry.TABLE_NAME}")
    }

    class RunEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "runs"
            val COLUMN_ID = "_id"
            val COLUMN_NAME = "name"
            val COLUMN_DISTANCE = "distance"
            val COLUMN_TIME = "time"
        }
    }

}