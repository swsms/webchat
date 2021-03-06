package org.artb.webchat.storage

interface UserStorage {

    fun getUserBySessionId(sessionId: String): String?

    fun removeUserBySessionId(sessionId: String): String?

    fun getAllUsers(): Collection<String>

    fun addUser(sessionId: String, username: String)

    fun containsUsername(username: String): Boolean

    fun containsSessionId(sessionId: String): Boolean
}