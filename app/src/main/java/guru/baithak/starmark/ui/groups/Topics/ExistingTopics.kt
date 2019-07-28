package guru.baithak.starmark.ui.groups.Topics


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


class ExistingTopics : Fragment() {
    val details =ArrayList<TopicsCollection>()
    var key:String?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        key = arguments!![groupKey] as String
        return inflater.inflate(R.layout.fragment_existing_topics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
    }

    fun getData(){
        val path="groups/"+key+"/subjects"
        FirebaseDatabase.getInstance().getReference(path).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                details.clear()
                for(p1 in p0.children){
                    val subName = (p1.child("detailsTopic").value as String).split("/")
                    val local = ArrayList<TopicsCollection>()
                    for(subTop in p1.child("contents").children){
                        local.add(generateData(subTop))
                    }
                    details.add(TopicsCollection(subName[subName.size-1],0,local))
                }

                topicsLoader.visibility = View.GONE
                if(details.size == 0){
                    noTopicsAdded.visibility = View.VISIBLE
                }

                expandableTopics?.adapter = (ExpandAdapter(context!!, details, true))
                expandableTopics?.layoutManager = LinearLayoutManager(context)
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
