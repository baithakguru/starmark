package guru.baithak.starmark.ui.groups.groupDetails


import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import guru.baithak.starmark.R
import guru.baithak.starmark.ui.newGroup.SelectMembers.AdapterSelectContacts
import guru.baithak.starmark.ui.newGroup.SelectMembers.ContactSelectedCallback
import kotlinx.android.synthetic.main.activity_select_contacts.*
import kotlinx.android.synthetic.main.fragment_get_contact.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GetContact : DialogFragment() {

    var allContacts: ArrayList<AdapterSelectContacts.Contact> = ArrayList()
    var listener : ContactSelectedCallback?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allContacts.clear()
        getContacts()
        val adapter = AdapterSelectContacts(context!!,allContacts,listener!!)
        selectSingleContact.adapter = adapter
        selectSingleContact.layoutManager = LinearLayoutManager(context!!)
        searchContact.addTextChangedListener(
                object :TextWatcher{
                    override fun afterTextChanged(s: Editable?) {
                        adapter.filter.filter(s!!.toString())
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                }
        )


    }

    fun getContacts(search:String?=null){
//        progress = ProgressDialog.show(this,"Loading Your Contacts","Please wait while we load your allContacts")
            val type =  arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.LABEL)

            val cursor = context!!.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    type,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + ">0 AND LENGTH(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ")>0",
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            cursor.let {
                while (it.moveToNext()) {

                    var displayName = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    displayName = displayName[0].toUpperCase()+displayName.substring(1)
                    val number = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val type = it.getInt(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                    Log.i("cursor",displayName+"   "+type)
                    val label = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL))
                    val phoneLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, label)
                    allContacts.add(AdapterSelectContacts.Contact(
                            displayName,
                            number,
                            false
                    ))

                }

                it.close()
            }
            allContacts.sortBy { it.name }
    }



}
