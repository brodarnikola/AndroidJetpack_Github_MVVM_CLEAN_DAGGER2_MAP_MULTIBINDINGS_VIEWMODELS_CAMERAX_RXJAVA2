package com.vjezba.androidjetpackgithub.ui.fragments

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.databinding.FragmentRxjava2FlowabletToLivedataBinding
import com.vjezba.androidjetpackgithub.di.Injectable
import com.vjezba.androidjetpackgithub.di.ViewModelFactory
import com.vjezba.androidjetpackgithub.di.injectViewModel
import com.vjezba.androidjetpackgithub.ui.adapters.RepositoriesFlowableToLiveDataAdapter
import com.vjezba.androidjetpackgithub.viewmodels.RxJava2FlowableToLiveDataViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_languages_main.*
import kotlinx.android.synthetic.main.fragment_rxjava2_flowablet_to_livedata.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class RxJava2FlowableToLiveDataFragment : Fragment(), Injectable {

    private val adapter = RepositoriesFlowableToLiveDataAdapter()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var repositoriesViewModel: RxJava2FlowableToLiveDataViewModel

    var currentSearchText: String = ""
    var lastCurrentSearchText: String = ""

    lateinit var binding:FragmentRxjava2FlowabletToLivedataBinding

    private var disposable: Disposable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        repositoriesViewModel = injectViewModel(viewModelFactory)

        binding = FragmentRxjava2FlowabletToLivedataBinding.inflate(inflater, container, false)
        context ?: return binding.root

        activity?.speedDial?.visibility = View.GONE
        activity?.toolbar?.title = getString(R.string.gallery_title)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        setupRadioGroupClickListener()

        binding.let { search(it) }
        binding.let { setEdittextListener(it) }
        binding.let { setAutomaticSearchViewListener() }

        binding.let { setupAdapter(it) }

        repositoriesViewModel.observeReposInfo().observe(viewLifecycleOwner, Observer { repos -> 
            hideOrShowRecyclerViewAndProgressBar(
                showRecyclerView = true,
                showProgressBar = false
            )
            adapter.setRepos(repos.items.toMutableList())
        })

        repositoriesViewModel.observeReposInfoAutomatic()?.observe(viewLifecycleOwner, Observer { repos ->
            hideOrShowRecyclerViewAndProgressBar(
                showRecyclerView = true,
                showProgressBar = false
            )
            adapter.notifyItemRangeRemoved(0, adapter.itemCount)
            adapter.setRepos(repos.items.toMutableList())
        })
    }

    private fun setupRadioGroupClickListener() {
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: View = radioGroup.findViewById(checkedId)
            val index = radioGroup.indexOfChild(radioButton)
            when (index) {
                0 -> {
                    enableSearchView(etInsertTextAutomatic, true)
                    etInsertTextAutomatic.alpha = 1.0f

                    etInsertText.isEnabled = false
                    etInsertText.alpha = 0.4f
                    btnFindRepos.isEnabled = false
                    btnFindRepos.alpha = 0.4f
                }
                1 -> {
                    enableSearchView(etInsertTextAutomatic, false)
                    etInsertTextAutomatic.alpha = 0.4f

                    etInsertText.isEnabled = true
                    etInsertText.alpha = 1.0f
                    btnFindRepos.isEnabled = true
                    btnFindRepos.alpha = 1.0f
                }
            }
        }
    }

    private fun enableSearchView(
        view: View,
        enabled: Boolean
    ) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            val viewGroup = view
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)
                enableSearchView(child, enabled)
            }
        }
    }

    private fun setupAdapter(binding: FragmentRxjava2FlowabletToLivedataBinding) {
        binding.listRepos.adapter = adapter
    }

    private fun setAutomaticSearchViewListener() {

        val subject = PublishSubject.create<String>()
        disposable = subject
            .debounce(2000L, TimeUnit.MILLISECONDS)
            .filter({ text -> !text.isEmpty() && text.length >= 2 })
            .map({ text -> text.toLowerCase().trim() })
            .distinctUntilChanged()
            .switchMap({ s -> io.reactivex.Observable.just(s) })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ query ->
                hideKeyboard(this.requireActivity())
                hideOrShowRecyclerViewAndProgressBar(
                    showRecyclerView = false,
                    showProgressBar = true
                )
                repositoriesViewModel.searchGithubRepositoryByLastUpdateTimeWithFlowableAndLiveData(query)
            })

        etInsertTextAutomatic.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                Log.d(ContentValues.TAG, "Hide keyboard user pressed done button")
                hideKeyboard(this@RxJava2FlowableToLiveDataFragment.requireActivity())
                //subject.onComplete()
                //etInsertTextAutomatic.clearFocus()
                return true
            }

            override fun onQueryTextChange(text: String): Boolean {
                subject.onNext(text)
                return true
            }
        })
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

                    hideOrShowRecyclerViewAndProgressBar(
                        showRecyclerView = false,
                        showProgressBar = true
                    )

                    repositoriesViewModel.searchGithubRepositoryByLastUpdateTimeWithFlowableAndLiveData(currentSearchText)
                } else {
                    lastCurrentSearchText = currentSearchText
                    adapter.notifyItemRangeRemoved(0, adapter.itemCount)

                    hideOrShowRecyclerViewAndProgressBar(
                        showRecyclerView = false,
                        showProgressBar = true
                    )

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

    private fun hideOrShowRecyclerViewAndProgressBar( showRecyclerView: Boolean, showProgressBar: Boolean) {
        list_repos?.visibility = if( showRecyclerView ) View.VISIBLE else View.GONE
        progressBarRepositories?.visibility = if( showProgressBar ) View.VISIBLE else View.GONE
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

    override fun onPause() {
        super.onPause()

        disposable?.apply {
            if( !isDisposed) {
                this.dispose()
                disposable = null
            }
        }
    }

}
