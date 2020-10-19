/*
 * Copyright 2018 Google LLC
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

package com.vjezba.androidjetpackgithub.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.databinding.FragmentRepositoriesBinding
import com.vjezba.androidjetpackgithub.databinding.FragmentRepositoriesDetailsBinding
import com.vjezba.androidjetpackgithub.di.Injectable
import com.vjezba.androidjetpackgithub.di.injectViewModel
import kotlinx.android.synthetic.main.activity_languages_main.*
import kotlinx.android.synthetic.main.fragment_repositories_details.*
import javax.inject.Inject


class RepositoriesDetailsFragment : Fragment() {

    private val args: RepositoriesDetailsFragmentArgs by navArgs()

    private var repositoryName: TextView? = null
    private var lastUpdateTime: TextView? = null
    private var ownerNameValue: TextView? = null
    private var repositoryDescription: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentRepositoriesDetailsBinding.inflate(inflater, container, false)
        context ?: return binding.root

        repositoryName = binding.repositoryName
        lastUpdateTime = binding.lastUpdateTimeValue
        ownerNameValue = binding.ownerNameValue
        repositoryDescription = binding.repositoryDescription

        setDetailsAboutLanguage()

        activity?.speedDial?.visibility = View.GONE
        activity?.toolbar?.title = "Repository details"

        return binding.root
    }

    private fun setDetailsAboutLanguage() {
        repositoryName?.text = "" + args.repositoryName
        lastUpdateTime?.text = "" + args.lastUpdateTime
        ownerNameValue?.text = "" + args.ownerName
        if( args.description != "null" )
            repositoryDescription?.text = "" + args.description
        else
            repositoryDescription?.text = "This repository does not have any description."
    }

}
