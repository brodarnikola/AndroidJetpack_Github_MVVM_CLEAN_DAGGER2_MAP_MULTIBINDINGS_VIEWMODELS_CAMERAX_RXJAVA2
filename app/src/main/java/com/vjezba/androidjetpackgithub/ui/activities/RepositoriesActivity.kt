package com.vjezba.androidjetpackgithub.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.di.Injectable
import com.vjezba.androidjetpackgithub.di.injectViewModel
import com.vjezba.androidjetpackgithub.ui.adapters.RepositoriesAdapter
import com.vjezba.androidjetpackgithub.viewmodels.GalleryRepositoriesViewModel
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import kotlinx.android.synthetic.main.activity_repositories.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


class RepositoriesActivity : AppCompatActivity(), HasActivityInjector, Injectable {

    @Inject
    lateinit var dispatchingAndroidActivityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = dispatchingAndroidActivityInjector

    private val adapter = RepositoriesAdapter()
    private var searchJob: Job? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var repositoriesViewModel: GalleryRepositoriesViewModel

    var currentSearchText: String = ""
    var lastCurrentSearchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repositories)

        repositoriesViewModel = injectViewModel(viewModelFactory)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        search()
        setEdittextListener()

        setupAdapter()
    }

    private fun setupAdapter() {
        list_repos.adapter = adapter
        adapter.addLoadStateListener { loadState ->
            list_repos.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            progressBarRepositories.isVisible = loadState.source.refresh is LoadState.Loading

            btnFind.isEnabled = loadState.source.refresh is LoadState.NotLoading

            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    this,
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
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

    private fun setEdittextListener() {
        etInsertText.addTextChangedListener(object : TextWatcher {
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
        btnFind.setOnClickListener {
            if( currentSearchText != "" ) {
                hideKeyboard(this)
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
                    content,
                    "You did not insert any text to search repositories.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                startActivity(Intent(applicationContext, LanguagesActivity::class.java))
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, LanguagesActivity::class.java))
        finish()
    }

}
