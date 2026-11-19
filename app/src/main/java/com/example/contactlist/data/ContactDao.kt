package com.example.contactlist.data

import android.content.ContentValues
import android.database.Cursor
import com.example.contactlist.Contact

class ContactDao(private val dbHelper: ContactDbHelper) {

    fun insert(contact: Contact): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.Columns.COLUMN_NOM, contact.nom)
            put(ContactContract.Columns.COLUMN_PRENOM, contact.prenom)
            put(ContactContract.Columns.COLUMN_TELEPHONE, contact.telephone)
            put(ContactContract.Columns.COLUMN_EMAIL, contact.email)
            put(ContactContract.Columns.COLUMN_PHOTO_URI, contact.photoUri)
            if (contact.latitude != null) {
                put(ContactContract.Columns.COLUMN_LATITUDE, contact.latitude)
            } else {
                putNull(ContactContract.Columns.COLUMN_LATITUDE)
            }
            if (contact.longitude != null) {
                put(ContactContract.Columns.COLUMN_LONGITUDE, contact.longitude)
            } else {
                putNull(ContactContract.Columns.COLUMN_LONGITUDE)
            }
        }
        return db.insert(ContactContract.TABLE_NAME, null, values)
    }

    fun update(contact: Contact): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ContactContract.Columns.COLUMN_NOM, contact.nom)
            put(ContactContract.Columns.COLUMN_PRENOM, contact.prenom)
            put(ContactContract.Columns.COLUMN_TELEPHONE, contact.telephone)
            put(ContactContract.Columns.COLUMN_EMAIL, contact.email)
            put(ContactContract.Columns.COLUMN_PHOTO_URI, contact.photoUri)
            if (contact.latitude != null) {
                put(ContactContract.Columns.COLUMN_LATITUDE, contact.latitude)
            } else {
                putNull(ContactContract.Columns.COLUMN_LATITUDE)
            }
            if (contact.longitude != null) {
                put(ContactContract.Columns.COLUMN_LONGITUDE, contact.longitude)
            } else {
                putNull(ContactContract.Columns.COLUMN_LONGITUDE)
            }
        }
        val selection = "${ContactContract.Columns._ID} = ?"
        val selectionArgs = arrayOf(contact.id.toString())
        return db.update(ContactContract.TABLE_NAME, values, selection, selectionArgs)
    }

    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase
        val selection = "${ContactContract.Columns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        return db.delete(ContactContract.TABLE_NAME, selection, selectionArgs)
    }

    fun getAll(): List<Contact> {
        val db = dbHelper.readableDatabase
        val orderBy = "${ContactContract.Columns.COLUMN_NOM} COLLATE NOCASE ASC, ${ContactContract.Columns.COLUMN_PRENOM} COLLATE NOCASE ASC"
        val cursor = db.query(
            ContactContract.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            orderBy
        )

        return cursor.use { c ->
            val list = mutableListOf<Contact>()
            while (c.moveToNext()) {
                list.add(extractContactFromCursor(c))
            }
            list
        }
    }

    fun getById(id: Long): Contact? {
        val db = dbHelper.readableDatabase
        val selection = "${ContactContract.Columns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.query(
            ContactContract.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        return cursor.use { c ->
            if (c.moveToFirst()) {
                extractContactFromCursor(c)
            } else {
                null
            }
        }
    }

    fun search(query: String): List<Contact> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) {
            return getAll()
        }

        val db = dbHelper.readableDatabase
        val selection = "${ContactContract.Columns.COLUMN_NOM} LIKE ? OR " +
                "${ContactContract.Columns.COLUMN_PRENOM} LIKE ? OR " +
                "${ContactContract.Columns.COLUMN_TELEPHONE} LIKE ?"
        val searchPattern = "%$trimmedQuery%"
        val selectionArgs = arrayOf(searchPattern, searchPattern, searchPattern)
        val orderBy = "${ContactContract.Columns.COLUMN_NOM} COLLATE NOCASE ASC, ${ContactContract.Columns.COLUMN_PRENOM} COLLATE NOCASE ASC"

        val cursor = db.query(
            ContactContract.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            orderBy
        )

        return cursor.use { c ->
            val list = mutableListOf<Contact>()
            while (c.moveToNext()) {
                list.add(extractContactFromCursor(c))
            }
            list
        }
    }

    private fun extractContactFromCursor(cursor: Cursor): Contact {
        val idIndex = cursor.getColumnIndexOrThrow(ContactContract.Columns._ID)
        val nomIndex = cursor.getColumnIndexOrThrow(ContactContract.Columns.COLUMN_NOM)
        val prenomIndex = cursor.getColumnIndexOrThrow(ContactContract.Columns.COLUMN_PRENOM)
        val phoneIndex = cursor.getColumnIndexOrThrow(ContactContract.Columns.COLUMN_TELEPHONE)
        val emailIndex = cursor.getColumnIndexOrThrow(ContactContract.Columns.COLUMN_EMAIL)
        val photoIndex = cursor.getColumnIndexOrThrow(ContactContract.Columns.COLUMN_PHOTO_URI)
        val latIndex = cursor.getColumnIndexOrThrow(ContactContract.Columns.COLUMN_LATITUDE)
        val lngIndex = cursor.getColumnIndexOrThrow(ContactContract.Columns.COLUMN_LONGITUDE)

        val id = cursor.getLong(idIndex)
        val nom = cursor.getString(nomIndex)
        val prenom = cursor.getString(prenomIndex)
        val telephone = cursor.getString(phoneIndex)
        val email = if (cursor.isNull(emailIndex)) null else cursor.getString(emailIndex)
        val photoUri = if (cursor.isNull(photoIndex)) null else cursor.getString(photoIndex)
        val latitude = if (cursor.isNull(latIndex)) null else cursor.getDouble(latIndex)
        val longitude = if (cursor.isNull(lngIndex)) null else cursor.getDouble(lngIndex)

        return Contact(
            id = id,
            nom = nom,
            prenom = prenom,
            telephone = telephone,
            email = email,
            photoUri = photoUri,
            latitude = latitude,
            longitude = longitude
        )
    }
}
