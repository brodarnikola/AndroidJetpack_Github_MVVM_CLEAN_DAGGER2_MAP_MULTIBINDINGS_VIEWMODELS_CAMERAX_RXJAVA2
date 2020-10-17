package com.vjezba.data.di

import com.vjezba.data.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val databaseModule = module {
  
  single { AppDatabase.getInstance(androidContext()) }

  factory { get<AppDatabase>().languagesDAO() }
  factory { get<AppDatabase>().savedLanguagesDAO() }
  factory { get<AppDatabase>().languageReposDAO() }
  factory { get<AppDatabase>().languageRemoteKeyDAO() }
}