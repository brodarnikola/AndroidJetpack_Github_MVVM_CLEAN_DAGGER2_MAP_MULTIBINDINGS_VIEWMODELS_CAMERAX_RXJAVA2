package com.vjezba.androidjetpackgithub.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
        binding.let { setAutomaticEdittextListener(it) }

        binding.let { setupAdapter(it) }

        repositoriesViewModel.observeReposInfo().observe(viewLifecycleOwner, Observer { repos ->
            list_repos?.visibility = View.VISIBLE
            progressBarRepositories?.visibility = View.GONE
            adapter.setRepos(repos.items.toMutableList())
        })

        repositoriesViewModel.observeReposInfoAutomatic()?.observe(viewLifecycleOwner, Observer { repos ->
            list_repos?.visibility = View.VISIBLE
            progressBarRepositories?.visibility = View.GONE
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

    private var disposable: Disposable? = null

    private fun observeSearchView() {

        val subject = PublishSubject.create<String>()
        disposable = subject
            .debounce(2000L, TimeUnit.MILLISECONDS)
            .filter({ text -> !text.isEmpty() && text.length >= 2 })
            .map({ text -> text.toLowerCase().trim() })
            .distinctUntilChanged()
            .switchMap({ s -> io.reactivex.Observable.just(s) })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ query ->

                repositoriesViewModel.searchGithubRepositoryByLastUpdateTimeWithFlowableAndLiveData(query)
            })

        etInsertTextAutomatic.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                subject.onComplete()
                etInsertTextAutomatic.clearFocus()
                return true
            }

            override fun onQueryTextChange(text: String): Boolean {
                subject.onNext(text)
                return true
            }
        })
    }

    private fun setAutomaticEdittextListener(binding: FragmentRxjava2FlowabletToLivedataBinding) {

        // DOBRO ISTO JAKO LIJEPO RADI
        observeSearchView()

        // TAJ PRIMJER MI RADI ODLICNO
        /*val subject = PublishSubject.create<String>()

        etInsertTextAutomatic.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                subject.onComplete()
                etInsertTextAutomatic.clearFocus()
                return true
            }

            override fun onQueryTextChange(text: String): Boolean {
                subject.onNext(text)
                return true
            }
        })

        subject
            .debounce(1000, TimeUnit.MILLISECONDS)
            .filter(Predicate<String> { text ->
                if (text.isEmpty()) {
                    //textViewResult.setText("")
                    false
                } else {
                    true
                }
            })
            .distinctUntilChanged()
            .switchMap(object : io.reactivex.functions.Function<String, ObservableSource<RepositoryResponseApi>> {
                override fun apply(query: String): Observable<RepositoryResponseApi> {
                    return setupRetrofitFlatMap().searchGithubRepositoryWithFlowable(query, 0, 10)
                        .doOnError({ throwable -> Log.d(TAG, "onError received EEEE: ${throwable}") })
                        .onErrorReturn { throwable -> Log.d(TAG, "onError received DDDD: ${throwable}")
                            RepositoryResponseApi(0, false, listOf()) }
                        .toObservable()
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<RepositoryResponseApi> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(response: RepositoryResponseApi) {
                    Log.d(TAG, "Da li ce uci sim EEEE: ${response}")
                    list_repos?.visibility = View.VISIBLE
                    progressBarRepositories?.visibility = View.GONE
                    val proba: RepositoryResponse = dbMapperImpl.mapApiResponseGithubToDomainGithuWithbRxJavaAndFlowable(response)
                    adapter.setRepos(proba.items.toMutableList())
                    //reposInfoAutomatic.value = response
                }

                override fun onError(e: Throwable) {
                    Log.e(ContentValues.TAG, "onError received: ${e}")
                }
            })*/

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
