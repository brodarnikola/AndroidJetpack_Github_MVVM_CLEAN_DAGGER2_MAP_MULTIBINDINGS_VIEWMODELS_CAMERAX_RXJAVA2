/*
 * Copyright 2020 Google LLC
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

package com.vjezba.androidjetpackgithub.ui.adapters.camerax

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.databinding.ListCameraxTextOptionBinding

class CameraXTextOptionAdapter( val clickListener: (String) -> Unit ) :  RecyclerView.Adapter<CameraXTextOptionAdapter.RepositoriesViewHolder>() {

    var listCameraXOptions: MutableList<CameraXOptions> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoriesViewHolder {
        return RepositoriesViewHolder(
            ListCameraxTextOptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RepositoriesViewHolder, position: Int) {
        val cameraXOptions = listCameraXOptions.get(position)
        if (cameraXOptions != null) {
            holder.bind(cameraXOptions, clickListener)
        }
    }

    fun setCameraXOptions(mCameraXOptions: MutableList<CameraXOptions>) {
        listCameraXOptions = mCameraXOptions
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listCameraXOptions.size
    }

    inner class RepositoriesViewHolder(
        private val binding: ListCameraxTextOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CameraXOptions, clickListener: (String) -> Unit) {

            binding.apply {
                cameraxOptionText.text = item.cameraXOptions
                when {
                    item.isOptionSelected ->
                        clCameraXOption.setBackgroundColor( ContextCompat.getColor(this@RepositoriesViewHolder.itemView.context, R.color.sunflower_green_500) )
                    else ->
                        clCameraXOption.setBackgroundColor( ContextCompat.getColor(this@RepositoriesViewHolder.itemView.context, R.color.sunflower_black) )
                }

                clCameraXOption.setOnClickListener {

                    for( position in 0 until listCameraXOptions.size) {
                        val cameraXOption = listCameraXOptions[position]
                        if( cameraXOption.isOptionSelected )
                            cameraXOption.isOptionSelected = false
                        if( position == absoluteAdapterPosition )
                            cameraXOption.isOptionSelected = true
                    }
                    notifyDataSetChanged()

                    val cameraXAction = if( absoluteAdapterPosition == 0 ) "Picture" else "Video"
                    clickListener( cameraXAction )
                }
                executePendingBindings()
            }
        }
    }
}

