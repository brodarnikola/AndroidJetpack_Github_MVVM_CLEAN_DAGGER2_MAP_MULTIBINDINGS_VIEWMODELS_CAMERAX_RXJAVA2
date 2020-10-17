package com.vjezba.androidjetpackgithub.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vjezba.androidjetpackgithub.ui.activities.LoginError
import com.vjezba.androidjetpackgithub.ui.activities.LoginSuccess
import com.vjezba.androidjetpackgithub.ui.activities.LoginViewState
import com.vjezba.domain.repository.SavedLanguagesRepository
import com.vjezba.domain.repository.UserManager
import javax.inject.Inject

class LoginViewModel @Inject internal constructor(private val userManager: UserManager
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginViewState>()
    val loginState: LiveData<LoginViewState>
        get() = _loginState

    fun login(username: String, password: String) {
        if (userManager.loginUser(username, password)) {
            _loginState.value = LoginSuccess
        } else {
            _loginState.value = LoginError
        }
    }

    fun unregister() {
        userManager.unregister()
    }


    fun getUsername(): String = userManager.getUserName()

}
