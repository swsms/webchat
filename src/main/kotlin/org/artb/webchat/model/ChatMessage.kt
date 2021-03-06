package org.artb.webchat.model

data class ChatMessage(val type: MessageType? = MessageType.CHAT,
                       val content: String? = "",
                       val sender: String)

enum class MessageType {
    CHAT,
    JOIN,
    LEAVE,

    COMMAND_RESULT,
    COMMAND_ERROR,

    LOGIN_REQUIRED,
    LOGIN_DECLINED,
    LOGIN_ACCEPTED
}