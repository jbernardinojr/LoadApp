package br.com.bernardino.loadapp.extensions

import android.widget.RadioGroup

private const val NO_OPTION_SELECTED_ID = -1

fun RadioGroup.isOptionSelected() = this.checkedRadioButtonId != NO_OPTION_SELECTED_ID
