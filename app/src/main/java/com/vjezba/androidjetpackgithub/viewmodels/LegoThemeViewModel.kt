

package com.vjezba.androidjetpackgithub.viewmodels

import androidx.lifecycle.ViewModel
import com.vjezba.data.lego.repository.LegoThemeRepository
import javax.inject.Inject

/**
 * The ViewModel for [LegoThemeFragment].
 */
class LegoThemeViewModel @Inject constructor(repository: LegoThemeRepository) : ViewModel() {

    val legoThemes= repository.themes
}
