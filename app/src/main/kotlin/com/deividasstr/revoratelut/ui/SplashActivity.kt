package com.deividasstr.revoratelut.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.deividasstr.revoratelut.ui.ratelist.RateListActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, RateListActivity::class.java))
        finish()
    }
}
