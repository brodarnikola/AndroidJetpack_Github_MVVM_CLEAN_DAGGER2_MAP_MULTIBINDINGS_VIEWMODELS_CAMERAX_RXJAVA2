package com.vjezba.domain.di

import com.vjezba.domain.repository.UserManager
import com.vjezba.domain.storage.SharedPreferencesStorage
import com.vjezba.domain.storage.Storage
import com.vjezba.domain.user.UserManagerImpl
import org.koin.dsl.module


val interactionModule = module {

  factory<UserManager> { UserManagerImpl(get())}
  factory<Storage> { SharedPreferencesStorage(get()) }
}