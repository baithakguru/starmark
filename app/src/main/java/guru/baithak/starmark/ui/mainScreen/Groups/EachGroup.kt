package guru.baithak.starmark.ui.mainScreen.Groups

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.util.Log
import android.util.TypedValue
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
import guru.baithak.starmark.ui.groups.Topics.AllTopics
import guru.baithak.starmark.ui.groups.Topics.ExistingTopics
import guru.baithak.starmark.ui.groups.groupDetails.GroupDetails
import kotlinx.android.synthetic.main.activity_each_group.*
import java.io.File

class EachGroup : AppCompatActivity() {

    var group: Groups?= null
    var subjectKey: String? = null

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
        b.putString(groupKey,group!!.groupKey)
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
                    val noti = AllTopics()
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
        val noti = AllTopics()
        noti.arguments = b
        swapFragment(noti)
        groupMembersEachGroup.text = group!!.member
//        Toast.makeText(this,"Size :"+group!!.keyTopic.size,Toast.LENGTH_SHORT ).show()
//        eachTopicRecycler.adapter = TopicListAdapter(this,group!!.keyTopic)
//        eachTopicRecycler.layoutManager = LinearLayoutManager(this)

       setActionBar(group!!.groupName!!)

        val intentFilter = IntentFilter(packageName+".TopicSelected")
        registerReceiver(object :BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    var name = it.getStringExtra("groupName")
                    if(name.equals("reset")){
                        subjectKey =null
                        name = group!!.groupName
                    }else{
                        it.getStringExtra("subjectKey")
                    }
                    setActionBar(name)
                }
            }
        },intentFilter)

    }

    fun setActionBar(name:String){
        groupNameEachGroup.text =name
        val bitmap = Bitmap.createBitmap(dpToPx(80f).toInt(),dpToPx(80f).toInt(), Bitmap.Config.ARGB_8888)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.isAntiAlias=true
        p.textSize=dpToPx(50f)
        p.color= Color.parseColor("#01579b")
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.parseColor("#FFFFFF"))
        canvas.drawText(name.capitalize().substring(0,1),bitmap.width/2f-dpToPx(16f),bitmap.height/2f+dpToPx(16f),p)
        groupIconEachGroup.setImageBitmap(bitmap)

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

    fun dpToPx(dp:Float):Float{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,resources.displayMetrics)
    }



}
