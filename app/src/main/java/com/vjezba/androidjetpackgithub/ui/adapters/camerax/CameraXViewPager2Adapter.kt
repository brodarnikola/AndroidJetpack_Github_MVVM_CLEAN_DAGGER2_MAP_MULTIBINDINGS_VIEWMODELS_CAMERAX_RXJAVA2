package com.vjezba.androidjetpackgithub.ui.adapters.camerax

import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vjezba.androidjetpackgithub.BuildConfig
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.ui.utilities.showImmersive
import kotlinx.android.synthetic.main.list_details_camerax_picture_video.view.*
import java.io.File

val VIDEO_FILE = ".mp4"

class CameraXViewPager2Adapter(
    var pictureVideoList: MutableList<File>,
    val fragmentActivity: FragmentActivity
) : RecyclerView.Adapter<CameraXViewPager2Adapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val photo: ImageView = itemView.ivPicture

        val videoBuffering: TextView = itemView.buffering_textview
        val videoview: VideoView = itemView.videoview

        val shareButton: ImageButton = itemView.share_button
        val deleteButton: ImageButton = itemView.delete_button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_details_camerax_picture_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindItem(holder, pictureVideoList[position], position)
    }

    private fun bindItem(holder: ViewHolder, file: File, position: Int) {

        Glide.with(holder.itemView)
            .load(file)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.photo)

        holder.shareButton.setOnClickListener {
            displayShareDialog(holder, file)
        }

        holder.deleteButton.setOnClickListener {
            displayDeleteDialog(holder, file)
        }

        if (file.toString().contains(VIDEO_FILE)) {
            holder.photo.visibility = View.GONE
            holder.videoview.visibility = View.VISIBLE
            holder.videoBuffering.visibility = View.VISIBLE

            val controller = MediaController(holder.itemView.context)
            // Set up the media controller widget and attach it to the video view.
            controller.setMediaPlayer(holder.videoview)
            holder.videoview.setMediaController(controller)
            initializePlayer(holder.videoBuffering, holder.videoview, position, file, holder)

        } else {
            holder.photo.visibility = View.VISIBLE
            holder.videoBuffering.visibility = View.GONE
            holder.videoview.visibility = View.GONE
            holder.videoview.pause()
        }
    }

    private fun displayShareDialog(holder: ViewHolder, file: File) {

        // Create a sharing intent
        val intent = Intent().apply {
            // Infer media type from file extension
            val mediaType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(file.extension)
            // Get URI from our FileProvider implementation
            val uri = FileProvider.getUriForFile(
                holder.itemView.context, BuildConfig.APPLICATION_ID + ".provider", file
            )
            // Set the appropriate intent extra, type, action and flags
            putExtra(Intent.EXTRA_STREAM, uri)
            type = mediaType
            action = Intent.ACTION_SEND
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        // Launch the intent letting the user choose which app to share with
        holder.itemView.context.startActivity(
            Intent.createChooser(
                intent,
                holder.itemView.resources.getString(R.string.share_lego_set)
            )
        )
    }

    private fun displayDeleteDialog(holder: ViewHolder, file: File) {
        AlertDialog.Builder(holder.itemView.context, android.R.style.Theme_Material_Dialog)
            .setTitle(holder.itemView.resources.getString(R.string.delete_title))
            .setMessage(holder.itemView.resources.getString(R.string.delete_dialog))
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes) { _, _ ->

                // Delete current photo
                file.delete()

                // Notify our view pager
                pictureVideoList.remove(file)
                this@CameraXViewPager2Adapter.notifyDataSetChanged()

                // Send relevant broadcast to notify other apps of deletion
                MediaScannerConnection.scanFile(
                    holder.itemView.context, arrayOf(file.absolutePath), null, null
                )

                // If all photos have been deleted, return to camera
                if (pictureVideoList.isEmpty()) {
                    Navigation.findNavController( fragmentActivity, R.id.nav_host_fragment)
                        .navigateUp()
                }
            }

            .setNegativeButton(android.R.string.no, null)
            .create().showImmersive()
    }

    private fun initializePlayer(
        videoBuffering: TextView,
        videoview: VideoView,
        position: Int,
        article: File,
        holder: ViewHolder
    ) {
        // Show the "Buffering..." message while the video loads.
        videoBuffering.setVisibility(VideoView.VISIBLE)

        // Buffer and decode the video sample.
        videoview.setVideoURI(Uri.parse(article.toString()))

        // Listener for onPrepared() event (runs after the media is prepared).
        videoview.setOnPreparedListener { // Hide buffering message.
            videoBuffering.setVisibility(VideoView.INVISIBLE)

            // Restore saved position, if available.
            if (position > 0) {
                videoview.seekTo(position)
            } else {
                // Skipping to 1 shows the first frame of the video.
                videoview.seekTo(1)
            }

            // Start playing!
            videoview.start()
        }

        // Listener for onCompletion() event (runs after media has finished
        // playing).
        videoview.setOnCompletionListener {
            Toast.makeText(
                holder.itemView.context,
                "Video is finished",
                Toast.LENGTH_SHORT
            ).show()

            // Return the video position to the start.
            videoview.seekTo(0)
        }
    }

    override fun getItemCount(): Int {
        return pictureVideoList.size
    }

}