package com.vjezba.androidjetpackgithub.di

import com.vjezba.data.di.*
import dagger.Module

@Module(includes = [ViewModelModule::class, StorageModule::class, UserManagerModule::class, RepositoryModule::class, DatabaseModule::class, MapperModule::class, NetworkModule::class])
class AppModule {

}
