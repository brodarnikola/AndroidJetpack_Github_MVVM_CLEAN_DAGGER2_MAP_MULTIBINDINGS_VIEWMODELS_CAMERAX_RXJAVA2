package com.vjezba.androidjetpackgithub.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.databinding.FragmentRepositoriesBinding
import com.vjezba.androidjetpackgithub.di.Injectable
import com.vjezba.androidjetpackgithub.di.ViewModelFactory
import com.vjezba.androidjetpackgithub.di.injectViewModel
import com.vjezba.androidjetpackgithub.ui.adapters.RepositoriesRxJava2FromPublisherAdapter
import com.vjezba.androidjetpackgithub.viewmodels.RepositoriesRxJava2ViewModel
import kotlinx.android.synthetic.main.activity_languages_main.*
import javax.inject.Inject


class RepositoriesRxJava2Fragment : Fragment(), Injectable {

    private var progressBarRepos: ProgressBar? = null
    private var languageListRepository: RecyclerView? = null
    private var btnFind: Button? = null
    private var etInserText: EditText? = null

    private val adapter = RepositoriesRxJava2FromPublisherAdapter()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var repositoriesViewModel: RepositoriesRxJava2ViewModel

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
        activity?.toolbar?.title = getString(R.string.menu_rxjava2_example)

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
                hideKeyboard(this.requireActivity())
                // Make sure we cancel the previous job before creating a new one
                if (currentSearchText == lastCurrentSearchText) {
                    progressBarRepos?.visibility = View.VISIBLE
                    languageListRepository?.visibility = View.GONE
                    repositoriesViewModel.searchGithubRepositoryByLastUpdateTimeWithLiveData(currentSearchText).observe(viewLifecycleOwner, Observer { repos ->
                        languageListRepository?.visibility = View.VISIBLE
                        progressBarRepos?.visibility = View.GONE
                        adapter.setRepos(repos.items.toMutableList())
                    })
                } else {
                    lastCurrentSearchText = currentSearchText
                    adapter.notifyItemRangeRemoved(0, adapter.itemCount)
                    progressBarRepos?.visibility = View.VISIBLE
                    languageListRepository?.visibility = View.GONE
                    repositoriesViewModel.searchGithubRepositoryByLastUpdateTimeWithLiveData(currentSearchText).observe(viewLifecycleOwner, Observer { repos ->
                        languageListRepository?.visibility = View.VISIBLE
                        progressBarRepos?.visibility = View.GONE
                        adapter.setRepos(repos.items.toMutableList())
                    })
                }
            }
            else {
                Snackbar.make(
                    this@RepositoriesRxJava2Fragment.requireView(),
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
