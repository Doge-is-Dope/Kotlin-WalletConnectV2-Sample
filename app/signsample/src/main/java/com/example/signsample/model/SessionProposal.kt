package com.example.signsample.model

data class SessionProposal(
    val peerIcon: String,
    val peerName: String,
    val proposalUri: String,
    val peerDescription: String,
    val chains: String,
    val methods: String,
    val events: String,
)