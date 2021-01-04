/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vjezba.androidjetpackgithub.ui.fragments.camerax

import android.content.ContentValues.TAG
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.vjezba.androidjetpackgithub.R
import kotlinx.android.synthetic.main.viewpager2_camera_videos_images.*
import java.io.File


val VIDEO_FILE = ".mp4"

/** Fragment used for each individual page showing a photo inside of [GalleryFragment] */
class PhotoFragment internal constructor() : Fragment() {

    // Current playback position (in milliseconds).
    private val mCurrentPosition = 0

    // Tag for the instance state bundle.
    private val PLAYBACK_TIME = "play_time"

    private var fileName = ""
    var controller: MediaController? = null // MediaController(this.requireContext())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? = inflater.inflate(R.layout.viewpager2_camera_videos_images, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments ?: return
        val resource = args.getString(FILE_NAME_KEY)?.let { File(it) } ?: R.drawable.ic_menu_gallery

        fileName = args.getString(FILE_NAME_KEY) ?: ""

        if( fileName.contains(VIDEO_FILE) ) {

            videoview.visibility = View.VISIBLE
            //Glide.with(view).load(resource).into(ivPicture)

            controller = MediaController(this.requireContext())
            // Set up the media controller widget and attach it to the video view.
            controller?.setMediaPlayer(videoview)
            videoview!!.setMediaController(controller)
        }
        else {
            releasePlayer()
            videoview.visibility = View.GONE
            buffering_textview.visibility = View.GONE
            Glide.with(view).load(resource).into(ivPicture)
        }
    }

    override fun onStart() {
        super.onStart()

        if( fileName.contains(VIDEO_FILE) ) {
            // Load the media each time onStart() is called.
            initializePlayer()
        }
    }

    private fun initializePlayer() {
        // Show the "Buffering..." message while the video loads.
        buffering_textview.setVisibility(VideoView.VISIBLE)

        // Buffer and decode the video sample.
        //val videoUri: Uri = getMedia(VIDEO_SAMPLE)
        videoview.setVideoURI(Uri.parse(fileName))

        // Listener for onPrepared() event (runs after the media is prepared).
        videoview.setOnPreparedListener(
            OnPreparedListener { // Hide buffering message.
                buffering_textview.setVisibility(VideoView.INVISIBLE)

                // Restore saved position, if available.
                if (mCurrentPosition > 0) {
                    videoview.seekTo(mCurrentPosition)
                } else {
                    // Skipping to 1 shows the first frame of the video.
                    videoview.seekTo(1)
                }

                // Start playing!
                videoview.start()
            })

        // Listener for onCompletion() event (runs after media has finished
        // playing).
        videoview.setOnCompletionListener(
            OnCompletionListener {
                Toast.makeText(
                    this@PhotoFragment.requireContext(),
                    "Video is finished",
                    Toast.LENGTH_SHORT
                ).show()

                // Return the video position to the start.
                videoview.seekTo(0)
            })
    }

    // Release all media-related resources. In a more complicated app this
    // might involve unregistering listeners or releasing audio focus.
    private fun releasePlayer() {
        Log.d(TAG, "Da li ce uci sim, proba zaustavi video U RELEASE PLAYER METODU")
        //videoview.stopPlayback()
        videoview.pause()
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//        // Save the current playback position (in milliseconds) to the
//        // instance state bundle.
//        if( fileName.contains(VIDEO_FILE) )
//            outState.putInt(PLAYBACK_TIME, videoview.getCurrentPosition())
//    }

    override fun onPause() {
        super.onPause()

        // In Android versions less than N (7.0, API 24), onPause() is the
        // end of the visual lifecycle of the app.  Pausing the video here
        // prevents the sound from continuing to play even after the app
        // disappears.
        //
        // This is not a problem for more recent versions of Android because
        // onStop() is now the end of the visual lifecycle, and that is where
        // most of the app teardown should take place.
        if ( fileName.contains(VIDEO_FILE) ) {
            Log.d(TAG, "Da li ce uci sim, proba zaustavi video  ON PAUSE")
            videoview.pause()
        }
    }

//    override fun onStop() {
//        super.onStop()
//
//        // Media playback takes a lot of resources, so everything should be
//        // stopped and released at this time.
//        if( fileName.contains(VIDEO_FILE) ) {
//            Log.d(TAG, "Da li ce uci sim, proba zaustavi video  ON STOP")
//            releasePlayer()
//        }
//    }

    companion object {
        private const val FILE_NAME_KEY = "file_name"

        fun create(image: File) = PhotoFragment().apply {
            arguments = Bundle().apply {
                putString(FILE_NAME_KEY, image.absolutePath)
            }
        }
    }
}