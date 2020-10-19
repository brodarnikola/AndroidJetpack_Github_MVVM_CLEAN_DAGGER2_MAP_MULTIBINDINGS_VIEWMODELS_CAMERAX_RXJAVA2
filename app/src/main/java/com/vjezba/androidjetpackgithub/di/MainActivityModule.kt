package com.vjezba.androidjetpackgithub.di


import com.vjezba.androidjetpackgithub.ui.activities.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): LanguagesActivity

    @ContributesAndroidInjector()
    abstract fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeRegisterActivity(): RegistrationActivity


    @ContributesAndroidInjector()
    abstract fun contributeSplashActivity(): SplashActivity


    @ContributesAndroidInjector()
    abstract fun contributeRepositoriesActivity(): RepositoriesActivity

}
