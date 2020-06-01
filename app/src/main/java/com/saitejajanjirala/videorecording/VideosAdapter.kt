package com.saitejajanjirala.videorecording

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class VideosAdapter(val context:Context,val arraylist:ArrayList<UploadInfo>) : RecyclerView.Adapter<VideosAdapter.VideosViewHolder>() {
    class VideosViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val thumbnail=itemView.findViewById<ImageView>(R.id.thumbnail)
        val date=itemView.findViewById<TextView>(R.id.date)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.videoitem,parent,false)
        return VideosViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arraylist.size
    }

    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        try {
            val interval: Long = position * 1000.toLong()
            val options = RequestOptions().frame(interval)
            Glide.with(context).asBitmap().load(arraylist[position].url).apply(options)
                .into(holder.thumbnail)
            holder.date.text = arraylist[position].name
            holder.thumbnail.setOnClickListener {
                    val intent= Intent(context,PlayActivity::class.java)
                    intent.putExtra("videourl",arraylist[position].url)
                    context.startActivity(intent)
            }
        }
        catch(e:Exception){
            Log.i("error while",e.message.toString())
        }
    }
}