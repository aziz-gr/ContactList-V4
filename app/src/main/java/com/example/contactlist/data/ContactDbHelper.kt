package com.example.contactlist.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContactDbHelper private constructor(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "contacts.db"
        const val DATABASE_VERSION = 1

        @Volatile
        private var INSTANCE: ContactDbHelper? = null

        fun getInstance(context: Context): ContactDbHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = ContactDbHelper(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ContactContract.SQL_CREATE_ENTRIES)
        insertSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(ContactContract.SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    private fun insertSampleData(db: SQLiteDatabase) {
        val sampleContacts = listOf(
            ContentValues().apply {
                put(ContactContract.Columns.COLUMN_NOM, "Dupont")
                put(ContactContract.Columns.COLUMN_PRENOM, "Jean")
                put(ContactContract.Columns.COLUMN_TELEPHONE, "+33 6 12 34 56 78")
                put(ContactContract.Columns.COLUMN_EMAIL, "jean.dupont@email.com")
                put(ContactContract.Columns.COLUMN_LATITUDE, 48.8566)
                put(ContactContract.Columns.COLUMN_LONGITUDE, 2.3522)
            },
            ContentValues().apply {
                put(ContactContract.Columns.COLUMN_NOM, "Curie")
                put(ContactContract.Columns.COLUMN_PRENOM, "Marie")
                put(ContactContract.Columns.COLUMN_TELEPHONE, "+33 6 98 76 54 32")
                put(ContactContract.Columns.COLUMN_EMAIL, "marie.curie@science.fr")
                put(ContactContract.Columns.COLUMN_LATITUDE, 48.8462)
                put(ContactContract.Columns.COLUMN_LONGITUDE, 2.3444)
            },
            ContentValues().apply {
                put(ContactContract.Columns.COLUMN_NOM, "Pesquet")
                put(ContactContract.Columns.COLUMN_PRENOM, "Thomas")
                put(ContactContract.Columns.COLUMN_TELEPHONE, "+33 6 11 22 33 44")
                put(ContactContract.Columns.COLUMN_EMAIL, "thomas.pesquet@esa.int")
                put(ContactContract.Columns.COLUMN_LATITUDE, 43.6047)
                put(ContactContract.Columns.COLUMN_LONGITUDE, 1.4442)
            },
            ContentValues().apply {
                put(ContactContract.Columns.COLUMN_NOM, "Martin")
                put(ContactContract.Columns.COLUMN_PRENOM, "Sophie")
                put(ContactContract.Columns.COLUMN_TELEPHONE, "+33 7 44 55 66 77")
                put(ContactContract.Columns.COLUMN_EMAIL, "sophie.martin@tech.org")
                putNull(ContactContract.Columns.COLUMN_LATITUDE)
                putNull(ContactContract.Columns.COLUMN_LONGITUDE)
            },
            ContentValues().apply {
                put(ContactContract.Columns.COLUMN_NOM, "Bernard")
                put(ContactContract.Columns.COLUMN_PRENOM, "Lucas")
                put(ContactContract.Columns.COLUMN_TELEPHONE, "+33 6 55 44 33 22")
                put(ContactContract.Columns.COLUMN_EMAIL, "lucas.bernard@work.com")
                putNull(ContactContract.Columns.COLUMN_LATITUDE)
                putNull(ContactContract.Columns.COLUMN_LONGITUDE)
            }
        )

        for (values in sampleContacts) {
            db.insert(ContactContract.TABLE_NAME, null, values)
        }
    }
}
