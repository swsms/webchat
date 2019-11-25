package org.artb.webchat.service

import org.artb.webchat.common.*
import org.artb.webchat.common.Constants.PUBLIC_TOPIC_DEST
import org.artb.webchat.exceptions.InvalidUsernameException
import org.artb.webchat.model.ChatMessage
import org.artb.webchat.storage.UserStorage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service

@Service
class ChatService {
    private val logger = LoggerFactory.getLogger(ChatService::class.java)

    @Autowired
    private lateinit var messagingTemplate: SimpMessageSendingOperations

    @Autowired
    private lateinit var userStorage: UserStorage

    fun addUserToChat(sessionId: String, username: String?): ChatMessage {
        return try {
            checkUsernameIsNotEmpty(username)

            userStorage.addUser(sessionId, username!!)
            messagingTemplate.convertAndSend(
                    PUBLIC_TOPIC_DEST,
                    prepareUserJoinedChatMessage(username))

            logger.info("User $username joined the chat")
            prepareSuccessfullyLoggedMessage(username)
        } catch (e: InvalidUsernameException) {
            logger.info("Unsuitable name $username")
            prepareDeclinedMessage(e.message)
        }
    }

    fun removeUserFromChat(sessionId: String) {
        userStorage.removeUserBySessionId(sessionId)?.let {
            messagingTemplate.convertAndSend(
                    PUBLIC_TOPIC_DEST,
                    prepareUserLeftChatMessage(it))
        }
    }

}