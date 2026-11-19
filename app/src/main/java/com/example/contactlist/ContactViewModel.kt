package com.example.contactlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.contactlist.data.ContactDao
import com.example.contactlist.data.ContactDbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = ContactDbHelper.getInstance(application)
    private val contactDao = ContactDao(dbHelper)

    private val _contacts = MutableLiveData<List<Contact>>(emptyList())
    val contacts: LiveData<List<Contact>> get() = _contacts

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> get() = _searchQuery

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch {
            val query = _searchQuery.value.orEmpty()
            val list = withContext(Dispatchers.IO) {
                if (query.trim().isEmpty()) {
                    contactDao.getAll()
                } else {
                    contactDao.search(query)
                }
            }
            _contacts.value = list
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadContacts()
    }

    fun getContactById(id: Long, onResult: (Contact?) -> Unit) {
        viewModelScope.launch {
            val contact = withContext(Dispatchers.IO) {
                contactDao.getById(id)
            }
            onResult(contact)
        }
    }

    fun saveContact(contact: Contact, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                if (contact.id == 0L) {
                    val rowId = contactDao.insert(contact)
                    rowId > 0
                } else {
                    val count = contactDao.update(contact)
                    count > 0
                }
            }
            if (success) {
                loadContacts()
            }
            onResult(success)
        }
    }

    fun deleteContact(id: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val count = withContext(Dispatchers.IO) {
                contactDao.delete(id)
            }
            val success = count > 0
            if (success) {
                loadContacts()
            }
            onResult(success)
        }
    }
}
