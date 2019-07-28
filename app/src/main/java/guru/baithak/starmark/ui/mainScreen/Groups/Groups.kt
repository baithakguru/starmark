package guru.baithak.starmark.ui.mainScreen.Groups


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import guru.baithak.starmark.Models.Groups
import guru.baithak.starmark.Models.Topic

import guru.baithak.starmark.R
import guru.baithak.starmark.ui.newGroup.SelectMembers.SelectContacts
import kotlinx.android.synthetic.main.fragment_groups.*
import java.lang.Exception

class Groups : Fragment() {

    val groups = ArrayList<Groups>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    fun viewSetter(){
        try {
            groupsRecycler.adapter = GroupsAdapter(context!!, groups)
            groupsRecycler.layoutManager = LinearLayoutManager(context)

        }catch (e:Exception){

        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getGroups()
        //        viewSetter()
        createGroup.setOnClickListener{v->
            switchFab()
        }

    }

    fun setRotation(r:Int){
        createGroup.animate().rotation(r.toFloat())
    }

    fun switchFab(){
        val rotation = createGroup.rotation.toInt()
        addGroup()
//        when(rotation){
//            0->{
//                setRotation(45)
//            }
//
//            else->{
//                setRotation(0)
//            }
//        }

    }

    fun addGroup(){
        startActivity(Intent(context, SelectContacts::class.java))
    }

    fun getGroups(){

        val ref = FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().currentUser!!.uid+"/groups")
//        ref.keepSynced(true)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Snackbar.make(rootGroups,"Error Getting Data",Snackbar.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                groups.clear()
                for(child in p0.children){
                    val childData: Groups? = child.getValue(Groups::class.java)
                    childData!!.isActive = child.child("isActive").value as Boolean
                    childData.groupKey = child.key
                    Log.i("group get",child.key)
                    groups.add(childData)
                }

                Log.i("Data",p0.toString())
                viewSetter()
            }

        })



    }


}
