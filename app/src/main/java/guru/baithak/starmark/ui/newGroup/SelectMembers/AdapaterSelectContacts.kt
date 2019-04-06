package guru.baithak.starmark.ui.newGroup.SelectMembers

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.content.res.ResourcesCompat.getColor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import guru.baithak.starmark.R
import java.util.*

class AdapterSelectContacts(var C:Context, val contacts:ArrayList<Contact>, val callback: ContactSelectedCallback): RecyclerView.Adapter<AdapterSelectContacts.Views>() {

    companion object {
        var selected:Int =0
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Views {

        return Views(
            LayoutInflater.from(C).inflate(
                R.layout.view_holder_each_contact_long,
                p0,
                false
            ), C, callback
        )

    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(p0: Views, p1: Int) {
        p0.name.text=contacts[p1].name
        p0.number.text=contacts[p1].number
        p0.root.tag=contacts[p1]
    }


    class Views(item : View,C: Context,callback: ContactSelectedCallback):RecyclerView.ViewHolder(item){
        val name:TextView = item.findViewById(R.id.viewHolderNamePerson)
        val number:TextView = item.findViewById(R.id.viewHolderPhonePerson)
        val root =  item.findViewById(R.id.contactSelectRoot) as View

        init {


            root.setOnClickListener{v->
                if(!(root.tag as Contact).selected){
                    if(AdapterSelectContacts.selected >=5){
                        Toast.makeText(C,"Max users selected",Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                    AdapterSelectContacts.selected++
                    v.setBackgroundColor(getColor(C.resources,R.color.colorSelected,null))
                    (root.tag as Contact).selected =true
                }else{
                    AdapterSelectContacts.selected--
                    v.setBackgroundColor(getColor(C.resources,R.color.white,null))
                    (root.tag as Contact).selected =false

                }

                callback.personSelected(root.tag as Contact)

//                if(v.background.alpha == 0){
//                    v.background.alpha =100
//                }else{
//                    v.background.alpha =0
//
//
//                }
            }
        }

    }

    class Contact(var name:String ,val number:String,var selected:Boolean = false):Parcelable{
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte()
        )
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeString(number)
            parcel.writeByte(if (selected) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Contact> {
            override fun createFromParcel(parcel: Parcel): Contact {
                return Contact(parcel)
            }

            override fun newArray(size: Int): Array<Contact?> {
                return arrayOfNulls(size)
            }
        }

    }
}