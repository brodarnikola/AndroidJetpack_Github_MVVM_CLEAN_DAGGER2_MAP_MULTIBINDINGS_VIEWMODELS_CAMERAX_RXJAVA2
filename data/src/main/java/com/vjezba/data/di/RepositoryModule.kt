package com.vjezba.data.di

import com.vjezba.data.database.mapper.DbMapper
import com.vjezba.data.database.mapper.DbMapperImpl
import com.vjezba.data.repository.GithubRepositoryImpl
import com.vjezba.data.repository.LanguagesRepositoryImpl
import com.vjezba.data.repository.SavedLanguagesRepositoryImpl
import com.vjezba.domain.repository.GithubRepository
import com.vjezba.domain.repository.LanguagesRepository
import com.vjezba.domain.repository.SavedLanguagesRepository
import org.koin.dsl.module

val repositoryModule = module {

  factory<DbMapper> { DbMapperImpl() }
  factory<LanguagesRepository> { LanguagesRepositoryImpl(get(), get()) }
  factory<SavedLanguagesRepository> { SavedLanguagesRepositoryImpl(get(), get()) }
  factory<GithubRepository> { GithubRepositoryImpl(get(), get(), get()) }
}