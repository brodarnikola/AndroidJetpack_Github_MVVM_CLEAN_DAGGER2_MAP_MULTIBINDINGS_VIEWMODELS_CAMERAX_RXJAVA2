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

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vjezba.domain.model.RepositoryDetailsResponse
import com.vjezba.domain.model.RepositoryResponse
import com.vjezba.domain.repository.GithubRepository

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

import java.util.concurrent.TimeUnit
import javax.inject.Inject


class RxJava2FlowableToLiveDataViewModel @Inject internal constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private val reposInfo: MediatorLiveData<RepositoryResponse> = MediatorLiveData<RepositoryResponse>()
    private val reposInfoAutomatic: LiveData<RepositoryResponse>? = null
    private val compositeDisposable = CompositeDisposable()

    private val loading = MutableLiveData<Boolean>()
    private val apiError = MutableLiveData<Throwable>()



    fun apiError(): MutableLiveData<Throwable> {
        return apiError
    }


    fun observeReposInfo(): LiveData<RepositoryResponse> {
        return reposInfo
    }

    fun observeReposInfoAutomatic(): LiveData<RepositoryResponse>? {
        return reposInfoAutomatic
    }


    fun searchGithubRepositoryByLastUpdateTimeWithFlowableAndLiveData(query: String) {
        repository.getSearchRepositorieWithFlowableRxJava2(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
            .subscribe(object : io.reactivex.Observer<RepositoryResponse> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(response: RepositoryResponse) {
                    Log.d(TAG, "Da li ce uci sim EEEE: ${response}")
                    reposInfoAutomatic?.value?.items = response.items
                    reposInfo.value = response
                }

                override fun onError(e: Throwable) {
                    Log.d(
                       TAG,
                        "onError received: " + e.message
                    )
                }

                override fun onComplete() {}
            })
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}
