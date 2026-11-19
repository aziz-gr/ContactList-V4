package com.example.contactlist

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.contactlist.data.PreferencesManager
import com.example.contactlist.databinding.FragmentDetailContactBinding

class DetailContactFragment : Fragment() {

    companion object {
        private const val ARG_CONTACT_ID = "arg_contact_id"

        fun newInstance(contactId: Long): DetailContactFragment {
            val fragment = DetailContactFragment()
            val args = Bundle().apply {
                putLong(ARG_CONTACT_ID, contactId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentDetailContactBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels()

    private var contactId: Long = 0L
    private var contact: Contact? = null

    private var currentPhotoUri: String? = null
    private var tempCameraUri: Uri? = null
    private var tempCameraFilePath: String? = null

    // Gallery Picker Launcher
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        val ctx = context ?: return@registerForActivityResult
        if (uri != null) {
            val savedPath = ImageUtils.saveImageToInternalStorage(ctx, uri)
            if (savedPath != null) {
                currentPhotoUri = savedPath
                updatePhotoDisplay()
                Toast.makeText(ctx, R.string.msg_photo_updated, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(ctx, R.string.msg_photo_cancelled, Toast.LENGTH_SHORT).show()
        }
    }

    // Camera Capture Launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val ctx = context ?: return@registerForActivityResult
        if (success && tempCameraFilePath != null) {
            currentPhotoUri = tempCameraFilePath
            updatePhotoDisplay()
            Toast.makeText(ctx, R.string.msg_photo_updated, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(ctx, R.string.msg_photo_cancelled, Toast.LENGTH_SHORT).show()
        }
    }

    // Permission Launcher for Camera
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        val ctx = context ?: return@registerForActivityResult
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(ctx, R.string.error_camera_permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactId = arguments?.getLong(ARG_CONTACT_ID, 0L) ?: 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyPreferences()
        setupListeners()

        if (contactId == 0L) {
            (activity as? MainActivity)?.updateTitle(getString(R.string.title_add_contact), showBack = true)
            binding.cardQuickActions.visibility = View.GONE
            binding.btnDelete.visibility = View.GONE
        } else {
            (activity as? MainActivity)?.updateTitle(getString(R.string.title_contact_detail), showBack = true)
            binding.cardQuickActions.visibility = View.VISIBLE
            binding.btnDelete.visibility = View.VISIBLE

            viewModel.getContactById(contactId) { loadedContact ->
                if (_binding != null) {
                    if (loadedContact != null) {
                        contact = loadedContact
                        currentPhotoUri = loadedContact.photoUri
                        populateFields(loadedContact)
                    } else {
                        Toast.makeText(requireContext(), "Erreur : Contact introuvable", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                }
            }
        }
    }

    private fun applyPreferences() {
        val ctx = context ?: return
        val prefs = PreferencesManager(ctx)
        val fontSp = prefs.fontSizeSp
        val colorInt = prefs.textColorInt

        binding.etNom.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSp)
        binding.etNom.setTextColor(colorInt)

        binding.etPrenom.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSp)
        binding.etPrenom.setTextColor(colorInt)

        binding.etTelephone.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSp)
        binding.etTelephone.setTextColor(colorInt)
    }

    private fun populateFields(c: Contact) {
        binding.etNom.setText(c.nom)
        binding.etPrenom.setText(c.prenom)
        binding.etTelephone.setText(c.telephone)
        binding.etEmail.setText(c.email.orEmpty())

        binding.etLatitude.setText(c.latitude?.toString().orEmpty())
        binding.etLongitude.setText(c.longitude?.toString().orEmpty())

        updatePhotoDisplay()
    }

    private fun updatePhotoDisplay() {
        val ctx = context ?: return
        ImageUtils.loadContactPhoto(
            context = ctx,
            photoUriStr = currentPhotoUri,
            photoResId = contact?.photoResId,
            imageView = binding.ivDetailPhoto
        )
    }

    private fun setupListeners() {
        binding.btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnCamera.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        binding.btnCall.setOnClickListener {
            makePhoneCall()
        }

        binding.btnEmail.setOnClickListener {
            sendEmail()
        }

        binding.btnViewMap.setOnClickListener {
            openMapFragment()
        }

        binding.btnSave.setOnClickListener {
            saveContact()
        }

        binding.btnDelete.setOnClickListener {
            confirmAndDelete()
        }

        binding.btnCancel.setOnClickListener {
            cancelAndExit()
        }
    }

    private fun checkCameraPermissionAndLaunch() {
        val ctx = context ?: return
        if (!ctx.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(ctx, R.string.error_no_camera, Toast.LENGTH_SHORT).show()
            return
        }

        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val ctx = context ?: return
        try {
            val (contentUri, filePath) = ImageUtils.createTempCameraUri(ctx)
            tempCameraUri = contentUri
            tempCameraFilePath = filePath
            cameraLauncher.launch(contentUri)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(ctx, R.string.error_no_camera, Toast.LENGTH_SHORT).show()
        }
    }

    private fun makePhoneCall() {
        val phone = binding.etTelephone.text.toString().trim()
        if (phone.isEmpty()) {
            binding.inputLayoutTelephone.error = getString(R.string.error_field_required)
            Toast.makeText(requireContext(), R.string.error_field_required, Toast.LENGTH_SHORT).show()
            return
        }
        binding.inputLayoutTelephone.error = null

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }

        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.error_no_dialer_app, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail() {
        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty()) {
            binding.inputLayoutEmail.error = getString(R.string.error_field_required)
            Toast.makeText(requireContext(), R.string.error_field_required, Toast.LENGTH_SHORT).show()
            return
        }
        binding.inputLayoutEmail.error = null

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }

        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.error_no_email_app, Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseCoordinates(): Pair<Double?, Double?>? {
        val latStr = binding.etLatitude.text.toString().trim()
        val lngStr = binding.etLongitude.text.toString().trim()

        binding.inputLayoutLatitude.error = null
        binding.inputLayoutLongitude.error = null

        if (latStr.isEmpty() && lngStr.isEmpty()) {
            return Pair(null, null)
        }

        var isValid = true

        val lat = latStr.toDoubleOrNull()
        if (latStr.isNotEmpty() && (lat == null || lat !in -90.0..90.0)) {
            binding.inputLayoutLatitude.error = getString(R.string.error_invalid_latitude)
            isValid = false
        }

        val lng = lngStr.toDoubleOrNull()
        if (lngStr.isNotEmpty() && (lng == null || lng !in -180.0..180.0)) {
            binding.inputLayoutLongitude.error = getString(R.string.error_invalid_longitude)
            isValid = false
        }

        if (!isValid) return null

        return Pair(lat, lng)
    }

    private fun openMapFragment() {
        val coords = parseCoordinates()
        if (coords == null) {
            Toast.makeText(requireContext(), R.string.msg_no_gps_coordinates, Toast.LENGTH_SHORT).show()
            return
        }

        val lat = coords.first
        val lng = coords.second

        if (lat == null || lng == null || lat !in -90.0..90.0 || lng !in -180.0..180.0) {
            Toast.makeText(requireContext(), R.string.msg_no_gps_coordinates, Toast.LENGTH_SHORT).show()
            return
        }

        if (contactId > 0L) {
            val fragment = CarteContactFragment.newInstance(contactId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit()
        } else {
            Toast.makeText(requireContext(), "Veuillez d'abord enregistrer le contact pour voir la carte", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveContact() {
        val nom = binding.etNom.text.toString().trim()
        val prenom = binding.etPrenom.text.toString().trim()
        val telephone = binding.etTelephone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        var isValid = true

        if (nom.isEmpty()) {
            binding.inputLayoutNom.error = getString(R.string.error_field_required)
            isValid = false
        } else {
            binding.inputLayoutNom.error = null
        }

        if (prenom.isEmpty()) {
            binding.inputLayoutPrenom.error = getString(R.string.error_field_required)
            isValid = false
        } else {
            binding.inputLayoutPrenom.error = null
        }

        if (telephone.isEmpty()) {
            binding.inputLayoutTelephone.error = getString(R.string.error_field_required)
            isValid = false
        } else {
            binding.inputLayoutTelephone.error = null
        }

        val coords = parseCoordinates()
        if (coords == null) {
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(requireContext(), R.string.error_field_required, Toast.LENGTH_SHORT).show()
            return
        }

        val targetContact = contact ?: Contact(
            id = contactId,
            nom = nom,
            prenom = prenom,
            telephone = telephone,
            email = email.ifEmpty { null },
            photoUri = currentPhotoUri,
            latitude = coords?.first,
            longitude = coords?.second
        )

        targetContact.nom = nom
        targetContact.prenom = prenom
        targetContact.telephone = telephone
        targetContact.email = email.ifEmpty { null }
        targetContact.photoUri = currentPhotoUri
        targetContact.latitude = coords?.first
        targetContact.longitude = coords?.second

        val isNew = contactId == 0L

        viewModel.saveContact(targetContact) { success ->
            if (_binding != null) {
                if (success) {
                    val msgRes = if (isNew) R.string.msg_contact_added else R.string.msg_contact_updated
                    Toast.makeText(requireContext(), msgRes, Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Erreur d'enregistrement", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun confirmAndDelete() {
        val ctx = context ?: return
        if (contactId == 0L) return

        AlertDialog.Builder(ctx)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(R.string.dialog_delete_message)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                viewModel.deleteContact(contactId) { success ->
                    if (_binding != null) {
                        if (success) {
                            Toast.makeText(requireContext(), R.string.msg_contact_deleted, Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(requireContext(), "Erreur de suppression", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton(R.string.dialog_no, null)
            .show()
    }

    private fun cancelAndExit() {
        Toast.makeText(requireContext(), R.string.msg_changes_cancelled, Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
