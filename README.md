# 📱 Application Liste de Contacts (TP Android V4)

[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![Android SDK](https://img.shields.io/badge/SDK-24%2B-green.svg)](https://developer.android.com/)
[![Material Design 3](https://img.shields.io/badge/Design-Material%203-blue.svg)](https://m3.material.io/)
[![SQLite](https://img.shields.io/badge/Storage-SQLite-lightgrey.svg)](https://developer.android.com/reference/android/database/sqlite/package-summary)
[![Google Maps SDK](https://img.shields.io/badge/Maps-SDK-red.svg)](https://developers.google.com/maps)

Application Android native développée en **Kotlin** avec des **interfaces XML**, créée dans le cadre du **TP Android — Contacts V4**. Cette version fait évoluer l'application V3 en ajoutant la **persistance locale via SQLite** (`contacts.db`), un écran de **préférences utilisateur** avec `SharedPreferences`, et une gestion dynamique complète du **CRUD**.

---

## 🚀 Nouvelles Fonctionnalités & Architecture (V4)

### 1. 💾 Persistance des Données avec SQLite (`contacts.db`)
- **API Native SQLite** : Utilisation de `SQLiteOpenHelper`, `SQLiteDatabase`, `ContentValues` et `Cursor` (sans framework Room).
- **Architecture de données (`com.example.contactlist.data`)** :
  - **`ContactContract`** : Regroupe le nom de la table `contacts` et l'ensemble des noms de colonnes (`_id`, `nom`, `prenom`, `telephone`, `email`, `photo_uri`, `latitude`, `longitude`). Aucun nom de colonne n'est écrit en dur dans le code.
  - **`ContactDbHelper`** : Singleton d'accès à la base de données (`contacts.db`). Crée la table dans `onCreate` et y insère 5 contacts de démonstration au premier lancement.
  - **`ContactDao`** : Seule classe contenant les requêtes SQL paramétrées (`insert`, `update`, `delete`, `getAll`, `getById`, `search`).
- **Sécurité & Requêtes Paramétrées** : Toutes les valeurs variables utilisent des arguments `?` (`selectionArgs`) pour bloquer les injections SQL.
- **Accès Asynchrone (Coroutines)** : Toutes les opérations de base de données s'exécutent hors du thread principal via `viewModelScope` et `withContext(Dispatchers.IO)`.
- **Tri Insensible à la Casse** : Les contacts sont ordonnés par nom puis prénom via `COLLATE NOCASE`.

### 2. ⚙️ Écran de Préférences Utilisateur (`SharedPreferences`)
- **`ParametresFragment`** : Hérite de `PreferenceFragmentCompat` et accessible depuis l'icône de paramètres dans la barre d'application.
- **3 Préférences Personnalisables** :
  1. **Taille de police (`taille_police`)** : Petite (14 sp), Moyenne (16 sp), Grande (20 sp).
  2. **Couleur préférée du texte (`couleur_preferee`)** : Noir (`#212121`), Bleu (`#1565C0`), Vert (`#2E7D32`), Rouge (`#C62828`), Violet (`#6A1B9A`).
  3. **Appel depuis la liste (`appel_depuis_liste`)** : Interrupteur (Switch) affichant ou masquant un bouton d'appel direct sur chaque ligne de la `RecyclerView`.
- **`PreferencesManager`** : Classe centralisant la lecture et la conversion des préférences.
- **Application Immédiate** : Les réglages sont relus dans `onResume()` et appliqués directement à l'interface sans nécessiter de redémarrage de l'application.

### 3. ➕ Opérations CRUD Complètes
- **Create (Ajout)** : Bouton flottant « `+` » (FAB) dans la liste ouvrant `DetailContactFragment` en mode création.
- **Read (Lecture)** : Chargement persistant depuis la base SQLite à chaque affichage.
- **Update (Modification)** : Enregistrement des modifications (informations, photo, coordonnées GPS) mettant à jour la ligne SQLite correspondante.
- **Delete (Suppression)** : Bouton « Supprimer » dans le détail demandant une confirmation (`AlertDialog`) puis supprimant le contact de la base SQLite.

---

## 🔒 Sécurité de la Clé API Google Maps

Afin de respecter la règle de non-publication des clés privées sur GitHub :
1. La clé API est stockée dans le fichier `local.properties` (exclu du suivi Git via `.gitignore`) :
   ```properties
   MAPS_API_KEY=VOTRE_CLE_API_GOOGLE_MAPS
   ```
2. Le fichier `app/build.gradle.kts` extrait la variable `MAPS_API_KEY` pour l'injecter dynamiquement dans le manifest.
3. Le fichier `AndroidManifest.xml` utilise la placeholder `${MAPS_API_KEY}`. Aucune clé réelle n'est présente dans le dépôt public.

---

## 📞 Fonctionnalités Conservées de la V3

- **Architecture à activité unique (`MainActivity`)** hébergeant des fragments dynamiques dans un `FragmentContainerView`.
- **Recherche réactive** avec filtrage au fil de la saisie (`doOnTextChanged`).
- **Appels téléphoniques (`Intent.ACTION_DIAL`)** sans permission requise.
- **Envoi d'e-mails (`Intent.ACTION_SENDTO`)**.
- **Gestion des photos** avec stockage interne permanent dans `filesDir`.
- **Carte Google Maps (`CarteContactFragment`)** affichant le marqueur du contact selon ses coordonnées GPS.

---

## 📁 Structure du Projet

```text
com.example.contactlist/
├── Contact.kt               # Modèle de données parcelable (id: Long)
├── ContactAdapter.kt        # Adaptateur RecyclerView dynamique (tailles, couleurs, bouton appel)
├── ContactViewModel.kt      # ViewModel partagé (gère les coroutines et les données SQLite)
├── ImageUtils.kt            # Stockage interne des photos et FileProvider
├── MainActivity.kt          # Activité unique hôte de fragments avec menu d'options
├── ListeContactsFragment.kt # Fragment liste avec filtrage, FAB '+' et rafraîchissement des préférences
├── DetailContactFragment.kt # Fragment d'édition/création, validation et confirmation de suppression
├── CarteContactFragment.kt  # Fragment Google Maps avec marqueur interactif
├── ParametresFragment.kt    # Écran de préférences (PreferenceFragmentCompat)
└── data/
    ├── ContactContract.kt   # Schéma de la table et constantes de colonnes
    ├── ContactDbHelper.kt   # SQLiteOpenHelper singleton avec données de démo
    ├── ContactDao.kt        # Accès exclusif aux requêtes SQL (CRUD & Search)
    └── PreferencesManager.kt # Conversion et lecture centralisée des SharedPreferences
```

---

## 🛠️ Configuration Technique

- **Langage** : Kotlin
- **Min SDK** : 24 (Android 7.0)
- **Target SDK / Compile SDK** : Android API 35 / 37
- **Bibliothèques principales** :
  - `androidx.appcompat`
  - `androidx.fragment:fragment-ktx`
  - `androidx.preference:preference-ktx`
  - `androidx.activity:activity-ktx`
  - `com.google.android.material:material`
  - `com.google.android.gms:play-services-maps`
  - `androidx.constraintlayout`

---

## 💻 Installation et Exécution

1. Cloner ce dépôt GitHub :
   ```bash
   git clone https://github.com/aziz-gr/ContactList-V4.git
   ```
2. Créer un fichier `local.properties` à la racine du projet et ajouter votre clé Google Maps API :
   ```properties
   MAPS_API_KEY=VOTRE_CLE_API_GOOGLE_MAPS
   ```
3. Ouvrir le projet dans **Android Studio** et lancer l'application sur un émulateur ou appareil physique.
