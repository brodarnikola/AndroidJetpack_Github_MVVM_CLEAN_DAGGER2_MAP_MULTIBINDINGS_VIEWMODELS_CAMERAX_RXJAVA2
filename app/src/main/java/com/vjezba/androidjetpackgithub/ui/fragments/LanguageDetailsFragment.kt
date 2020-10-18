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

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.databinding.FragmentLanguageDetailsBinding
import com.vjezba.androidjetpackgithub.di.Injectable
import com.vjezba.androidjetpackgithub.di.injectViewModel
import com.vjezba.androidjetpackgithub.viewmodels.LanguageDetailsViewModel
import com.vjezba.domain.model.Languages
import kotlinx.android.synthetic.main.activity_languages_main.*
import kotlinx.android.synthetic.main.fragment_language_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A fragments representing a single Plant detail screen.
 */
class LanguageDetailsFragment : Fragment(), Injectable {

    private val args: LanguageDetailsFragmentArgs by navArgs()

    /*private val languageDetailsViewModel: LanguageDetailsViewModel by viewModels {
        InjectorUtils.provideLanguageDetailsViewModelFactory(requireActivity(), args.languagesId)
    }*/

//    private val languageDetailsViewModel : LanguageDetailsViewModel by viewModel {
//        parametersOf( args.languagesId)
//    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var languageDetailsViewModel: LanguageDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        languageDetailsViewModel = injectViewModel(viewModelFactory)

        val binding = DataBindingUtil.inflate<FragmentLanguageDetailsBinding>(
            inflater,
            R.layout.fragment_language_details,
            container,
            false
        ).apply {

            val languageId = args.languagesId

            viewModel = languageDetailsViewModel
            lifecycleOwner = viewLifecycleOwner
            callback =
                Callback { repoDetails ->
                    repoDetails?.let {
                        hideAppBarFab(fab)
                        languageDetailsViewModel.saveProgrammingLanguage(languageId)
                        Snackbar.make(
                            root,
                            R.string.saved_language_successfully,
                            Snackbar.LENGTH_LONG
                        )
                            .show()
                    }
                }

//                val result = languageDetailsViewModel.isLanguageSaved(languageId)
//                    if(result.value!!) {
//                        fab.hide()
//                    }
//                    else
//                        fab.show()


            languageDetailsViewModel.isLanguageSaved(languageId)
                .observe(viewLifecycleOwner, Observer { isLanguageSaved ->
                    if (isLanguageSaved != null && isLanguageSaved) {
                        fab.hide()
                    } else {
                        fab.show()
                        fab.setOnClickListener {
                            ///callback. .add(viewModel.languageDetails)}
                            //callback.add(languageDetailsViewModel.getLanguageDetails(languageId))
                            hideAppBarFab(fab)
                            languageDetailsViewModel.saveProgrammingLanguage(languageId)
                            Snackbar.make(
                                root,
                                R.string.saved_language_successfully,
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                })

            setDetailsAboutLanguage()

            activity?.speedDial?.visibility = View.GONE
            galleryNav.setOnClickListener { navigateToGallery() }

            var isToolbarShown = false

            // scroll change listener begins at Y = 0 when image is fully collapsed
            /*languageDetailScrollview.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->

                    // User scrolled past image to height of toolbar and the title text is
                    // underneath the toolbar, so the toolbar should be shown.
                    val shouldShowToolbar = scrollY > toolbar.height

                    // The new state of the toolbar differs from the previous state; update
                    // appbar and toolbar attributes.
                    if (isToolbarShown != shouldShowToolbar) {
                        isToolbarShown = shouldShowToolbar

                        // Use shadow animator to add elevation if toolbar is shown
                        appbar.isActivated = shouldShowToolbar

                        // Show the plant name if toolbar is shown
                        toolbarLayout.isTitleEnabled = shouldShowToolbar
                    }
                }
            )*/

            /*toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }*/

            activity?.toolbar?.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_share -> {
                        createShareIntent()
                        true
                    }
                    else -> false
                }
            }
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setDetailsAboutLanguage() {

        languageDetailsViewModel.languageDetails(args.languagesId)
            .observe(viewLifecycleOwner, Observer { languages ->
                Glide.with(requireContext())
                    .load(languages.imageUrl)
                    .placeholder(R.color.sunflower_gray_50)
                    //.error(R.drawable.ic_detail_share)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(detail_image)

                language_detail_name.text = "" + languages.name
                language_created_by.text = "" + languages.createdBy
                language_created_at.text = "" + languages.createdAt
                language_description.text = "" + languages.description
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_language_details, menu)
    }

    private fun navigateToGallery() {
        languageDetailsViewModel.languageDetails(args.languagesId)
            .observe(viewLifecycleOwner, Observer { languages ->
                val direction =
                    LanguageDetailsFragmentDirections.actionLanguageDetailFragmentToGalleryFragment(
                        languages.name ?: ""
                    )
                findNavController().navigate(direction)
            })
    }

    // Helper function for calling a share functionality.
    // Should be used when user presses a share button/menu item.
    @Suppress("DEPRECATION")
    private fun createShareIntent() {
        val shareText =
            languageDetailsViewModel.languageDetails(args.languagesId).value.let { languages ->
                if (languages == null) {
                    ""
                } else {
                    getString(R.string.share_text_language_details, languages.name)
                }
            }
        val shareIntent = ShareCompat.IntentBuilder.from(requireActivity())
            .setText(shareText)
            .setType("text/plain")
            .createChooserIntent()
            .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        startActivity(shareIntent)
    }

    // FloatingActionButtons anchored to AppBarLayouts have their visibility controlled by the scroll position.
    // We want to turn this behavior off to hide the FAB when it is clicked.
    //
    // This is adapted from Chris Banes' Stack Overflow answer: https://stackoverflow.com/a/41442923
    private fun hideAppBarFab(fab: FloatingActionButton) {
        //val params = fab.layoutParams as CoordinatorLayout.LayoutParams
        //val behavior = params.behavior as FloatingActionButton.Behavior
        //behavior.isAutoHideEnabled = false
        fab.hide()
    }

    fun
    interface Callback {
        fun add(language: Languages?)
    }
}
