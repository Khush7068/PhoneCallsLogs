package im.chimerical.phonelogs.fragments

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import im.chimerical.phonelogs.Contact
import im.chimerical.phonelogs.adapters.ContactsListAdapter
import im.chimerical.phonelogs.databinding.ContactsFragmentBinding
import im.chimerical.phonelogs.interfaces.OnSelected
import im.chimerical.phonelogs.toToast
import java.io.ByteArrayInputStream

class ContactsFragment: Fragment() {
    private lateinit var binding: ContactsFragmentBinding
    private lateinit var contactListAdapter: RecyclerView.Adapter<*>

    private var contactList: ArrayList<Contact> = arrayListOf()
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ContactsFragmentBinding.inflate(layoutInflater)
        readContacts()
        contactListAdapter = ContactsListAdapter(contactList,object : OnSelected {
            override fun onContactClick(list: Contact) {
                super.onContactClick(list)
                when{
                    list.name.isNotEmpty()-> list.name.toToast(requireContext())
                    list.number.isNotEmpty() -> list.number.toToast(requireContext())
                    list.number.isNotEmpty() && list.name.isNotEmpty()->
                        "${list.number} : ${list.name}".toToast(requireContext())
                }
            }
        })

        binding.contactRecyclerView.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            adapter = contactListAdapter
        }

        return binding.root
    }

    @SuppressLint("Range")
    private fun readContacts() {
        Log.i("readContacts", "Reading Contacts")
        val contentResolver = mContext.contentResolver
        val nameCursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, "DISPLAY_NAME ASC")
        if (nameCursor!!.moveToFirst()) {
            do {
                val id: String = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name: String = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                var number: String = ""
                var photo: Bitmap? = null
                if (nameCursor.getInt(nameCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0){
                    val numberCursor: Cursor? = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
                    while (numberCursor!!.moveToNext()) {
                        number = numberCursor.getString(numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    }
                    numberCursor.close()

                    val phoneContactID = nameCursor.getLong(nameCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
                    val contactUri: Uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, phoneContactID)
                    val photoUri: Uri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)

                    val photoCursor: Cursor? = contentResolver.query(photoUri, arrayOf(ContactsContract.Contacts.Photo.PHOTO),
                        null, null, null)

                    if (photoCursor!!.moveToFirst()) {
                        val data = photoCursor.getBlob(0)
                        if (data != null){
                            photo = BitmapFactory.decodeStream(ByteArrayInputStream(data))
                        }
                    }
                    photoCursor.close()
                }
                contactList.add(Contact(name, number, photo))
            } while (nameCursor.moveToNext())
        }
        nameCursor.close()
    }
}
