package com.saitejajanjirala.videorecording

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var madapter:VideosAdapter
    lateinit var layoutmanager:RecyclerView.LayoutManager
    lateinit var marraylist:ArrayList<UploadInfo>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        marraylist=ArrayList()
        layoutmanager=GridLayoutManager(this,2)
        val connectivity=Connectivity(this)
        mostrepeated()
        refreshlayout.setOnRefreshListener {
            mostrepeated()
            refreshlayout.isRefreshing=false
        }
        floatingActionButton.setOnClickListener {
            val strings=arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO)
                if (ContextCompat.checkSelfPermission(this,strings[0]) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this,strings[1]) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this,strings[2]) != PackageManager.PERMISSION_GRANTED||
                      ContextCompat.checkSelfPermission(this,strings[3])!=PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, strings,1378)
                }
                else{
                    startActivity(Intent(this,VideoActivity::class.java))
                }

        }
    }

    override fun onRestart() {
        super.onRestart()
        mostrepeated()
    }
    fun initrecyclerview(){
        val ref=FirebaseDatabase.getInstance().reference
        val listener=object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                marraylist.clear()
                p0.children.forEach {
                    it!!.getValue(UploadInfo::class.java)?.let { it1 -> marraylist.add(it1) }
                }
                marraylist.reverse()
                madapter= VideosAdapter(this@MainActivity,marraylist)
                recyclerview.layoutManager=layoutmanager
                recyclerview.adapter=madapter

            }
        }
        ref.addValueEventListener(listener)

    }
    fun mostrepeated(){
        val connectivity=Connectivity(this)
        if(connectivity.checkconnectivity()) {
            initrecyclerview()
        }
        else{
            connectivity.showdialog()
        }
    }

}
