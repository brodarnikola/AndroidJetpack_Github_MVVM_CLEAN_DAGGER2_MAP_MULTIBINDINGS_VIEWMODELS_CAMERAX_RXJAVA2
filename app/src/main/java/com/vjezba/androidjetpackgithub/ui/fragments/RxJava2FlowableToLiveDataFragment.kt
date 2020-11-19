package com.vjezba.androidjetpackgithub.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.databinding.FragmentRxjava2FlowabletToLivedataBinding
import com.vjezba.androidjetpackgithub.di.Injectable
import com.vjezba.androidjetpackgithub.di.ViewModelFactory
import com.vjezba.androidjetpackgithub.di.injectViewModel
import com.vjezba.androidjetpackgithub.ui.adapters.RepositoriesFlowableToLiveDataAdapter
import com.vjezba.androidjetpackgithub.viewmodels.GalleryRepositoriesViewModel
import com.vjezba.androidjetpackgithub.viewmodels.RxJava2FlowableToLiveDataViewModel
import kotlinx.android.synthetic.main.activity_languages_main.*
import kotlinx.android.synthetic.main.fragment_rxjava2_flowablet_to_livedata.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class RxJava2FlowableToLiveDataFragment : Fragment(), Injectable {

    private val adapter = RepositoriesFlowableToLiveDataAdapter()
    private var searchJob: Job? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var repositoriesViewModel: RxJava2FlowableToLiveDataViewModel

    var currentSearchText: String = ""
    var lastCurrentSearchText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        repositoriesViewModel = injectViewModel(viewModelFactory)

        val binding = FragmentRxjava2FlowabletToLivedataBinding.inflate(inflater, container, false)
        context ?: return binding.root

        activity?.speedDial?.visibility = View.GONE
        activity?.toolbar?.title = getString(R.string.gallery_title)

        //initializeViews(binding)
        search(binding)
        setEdittextListener(binding)

        setupAdapter(binding)

        return binding.root
    }

    private fun setupAdapter(binding: FragmentRxjava2FlowabletToLivedataBinding) {
        binding.listRepos.adapter = adapter
        adapter.addLoadStateListener { loadState ->
            binding.listRepos.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBarRepositories.isVisible = loadState.source.refresh is LoadState.Loading

            binding.btnFindRepos.isEnabled = loadState.source.refresh is LoadState.NotLoading

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    this@RxJava2FlowableToLiveDataFragment.requireContext(),
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setEdittextListener(binding: FragmentRxjava2FlowabletToLivedataBinding) {
        binding.etInsertText.addTextChangedListener(object : TextWatcher {
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

    private fun search(binding: FragmentRxjava2FlowabletToLivedataBinding) {
        binding.btnFindRepos.setOnClickListener {
            if( currentSearchText != "" ) {
                hideKeyboard(this.requireActivity())
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
                    clMainLayout,
                    "You did not insert any text to search repositories.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }


}
