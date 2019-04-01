package guru.baithak.starmark.ui.newGroup.SelectMembers

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import guru.baithak.starmark.R
import guru.baithak.starmark.Helpers.permissionContacts
import guru.baithak.starmark.Helpers.selectedIntent
import guru.baithak.starmark.ui.newGroup.FinalizeGroup.NewGroup
import kotlinx.android.synthetic.main.activity_select_contacts.*

class SelectContacts : AppCompatActivity() {
    var contacts: ArrayList<AdapterSelectContacts.Contact> = ArrayList()
    var progress : ProgressDialog?= null
    val adapterForSelected = SelectedAdapter(this, ArrayList())
    var totalContacts :Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_contacts)
        setSupportActionBar(searchContactsActionbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        askPermissions()
        selectedRecycler.adapter = adapterForSelected
        selectedRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
    }

    fun getContacts(){
        progress = ProgressDialog.show(this,"Loading Your Contacts","Please wait while we load your contacts")

        val t = Thread(Runnable { ->


                    val cr : ContentResolver = contentResolver
        val cursorRoot : Cursor? = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null,null)

        val nameColumn = cursorRoot!!.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
        val contactColumnId = cursorRoot.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
        val contactId = cursorRoot.getColumnIndex(ContactsContract.Contacts._ID)

        while(cursorRoot.moveToNext()){


            if(cursorRoot.getInt(contactColumnId)==0){
                continue
            }
            val cursorNumber : Cursor =cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"=?",
                arrayOf(cursorRoot.getString(contactId)),null)

            val phoneNoIndex = cursorNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursorNumber.moveToNext()){
                contacts.add(
                    AdapterSelectContacts.Contact(
                        cursorRoot.getString(nameColumn),
                        cursorNumber.getString(phoneNoIndex)
                    )
                )
            }
            cursorNumber.close()



        }
        cursorRoot.close()
//            val sort : Comparator<AdapterSelectContacts.Contact>()
            val sorted =  contacts.sortedWith(compareBy {it.name})
            contacts = ArrayList<AdapterSelectContacts.Contact>(sorted)
            Log.d("Size of arra",""+contacts.size)
            runOnUiThread { ->viewSetter() }
        })
        t.start()


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == permissionContacts){
            if (!grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Snackbar.make(rootSearch,"Permission Granted",Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(rootSearch,"Permission Denied...Please Grant",Snackbar.LENGTH_LONG).setAction("Grant",{v->askPermissions()}).show()

            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.search_add_contacts,menu)
        return true
    }

    fun viewSetter(){
        var callback = object : ContactSelectedCallback {
            override fun personSelected(person: AdapterSelectContacts.Contact) {
//                Toast.makeText(this@SelectContacts,person.name,Toast.LENGTH_LONG).show()
                adapterForSelected.updateData(person)
                selectedCountToolbar.text = String.format("Selected %d of %d",adapterForSelected.itemCount,totalContacts)

            }
        }

        progress!!.dismiss()
        Snackbar.make(rootSearch,"Trying adapter",Snackbar.LENGTH_LONG).show()
        var  adapterAllContacts = AdapterSelectContacts(this, contacts, callback)
        allContactsRecycler.adapter = adapterAllContacts
        totalContacts = adapterAllContacts.itemCount

        allContactsRecycler.layoutManager = LinearLayoutManager(this)
        selectedCountToolbar.text = String.format("Selected %d of %d",0,totalContacts)
        fabFinalizeGroup.setOnClickListener{v->
            val i = Intent(this,NewGroup::class.java)
            i.putParcelableArrayListExtra(selectedIntent,adapterForSelected.selected)
            startActivity(i)
        }
    }

    fun askPermissions(){
        if(!hasPermissions()){
//            Snackbar.make(rootSearch,"Permission Denied...Please Grant",Snackbar.LENGTH_LONG).setAction("Grant",{v->askPermissions()}).show()

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), permissionContacts)
        }else{
            getContacts()

//            viewSetter()
        }



    }

    fun hasPermissions():Boolean{
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }





}
