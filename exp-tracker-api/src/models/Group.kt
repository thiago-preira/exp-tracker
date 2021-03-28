package com.exp.tracker.models

import kotlinx.serialization.Serializable


@Serializable
data class Group(
    val id: Long,
    val description: String
)