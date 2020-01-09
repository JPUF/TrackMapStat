package com.jlbennett.trackmapstat.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import com.jlbennett.trackmapstat.database.RunContract.RunEntry

class RunContentProvider : ContentProvider() {

    private val RUNS = 1
    private val RUN_ID = 2
    private var dbHelper: DBHelper? = null

    companion object {
        lateinit var uriMatcher: UriMatcher
    }

    init {
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(RunContract.AUTHORITY, RunEntry.TABLE_NAME, RUNS)
        uriMatcher.addURI(RunContract.AUTHORITY, "${RunEntry.TABLE_NAME}/#", RUN_ID)
    }

    override fun onCreate(): Boolean {
        dbHelper = DBHelper(this.context)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = uriMatcher.match(uri)
        val db = dbHelper!!.writableDatabase
        val id: Long
        when (uriType) {
            RUNS -> {
                id = db.insert(RunEntry.TABLE_NAME, null, values)
                Log.d("TrackDB", "ContentProvider insert")
            }
            else -> throw IllegalArgumentException("Bad URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return Uri.parse("${RunEntry.TABLE_NAME}/$id")
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = RunEntry.TABLE_NAME
        when (uriMatcher.match(uri)) {
            RUN_ID -> queryBuilder.appendWhere("${RunEntry.COLUMN_ID} = ${uri.lastPathSegment}")
            RUNS -> {
            }
            else -> throw java.lang.IllegalArgumentException("Bad URI: $uri")
        }

        val cursor = queryBuilder.query(
            dbHelper!!.readableDatabase,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to delete one or more rows")
    }

    override fun getType(uri: Uri): String? {
        return if (uri.lastPathSegment == null) {
            "vnd.android.cursor.dir/RunContentProvider.data.text"
        } else {
            "vnd.android.cursor.item/RunContentProvider.data.text"
        }
    }
}
