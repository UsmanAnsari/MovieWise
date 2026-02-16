package com.uansari.moviewise.domain.util

import java.util.Locale

fun Double.formatToRatingString(): String = String.format(Locale.UK, "%.1f", this)
