package com.vjezba.androidjetpackgithub.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.databinding.FragmentRepositoriesBinding
import com.vjezba.androidjetpackgithub.di.Injectable
import com.vjezba.androidjetpackgithub.di.ViewModelFactory
import com.vjezba.androidjetpackgithub.di.injectViewModel
import com.vjezba.androidjetpackgithub.ui.adapters.RepositoriesFragmentAdapter
import com.vjezba.androidjetpackgithub.viewmodels.GalleryRepositoriesViewModel
import kotlinx.android.synthetic.main.activity_languages_main.*
import kotlinx.coroutines.Job
import javax.inject.Inject


class RepositoriesFragment : Fragment(), Injectable {

    private var progressBarRepos: ProgressBar? = null
    private var languageListRepository: RecyclerView? = null
    private var btnFind: Button? = null
    private var etInserText: EditText? = null

    private val adapter = RepositoriesFragmentAdapter()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var repositoriesViewModel: GalleryRepositoriesViewModel

    var currentSearchText: String = ""
    var lastCurrentSearchText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        repositoriesViewModel = injectViewModel(viewModelFactory)

        val binding = FragmentRepositoriesBinding.inflate(inflater, container, false)
        context ?: return binding.root

        activity?.speedDial?.visibility = View.GONE
        activity?.toolbar?.title = getString(R.string.gallery_title)

        initializeViews(binding)
        search()
        setEdittextListener()

        setupAdapter(binding)

        return binding.root
    }

    private fun setupAdapter(binding: FragmentRepositoriesBinding) {
        binding.languageListRepos.adapter = adapter
    }

    private fun initializeViews(binding: FragmentRepositoriesBinding) {
        progressBarRepos = binding.progressBarRepositories
        languageListRepository = binding.languageListRepos
        btnFind = binding.btnFind
        etInserText = binding.etInsertText
    }

    private fun setEdittextListener() {
        etInserText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                currentSearchText = s.toString()
            }
        })
    }

    private fun search() {
        btnFind?.setOnClickListener {
            if( currentSearchText != "" ) {
                // Make sure we cancel the previous job before creating a new one
                if (currentSearchText == lastCurrentSearchText) {
                    progressBarRepos?.visibility = View.VISIBLE
                    repositoriesViewModel.searchGithubRepositoryByLastUpdateTimeWithLiveData(currentSearchText).observe(viewLifecycleOwner, Observer { repos ->
                        progressBarRepos?.visibility = View.GONE
                        adapter.setRepos(repos.items.toMutableList())
                    })
                } else {
                    lastCurrentSearchText = currentSearchText
                    adapter.notifyItemRangeRemoved(0, adapter.itemCount)
                    progressBarRepos?.visibility = View.VISIBLE
                    repositoriesViewModel.searchGithubRepositoryByLastUpdateTimeWithLiveData(currentSearchText).observe(viewLifecycleOwner, Observer { repos ->
                        progressBarRepos?.visibility = View.GONE
                        adapter.setRepos(repos.items.toMutableList())
                    })
                }
            }
            else {
                Snackbar.make(
                    this@RepositoriesFragment.requireView(),
                    "You did not insert any text to search repositories.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

}
