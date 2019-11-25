package org.artb.webchat.storage

import org.artb.webchat.exceptions.InvalidUsernameException
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap


@Component
class InMemoryUserStorage: UserStorage {

    private val sessionIdToUsernameMap = ConcurrentHashMap<String, String>()
    private val lockedNames = Collections.newSetFromMap<String>(ConcurrentHashMap())

    override fun getUserBySessionId(sessionId: String): String? {
        return sessionIdToUsernameMap[sessionId]
    }

    override fun removeUserBySessionId(sessionId: String): String? {
        return sessionIdToUsernameMap.remove(sessionId)
    }

    override fun getAllUsers(): Collection<String> {
        return sessionIdToUsernameMap.values
    }

    override fun addUser(sessionId: String, username: String) {
        if (!lockedNames.add(username)) {
            throw InvalidUsernameException(
                    "The name $username is already taken")
        }
        try {
            if (getAllUsers().contains(username)) {
                throw InvalidUsernameException(
                        "The name $username is already taken")
            }
            sessionIdToUsernameMap[sessionId] = username
        } finally {
            lockedNames.remove(username)
        }
    }
}