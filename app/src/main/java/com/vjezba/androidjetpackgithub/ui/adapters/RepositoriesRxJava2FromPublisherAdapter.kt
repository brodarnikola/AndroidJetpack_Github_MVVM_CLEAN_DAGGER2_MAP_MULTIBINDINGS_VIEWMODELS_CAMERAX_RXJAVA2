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

package com.vjezba.androidjetpackgithub.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.vjezba.androidjetpackgithub.databinding.ListRepositoryDataBinding
import com.vjezba.androidjetpackgithub.ui.fragments.RepositoriesRxJava2FragmentDirections
import com.vjezba.domain.model.RepositoryDetailsResponse

/**
 * Adapter for the [RecyclerView] in [GalleryFragment].
 */

class RepositoriesRxJava2FromPublisherAdapter : RecyclerView.Adapter<RepositoriesRxJava2FromPublisherAdapter.RepositoriesViewHolder>() {

    private var repos: MutableList<RepositoryDetailsResponse> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoriesViewHolder {
        return RepositoriesViewHolder(
            ListRepositoryDataBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RepositoriesViewHolder, position: Int) {
        val repos = repos.get(position)
        if (repos != null) {
            holder.bind(repos)
        }
    }

    override fun getItemCount(): Int {
        return repos.size
    }

    fun setRepos(mRepos: MutableList<RepositoryDetailsResponse>) {
        repos = mRepos
        notifyDataSetChanged()
    }

    class RepositoriesViewHolder(
        private val binding: ListRepositoryDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener { view ->
                binding.repo?.let { repo ->
                    navigateToRepositoryDetails(repo, view)
                }
            }
        }

        fun navigateToRepositoryDetails(repo: RepositoryDetailsResponse, view: View?) {

            val direction = RepositoriesRxJava2FragmentDirections.repositoryFragmentToDetailsRepositoryFragment(repo.name.toString(), repo.lastUpdateTime.toString(), repo.ownerApi.login, repo.description.toString())
            view?.findNavController()?.navigate(direction)
        }

        fun bind(item: RepositoryDetailsResponse) {
            binding.apply {
                repo = item
                binding.repositorieName.text = item.name
                binding.description.text = item.description
                executePendingBindings()
            }
        }
    }
}
