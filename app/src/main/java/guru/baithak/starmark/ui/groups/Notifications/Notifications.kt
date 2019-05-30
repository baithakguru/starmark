package guru.baithak.starmark.ui.groups.Notifications


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import guru.baithak.starmark.Helpers.groupName
import guru.baithak.starmark.Models.Groups
import guru.baithak.starmark.Models.Topic

import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.fragment_notifications.*

class Notifications : Fragment() {

    var group:Groups?=null
    val topics = ArrayList<Topic>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        getTopics()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments == null){
            return
        }
        group = arguments!!.getParcelable(groupName)
        getTopics()
    }

    fun viewSetter(){
        val adapter = Adapter(context!!,topics)
        val manager = LinearLayoutManager(context!!)
        notificationsRecycler.adapter = adapter
        notificationsRecycler.layoutManager = manager


    }

    fun getTopics(){
        val refPath = "groups/"+group!!.groupKey+"/topics"
        Log.i("topics path",refPath)
        val ref = FirebaseDatabase.getInstance().getReference(refPath)
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                topics.clear()
                Log.i("topics whole",p0.key)
                Log.i("topics whole",p0.toString())
                for (topic in p0.children){
//                    Toast.makeText(context,(topic.value as HashMap<String,Any>).size,Toast.LENGTH_LONG).show()
                    Log.i("topics data ",topic.toString())
                    val key = topic.key
                    val name = topic.child("detailsTopic").getValue(String::class.java)
                    topics.add(Topic(name!!,key!!))
                }
                viewSetter()
            }
        })
    }
}
