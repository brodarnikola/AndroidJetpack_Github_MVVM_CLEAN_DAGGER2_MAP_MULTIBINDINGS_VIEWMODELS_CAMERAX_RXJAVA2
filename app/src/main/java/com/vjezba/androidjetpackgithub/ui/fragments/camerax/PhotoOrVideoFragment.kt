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
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.ui.adapters.camerax.CameraXViewPager2Adapter
import com.vjezba.androidjetpackgithub.ui.utilities.ZoomOutPageTransformer
import kotlinx.android.synthetic.main.activity_languages_main.*
import kotlinx.android.synthetic.main.viewpager2_camera_videos_images.*
import java.io.File

/** Fragment used for each individual page showing a photo inside of [GalleryFragment] */
class PhotoOrVideoFragment : Fragment() {

    private lateinit var mediaList: MutableList<File>

    private var fileName = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? = inflater.inflate(R.layout.viewpager2_camera_videos_images, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.speedDial?.visibility = View.GONE

        val args = arguments ?: return
        //val resource = args.getString(FILE_NAME_KEY)?.let { File(it) } ?: R.drawable.ic_menu_gallery

        // Get root directory of media from navigation arguments
        val rootDirectory = args.getString(ROOT_DIRECTORY).let { File(it) } //File(args.rootDirectory)

        // Walk through all files in the root directory
        // We reverse the order of the list to present the last photos first
//        mediaList = rootDirectory.listFiles() { file ->
//            //EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
//        }.sortedDescending().toMutableList() ?: mutableListOf()
        fileName = args.getString(FILE_NAME_KEY) ?: ""

        mediaList = rootDirectory.listFiles().sortedDescending().toMutableList() ?: mutableListOf()

        val currentPosition = mediaList.indexOfFirst { it.toString() == fileName }

        camerax_view_pager.setPageTransformer(ZoomOutPageTransformer())

        val viewPagerAdapter = CameraXViewPager2Adapter(mediaList, this.requireActivity())
        camerax_view_pager.adapter = viewPagerAdapter
        camerax_view_pager.setCurrentItem( currentPosition, false)
    }

    companion object {
        private const val FILE_NAME_KEY = "selected_photo_or_video"
        private const val ROOT_DIRECTORY = "root_directory"

        fun create( rootDirectory: String, selectedPhotoOrVideo: File) = PhotoOrVideoFragment().apply {
            arguments = Bundle().apply {
                putString(ROOT_DIRECTORY, rootDirectory)
                putString(FILE_NAME_KEY, selectedPhotoOrVideo.absolutePath)
            }
        }
    }
}