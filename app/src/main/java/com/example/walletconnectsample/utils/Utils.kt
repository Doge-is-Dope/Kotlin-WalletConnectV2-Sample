package com.example.walletconnectsample.utils

import android.net.Uri


fun String.extractHost(): String = Uri.parse(this).host ?: ""