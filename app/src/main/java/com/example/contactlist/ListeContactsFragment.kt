package com.example.contactlist

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.contactlist.data.PreferencesManager
import com.example.contactlist.databinding.FragmentListeContactsBinding

class ListeContactsFragment : Fragment() {

    private var _binding: FragmentListeContactsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels()
    private lateinit var adapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListeContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.updateTitle(getString(R.string.title_contact_list), showBack = false)

        setupRecyclerView()
        setupSearch()
        setupFab()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        applyPreferences()
        viewModel.loadContacts()
    }

    private fun setupRecyclerView() {
        adapter = ContactAdapter(
            onItemClick = { contact ->
                openDetailFragment(contact.id)
            },
            onCallClick = { contact ->
                makeDirectPhoneCall(contact.telephone)
            }
        )
        binding.recyclerViewContacts.adapter = adapter
    }

    private fun applyPreferences() {
        val ctx = context ?: return
        val prefsManager = PreferencesManager(ctx)
        adapter.updatePreferences(
            fontSizeSp = prefsManager.fontSizeSp,
            textColorInt = prefsManager.textColorInt,
            isCallEnabled = prefsManager.isCallFromListEnabled
        )
    }

    private fun setupSearch() {
        binding.etSearch.setText(viewModel.searchQuery.value)

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.setSearchQuery(text?.toString().orEmpty())
        }
    }

    private fun setupFab() {
        binding.fabAddContact.setOnClickListener {
            openDetailFragment(0L)
        }
    }

    private fun observeViewModel() {
        viewModel.contacts.observe(viewLifecycleOwner) { contactsList ->
            updateListDisplay(contactsList)
        }
    }

    private fun updateListDisplay(contactsList: List<Contact>) {
        adapter.submitList(contactsList)

        val count = contactsList.size
        binding.tvContactCount.text = getString(R.string.subtitle_contact_count, count)

        if (contactsList.isEmpty()) {
            binding.recyclerViewContacts.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE

            val query = viewModel.searchQuery.value.orEmpty()
            binding.tvEmptyMessage.text = if (query.isNotEmpty()) {
                getString(R.string.msg_no_search_results)
            } else {
                getString(R.string.msg_no_contacts)
            }
        } else {
            binding.recyclerViewContacts.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
        }
    }

    private fun makeDirectPhoneCall(phone: String) {
        val trimmedPhone = phone.trim()
        if (trimmedPhone.isEmpty()) {
            Toast.makeText(requireContext(), R.string.msg_empty_phone, Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$trimmedPhone")
        }

        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.error_no_dialer_app, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDetailFragment(contactId: Long) {
        val fragment = DetailContactFragment.newInstance(contactId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
