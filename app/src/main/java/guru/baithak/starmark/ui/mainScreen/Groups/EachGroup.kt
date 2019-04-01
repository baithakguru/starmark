package guru.baithak.starmark.ui.mainScreen.Groups

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.MenuItem
import android.widget.Toast
import guru.baithak.starmark.ui.mainScreen.Calls.Calls
import guru.baithak.starmark.Models.Groups
import guru.baithak.starmark.Helpers.groupName
import guru.baithak.starmark.R
import guru.baithak.starmark.ui.groups.Chats.Chats
import guru.baithak.starmark.ui.groups.Files.Files
import guru.baithak.starmark.ui.groups.NewTopic.AddTopic
import guru.baithak.starmark.ui.groups.Notifications.Notifications
import kotlinx.android.synthetic.main.activity_each_group.*

class EachGroup : AppCompatActivity() {

    var group: Groups?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_each_group)
        setSupportActionBar(eachGroupActionBar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        group = intent.getParcelableExtra<Groups>(groupName)

        if(group!=null){
            viewSetter()
        }else{
            Toast.makeText(this,"Error getting group",Toast.LENGTH_SHORT).show()
        }

    }

    fun viewSetter(){
        val b = Bundle()
        b.putParcelable(groupName,group)
        eachGroupBottomNav.setOnNavigationItemSelectedListener{menu:MenuItem->
            when(menu.itemId){
                R.id.bottomMessage->{

                    val chat = Chats()
                    chat.arguments = b
                    swapFragment(chat)

                }
                R.id.bottomAdd->
                    swapFragment(AddTopic())
                R.id.bottomCall->
                    swapFragment(Calls())
                R.id.bottomNoti->
                    swapFragment(Notifications())
                R.id.bottomFiles->
                    swapFragment(Files())

            }
            return@setOnNavigationItemSelectedListener true

        }
        groupNameEachGroup.text = group!!.name
        groupMembersEachGroup.text = group!!.member
//        Toast.makeText(this,"Size :"+group!!.topics.size,Toast.LENGTH_SHORT ).show()
//        eachTopicRecycler.adapter = TopicListAdapter(this,group!!.topics)
//        eachTopicRecycler.layoutManager = LinearLayoutManager(this)

    }
    fun swapFragment(frag:Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.eachTopicFragment,frag).commit()

    }

}
