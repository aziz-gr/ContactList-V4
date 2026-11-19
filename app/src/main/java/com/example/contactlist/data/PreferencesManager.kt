package com.example.contactlist.data

import android.content.Context
import android.graphics.Color
import androidx.preference.PreferenceManager

class PreferencesManager(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    companion object {
        const val KEY_TAILLE_POLICE = "taille_police"
        const val KEY_COULEUR_PREFEREE = "couleur_preferee"
        const val KEY_APPEL_DEPUIS_LISTE = "appel_depuis_liste"

        const val DEFAULT_TAILLE_POLICE = "16"
        const val DEFAULT_COULEUR_PREFEREE = "#212121"
        const val DEFAULT_APPEL_DEPUIS_LISTE = false
    }

    val fontSizeSp: Float
        get() {
            val strVal = sharedPreferences.getString(KEY_TAILLE_POLICE, DEFAULT_TAILLE_POLICE) ?: DEFAULT_TAILLE_POLICE
            return strVal.toFloatOrNull() ?: 16f
        }

    val textColorInt: Int
        get() {
            val colorHex = sharedPreferences.getString(KEY_COULEUR_PREFEREE, DEFAULT_COULEUR_PREFEREE) ?: DEFAULT_COULEUR_PREFEREE
            return try {
                Color.parseColor(colorHex)
            } catch (_: Exception) {
                Color.parseColor(DEFAULT_COULEUR_PREFEREE)
            }
        }

    val isCallFromListEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_APPEL_DEPUIS_LISTE, DEFAULT_APPEL_DEPUIS_LISTE)
}
