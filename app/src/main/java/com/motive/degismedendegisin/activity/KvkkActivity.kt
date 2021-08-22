package com.motive.degismedendegisin.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.motive.degismedendegisin.R

class KvkkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kvkk)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}