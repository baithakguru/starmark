package guru.baithak.starmark.ui.groups.groupDetails

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.TimeUtils
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import guru.baithak.starmark.Helpers.groupKey
import guru.baithak.starmark.Models.Groups
import guru.baithak.starmark.Models.Person
import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.activity_group_details.*
import kotlinx.android.synthetic.main.viewholder_groups.*
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
//        actionBar!!.title = "Trial"
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
        }
        val date = Date(dataSnapshot.child("created").value as Long)
        Log.i("date",date.toString())
        val dateFormat = SimpleDateFormat("dd MMM yy").format(date)


        groupDetails.text =  String.format("%d members\nCreator: %s\nCreated at: %s",memberList.size,dataSnapshot.child("createdBy").value,dateFormat)


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




    }

    private fun setRecycler() {
        membersRecycler.adapter = GroupAdapter(this,memberList,isAdmin,groupId!!)
        Log.i("isAdmin",memberList[0].isAdmin!!.toString())
        membersRecycler.layoutManager = LinearLayoutManager(this)
        progress!!.dismiss()



    }
}
