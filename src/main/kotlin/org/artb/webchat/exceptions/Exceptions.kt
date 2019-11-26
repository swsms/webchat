package org.artb.webchat.exceptions

class InvalidUsernameException(message: String): Exception(message)

class NotAuthorizedUserException(message: String): Exception(message)