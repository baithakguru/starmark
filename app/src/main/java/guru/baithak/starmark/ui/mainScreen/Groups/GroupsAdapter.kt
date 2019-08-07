package guru.baithak.starmark.ui.mainScreen.Groups

import android.app.AlertDialog
import android.app.Dialog
import android.content.ComponentCallbacks
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.service.autofill.Validators.not
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import guru.baithak.starmark.Helpers.groupKey
import guru.baithak.starmark.Models.Groups
import guru.baithak.starmark.Helpers.groupName
import guru.baithak.starmark.R

class GroupsAdapter(c : Context , groups : ArrayList<Groups>) : RecyclerView.Adapter<ViewHolderGroups>(),ViewHolderGroups.DeleteGroup {
    override fun delete(groupId: String) {
        for(g in groups){
            if(g.groupKey == groupId){
                groups.remove(g)
                val path ="users/"+FirebaseAuth.getInstance().currentUser?.uid+"/groups/"+groupId
                FirebaseDatabase.getInstance().getReference(path).setValue(null)
                notifyDataSetChanged()
                break
            }
        }
    }

    val c:Context =c
    val groups:ArrayList<Groups> = groups

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolderGroups {
       return ViewHolderGroups(
           LayoutInflater.from(c).inflate(
               R.layout.viewholder_groups,
               p0,
               false
           ), c,this
       )
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    override fun onBindViewHolder(p0: ViewHolderGroups, p1: Int) {
        p0.name.text=groups[p1].groupName
        p0.lastActive.text= groups[p1].lastActive
        p0.members.text= groups[p1].member
        p0.item.tag = groups[p1]
        Log.i("group recycler",groups[p1].groupKey)

        if (groups[p1].lastActive == null){
            p0.lastActive.visibility=View.GONE
        }

        p0.item.setOnClickListener { v ->
            val g = groups[p1]
            g.notify = false
            notifyDataSetChanged()
            if ((g.isActive)) {
                val i = Intent(c, EachGroup::class.java)
                i.putExtra(groupName, g)
                i.putExtra(groupKey, g.groupKey)
                c.startActivity(i)
            } else {
                Toast.makeText(c, "You are no longer member of this group", Toast.LENGTH_SHORT).show()
            }
        }



            val isMuted : Boolean = groups[p1].isMuted!!
        val isNoti : Boolean = groups[p1].notify!!

        if(isMuted){
            p0.isMuted.visibility = View.VISIBLE
            p0.isNoti.background=c.getDrawable(R.color.colorAccent)
        }

        if(!isNoti){
            p0.isNoti.visibility = View.GONE
        }


        val b = Bitmap.createBitmap(dpToPx(80f).toInt(),dpToPx(80f).toInt(),Bitmap.Config.ARGB_8888)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.isAntiAlias=true


        p.textSize=dpToPx(50f)
        p.color=Color.WHITE
        val canvas = Canvas(b)
//        canvas.drawARGB(100,45,45,45)
        canvas.drawColor(Color.parseColor("#01579b"))
        canvas.drawText(groups[p1].groupName!!.capitalize().substring(0,1),b.width/2f-dpToPx(16f),b.height/2f+dpToPx(16f),p)

        p0.icon.setImageBitmap(b)


    }

    fun dpToPx(dp:Float):Float{
       return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,c.resources.displayMetrics)
    }

}


class ViewHolderGroups(val item : View,val c:Context,val callbacks: DeleteGroup) : RecyclerView.ViewHolder(item){
    val name : TextView = item.findViewById(R.id.groupName) as TextView
    val members : TextView = item.findViewById(R.id.groupMembers) as TextView
    val lastActive : TextView = item.findViewById(R.id.groupActive) as TextView
    val isMuted : ImageView = item.findViewById(R.id.groupIsMuted) as ImageView
    val isNoti : View = item.findViewById(R.id.groupIsNotiAvail)
    val icon :ImageView = item.findViewById(R.id.groupIcon)
    init {


        item.setOnLongClickListener(object :View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                if(((v!!.tag as Groups).isActive)){
                    Toast.makeText(c,"Cannot delete an active group",Toast.LENGTH_SHORT).show()
                    return true
                }
                AlertDialog.Builder(c).setMessage("Are you sure you want to delete this group?").setPositiveButton("No",
                        object :DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog?.dismiss()
                            }
                        }
                        ).setNegativeButton("Yes",
                        object :DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                callbacks.delete((v.tag as Groups).groupKey!!)
                                dialog?.dismiss()
                            }
                        }
                        ).show()
                return true

            }

        })
    }

    interface DeleteGroup{
        fun delete(groupId:String)
    }

}