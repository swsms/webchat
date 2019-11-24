package org.artb.webchat.controller

import org.artb.webchat.model.ChatMessage
import org.artb.webchat.model.MessageType
import org.artb.webchat.utils.Constants.SERVER_SENDER
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller


@Controller
class ChatController {
    private val logger = LoggerFactory.getLogger(ChatController::class.java)

    @Autowired
    private lateinit var messagingTemplate: SimpMessageSendingOperations

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
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
    @SendToUser("/queue/reply")
    fun enter(@Payload message: ChatMessage,
              headerAccessor: SimpMessageHeaderAccessor,
              @Header("simpSessionId") sessionId: String): ChatMessage {
        val username = message.content
        return if (username.equals("John")) {
            logger.info("Unsuitable name $username")
            ChatMessage(
                    type = MessageType.AUTH_DECLINED,
                    content = "Invalid username",
                    sender = SERVER_SENDER
            )
        } else {
            logger.info("User ${message.sender} joined the chat")
            headerAccessor.sessionAttributes?.set("username", message.content)
            headerAccessor.sessionId=sessionId;
            messagingTemplate.convertAndSend(
                    "/topic/public",
                    ChatMessage(
                            type = MessageType.JOIN,
                            content = "$username joined the chat",
                            sender = SERVER_SENDER

                    ))
            ChatMessage(
                    type = MessageType.AUTH_ACCEPTED,
                    content = "You have successfully logged as $username",
                    sender = SERVER_SENDER
            )
        }
    }
}
