package com.vjezba.androidjetpackgithub.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.ui.adapters.ALL_GITHUBS
import com.vjezba.androidjetpackgithub.ui.adapters.SavedLanguagesAdapter
import com.vjezba.androidjetpackgithub.databinding.FragmentSavedLanguagesBinding
import com.vjezba.androidjetpackgithub.di.Injectable
import com.vjezba.androidjetpackgithub.di.injectViewModel
import com.vjezba.androidjetpackgithub.viewmodels.SavedLanguagesListViewModel
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [SavedLanguagesFragment.newInstance] factory method to
 * create an instance of this fragments.
 */
class SavedLanguagesFragment : Fragment(), Injectable {

    private lateinit var binding: FragmentSavedLanguagesBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SavedLanguagesListViewModel
    //private val viewModel : SavedLanguagesListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = injectViewModel(viewModelFactory)

        binding = FragmentSavedLanguagesBinding.inflate(inflater, container, false)
        val adapter =
            SavedLanguagesAdapter( { position: Int -> setDeleteLanguageClickListener(position) })
        binding.savedLanguageList.adapter = adapter

        binding.addLanguage.setOnClickListener {
            navigateToPlantListPage()
        }

        subscribeUi(adapter, binding)
        return binding.root
    }

    private fun setDeleteLanguageClickListener(position: Int) {
        viewModel.deleteSelectedProgrammingLanguage(position)
    }

    private fun subscribeUi(adapter: SavedLanguagesAdapter, binding: FragmentSavedLanguagesBinding) {
        viewModel.savedAndAllLanguages.observe(viewLifecycleOwner, Observer { result ->
            binding.hasLanguages = !result.isNullOrEmpty()
            adapter.submitList(result)
        })
    }

    private fun navigateToPlantListPage() {
        requireActivity().findViewById<ViewPager2>(R.id.view_pager).currentItem =
            ALL_GITHUBS
    }
}