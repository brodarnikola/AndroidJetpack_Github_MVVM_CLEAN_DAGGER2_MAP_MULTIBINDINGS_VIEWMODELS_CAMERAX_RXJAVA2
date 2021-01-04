package com.vjezba.androidjetpackgithub.ui.adapters.camerax

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vjezba.androidjetpackgithub.R
import kotlinx.android.synthetic.main.list_camerax_picture_video.view.*
import java.io.File

class CameraXGalleryAdapter(var pictureVideoList: MutableList<File>,
                            val pictureVideoClickListener: (File) -> Unit )
    : RecyclerView.Adapter<CameraXGalleryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val layoutParent: ConstraintLayout = itemView.list_camerax_top_layout
        val photo: ImageView = itemView.ivPicture
        val playForVideo: ImageView = itemView.ivPlayVideo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_camerax_picture_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindItem(holder, pictureVideoList[position], position)
    }

    private fun bindItem(holder: ViewHolder, article: File, position: Int) {

        Glide.with(holder.itemView)
            .load(article)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.photo)

        if( article.toString().contains(VIDEO_FILE) ) {
            holder.playForVideo.visibility = View.VISIBLE
        }
        else {
            holder.playForVideo.visibility = View.GONE
        }

        holder.layoutParent.setOnClickListener{
            pictureVideoClickListener(article)
        }
    }

    override fun getItemCount(): Int {
        return pictureVideoList.size
    }

}