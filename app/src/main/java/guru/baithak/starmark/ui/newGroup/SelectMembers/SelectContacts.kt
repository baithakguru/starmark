package guru.baithak.starmark.ui.newGroup.SelectMembers

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import guru.baithak.starmark.R
import guru.baithak.starmark.Helpers.permissionContacts
import guru.baithak.starmark.Helpers.selectedIntent
import guru.baithak.starmark.ui.newGroup.FinalizeGroup.NewGroup
import kotlinx.android.synthetic.main.activity_select_contacts.*




class SelectContacts : AppCompatActivity() {
    var allContacts: ArrayList<AdapterSelectContacts.Contact> = ArrayList()
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
        viewSetter()
    }

    fun getContacts(search:String?=null){
        progress = ProgressDialog.show(this,"Loading Your Contacts","Please wait while we load your allContacts")
        Thread(Runnable { ->
            val type =  arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.LABEL)

            val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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

            runOnUiThread {
                allContactsRecycler.adapter!!.notifyDataSetChanged()
                selectedCountToolbar.text = String.format("Selected %d from %d",0,allContacts.size)
                (allContactsRecycler.adapter as AdapterSelectContacts).dataUpdated()
            }
        }).start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == permissionContacts){
            if (!grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Snackbar.make(rootSearch,"Permission Granted",Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(rootSearch,"Permission Denied...Please Grant",Snackbar.LENGTH_LONG).setAction("Grant",{v->askPermissions()}).show()

            }
            askPermissions()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(guru.baithak.starmark.R.menu.search_add_contacts,menu)
        val findItem = menu!!.findItem(R.id.searchContactsActionbar)
        (findItem.actionView as SearchView).setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (allContactsRecycler.adapter as AdapterSelectContacts).filter.filter(newText)
                return true
            }
        })

        return true
    }

    fun viewSetter(){
        var callback = object : ContactSelectedCallback {
            override fun personSelected(person: AdapterSelectContacts.Contact) {
//                Toast.makeText(this@SelectContacts,person.groupName,Toast.LENGTH_LONG).show()
                adapterForSelected.updateData(person)
                if(adapterForSelected.itemCount==0){
                    errorNoContactSelected.visibility = View.VISIBLE
                }else{
                    errorNoContactSelected.visibility = View.INVISIBLE
                }
                selectedCountToolbar.text = String.format("Selected %d of %d",adapterForSelected.itemCount,allContacts.size)

            }
        }

        progress!!.dismiss()
//        Snackbar.make(rootSearch,"Trying adapter",Snackbar.LENGTH_LONG).show()
        var  adapterAllContacts = AdapterSelectContacts(this, allContacts, callback)
        allContactsRecycler.adapter = adapterAllContacts
//        totalContacts = adapterAllContacts.itemCount
        adapterAllContacts.notifyDataSetChanged()

        allContactsRecycler.layoutManager = LinearLayoutManager(this)

        selectedCountToolbar.text = String.format("Selected %d from %d",0,totalContacts)
        fabFinalizeGroup.setOnClickListener{v->
            val i = Intent(this,NewGroup::class.java)
            i.putParcelableArrayListExtra(selectedIntent,adapterForSelected.selected)
            startActivity(i)
        }
        fastscroll.setRecyclerView(allContactsRecycler)
//        fastscroll.visibility=View.VISIBLE
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            android.R.id.home -> {
//                Toast.makeText(this,"up", Toast.LENGTH_LONG).show()
                // Respond to the action bar's Up/Home button
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



}
