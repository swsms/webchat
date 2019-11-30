package org.artb.webchat.common

import org.artb.webchat.exceptions.InvalidUsernameException
import org.artb.webchat.model.ChatMessage
import org.artb.webchat.model.MessageType


fun checkUsernameIsNotEmpty(username: String?) {
    if (username == null || username.trim().isEmpty()) {
        throw InvalidUsernameException("Username must not be null")
    }
}

fun prepareUserJoinedChatMessage(username: String): ChatMessage {
    return ChatMessage(
            type = MessageType.JOIN,
            content = "$username joined the chat",
            sender = Constants.SERVER_SENDER
    )
}

fun prepareUserLeftChatMessage(username: String): ChatMessage {
    return ChatMessage(
            type = MessageType.LEAVE,
            content = "$username left the chat",
            sender = Constants.SERVER_SENDER
    )
}

fun prepareSuccessfullyLoggedMessage(username: String): ChatMessage {
    return ChatMessage(
            type = MessageType.LOGIN_ACCEPTED,
            content = "You have successfully logged as $username",
            sender = Constants.SERVER_SENDER
    )
}

fun prepareDeclinedMessage(reason: String?): ChatMessage {
    return ChatMessage(
            type = MessageType.LOGIN_DECLINED,
            content = reason,
            sender = Constants.SERVER_SENDER
    )
}