package guru.baithak.starmark.ui.newGroup.FinalizeGroup

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import guru.baithak.starmark.Helpers.createGroupUrl
import guru.baithak.starmark.ui.mainScreen.MainActivity
import guru.baithak.starmark.Helpers.selectedIntent
import guru.baithak.starmark.Helpers.sharedPref
import guru.baithak.starmark.Helpers.userNameSharedPref
import guru.baithak.starmark.R
import guru.baithak.starmark.ui.newGroup.SelectMembers.AdapterSelectContacts
import guru.baithak.starmark.ui.newGroup.SelectMembers.SelectedAdapter
import kotlinx.android.synthetic.main.activity_new_group.*
import org.json.JSONArray
import org.json.JSONObject

class NewGroup : AppCompatActivity() {
    var progress:ProgressDialog?=null
    var data : ArrayList<AdapterSelectContacts.Contact>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)
        setSupportActionBar(finalizeActionbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        data = intent.getParcelableArrayListExtra(selectedIntent) //as ArrayList<AdapterSelectContacts.Contact>
        data = data as ArrayList<AdapterSelectContacts.Contact>
        if(data!=null){
            viewSetter()
        }else{
            Toast.makeText(this,"Error getting data",Toast.LENGTH_LONG).show()
        }
    }


    fun viewSetter(){

//        Toast.makeText(this,"Got  data "+data!!.size,Toast.LENGTH_LONG).show()
        selectedFinalizeRecycler.adapter = SelectedAdapter(this, data!!)
        selectedFinalizeRecycler.layoutManager = GridLayoutManager(this,3)
        finalSelectedCount.text = String.format("%d participants selected",data!!.size)
        fabAddGroupFinal.setOnClickListener{v->
            if(groupTitleFinal.text.isEmpty() ){
                groupTitleFinal.error = "Please enter a title"
                return@setOnClickListener
            }else{
                groupTitleFinal.error=null
            }
            createGroup()
        }

    }

    fun createGroup(){
        progress = ProgressDialog.show(this,"Creating Group","Please wait while we add the members to group",true,false)

        Toast.makeText(this,"Creating group ",Toast.LENGTH_LONG).show()
        val params = HashMap<String,String>()
        params.put("selfUid",FirebaseAuth.getInstance().currentUser!!.uid)
        params.put("title",groupTitleFinal.text.toString())
        params.put("desc",groupDescriptionFinal.text.toString())
        var name = FirebaseAuth.getInstance().currentUser!!.displayName
        if(name == null){
            name=getSharedPreferences(sharedPref, Context.MODE_PRIVATE).getString(userNameSharedPref,"unknown")
        }
        params.put("selfName",name!!)
        Log.i("selfName",name!!)
        val allMembers = JSONArray()
        for(c:AdapterSelectContacts.Contact in data!!){
            val member = JSONObject()
            member.put("groupName",c.name)

            member.put("contact",c.number.replace(" ","").replace("-","").replace("+91",""))
            allMembers.put(member)
        }
        params.put("data",allMembers.toString())

        Log.i("JSON",JSONObject(params).toString())

        val request  = JsonObjectRequest(Request.Method.POST, createGroupUrl,JSONObject(params),{response ->

            Log.i("Response",response.toString())
            progress!!.dismiss()
            startActivity(Intent(this, MainActivity::class.java))

        },{error->
            progress!!.dismiss()
            Log.i("Response",error.toString())
            Toast.makeText(this,"Error Occurred",Toast.LENGTH_LONG).show()
        })

        request.retryPolicy=DefaultRetryPolicy(0,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            android.R.id.home -> {
//                Toast.makeText(this,"up",Toast.LENGTH_LONG).show()
                // Respond to the action bar's Up/Home button
//                NavUtils.navigateUpFromSameTask(this)
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
