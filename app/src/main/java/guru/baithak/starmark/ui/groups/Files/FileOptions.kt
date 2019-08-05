package guru.baithak.starmark.ui.groups.Files


import android.Manifest
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import guru.baithak.starmark.Helpers.DownloadCompleted
import guru.baithak.starmark.Helpers.UploadFile

import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.fragment_file_options.*
import java.io.File
import java.net.URI


class FileOptions : DialogFragment() {

    var data = HashMap<String,Any>()
    val notImp = "Remove from important"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        arguments?.let {
            data.putAll((it.getSerializable("data") as HashMap<String,Any>))
        }
        return inflater.inflate(R.layout.fragment_file_options, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(data["selfMark"] as Boolean){
            fileImportant.text = notImp
        }
        fileImportant.setOnClickListener {
            if (fileImportant.text.equals(notImp)){
                FirebaseDatabase.getInstance().getReference(data["topic"].toString()).child("stars")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).removeValue()
            }
            else{
                FirebaseDatabase.getInstance().getReference(data["topic"].toString()).child("stars")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(ServerValue.TIMESTAMP)
            }
            dismiss()
        }
//        fileDownload.text = data["topic"].toString()
        if(fileExists(data["fileName"].toString())){
            fileDownload.visibility = View.GONE
            fileView.visibility = View.VISIBLE
        }

        fileDownload.setOnClickListener {
            if(!hasPermissions())
                return@setOnClickListener
            val request = DownloadManager.Request(Uri.parse(data["url"] as String))
            Log.i("downloadUrl",(Uri.parse(data["url"] as String)).toString())
            request.setDestinationInExternalPublicDir("StarMark"
                    ,data["fileName"] as String)
            (context!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
            Toast.makeText(context!!,"Your download is started", Toast.LENGTH_SHORT).show()
            context!!.registerReceiver(DownloadCompleted(), IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            dismiss()
        }

        fileView.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            val path = "file://"+Environment.getExternalStoragePublicDirectory("StarMark").absolutePath + File.separator +
                    data["fileName"]
            val uri = FileProvider.getUriForFile(context!!,"com.starmark.fileprovider", File(Uri.parse(path).path))
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            if (mimeType == null)
            {
                Toast.makeText(context!!,"File invalid",Toast.LENGTH_SHORT).show()
                dismiss()
                return@setOnClickListener
            }
            Log.i("fileFormat","  p "+path+" type  "+mimeType+" uri  "+uri.toString())
            i.setDataAndType(uri,mimeType)
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(i)
            dismiss()

        }

    }

    fun hasPermissions():Boolean{
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1)

            return false
        }
        return true
    }
    fun fileExists(fileName :String):Boolean{
        if(!hasPermissions()){
            return false
        }
        val basePath = Environment.getExternalStoragePublicDirectory("StarMark").absolutePath
        val base = File(basePath)
        if(!base.exists()){
            base.mkdirs()
            return false
        }
        if(fileName.equals("")){
            return false
        }
        val finalPath = basePath + File.separator + fileName
        return File(finalPath).exists()
    }
}
