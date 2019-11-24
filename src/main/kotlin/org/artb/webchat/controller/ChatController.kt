package org.artb.webchat.controller

import org.artb.webchat.model.ChatMessage
import org.artb.webchat.model.MessageType
import org.artb.webchat.service.ChatService
import org.artb.webchat.common.Constants.PUBLIC_TOPIC_DEST
import org.artb.webchat.common.Constants.USER_QUEUE_DEST
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller


@Controller
class ChatController {
    private val logger = LoggerFactory.getLogger(ChatController::class.java)

    @Autowired
    private lateinit var service: ChatService

    @MessageMapping("/chat.send")
    @SendTo(PUBLIC_TOPIC_DEST)
    fun sendMessage(@Payload message: ChatMessage): ChatMessage {
        logger.info("User ${message.sender} sends '${message.content}'")
        return message
    }

//    @MessageMapping("/chat.command")
//    @SendToUser("/queue/reply")
//    fun executeCommand(@Payload message: ChatMessage): ChatMessage {
//        Thread.sleep(2000L)
//        return ChatMessage(
//                type=MessageType.JOIN,
//                content="[Vasia, Polya, Kate]",
//                sender="server"
//        );
//    }

    @MessageMapping("/chat.enter")
    @SendToUser(USER_QUEUE_DEST)
    fun enter(@Payload message: ChatMessage,
              headerAccessor: SimpMessageHeaderAccessor,
              @Header("simpSessionId") sessionId: String): ChatMessage {
        logger.info("New entering message: $message")

        val resultMessage = service.addUserToChat(sessionId, message.content)
        if (resultMessage.type == MessageType.AUTH_ACCEPTED) {
            headerAccessor.sessionAttributes?.set("username", message.content)
            headerAccessor.sessionId=sessionId;
        }

        return resultMessage
    }
}
