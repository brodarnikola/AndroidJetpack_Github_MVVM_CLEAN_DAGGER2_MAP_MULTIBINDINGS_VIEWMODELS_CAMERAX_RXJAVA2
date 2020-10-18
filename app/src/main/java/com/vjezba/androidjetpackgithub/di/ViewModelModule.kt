package com.vjezba.androidjetpackgithub.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vjezba.androidjetpackgithub.viewmodels.*

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    // login, registration viewmodels
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    abstract fun bindRegistrationViewModel(viewModel: RegistrationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EnterDetailsViewModel::class)
    abstract fun bindEnterDetailsViewModel(viewModel: EnterDetailsViewModel): ViewModel


    // programing languages viewmodels
    @Binds
    @IntoMap
    @ViewModelKey(LanguagesActivityViewModel::class)
    abstract fun bindLanguagesActivityViewModel(viewModel: LanguagesActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SavedLanguagesListViewModel::class)
    abstract fun bindSavedLanguagesViewModel(viewModel: SavedLanguagesListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LanguagesListViewModel::class)
    abstract fun bindLanguagesListViewModel(viewModel: LanguagesListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LanguageDetailsViewModel::class)
    abstract fun bindLanguageDetaislViewModel(viewModel: LanguageDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GalleryRepositoriesViewModel::class)
    abstract fun bindGalleryViewModel(repositoriesViewModel: GalleryRepositoriesViewModel): ViewModel


    // pagging with network and database viewmodels
    @Binds
    @IntoMap
    @ViewModelKey(PaggingWithNetworkAndDbViewModel::class)
    abstract fun bindPaggingWithNetworkAndDbViewModel(viewModel: PaggingWithNetworkAndDbViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaggingWithNetworkAndDbDataViewModel::class)
    abstract fun bindPaggingWithNetworkAndDbDataViewModel(viewModel: PaggingWithNetworkAndDbDataViewModel): ViewModel


    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
