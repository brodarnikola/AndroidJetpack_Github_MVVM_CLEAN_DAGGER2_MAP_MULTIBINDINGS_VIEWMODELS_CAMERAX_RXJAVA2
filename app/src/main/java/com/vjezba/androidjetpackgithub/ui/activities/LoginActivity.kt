package com.vjezba.androidjetpackgithub.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vjezba.androidjetpackgithub.R
import com.vjezba.androidjetpackgithub.di.Injectable
import com.vjezba.androidjetpackgithub.di.injectViewModel
import com.vjezba.androidjetpackgithub.viewmodels.GalleryViewModel
import com.vjezba.androidjetpackgithub.viewmodels.LegoThemeViewModel
import com.vjezba.androidjetpackgithub.viewmodels.LoginViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import javax.inject.Inject
import kotlin.error

class LoginActivity : AppCompatActivity(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = dispatchingAndroidInjector

    //private val viewModel : LoginViewModel by viewModel()

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = injectViewModel(viewModelFactory)

        viewModel.loginState.observe(this, Observer<LoginViewState> { state ->
            when (state) {
                is LoginSuccess -> {
                    startActivity(Intent(this, LanguagesActivity::class.java))
                    finish()
                }
                is LoginError -> error.visibility = View.VISIBLE
            }
        })

        setupViews()
    }

    private fun setupViews() {

        username.setText(viewModel.getUsername())

        findViewById<Button>(R.id.login).setOnClickListener {
            viewModel.login(username.text.toString(), password.text.toString())
        }

        findViewById<Button>(R.id.unregister).setOnClickListener {
            viewModel.unregister()
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

    }

}



sealed class LoginViewState
object LoginSuccess : LoginViewState()
object LoginError : LoginViewState()