package org.artb.webchat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebChatApplication

fun main(args: Array<String>) {
	runApplication<WebChatApplication>(*args)
}
