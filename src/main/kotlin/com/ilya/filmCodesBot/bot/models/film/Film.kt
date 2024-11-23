package com.ilya.filmCodesBot.bot.models.film


data class Film(
    val type: String,
    val code: Int,
    val imageFileId: String,
    val name: String,
    val year: Int,
    val genre: String,
    val country: String,
    val description: String
)
