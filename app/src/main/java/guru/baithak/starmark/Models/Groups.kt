package guru.baithak.starmark.Models

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Groups(var groupName : String?=null, var isMuted:Boolean?=false, var member:String?=null, var lastActive: String?=null, var notify:Boolean?=false, var topics:ArrayList<Topic> = ArrayList(), var groupKey:String?=null,val lastModifiedAt:Long?=null):Parcelable{

    @field:JvmField var isActive:Boolean = true


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
        parcel.writeString(groupName)
        parcel.writeByte(if (isMuted!!) 1 else 0)
        parcel.writeString(member)
        parcel.writeString(lastActive)
        parcel.writeByte(if (notify!!) 1 else 0)
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