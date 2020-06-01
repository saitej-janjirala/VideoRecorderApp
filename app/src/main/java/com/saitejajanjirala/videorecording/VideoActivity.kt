package com.saitejajanjirala.videorecording

import android.annotation.SuppressLint
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.size.SizeSelector
import kotlinx.android.synthetic.main.activity_video.*
import java.io.File
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        camview.setLifecycleOwner(this)
        camview.mode=Mode.VIDEO
        camview.videoBitRate=3000000
        camview.addCameraListener(object:CameraListener(){
            @SuppressLint("SimpleDateFormat", "SetTextI18n")
            override fun onVideoTaken(result: VideoResult) {
                super.onVideoTaken(result)
                Log.i("path",result.file.path.toString())
                start.visibility=View.VISIBLE
                stop.visibility=View.GONE
                val connectivity=Connectivity(this@VideoActivity)
                if(connectivity.checkconnectivity()) {
                    val file=result.file
                    val uploaddialog = AlertDialog.Builder(this@VideoActivity)
                    val view = LayoutInflater.from(this@VideoActivity)
                        .inflate(R.layout.uploadinglayout, camview, false)
                    val progressbartext=view.findViewById<TextView>(R.id.uploadtext)
                    val progressbar=view.findViewById<ProgressBar>(R.id.uploadingprogress)
                    uploaddialog.setCancelable(true)
                    uploaddialog.setPositiveButton("close"){
                            text,listener->
                    }
                    uploaddialog.setView(view)
                    uploaddialog.create()
                    uploaddialog.show()
                    try{
                        val uid=UUID.randomUUID().toString()
                        val ref=FirebaseStorage.getInstance().reference
                        val reference=ref?.child("uploads/$uid")
                        reference.putFile(Uri.fromFile(file)).addOnSuccessListener {
                            reference.downloadUrl.addOnSuccessListener {
                                val url=it.toString()
                                val timeStamp: String =
                                    SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss").format(Date())
                                val obj=UploadInfo(timeStamp,url)
                                val dbref=FirebaseDatabase.getInstance().reference
                                val uploadid: String? = dbref.push().getKey()
                                if (uploadid != null) {
                                    dbref.child(uploadid).setValue(obj).addOnSuccessListener {
                                        progressbar.visibility=View.GONE
                                        progressbartext.text="Successfully uploaded go back and check the video"
                                        file.delete()
                                    }
                                }
                            }
                        }
                            .addOnProgressListener {
                                val total=it.totalByteCount.toInt()
                                val current=it.bytesTransferred.toInt()
                                progressbar.max=total
                                progressbar.progress=current
                            }
                            .addOnFailureListener {
                                progressbartext.text=it.toString()
                                progressbar.visibility=View.GONE

                            }
                            .addOnCanceledListener {

                            }
                    }
                    catch(e:Exception){
                            Toast.makeText(this@VideoActivity,e.message.toString(),Toast.LENGTH_LONG).show()
                    }



                }
            }
        })
        start.setOnClickListener {
            val videofile=createvideofile()
            if(videofile!=null) {
                camview.takeVideo(
                    videofile
                    , 30000
                )
                start.visibility = View.GONE
                stop.visibility = View.VISIBLE
            }
            else{
                Toast.makeText(this,"Cannot do recording",Toast.LENGTH_LONG).show()
            }
        }
        stop.setOnClickListener {
            start.visibility=View.VISIBLE
            stop.visibility=View.GONE
            camview.stopVideo()
        }
    }
    fun createvideofile():File{
        val filename="MyVideo ${System.currentTimeMillis()}"
        val storagedir=getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File.createTempFile(filename,"mp4",storagedir)
    }

    override fun onBackPressed() {
        if(camview.isTakingVideo){
            val dialog=AlertDialog.Builder(this)
            dialog.setTitle("Stop the Video Before closing")
            dialog.setCancelable(false)
            dialog.setNegativeButton("Stop and Go Back"){text,listener->
                if(camview.isTakingVideo) {
                    camview.stopVideo()
                    super.onBackPressed()
                }
                else{
                    super.onBackPressed()
                }
            }
            dialog.setPositiveButton("Don't Stop"){text,listenr->{
            }}
            dialog.create()
            dialog.show()
        }
        else {
            super.onBackPressed()
        }
    }
}
