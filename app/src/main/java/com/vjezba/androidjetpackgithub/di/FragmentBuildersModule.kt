package com.vjezba.androidjetpackgithub.di


import com.vjezba.androidjetpackgithub.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeThemeFragment(): LegoThemeFragment

    @ContributesAndroidInjector
    abstract fun contributeLegoSetsFragment(): LegoSetsFragment

    @ContributesAndroidInjector
    abstract fun contributeLegoSetFragment(): LegoSetFragment




    @ContributesAndroidInjector
    abstract fun contributeEnterDetailsFragment(): EnterDetailsFragment




    @ContributesAndroidInjector
    abstract fun contributeSavedLanguagesFragment(): SavedLanguagesFragment

    @ContributesAndroidInjector
    abstract fun contributeLanguagesFragment(): LanguagesFragment

    @ContributesAndroidInjector
    abstract fun contributeLanguageDetailsFragment(): LanguageDetailsFragment



}
