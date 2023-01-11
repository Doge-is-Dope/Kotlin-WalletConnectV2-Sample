package com.example.signsample.model

import androidx.annotation.DrawableRes

sealed class SessionDetails {

    data class Content(
        val icon: String?,
        val name: String,
        val url: String,
        val description: String,
        val accountList: List<ChainAccountInfo>,
        val methods: String,
        val events: String,
    ) : SessionDetails() {

        data class ChainAccountInfo(
            val chainName: String,
            @DrawableRes val chainIcon: Int,
            val chainNamespace: String,
            val chainReference: String,
            val accountAddress: String
        )
    }

    object NoContent : SessionDetails()
}