package org.artb.webchat.controller

import org.artb.webchat.service.ChatService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent


@Component
class WSEventListener {
    private val logger = LoggerFactory.getLogger(WSEventListener::class.java)

    @Autowired
    private lateinit var chatService: ChatService

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        val sessionId = StompHeaderAccessor.wrap(event.message).sessionId
        logger.info("Established a new web socket connection $sessionId")
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        logger.info("Disconnected ${headerAccessor.sessionId}")

        val username = headerAccessor.sessionAttributes?.get("username") as String?
        if (username != null) {
            chatService.removeUserFromChat(headerAccessor.sessionId!!)
        }
    }

}