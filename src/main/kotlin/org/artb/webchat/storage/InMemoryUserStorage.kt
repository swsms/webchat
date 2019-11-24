package org.artb.webchat.storage

import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryUserStorage {

    private val sessionIdToUsernameMap = ConcurrentHashMap<String, String>()

    fun getUserBySessionId(sessionId: String): String? {
        return sessionIdToUsernameMap[sessionId]
    }

    fun removeUserBySessionId(sessionId: String): String? {
        return sessionIdToUsernameMap.remove(sessionId)
    }

    fun getAllUsers(): Map<String, String> {
        return Collections.unmodifiableMap(sessionIdToUsernameMap)
    }
}