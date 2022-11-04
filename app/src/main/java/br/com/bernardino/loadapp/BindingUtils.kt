package br.com.bernardino.loadapp

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("textColor")
fun bindTextColor(textInputEditText: TextView, textColorResource: Int?) {
    if (textColorResource != null) {
        textInputEditText.setTextColor(ContextCompat.getColor(textInputEditText.context, textColorResource))
    }
}