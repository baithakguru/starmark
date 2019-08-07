package guru.baithak.starmark.ui.groups.groupDetails

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.TimeUtils
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import guru.baithak.starmark.Helpers.addMemberUrl
import guru.baithak.starmark.Helpers.groupKey
import guru.baithak.starmark.Models.Groups
import guru.baithak.starmark.Models.Person
import guru.baithak.starmark.R
import guru.baithak.starmark.ui.newGroup.SelectMembers.AdapterSelectContacts
import guru.baithak.starmark.ui.newGroup.SelectMembers.ContactSelectedCallback
import kotlinx.android.synthetic.main.activity_group_details.*
import kotlinx.android.synthetic.main.viewholder_groups.*
import org.json.JSONObject
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class GroupDetails : AppCompatActivity() {

    var groupId :String?=null
    val memberList:ArrayList<Person> = ArrayList()
    var progress :ProgressDialog?=null
    var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)
        progress = ProgressDialog.show(this,"Getting group info","Please wait while we fetch details")
        setSupportActionBar(toolbar)
        supportActionBar!!.title=" "
        groupId = intent.getStringExtra(groupKey)
        val group = intent.getParcelableExtra(guru.baithak.starmark.Helpers.groupName) as Groups
        groupNameConsistent.text  = group.groupName
        groupNameToolbar.text = groupNameConsistent.text
        getData()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            val alpha : Float = Math.abs(p1 / app_bar.totalScrollRange.toFloat())
            groupNameToolbar.alpha = alpha
            groupInfoTable.alpha = 1f-alpha
        })

    }

    fun getData(){
        val selfId = FirebaseAuth.getInstance().currentUser!!.uid
        val path = "groups/"+groupId//+"/members/avail"
        Log.i("self",selfId)
        Log.i("path",path)

        FirebaseDatabase.getInstance().getReference(path).addValueEventListener(
                object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(this@GroupDetails, "Error getting details", Toast.LENGTH_LONG).show()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        Log.d("data",p0.toString())
                        memberList.clear()
                       for (members in p0.child("members").child("avail").children){
                           Log.i("all",members.key)

                           if(members.key == selfId){
                               Log.i("self",members.value.toString())
                                try {
                                    isAdmin = members.child("isAdmin").value as Boolean
                                }catch (e:Exception){

                                }

                           }
                            val person = members.getValue(Person::class.java)
                           person!!.userKey = members.key
                           memberList.add(person)
                       }
                        viewSetter(p0)
                    }
                }
        )


    }

    private fun viewSetter(dataSnapshot: DataSnapshot){
        if(isAdmin){
            leaveGroup.text = "Delete Group"
            addMemberGroup.visibility = View.VISIBLE
        }
        val date = Date(dataSnapshot.child("created").value as Long)
        Log.i("date",date.toString())
        val dateFormat = SimpleDateFormat("dd MMM yy").format(date)


        groupDetails.text =  String.format("%d members\nCreator: %s\nCreated at: %s",memberList.size,dataSnapshot.child("createdBy").value,dateFormat)
        var desc = "No description available"
        if(dataSnapshot.child("desc").exists() and !(dataSnapshot.child("desc").value as String).trim().isEmpty()){
            desc =  dataSnapshot.child("desc").value as String
        }
        groupDecs.text =desc

        setRecycler()
        leaveGroup.setOnClickListener {
            if(isAdmin){
                //delete group
                FirebaseDatabase.getInstance().getReference("groups/"+groupId+"/isActive")
                        .setValue(false).addOnCompleteListener {
                            NavUtils.navigateUpFromSameTask(this)
                        }


            }else{
                //leave group
                FirebaseDatabase.getInstance().getReference("groups/"+groupId+"/members/avail/"+FirebaseAuth.getInstance().currentUser!!.uid+"/remove")
                        .setValue(true).addOnCompleteListener {
                            NavUtils.navigateUpFromSameTask(this)
                        }
            }


        }



        addMemberGroup.setOnClickListener {
            val frag = GetContact()
            val listener = object : ContactSelectedCallback{
                override fun personSelected(person: AdapterSelectContacts.Contact) {
                    frag.dismiss()
                    var phoneNo = person.number
                    phoneNo = phoneNo.replace(" ","").replace("-","")
                            .replace("(","").replace(")","")
                            .replace("+91","").replace("\\.","")


                    AlertDialog.Builder(this@GroupDetails).setMessage("Do you want to add "+person.name+" to group?").setPositiveButton("Yes",
                            object:DialogInterface.OnClickListener{
                                override fun onClick(dialog: DialogInterface?, which: Int) {
                                    val progress = ProgressDialog(this@GroupDetails)
                                    progress.setCancelable(false)
                                    progress.setMessage("Please wait while we add member to group")
                                    progress.show()
                                    val params = JSONObject()
                                    params.put("groupId",groupId)
                                    params.put("phone",phoneNo)
                                    val request = JsonObjectRequest(Request.Method.POST, addMemberUrl,params,
                                            object : Response.Listener<JSONObject>{
                                                override fun onResponse(response: JSONObject?) {
                                                    response?.let {
                                                        if(it.getString("status").equals("success")){
                                                            Toast.makeText(this@GroupDetails,"Member will be added successfully",Toast.LENGTH_SHORT).show()
                                                        }else{
                                                            Toast.makeText(this@GroupDetails,it.getString("error"),Toast.LENGTH_SHORT).show()
                                                        }
                                                    }


                                                    progress.dismiss()
                                                }
                                            },
                                            object :Response.ErrorListener {
                                                override fun onErrorResponse(error: VolleyError?) {
                                                    Toast.makeText(this@GroupDetails, "Some error occured try again", Toast.LENGTH_SHORT).show()
                                                    progress.dismiss()
                                                }
                                            })
                                    Volley.newRequestQueue(this@GroupDetails).add(request)
                                }
                            }).setNegativeButton("No",object:DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog!!.dismiss()
                        }
                    }).setCancelable(false).show()


                }
            }
            frag.listener = listener
            frag.show(supportFragmentManager,"GET_PERSON")
        }




    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
//                Toast.makeText(this,"djksdjkd",Toast.LENGTH_SHORT).show()
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setRecycler() {
        membersRecycler.adapter = GroupAdapter(this,memberList,isAdmin,groupId!!)
        Log.i("isAdmin",memberList[0].isAdmin!!.toString())
        membersRecycler.layoutManager = LinearLayoutManager(this)
        progress!!.dismiss()



    }
}
