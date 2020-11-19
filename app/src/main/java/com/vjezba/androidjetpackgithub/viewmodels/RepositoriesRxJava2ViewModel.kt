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

package com.vjezba.androidjetpackgithub.viewmodels

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.vjezba.domain.model.RepositoryDetailsResponse
import com.vjezba.domain.model.RepositoryResponse
import com.vjezba.domain.repository.GithubRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoriesRxJava2ViewModel @Inject internal constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val authUser: MediatorLiveData<RepositoryResponse> = MediatorLiveData<RepositoryResponse>()

    fun searchGithubRepositoryByLastUpdateTimeWithLiveData(query: String) : LiveData<RepositoryResponse> {

        var source: LiveData<RepositoryResponse>? = null
        try {
            source = LiveDataReactiveStreams.fromPublisher(
                repository.getSearchRepositorieWithFlowableRxJava2(query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn { error ->
                        Log.e(ContentValues.TAG, "onError received: ${error}")
                        RepositoryResponse(0, false, listOf())
                    }
            )
        }
        catch (e : Exception) {
            print("Exception: ${e}")
        }

        if( source != null ) {
            authUser.addSource(source, object : Observer<RepositoryResponse?> {
                override fun onChanged(user: RepositoryResponse?) {
                    authUser.setValue(user)
                    authUser.removeSource(source)
                }
            })
        }

        return authUser
    }


}
