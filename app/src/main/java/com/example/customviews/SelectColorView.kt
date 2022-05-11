package com.example.customviews

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.customviews.databinding.ChooserLayoutBinding
import com.example.customviews.databinding.SelectColorBinding


const val APP_PREFERENCES = "APP_PREFERENCES"
const val PREF_COLOR_PLAYER_1 = "PREF_COLOR_PLAYER_1"
const val PREF_COLOR_PLAYER_2 = "PREF_COLOR_PLAYER_2"
const val PREF_COLOR_GRID = "PREF_COLOR_GRID"


class SelectColorView @JvmOverloads constructor(
    context: Context,
    attributesSet: AttributeSet? = null,
    defStyleAttr: Int = R.attr.selectColorStyle,
    defStyleRes: Int = 0
) : RelativeLayout(context, attributesSet, defStyleAttr, defStyleRes), View.OnClickListener {

    private val binding: SelectColorBinding
    private val sharedPreferences: SharedPreferences
    private lateinit var key: String

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.select_color, this, true)
        binding = SelectColorBinding.bind(this)
        binding.root.setOnClickListener(this)
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        initializeAttributes(context, attributesSet, defStyleAttr, defStyleRes)
    }

    private fun initializeAttributes(
        context: Context,
        attributesSet: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        if (attributesSet == null) return

        val typedArray = context.obtainStyledAttributes(attributesSet, R.styleable.SelectColorView, defStyleAttr, defStyleRes)
        val nameOfSetting = typedArray.getString(R.styleable.SelectColorView_nameOfSetting)

        with(binding) {
            key = typedArray.getString(R.styleable.SelectColorView_key).toString()
            settingName.text = nameOfSetting
            selectedColor.setBackgroundColor(sharedPreferences.getInt(key, typedArray.getColor(R.styleable.SelectColorView_defaultColor, 0)))
        }

        typedArray.recycle()
    }

    override fun onClick(v: View?) {
        val dialogBinding = ChooserLayoutBinding.inflate(LayoutInflater.from(context))
        val color = (binding.selectedColor.background as ColorDrawable).color
        dialogBinding.resColor.setBackgroundColor(color)

        var redValue = color.red
        var greenValue = color.green
        var blueValue = color.blue

        dialogBinding.seekRed.progress = color.red
        dialogBinding.seekGreen.progress = color.green
        dialogBinding.seekBlue.progress = color.blue

        dialogBinding.seekBlue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                blueValue = p1
                dialogBinding.resColor.setBackgroundColor(
                    Color.rgb(
                        redValue,
                        greenValue,
                        blueValue
                    )
                )
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        dialogBinding.seekGreen.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                greenValue = p1
                dialogBinding.resColor.setBackgroundColor(
                    Color.rgb(
                        redValue,
                        greenValue,
                        blueValue
                    )
                )
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        dialogBinding.seekRed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                redValue = p1
                dialogBinding.resColor.setBackgroundColor(
                    Color.rgb(
                        redValue,
                        greenValue,
                        blueValue
                    )
                )
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
         AlertDialog.Builder(context)
            .setCancelable(false)
            .setTitle("Choose color")
            .setView(dialogBinding.root)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Confirm") { _, _ ->
                binding.selectedColor.setBackgroundColor(
                    Color.rgb(
                        dialogBinding.seekRed.progress,
                        dialogBinding.seekGreen.progress,
                        dialogBinding.seekBlue.progress
                    )
                )
                sharedPreferences.edit().putInt(
                    key,
                    Color.rgb(
                        dialogBinding.seekRed.progress,
                        dialogBinding.seekGreen.progress,
                        dialogBinding.seekBlue.progress
                    )
                )
                    .apply()
            }.create().show()
    }

}

