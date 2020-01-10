package com.jlbennett.trackmapstat.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import com.jlbennett.trackmapstat.database.RunContract.RunEntry

/*
    The app's ContentProvider. Responsible for giving access to the persistent SQLite DB.
 */
class RunContentProvider : ContentProvider() {

    private val RUNS = 1
    private val RUN_ID = 2
    private var dbHelper: DBHelper? = null

    companion object {
        lateinit var uriMatcher: UriMatcher
    }

    init {
        //The URI Matcher is responsible for directing queries depending on their nature.
        //Especially whether or not they pertain to the table as a whole, or individual items.
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        uriMatcher.addURI(RunContract.AUTHORITY, RunEntry.TABLE_NAME, RUNS)
        uriMatcher.addURI(RunContract.AUTHORITY, "${RunEntry.TABLE_NAME}/#", RUN_ID)
    }

    override fun onCreate(): Boolean {
        //Creates a new DBHelper, in turn, initialising the DB.
        dbHelper = DBHelper(this.context)
        return true
    }

    /*
        Allows for data to be entered into the database.
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = uriMatcher.match(uri)
        val db = dbHelper!!.writableDatabase
        val id: Long
        when (uriType) {
            //Only handles URI's pertaining to the whole DB, as the individual row doesn't yet exist.
            RUNS -> {
                id = db.insert(RunEntry.TABLE_NAME, null, values)
                Log.d("TrackDB", "ContentProvider insert")
            }
            else -> throw IllegalArgumentException("Bad URI: $uri")
        }
        //Change is notified to ensure that View components (recyclerViews) are updated to reflect the new item.
        context!!.contentResolver.notifyChange(uri, null)
        return Uri.parse("${RunEntry.TABLE_NAME}/$id")
    }

    /*
        The method that gives read access to the database.
     */
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

    /*
        Only called if the database needs to be updated without destroying existing data.
        I have found this unnecessary
     */
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        Log.d("TrackDB", "ContentProvider update")
        return 0
    }

    /*
        Called when items in the database are to be deleted.
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        Log.d("TrackDB", "ContentProvider delete")
        return 0
    }

    override fun getType(uri: Uri): String? {
        return if (uri.lastPathSegment == null) {
            "vnd.android.cursor.dir/RunContentProvider.data.text"
        } else {
            "vnd.android.cursor.item/RunContentProvider.data.text"
        }
    }
}
