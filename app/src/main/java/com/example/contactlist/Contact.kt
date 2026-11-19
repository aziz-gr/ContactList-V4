package com.example.contactlist

import android.os.Parcel
import android.os.Parcelable

data class Contact(
    val id: Long = 0L,
    var nom: String,
    var prenom: String,
    var telephone: String,
    var email: String? = null,
    var photoUri: String? = null,
    var photoResId: Int? = null,
    var latitude: Double? = null,
    var longitude: Double? = null
) : Parcelable {

    val fullName: String
        get() = "$prenom $nom".trim()

    val hasValidCoordinates: Boolean
        get() = latitude != null && longitude != null &&
                latitude!! in -90.0..90.0 && longitude!! in -180.0..180.0

    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        nom = parcel.readString() ?: "",
        prenom = parcel.readString() ?: "",
        telephone = parcel.readString() ?: "",
        email = parcel.readString(),
        photoUri = parcel.readString(),
        photoResId = parcel.readValue(Int::class.java.classLoader) as? Int,
        latitude = parcel.readValue(Double::class.java.classLoader) as? Double,
        longitude = parcel.readValue(Double::class.java.classLoader) as? Double
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(nom)
        parcel.writeString(prenom)
        parcel.writeString(telephone)
        parcel.writeString(email)
        parcel.writeString(photoUri)
        parcel.writeValue(photoResId)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact = Contact(parcel)
        override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)
    }
}
