package com.vjezba.androidjetpackgithub.di


import com.vjezba.androidjetpackgithub.ui.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class RegistrationBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeEnterDetailsFragment(): EnterDetailsFragment

}
