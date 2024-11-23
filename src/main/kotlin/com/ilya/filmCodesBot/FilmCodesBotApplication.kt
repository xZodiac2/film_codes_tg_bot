package com.ilya.filmCodesBot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.nio.charset.Charset

@SpringBootApplication
class FilmCodesBotApplication

fun main(args: Array<String>) {
	runApplication<FilmCodesBotApplication>(*args)
}
