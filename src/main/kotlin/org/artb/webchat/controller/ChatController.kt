package org.artb.webchat.controller

import org.artb.webchat.model.ChatMessage
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller

@Controller
class ChatController {
    private val logger = LoggerFactory.getLogger(ChatController::class.java)

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    fun sendMessage(@Payload message: ChatMessage): ChatMessage {
        logger.info("User ${message.sender} sends '${message.content}'")
        return message
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    fun addUser(@Payload message: ChatMessage,
                headerAccessor: SimpMessageHeaderAccessor): ChatMessage {
        headerAccessor.sessionAttributes?.set("username", message.sender)
        logger.info("User ${message.sender} joined the chat")
        return message
    }

}
