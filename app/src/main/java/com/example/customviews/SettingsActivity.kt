package com.example.customviews

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.customviews.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }
        //To display arrow back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.resetSettings.setOnClickListener {
            getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putInt(
                    PREF_COLOR_PLAYER_1,
                    ContextCompat.getColor(applicationContext, R.color.player1Color)
                )
                .putInt(
                    PREF_COLOR_PLAYER_2,
                    ContextCompat.getColor(applicationContext, R.color.player2Color)
                )
                .putInt(
                    PREF_COLOR_GRID,
                    ContextCompat.getColor(applicationContext, R.color.gridColor)
                )
                .apply()

            //To skip animation, because we need to start activity again so that settings get new colors
            overridePendingTransition(0, 0)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}