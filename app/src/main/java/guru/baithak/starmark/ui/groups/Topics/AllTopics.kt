package guru.baithak.starmark.ui.groups.Topics


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import guru.baithak.starmark.Helpers.groupKey

import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.fragment_all_topics.*
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AllTopics : Fragment() {

    var key:String?=null
    val topics = HashMap<String,String>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        key = arguments?.getString(groupKey)
        return inflater.inflate(R.layout.fragment_all_topics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val path = "groups/"+key+"/subjects"
        FirebaseDatabase.getInstance().getReference(path).addValueEventListener(object :ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                topics.clear()

                    for(p1 in p0.children){
                        try{
                        val details = p1.child("detailsTopic").value.toString()
                        topics.put(details, p1.key!!)
                        }catch (e:Exception){}
                    }
                allTopicsRecycler?.let {
                    it.adapter= AllTopicsAdapter(context!!,key!!,topics)
                    it.layoutManager = LinearLayoutManager(context)
                }

            }
        })
    }
}
