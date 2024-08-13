package com.example.adaptivefontsizeedittext

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnLongClickListener
import androidx.appcompat.app.AppCompatActivity
import com.example.adaptivefontsizeedittext.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.autoAdaptSizeEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.autoAdaptSizeTextView.text = s
            }
        })

        binding.autoAdaptSizeTextView.setOnLongClickListener {
            binding.autoAdaptSizeTextView.setTextIsSelectable(true)
            true
        }
    }
}