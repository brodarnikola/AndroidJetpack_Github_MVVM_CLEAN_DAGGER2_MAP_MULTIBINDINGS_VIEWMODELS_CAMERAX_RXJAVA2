package com.vjezba.androidjetpackgithub

import android.app.Activity
import android.app.Application
import com.vjezba.androidjetpackgithub.di.AppInjector
import com.vjezba.androidjetpackgithub.di.presentationModule
import com.vjezba.data.di.databaseModule
import com.vjezba.data.di.networkingModule
import com.vjezba.data.di.repositoryModule
import com.vjezba.domain.di.interactionModule
import dagger.android.DispatchingAndroidInjector
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import javax.inject.Inject
import dagger.android.HasActivityInjector

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

    val appModules = listOf(presentationModule)
    val interactionModules = listOf(interactionModule)
    val dataModules = listOf( networkingModule, repositoryModule, databaseModule)

    startKoin {
      androidContext(this@App)
      if (BuildConfig.DEBUG) androidLogger(Level.ERROR)
      modules(appModules + interactionModules + dataModules)
    }

    AppInjector.init(this)

  }

  override fun activityInjector() = dispatchingAndroidInjector

}

