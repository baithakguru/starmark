package guru.baithak.starmark.ui.groups.Topics


import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import guru.baithak.starmark.Helpers.groupKey

import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.fragment_existing_topics.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ExistingTopics : Fragment() {
    val details =ArrayList<TopicsCollection>()
    var key:String?=null
    var keySubject:String?=null
    var name:String?=null
    var stars = HashMap<String,Int>()

    override fun onResume() {
        super.onResume()
        val br = Intent()
        br.setAction(context!!.packageName+".TopicSelected")
        br.putExtra("groupName",name)
        br.putExtra("subjectKey",keySubject)
        context!!.sendBroadcast(br)

    }

    override fun onPause() {
        super.onPause()
        val br = Intent()
        br.setAction(context!!.packageName+".TopicSelected")
        br.putExtra("groupName","reset")
        context!!.sendBroadcast(br)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        arguments?.let {
            key = it["groupKey"] as String
            name = it["groupName"] as String
            keySubject = it["subjectKey"] as String
        }
        return inflater.inflate(R.layout.fragment_existing_topics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    fun getData(){
        val path="groups/"+key+"/subjects/"+keySubject
        FirebaseDatabase.getInstance().getReference(path)
                .addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError){
            }

            override fun onDataChange(p1: DataSnapshot) {
                Log.i("new Data in existing",p1.toString())
                details.clear()
                for(subjects in p1.child("contents").children){
                    details.add(generateData(subjects))
                }
                stars.clear()
                for(subjects in p1.child("stars").children){
                    stars[subjects.key!!] = Integer.parseInt(subjects.value.toString())
                }
                topicsLoader?.visibility = View.GONE
                if(details.size == 0){
                    noTopicsAdded?.visibility = View.VISIBLE
                }
                expandableTopics?.let {
                    if(it.adapter == null){
                        it.adapter = (ExpandAdapter(context!!, details, true,
                                key+"/topics/"+keySubject,stars,""))
                        it.layoutManager = LinearLayoutManager(context)
                    }else{
                        (it.adapter as ExpandAdapter).details = details
                        (it.adapter as ExpandAdapter).stars = stars
                        (it.adapter as ExpandAdapter).notifyDataSetChanged()
                    }
                }
            }
        })
    }


    fun generateData(dataSnapshot: DataSnapshot): TopicsCollection{
        if(!dataSnapshot.hasChildren()){
            Log.i("groupName des",dataSnapshot.key)
            return TopicsCollection(dataSnapshot.key!!,0,ArrayList<TopicsCollection>())
        }else{
            val localTopic =ArrayList<TopicsCollection>()
            for(child in dataSnapshot.children){
                localTopic.add(generateData(child))
            }
            return TopicsCollection(dataSnapshot.key!!,0,localTopic)
        }



    }





}
