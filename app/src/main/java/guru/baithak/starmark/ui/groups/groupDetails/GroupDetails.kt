package guru.baithak.starmark.ui.groups.groupDetails

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import guru.baithak.starmark.Helpers.groupKey
import guru.baithak.starmark.Models.Person
import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.activity_group_details.*


class GroupDetails : AppCompatActivity() {

    var groupKey :String?=null
    val memberList:ArrayList<Person> = ArrayList()
    var progress :ProgressDialog?=null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)
        progress = ProgressDialog.show(this,"Getting group info","Please wait while we fetch details")
        setSupportActionBar(toolbar)
//        actionBar!!.title = "Trial"
        supportActionBar!!.title=" "
        groupKey = intent.getStringExtra(groupKey)
        getData()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, p1 ->
            val alpha : Float = Math.abs(p1 / app_bar.totalScrollRange.toFloat())
            groupNameToolbar.alpha = alpha
            groupInfoTable.alpha = 1f-alpha
        })

    }
    fun getData(){

        val path = "groups/"+groupKey+"/members/avail"
        FirebaseDatabase.getInstance().getReference(path).addValueEventListener(
                object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        Toast.makeText(this@GroupDetails, "Error getting details", Toast.LENGTH_LONG).show()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        memberList.clear()
                       for (members in p0.children){
                            val person = members.getValue(Person::class.java)
                           person!!.userKey = members.key
                           memberList.add(person)
                       }
                        setRecycler()
                    }
                }
        )


    }

    private fun setRecycler() {
        progress!!.dismiss()



    }
}
