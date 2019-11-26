package org.artb.webchat.model

data class ChatMessage(val type: MessageType? = MessageType.CHAT,
                       val content: String? = "",
                       val sender: String)

enum class MessageType {
    CHAT,
    JOIN,
    LEAVE,

    COMMAND_RESULT,
    UNKNOWN_COMMAND,

    LOGIN_REQUIRED,
    AUTH_ATTEMPT,
    AUTH_DECLINED,
    AUTH_ACCEPTED
}