package com.example.contactlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.contactlist.databinding.FragmentCarteContactBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CarteContactFragment : Fragment() {

    companion object {
        private const val ARG_CONTACT_ID = "arg_contact_id"

        fun newInstance(contactId: Long): CarteContactFragment {
            val fragment = CarteContactFragment()
            val args = Bundle().apply {
                putLong(ARG_CONTACT_ID, contactId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentCarteContactBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels()

    private var contactId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactId = arguments?.getLong(ARG_CONTACT_ID, 0L) ?: 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarteContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.updateTitle(getString(R.string.title_carte), showBack = true)

        viewModel.getContactById(contactId) { contact ->
            if (_binding != null) {
                val title = contact?.let { "${getString(R.string.title_carte)} - ${it.fullName}" }
                    ?: getString(R.string.title_carte)
                (activity as? MainActivity)?.updateTitle(title, showBack = true)

                if (contact != null && contact.hasValidCoordinates) {
                    binding.mapFragmentContainer.visibility = View.VISIBLE
                    binding.noLocationContainer.visibility = View.GONE
                    setupMap(contact)
                } else {
                    binding.mapFragmentContainer.visibility = View.GONE
                    binding.noLocationContainer.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupMap(contact: Contact) {
        val lat = contact.latitude ?: return
        val lng = contact.longitude ?: return

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.mapFragmentContainer, it)
                    .commit()
            }

        mapFragment.getMapAsync(OnMapReadyCallback { googleMap ->
            val position = LatLng(lat, lng)
            googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(contact.fullName)
                    .snippet("${contact.telephone} | ${contact.email.orEmpty()}")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
            googleMap.uiSettings.isZoomControlsEnabled = true
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
