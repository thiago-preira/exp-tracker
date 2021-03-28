package com.exp.tracker.models

import kotlinx.serialization.Serializable

@Serializable
data class Category(val id: Long, val name: String, val group: Group)