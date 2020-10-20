/*
 * Copyright 2018 Google LLC
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

package com.vjezba.androidjetpackgithub.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.vjezba.androidjetpackgithub.R
import kotlinx.android.synthetic.main.activity_repositories_details.*


class RepositoriesDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repositories_details)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setDetailsAboutLanguage()
    }

    private fun setDetailsAboutLanguage() {
        repositoryName?.text = "" + intent.getStringExtra("repositoryName")
        lastUpdateTimeValue?.text = "" + intent.getStringExtra("lastUpdateTime")
        ownerNameValue?.text = "" + intent.getStringExtra("ownerNameValue")
        if( intent.getStringExtra("repositoryDescription") != "null" )
            repositoryDescriptionValue?.text = "" + intent.getStringExtra("repositoryDescription")
        else
            repositoryDescriptionValue?.text = "This repository does not have any description."
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                startActivity(Intent(this, RepositoriesActivity::class.java))
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

}
