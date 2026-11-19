package com.example.contactlist.data

import android.provider.BaseColumns

object ContactContract {

    const val TABLE_NAME = "contacts"

    object Columns : BaseColumns {
        const val _ID = BaseColumns._ID // "_id"
        const val COLUMN_NOM = "nom"
        const val COLUMN_PRENOM = "prenom"
        const val COLUMN_TELEPHONE = "telephone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHOTO_URI = "photo_uri"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
    }

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE $TABLE_NAME (" +
                "${Columns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${Columns.COLUMN_NOM} TEXT NOT NULL, " +
                "${Columns.COLUMN_PRENOM} TEXT NOT NULL, " +
                "${Columns.COLUMN_TELEPHONE} TEXT NOT NULL, " +
                "${Columns.COLUMN_EMAIL} TEXT, " +
                "${Columns.COLUMN_PHOTO_URI} TEXT, " +
                "${Columns.COLUMN_LATITUDE} REAL, " +
                "${Columns.COLUMN_LONGITUDE} REAL" +
                ");"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
}
