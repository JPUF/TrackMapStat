package com.jlbennett.trackmapstat.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.jlbennett.trackmapstat.database.RunContract.RunEntry

class DBHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "runDB.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sqlCreate = "CREATE TABLE ${RunEntry.TABLE_NAME}(" +
                "${RunEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${RunEntry.COLUMN_NAME} VARCHAR(128), " +
                "${RunEntry.COLUMN_DISTANCE} REAL, " +
                "${RunEntry.COLUMN_TIME} REAL);"
        db?.execSQL(sqlCreate)
        Log.d("TrackDB", "DBHelper onCreate")
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS ${RunEntry.TABLE_NAME}")
        onCreate(db)
    }

}