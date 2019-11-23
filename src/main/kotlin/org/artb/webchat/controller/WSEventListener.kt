package org.artb.webchat.controller

import org.artb.webchat.model.ChatMessage
import org.artb.webchat.model.MessageType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent


@Component
class WSEventListener {
    private val logger = LoggerFactory.getLogger(WSEventListener::class.java)

    @Autowired
    private lateinit var messagingTemplate: SimpMessageSendingOperations

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        logger.info("Received a new web socket connection")
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)

        val username = headerAccessor.sessionAttributes?.get("username") as String
        logger.info("User disconnected : $username")

        val chatMessage = ChatMessage(
                type=MessageType.LEAVE,
                sender=username
        )

        messagingTemplate.convertAndSend("/topic/public", chatMessage)
    }

}