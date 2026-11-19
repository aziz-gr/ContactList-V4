package com.example.contactlist

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val onItemClick: (Contact) -> Unit,
    private val onCallClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private var contacts: List<Contact> = emptyList()
    private var fontSizeSp: Float = 16f
    private var textColorInt: Int = 0xFF212121.toInt()
    private var isCallEnabled: Boolean = false

    fun submitList(newList: List<Contact>) {
        contacts = newList
        notifyDataSetChanged()
    }

    fun updatePreferences(fontSizeSp: Float, textColorInt: Int, isCallEnabled: Boolean) {
        this.fontSizeSp = fontSizeSp
        this.textColorInt = textColorInt
        this.isCallEnabled = isCallEnabled
        notifyDataSetChanged()
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvItemName)
        val tvPhone: TextView = itemView.findViewById(R.id.tvItemPhone)
        val ivPhoto: ImageView = itemView.findViewById(R.id.ivItemPhoto)
        val btnCall: ImageButton = itemView.findViewById(R.id.btnItemCall)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.tvName.text = contact.fullName
        holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeSp)
        holder.tvName.setTextColor(textColorInt)

        holder.tvPhone.text = contact.telephone
        holder.tvPhone.setTextSize(TypedValue.COMPLEX_UNIT_SP, (fontSizeSp - 2f).coerceAtLeast(12f))

        ImageUtils.loadContactPhoto(
            context = holder.itemView.context,
            photoUriStr = contact.photoUri,
            photoResId = contact.photoResId,
            imageView = holder.ivPhoto
        )

        if (isCallEnabled) {
            holder.btnCall.visibility = View.VISIBLE
            holder.btnCall.setOnClickListener {
                onCallClick(contact)
            }
        } else {
            holder.btnCall.visibility = View.GONE
            holder.btnCall.setOnClickListener(null)
        }

        holder.itemView.setOnClickListener {
            onItemClick(contact)
        }
    }

    override fun getItemCount(): Int = contacts.size
}
