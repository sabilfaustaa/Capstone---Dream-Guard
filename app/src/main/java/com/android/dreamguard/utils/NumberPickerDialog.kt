package com.android.dreamguard.ui.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.TextView
import com.capstone.dreamguard.R

class NumberPickerDialog(
    context: Context,
    private val title: String,
    private val initialHour: Int,
    private val initialMinute: Int,
    private val onTimeSet: (Int, Int) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_number_picker)

        val titleView: TextView = findViewById(R.id.dialog_title)
        val hourPicker: NumberPicker = findViewById(R.id.numberPickerHour)
        val minutePicker: NumberPicker = findViewById(R.id.numberPickerMinute)
        val confirmButton: TextView = findViewById(R.id.confirm_button)

        titleView.text = title

        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        hourPicker.value = initialHour

        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = initialMinute

        confirmButton.setOnClickListener {
            onTimeSet(hourPicker.value, minutePicker.value)
            dismiss()
        }
    }
}
