package org.artb.webchat.storage

import org.artb.webchat.exceptions.InvalidUsernameException
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryUserStorage: UserStorage {

    private val sessionIdToUsernameMap = ConcurrentHashMap<String, String>()

    override fun getUserBySessionId(sessionId: String): String? {
        return sessionIdToUsernameMap[sessionId]
    }

    override fun removeUserBySessionId(sessionId: String): String? {
        return sessionIdToUsernameMap.remove(sessionId)
    }

    override fun getAllUsers(): Collection<String> {
        return sessionIdToUsernameMap.values
    }

    // TODO avoid duplicate names by locking
    override fun addUser(sessionId: String, username: String) {
        if (getAllUsers().contains(username)) {
            throw InvalidUsernameException(
                    "The name $username is already taken")
        }
        sessionIdToUsernameMap[sessionId] = username
    }
}