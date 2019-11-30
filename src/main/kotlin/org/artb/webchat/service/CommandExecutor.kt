package org.artb.webchat.service

import org.artb.webchat.common.Constants
import org.artb.webchat.model.ChatMessage
import org.artb.webchat.model.MessageType
import org.artb.webchat.storage.UserStorage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CommandExecutor {

    @Autowired
    private lateinit var userStorage: UserStorage

    // TODO it should be refactored with the command pattern
    public fun execute(message: ChatMessage): ChatMessage {
        return when (message.content) {
            "/users" -> ChatMessage(
                    type = MessageType.COMMAND_RESULT,
                    content = userStorage.getAllUsers().toString(),
                    sender = Constants.SERVER_SENDER
            )
            else -> ChatMessage(
                    type = MessageType.COMMAND_ERROR,
                    content = "${message.content} is not a valid command.",
                    sender = Constants.SERVER_SENDER
            )
        }
    }
}