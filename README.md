# 📱 Application Liste de Contacts (TP Android V3)

[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![Android SDK](https://img.shields.io/badge/SDK-24%2B-green.svg)](https://developer.android.com/)
[![Material Design 3](https://img.shields.io/badge/Design-Material%203-blue.svg)](https://m3.material.io/)
[![Google Maps SDK](https://img.shields.io/badge/Maps-SDK-red.svg)](https://developers.google.com/maps)

Application Android native développée en **Kotlin** avec des **interfaces XML**, créée dans le cadre du **TP Android — Contacts V3**. Cette version fait évoluer l'application V2 vers une architecture moderne à **activité unique et fragments dynamiques**, avec une **RecyclerView filtrable** et l'**intégration de Google Maps SDK** pour l'affichage cartographique des coordonnées GPS des contacts.

---

## 🚀 Nouvelles Fonctionnalités & Architecture (V3)

### 1. 🏛️ Architecture à Activité Unique & Fragments Dynamiques
- **`MainActivity` unique** : Héberge les différents écrans au sein d'un `FragmentContainerView`.
- **Navigation dynamique** : Utilisation du `FragmentManager` avec `replace()`, `addToBackStack()` et synchronisation automatique du titre du `Toolbar` et de la flèche de retour.
- **`ContactViewModel` partagé** : Modèle de vue partagé au niveau de l'activité (`by activityViewModels()`) garantissant la persistance des données lors de la rotation de l'écran et la communication fluide entre fragments.

### 2. 📜 Listes avec `RecyclerView` & Filtrage en Temps Réel
- **Remplacement de la `ListView`** : Utilisation d'une `RecyclerView` personnalisée avec `ContactAdapter` et le pattern `ViewHolder`.
- **Recherche réactive** : Champ de recherche `TextInputEditText` au-dessus de la liste filtrant de manière fluide au fur et à mesure de la saisie (`doOnTextChanged`).
- **Filtrage multicritère & insensible à la casse** : La recherche s'effectue sur le nom, le prénom et le numéro de téléphone.
- **Sécurité d'indexation** : L'ouverture de la fiche détaillée s'appuie sur l'identifiant unique (`id`) du contact et non sur sa position dans la liste filtrée, éliminant tout risque d'erreur d'indice.
- **Gestion des états vides** : Affichage de messages distincts selon que la liste globale est vide ou qu'aucun résultat ne correspond à la recherche.

### 3. 🗺️ Coordonnées GPS & Intégration Google Maps
- **Extension du modèle `Contact`** : Prise en charge des propriétés optionnelles `latitude` (Double?) et `longitude` (Double?).
- **Saisie & Validation des coordonnées** : Formulaire dans `DetailContactFragment` autorisant uniquement des latitudes valides (de -90.0 à 90.0) et des longitudes valides (de -180.0 à 180.0).
- **`CarteContactFragment`** : Intégration de `SupportMapFragment` (Google Maps SDK). La carte se centre automatiquement sur la position du contact à un niveau de zoom approprié et dépose un marqueur interactif à son nom.
- **Gestion des cas limites GPS** : Si les coordonnées sont absentes ou invalides, l'application affiche un écran explicite au lieu de faire planter la carte.

---

## 🔒 Sécurité de la Clé API Google Maps

Afin de respecter la règle de non-publication des clés privées sur GitHub :
1. La clé API est stockée dans le fichier `local.properties` (exclu du suivi Git via `.gitignore`) :
   ```properties
   MAPS_API_KEY=VOTRE_CLE_API_GOOGLE_MAPS
   ```
2. Le fichier `app/build.gradle.kts` extrait la variable `MAPS_API_KEY` pour l'injecter dynamiquement dans le manifest :
   ```kotlin
   val mapsApiKey = properties.getProperty("MAPS_API_KEY") ?: "YOUR_API_KEY_HERE"
   manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
   ```
3. Le fichier `AndroidManifest.xml` utilise la placeholder `${MAPS_API_KEY}`. Aucune clé réelle n'est présente dans le dépôt public.

---

## 📞 Fonctionnalités V2 Conservées

- **Consultation & Édition** : Visualisation et modification des informations du contact avec actualisation immédiate de la liste.
- **Appels téléphoniques (`Intent.ACTION_DIAL`)** : Ouverture du composeur avec le numéro prérempli.
- **Envoi d'e-mails (`Intent.ACTION_SENDTO`)** : Ouverture de la messagerie avec l'adresse préremplie.
- **Photos de profil (Galerie & Caméra)** : Choix depuis la galerie ou prise de vue directe via `FileProvider` avec sauvegarde interne permanente dans `filesDir`.
- **Validation & Annulation** : Contrôle des champs obligatoires (Nom, Prénom, Téléphone) et gestion des annulations sans modification.

---

## 📁 Structure du Projet

```text
com.example.contactlist/
├── Contact.kt               # Modèle de données parcelable (avec latitude & longitude)
├── ContactAdapter.kt        # Adaptateur RecyclerView avec ViewHolder
├── ContactViewModel.kt      # ViewModel partagé (Scope Activity)
├── ImageUtils.kt            # Utilitaire de gestion des photos (stockage interne & FileProvider)
├── MainActivity.kt          # Activité principale unique (hôte de fragments)
├── ListeContactsFragment.kt # Fragment de liste et recherche en temps réel
├── DetailContactFragment.kt # Fragment d'édition, saisie GPS et actions
└── CarteContactFragment.kt  # Fragment Google Maps avec marqueur interactif
```

---

## 💡 Questions de Compréhension (Section 11)

1. **Quelle différence existe-t-il entre une Activity et un Fragment ?**
   - Une **Activity** est un composant autonome d'application représentant un écran principal géré directement par le système Android. Un **Fragment** est une portion réutilisable et modulaire d'interface utilisateur hébergée au sein d'une Activity avec son propre sous-cycle de vie.
2. **Pourquoi utiliser `FragmentContainerView` et `addToBackStack()` ?**
   - `FragmentContainerView` est le conteneur officiel AndroidX assurant une gestion optimale des transactions, des animations et de la restauration d'état des fragments. `addToBackStack()` enregistre la transaction dans la pile d'historique pour permettre un retour arrière fluide via le bouton Retour.
3. **Quel est le rôle de `RecyclerView.Adapter` et de `ViewHolder` ?**
   - `RecyclerView.Adapter` fait la liaison entre la liste de données et l'affichage. Le `ViewHolder` garde en cache les références des vues d'un item pour réutiliser les composants visuels lors du défilement et améliorer la fluidité.
4. **Comment éviter une erreur d’indice après filtrage ?**
   - En identifiant chaque contact par son **ID unique (`id: String`)** au lieu d'utiliser l'index de la liste filtrée.
5. **Pourquoi transmettre l’identifiant d’un contact plutôt que la position de sa ligne ?**
   - La position d'un item change lors de la recherche ou du tri. Transmettre un identifiant immuable garantit que l'on manipule le bon objet dans le ViewModel quelle que soit la vue active.
6. **Comment garantir qu’une clé Google Maps ne soit pas exposée dans GitHub ?**
   - En définissant la clé dans `local.properties` (exclu par `.gitignore`) et en l'injectant dans le `AndroidManifest.xml` via `manifestPlaceholders` dans `build.gradle.kts`.

---

## 🛠️ Configuration Technique

- **Langage** : Kotlin
- **Min SDK** : 24 (Android 7.0)
- **Target SDK / Compile SDK** : Android API 35 / 37
- **Bibliothèques principales** :
  - `androidx.appcompat`
  - `androidx.fragment:fragment-ktx`
  - `androidx.activity:activity-ktx`
  - `com.google.android.material:material`
  - `com.google.android.gms:play-services-maps`
  - `androidx.constraintlayout`

---

## 💻 Installation et Exécution

1. Cloner ce dépôt GitHub :
   ```bash
   git clone https://github.com/aziz-gr/ContactList-V3.git
   ```
2. Créer un fichier `local.properties` à la racine du projet et ajouter votre clé Google Maps API :
   ```properties
   MAPS_API_KEY=VOTRE_CLE_API_GOOGLE_MAPS
   ```
3. Ouvrir le projet dans **Android Studio** et lancer l'application sur un émulateur ou appareil physique.
