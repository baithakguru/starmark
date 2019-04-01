package guru.baithak.starmark.Models

import android.os.Parcel
import android.os.Parcelable
import java.util.*
import guru.baithak.starmark.Models.Topic

class Groups(var name : String, var isMuted:Boolean,var member:String, var lastActive: String,var notify:Boolean,var topics:ArrayList<Topic> = ArrayList()):Parcelable{


    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readArrayList(Topic::class.java!!.classLoader) as ArrayList<Topic>
    ) {
    }


    fun addTopics(topic:Topic){
        topics.add(topic)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeByte(if (isMuted) 1 else 0)
        parcel.writeString(member)
        parcel.writeString(lastActive)
        parcel.writeByte(if (notify) 1 else 0)
        parcel.writeList(topics)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Groups> {
        override fun createFromParcel(parcel: Parcel): Groups {
            return Groups(parcel)
        }

        override fun newArray(size: Int): Array<Groups?> {
            return arrayOfNulls(size)
        }
    }


}