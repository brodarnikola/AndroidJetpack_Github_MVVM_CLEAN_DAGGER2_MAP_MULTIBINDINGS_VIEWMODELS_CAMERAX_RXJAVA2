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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.databinding.FragmentRepositoriesBinding
import com.vjezba.androidjetpackgithub.di.Injectable
import com.vjezba.androidjetpackgithub.di.ViewModelFactory
import com.vjezba.androidjetpackgithub.di.injectViewModel
import com.vjezba.androidjetpackgithub.ui.adapters.GalleryAdapter
import com.vjezba.androidjetpackgithub.ui.adapters.RepositoriesAdapter
import com.vjezba.androidjetpackgithub.viewmodels.GalleryRepositoriesViewModel
import kotlinx.android.synthetic.main.activity_languages_main.*
import kotlinx.android.synthetic.main.fragment_repositories.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class RepositoriesFragment : Fragment(), Injectable {

    private var progressBarRepos: ProgressBar? = null
    private var languageListRepository: RecyclerView? = null
    private var btnFind: Button? = null
    private var etInserText: EditText? = null

    private val adapter = RepositoriesAdapter()
    private var searchJob: Job? = null

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
        adapter.addLoadStateListener { loadState ->
            binding.languageListRepos.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBarRepositories.isVisible = loadState.source.refresh is LoadState.Loading

            binding.btnFind.isEnabled = loadState.source.refresh is LoadState.NotLoading

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
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
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        repositoriesViewModel.searchGithubRepositoryByLastUpdateTime(
                            currentSearchText
                        )
                            .collectLatest {
                                adapter.submitData(it)
                            }
                    }
                } else {
                    lastCurrentSearchText = currentSearchText
                    adapter.notifyItemRangeRemoved(0, adapter.itemCount)
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        delay(1000)
                        repositoriesViewModel.searchGithubRepositoryByLastUpdateTime(
                            currentSearchText
                        )
                            .collectLatest {
                                adapter.submitData(it)
                            }
                    }
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
