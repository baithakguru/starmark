package guru.baithak.starmark.ui.groups.groupDetails

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import guru.baithak.starmark.Models.Person
import guru.baithak.starmark.R

class GroupAdapter(val c:Context,val members:ArrayList<Person>,val isAdmin:Boolean
,val groupId:String
): RecyclerView.Adapter<GroupAdapter.EachView>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EachView {
        return EachView(LayoutInflater.from(c).inflate(R.layout.viewholder_members,p0,false))
    }

    override fun getItemCount(): Int {
       return members.size
    }

    override fun onBindViewHolder(p0: EachView, p1: Int) {
        p0.name.text= members[p1].name

        if(isAdmin){
            p0.isAdmin.setImageResource(R.drawable.ic_remove_circle_outline_black_24dp)
            p0.isAdmin.visibility = View.VISIBLE
            p0.isAdmin.setOnClickListener {
                   removeUser(p1)
            }
            if(members[p1].userKey == FirebaseAuth.getInstance().currentUser!!.uid){
                p0.isAdmin.visibility= View.GONE
            }
        }else{
            if(members[p1].isAdmin!!){
                p0.isAdmin.visibility=View.VISIBLE
            }
        }


    }

    fun removeUser(position:Int){
        val progress = ProgressDialog(c)
        progress.setMessage("Please wait while we remove user")
        progress.setTitle("Hold on...")
        progress.show()
        val uid = members[position].userKey
        val path = "groups/"+groupId+"/members/avail/"+
                uid+"/remove"
        FirebaseDatabase.getInstance().getReference(path).setValue(true).addOnCompleteListener {
            members.removeAt(position)
            notifyItemRemoved(position)
            progress.dismiss()
            Toast.makeText(c,"Member Removed",Toast.LENGTH_SHORT).show()
        }

    }

    class EachView(val view: View): RecyclerView.ViewHolder(view){
        val name:TextView= view.findViewById(R.id.memberName)
        val lastSeen:TextView= view.findViewById(R.id.memberLastSeen)
        val isAdmin:ImageView=view.findViewById(R.id.isAdmin)
    }
}