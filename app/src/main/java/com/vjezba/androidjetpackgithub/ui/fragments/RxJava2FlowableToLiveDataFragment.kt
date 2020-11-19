package com.vjezba.androidjetpackgithub.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.databinding.FragmentRxjava2FlowabletToLivedataBinding
import com.vjezba.androidjetpackgithub.di.Injectable
import com.vjezba.androidjetpackgithub.di.ViewModelFactory
import com.vjezba.androidjetpackgithub.di.injectViewModel
import com.vjezba.androidjetpackgithub.ui.adapters.RepositoriesFlowableToLiveDataAdapter
import com.vjezba.androidjetpackgithub.viewmodels.RxJava2FlowableToLiveDataViewModel
import com.vjezba.domain.model.RepositoryResponse
import kotlinx.android.synthetic.main.activity_languages_main.*
import kotlinx.android.synthetic.main.fragment_rxjava2_flowablet_to_livedata.*
import javax.inject.Inject


class RxJava2FlowableToLiveDataFragment : Fragment(), Injectable {

    private val adapter = RepositoriesFlowableToLiveDataAdapter()

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

    override fun onStart() {
        super.onStart()

        repositoriesViewModel.observeReposInfo().observe(viewLifecycleOwner, Observer { repos ->
            list_repos?.visibility = View.VISIBLE
            progressBarRepositories?.visibility = View.GONE
            adapter.setRepos(repos.items.toMutableList())
        })
    }

    private fun setupAdapter(binding: FragmentRxjava2FlowabletToLivedataBinding) {
        binding.listRepos.adapter = adapter
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
                if (currentSearchText == lastCurrentSearchText) {

                    progressBarRepositories?.visibility = View.VISIBLE
                    list_repos?.visibility = View.GONE

                    repositoriesViewModel.searchGithubRepositoryByLastUpdateTimeWithFlowableAndLiveData(currentSearchText)
                } else {
                    lastCurrentSearchText = currentSearchText
                    adapter.notifyItemRangeRemoved(0, adapter.itemCount)

                    progressBarRepositories?.visibility = View.VISIBLE
                    list_repos?.visibility = View.GONE

                    repositoriesViewModel.searchGithubRepositoryByLastUpdateTimeWithFlowableAndLiveData(currentSearchText)
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
