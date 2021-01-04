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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import java.io.File
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vjezba.androidjetpackgithub.BuildConfig
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.ui.adapters.camerax.CameraXGalleryAdapter
import com.vjezba.androidjetpackgithub.ui.fragments.HomeViewPagerFragmentDirections
import com.vjezba.androidjetpackgithub.ui.fragments.LanguageDetailsFragmentDirections
import com.vjezba.androidjetpackgithub.ui.utilities.padWithDisplayCutout
import com.vjezba.androidjetpackgithub.ui.utilities.showImmersive
import kotlinx.android.synthetic.main.activity_languages_main.*
import kotlinx.android.synthetic.main.fragment_gallery_camerax.*
import java.util.Locale

val EXTENSION_WHITELIST = arrayOf("JPG")

/** Fragment used to present the user with a gallery of photos taken */
class GalleryCameraXFragment : Fragment() {

    /** AndroidX navigation arguments */
    private val args: GalleryCameraXFragmentArgs by navArgs()

    private lateinit var mediaList: MutableList<File>
    var rootDirectory: File? = null

    private lateinit var galleryAdapter: CameraXGalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mark this as a retain fragment, so the lifecycle does not get restarted on config change
        retainInstance = true
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_gallery_camerax, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get root directory of media from navigation arguments
        rootDirectory = File(args.rootDirectory)

        // Walk through all files in the root directory
        // We reverse the order of the list to present the last photos first
//        mediaList = rootDirectory.listFiles() { file ->
//            //EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
//        }.sortedDescending().toMutableList() ?: mutableListOf()

        mediaList = rootDirectory?.listFiles()?.sortedDescending()?.toMutableList() ?: mutableListOf()

        activity?.speedDial?.visibility = View.GONE

        galleryAdapter = CameraXGalleryAdapter( mediaList,
            { pictureOrVideo: File -> setVideoPictureClickListener( pictureOrVideo ) }  )

        val galeryLayoutManager = GridLayoutManager(this.requireContext(), 4, RecyclerView.VERTICAL, false)

        rvVideoPictures.apply {
            layoutManager = galeryLayoutManager
            adapter = galleryAdapter
        }

        rvVideoPictures.adapter = galleryAdapter

        // Make sure that the cutout "safe area" avoids the screen notch if any
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Use extension method to pad "inside" view containing UI using display cutout's bounds
            view.findViewById<ConstraintLayout>(R.id.cutout_safe_area).padWithDisplayCutout()
        }

    }

    private fun setVideoPictureClickListener(pictureOrVideo: File) {

        val direction =
            GalleryCameraXFragmentDirections.galeryCameraxFragmentToVideoPhotoCameraxFragment (
                rootDirectory = rootDirectory.toString(), selectedPhotoOrVideo = pictureOrVideo.toString()
            )
        findNavController().navigate(direction)
    }

}
