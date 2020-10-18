package com.vjezba.androidjetpackgithub

import android.app.Activity
import android.app.Application
import com.vjezba.androidjetpackgithub.di.AppInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector {


  @Inject
  lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

  companion object {
    lateinit var instance: Application
      private set
  }
  
  override fun onCreate() {
    super.onCreate()
    instance = this

//    val appModules = listOf(presentationModule)
//    val interactionModules = listOf(interactionModule)
//    val dataModules = listOf( networkingModule, repositoryModule, databaseModule)
//
//    startKoin {
//      androidContext(this@App)
//      if (BuildConfig.DEBUG) androidLogger(Level.ERROR)
//      modules(appModules + interactionModules + dataModules)
//    }

    AppInjector.init(this)

  }

  override fun activityInjector() = dispatchingAndroidInjector

}

