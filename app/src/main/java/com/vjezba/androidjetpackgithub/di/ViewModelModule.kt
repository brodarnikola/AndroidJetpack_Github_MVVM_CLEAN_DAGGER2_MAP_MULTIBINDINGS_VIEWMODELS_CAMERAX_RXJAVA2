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
    @ViewModelKey(LegoThemeViewModel::class)
    abstract fun bindThemeViewModel(viewModel: LegoThemeViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(LegoSetsViewModel::class)
    abstract fun bindLegoSetsViewModel(viewModel: LegoSetsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LegoSetViewModel::class)
    abstract fun bindLegoSetViewModel(viewModel: LegoSetViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
