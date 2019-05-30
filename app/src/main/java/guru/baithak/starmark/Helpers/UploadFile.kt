package guru.baithak.starmark.Helpers


import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.lang.Exception

class UploadFile(val basePath:String, val callBack:ResultUpload,val c:Context){
    var file:Uri?=null
    var uri:String?=null
    var type:String ="url"
    var path:String = basePath

    fun getFileName():String{
        var name= file!!.lastPathSegment

    try {
        val cur = c.contentResolver.query(file!!,null,null,null,null)
        val index = cur.getColumnIndex(OpenableColumns.DISPLAY_NAME)
         name = cur.getString(index)
        cur!!.close()
    }catch (err:Exception){

    }

        return name!!
    }

    fun uploadFile(){

        if(file == null){
            if(uri!=null){
                addToDatabase()
            }else{
                callBack.results(false,error = "No data added")
            }
            return
        }
        if(file!!.path == null){
            callBack.results(false,error = "Null")
            return
        }

        path+="/"+getFileName()
        path.replace("\\.","")
        path.replace("#","")
        path.replace("$","")
        path.replace("[","")
        path.replace("]","")
        Log.i("Path",file!!.path)
        val storageRef = FirebaseStorage.getInstance().getReference(path)
        val uploadTask = storageRef.putFile(file!!)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation storageRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                uri = task.result.toString()
                addToDatabase()

            } else {
                callBack.results(false,error = "Error getting uri")
                return@addOnCompleteListener
            }
        }
    }


    private fun addToDatabase(){
        if(uri == null){
            callBack.results(false,error ="No URL found")
            return
        }
        val data = HashMap<String,Any>()
        ////todo add data
        data.put("createdAt",ServerValue.TIMESTAMP)
        data.put("createdBy",FirebaseAuth.getInstance().currentUser!!.uid)
        data.put("createdByName",c.getSharedPreferences(sharedPref,Context.MODE_PRIVATE).getString(userNameSharedPref,"error")!!)
        if(FirebaseAuth.getInstance().currentUser!!.displayName!=null)
            data.put("createdByName",FirebaseAuth.getInstance().currentUser!!.displayName!!)
        data.put("type",type)
        if(file != null){
            data.put("fileName",getFileName())
        }
        data.put("url",uri as String)
        val key = FirebaseDatabase.getInstance().getReference(basePath).push().key
        FirebaseDatabase.getInstance().getReference(basePath).child(key!!)
                .setValue(data).addOnSuccessListener(OnSuccessListener(){
            callBack.results(true,success = "Uploaded Successfully")
        }).addOnFailureListener { e->
                callBack.results(false, error = e.localizedMessage)
        }
    }


    interface ResultUpload {

        fun results(successFull:Boolean,success:String?=null,error:String?=null)
    }
}