package com.jlbennett.trackmapstat.database

import android.net.Uri
import android.provider.BaseColumns

/*
    A Class to contain a selection of Constants that refer to the structure of the database Table.
 */
class RunContract {
    companion object{
        const val AUTHORITY = "com.jlbennett.trackmapstat.database.RunContentProvider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/${RunEntry.TABLE_NAME}")
    }

    class RunEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "runs"
            const val COLUMN_ID = "_id"
            const val COLUMN_NAME = "name"
            const val COLUMN_DISTANCE = "distance"
            const val COLUMN_TIME = "time"
        }
    }

}