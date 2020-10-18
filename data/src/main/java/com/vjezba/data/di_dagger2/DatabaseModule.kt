/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vjezba.data.di

import android.app.Application
import android.content.Context
import com.vjezba.data.database.AppDatabase
import com.vjezba.data.database.dao.LanguagesDao
import com.vjezba.data.database.dao.SavedLanguagesDAO
import com.vjezba.domain.repository.UserManager
import com.vjezba.domain.storage.SharedPreferencesStorage
import com.vjezba.domain.storage.Storage
import com.vjezba.domain.user.UserManagerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

   @Singleton
    @Provides
    fun provideAppDatabase( app: Application) = AppDatabase.getInstance(app)

    @Provides
    fun providePlantDao(appDatabase: AppDatabase): LanguagesDao {
        return appDatabase.languagesDAO()
    }

    @Provides
    fun provideGardenPlantingDao(appDatabase: AppDatabase): SavedLanguagesDAO {
        return appDatabase.savedLanguagesDAO()
    }


}
