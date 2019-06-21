package guru.baithak.starmark.ui.mainScreen.Groups

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import guru.baithak.starmark.Helpers.groupKey
import guru.baithak.starmark.ui.mainScreen.Calls.Calls
import guru.baithak.starmark.Models.Groups
import guru.baithak.starmark.Helpers.groupName
import guru.baithak.starmark.R
import guru.baithak.starmark.ui.groups.Chats.Chats
import guru.baithak.starmark.ui.groups.Files.Files
import guru.baithak.starmark.ui.groups.NewTopic.AddTopic
import guru.baithak.starmark.ui.groups.Notifications.Notifications
import guru.baithak.starmark.ui.groups.groupDetails.GroupDetails
import kotlinx.android.synthetic.main.activity_each_group.*
import java.io.File

class EachGroup : AppCompatActivity() {

    var group: Groups?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_each_group)
        setSupportActionBar(eachGroupActionBar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        group = intent.getParcelableExtra<Groups>(groupName)
        group!!.groupKey = intent.getStringExtra(groupKey)
        Log.i("GROUP each",group!!.groupName)
        Log.i("GROUP each",group!!.groupKey)
        if(group!=null){
            viewSetter()
        }else{
            Toast.makeText(this,"Error getting group",Toast.LENGTH_SHORT).show()
        }

    }

    fun viewSetter(){
        val b = Bundle()
        falseUp.setOnClickListener{v->
            NavUtils.navigateUpFromSameTask(this)

        }
        b.putParcelable(groupName,group)
        b.putBoolean("fromMain",true)
        eachGroupBottomNav.setOnNavigationItemSelectedListener{menu:MenuItem->
            when(menu.itemId){
                R.id.bottomMessage->{

                    val chat = Chats()
                    chat.arguments = b
                    swapFragment(chat)

                }
                R.id.bottomAdd->{
                    val addtopic = AddTopic()
                    addtopic.arguments = b
                    swapFragment(addtopic)
                }

                R.id.bottomCall->
                    swapFragment(Calls())
                R.id.bottomNoti->{
                    val noti = Notifications()
                    noti.arguments = b
                    swapFragment(noti)
                }

                 R.id.bottomFiles->{
                     val f = Files()
                     f.arguments = b
                     swapFragment(f)

                 }
            }
            return@setOnNavigationItemSelectedListener true

        }
        groupNameEachGroup.text = group!!.groupName
        groupMembersEachGroup.text = group!!.member
//        Toast.makeText(this,"Size :"+group!!.keyTopic.size,Toast.LENGTH_SHORT ).show()
//        eachTopicRecycler.adapter = TopicListAdapter(this,group!!.keyTopic)
//        eachTopicRecycler.layoutManager = LinearLayoutManager(this)

    }
    fun swapFragment(frag:Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.eachTopicFragment,frag).commit()

    }

     fun groupInfo(view: View){
         val i = Intent(this,GroupDetails::class.java)
         i.putExtra(groupName,group)
         i.putExtra(groupKey,group!!.groupKey)
        startActivity(i)
    }



}
