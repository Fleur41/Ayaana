package com.sam.ayaana.domain.repository

import com.sam.ayaana.Utils.Result
import com.sam.ayaana.Utils.toChat
import com.sam.ayaana.Utils.toChatEntity
import com.sam.ayaana.Utils.toMessage
import com.sam.ayaana.Utils.toMessageEntity
import com.sam.ayaana.data.local.dao.ChatDao
import com.sam.ayaana.data.remote.api.ChatApi
import com.sam.ayaana.domain.model.Chat
import com.sam.ayaana.domain.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject

interface IChatRepository {
    suspend fun getChats(): Flow<Result<List<Chat>>>
    suspend fun getMessages(chatId: String): Flow<Result<List<Message>>>
    suspend fun sendTextMessage(chatId: String, content: String): Result<Unit>
    suspend fun sendMediaMessage(chatId: String, mediaUrl: String, type: String): Result<Unit>
    suspend fun markAsRead(chatId: String): Result<Unit>
    suspend fun getUnreadCount(): Flow<Int>
    suspend fun searchChats(query: String): List<Chat>
}

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val chatDao: ChatDao
) : IChatRepository {

    private val mockChats = listOf(
        Chat(
            id = "1",
            userId = "user1",
            username = "Shivan Wamuyu",
            profileImage = "https://picsum.photos/id/100/200/300",
            lastMessage = "Sent Tuesday",
            timestamp = Date(System.currentTimeMillis() - 86400000),
            unreadCount = 0,
            isOnline = false,
            messageStatus = com.sam.ayaana.domain.model.MessageStatus.SEEN
        ),
        Chat(
            id = "2",
            userId = "user2",
            username = "Juma Allan 💧",
            profileImage = "https://picsum.photos/id/101/200/300",
            lastMessage = "Sent",
            timestamp = Date(System.currentTimeMillis() - 172800000),
            unreadCount = 0,
            isOnline = true,
            messageStatus = com.sam.ayaana.domain.model.MessageStatus.SEEN
        ),
        Chat(
            id = "3",
            userId = "user3",
            username = "Reuben Apollo",
            profileImage = "https://picsum.photos/id/102/200/300",
            lastMessage = "Sent",
            timestamp = Date(System.currentTimeMillis() - 259200000),
            unreadCount = 0,
            isOnline = false,
            messageStatus = com.sam.ayaana.domain.model.MessageStatus.SEEN
        ),
        Chat(
            id = "4",
            userId = "user4",
            username = "Silvester mutiso",
            profileImage = "https://picsum.photos/id/103/200/300",
            lastMessage = "Seen",
            timestamp = Date(System.currentTimeMillis() - 345600000),
            unreadCount = 0,
            isOnline = true,
            messageStatus = com.sam.ayaana.domain.model.MessageStatus.SEEN
        ),
        Chat(
            id = "5",
            userId = "user5",
            username = "Finding Remote Jobs",
            profileImage = "https://picsum.photos/id/104/200/300",
            lastMessage = "Draft: O",
            timestamp = Date(System.currentTimeMillis() - 432000000),
            unreadCount = 1,
            isOnline = false,
            messageStatus = com.sam.ayaana.domain.model.MessageStatus.DELIVERED
        )
    )

    private val mockMessages = mapOf(
        "4" to listOf(
            Message(
                id = "1",
                chatId = "4",
                senderId = "user4",
                receiverId = "currentUser",
                content = "Mzee mzima Silva hambari gani... Mie Sina maneno kaka",
                timestamp = Date(System.currentTimeMillis() - 86400000),
                messageType = com.sam.ayaana.domain.model.MessageType.TEXT,
                isSentByMe = false,
                messageStatus = com.sam.ayaana.domain.model.MessageStatus.SEEN
            ),
            Message(
                id = "2",
                chatId = "4",
                senderId = "currentUser",
                receiverId = "user4",
                content = "Ulipotelea wapi mkuu kihunguro ulihama?",
                timestamp = Date(System.currentTimeMillis() - 43200000),
                messageType = com.sam.ayaana.domain.model.MessageType.TEXT,
                isSentByMe = true,
                messageStatus = com.sam.ayaana.domain.model.MessageStatus.SEEN
            ),
            Message(
                id = "3",
                chatId = "4",
                senderId = "user4",
                receiverId = "currentUser",
                content = "Niko baba ni venye Huwa sitokei sana",
                timestamp = Date(System.currentTimeMillis() - 21600000),
                messageType = com.sam.ayaana.domain.model.MessageType.TEXT,
                isSentByMe = false,
                messageStatus = com.sam.ayaana.domain.model.MessageStatus.SEEN
            ),
            Message(
                id = "4",
                chatId = "4",
                senderId = "currentUser",
                receiverId = "user4",
                content = "Ooh poa\nDouble tap to ▼\n\nLeo nitatokea hapo Kalabash utanipata hapo bz",
                timestamp = Date(System.currentTimeMillis() - 10800000),
                messageType = com.sam.ayaana.domain.model.MessageType.TEXT,
                isSentByMe = true,
                messageStatus = com.sam.ayaana.domain.model.MessageStatus.SEEN
            )
        )
    )

    override suspend fun getChats(): Flow<Result<List<Chat>>> = flow {
        emit(Result.Loading)
        try {
            // Try to get from local database first
            try {
                val localChats = chatDao.getChats()
                localChats.collect { chats ->
                    if (chats.isNotEmpty()) {
                        // FIX: Convert entities to domain models
                        emit(Result.Success(chats.map { it.toChat() }))
                        return@collect
                    }
                }
            } catch (e: Exception) {
                // If local fails, continue to mock data
            }

            // Try API call
            try {
                val response = chatApi.getChats()
                if (response.success && response.data != null) {
                    // Convert and save to local database
                    val chatEntities = response.data.map { it.toChatEntity() }
                    chatDao.insertChats(chatEntities)
                    emit(Result.Success(response.data.map { it.toChat() }))
                } else {
                    // Fallback to mock data
                    emit(Result.Success(mockChats))
                }
            } catch (e: Exception) {
                // Fallback to mock data if API fails
                emit(Result.Success(mockChats))
            }
        } catch (e: Exception) {
            // Final fallback to mock data
            emit(Result.Success(mockChats))
        }
    }

    override suspend fun getMessages(chatId: String): Flow<Result<List<Message>>> = flow {
        emit(Result.Loading)
        try {
            // Try to get from local database first
            try {
                val localMessages = chatDao.getMessages(chatId)
                localMessages.collect { messages ->
                    if (messages.isNotEmpty()) {
                        emit(Result.Success(messages.map { it.toMessage() }))
                        return@collect
                    }
                }
            } catch (e: Exception) {
                // If local fails, continue to mock data
            }

            // Try API call
            try {
                val response = chatApi.getMessages(chatId)
                if (response.success && response.data != null) {
                    val messageEntities = response.data.map { it.toMessageEntity() }
                    chatDao.insertMessages(messageEntities)
                    emit(Result.Success(response.data.map { it.toMessage() }))
                } else {
                    // Fallback to mock data
                    emit(Result.Success(mockMessages[chatId] ?: emptyList()))
                }
            } catch (e: Exception) {
                // Fallback to mock data if API fails
                emit(Result.Success(mockMessages[chatId] ?: emptyList()))
            }
        } catch (e: Exception) {
            // Final fallback to mock data
            emit(Result.Success(mockMessages[chatId] ?: emptyList()))
        }
    }

    override suspend fun sendTextMessage(chatId: String, content: String): Result<Unit> {
        return try {
            val response = chatApi.sendMessage(chatId, com.sam.ayaana.data.remote.model.request.MessageRequest(chatId, content, "TEXT"))
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to send message")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun sendMediaMessage(chatId: String, mediaUrl: String, type: String): Result<Unit> {
        return try {
            val response = chatApi.sendMessage(chatId, com.sam.ayaana.data.remote.model.request.MessageRequest(chatId, "", type, mediaUrl))
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to send media message")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun markAsRead(chatId: String): Result<Unit> {
        return try {
            val response = chatApi.markAsRead(chatId)
            if (response.success) {
                chatDao.markChatAsRead(chatId)
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to mark as read")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getUnreadCount(): Flow<Int> = flow {
        try {
            emit(chatDao.getTotalUnreadCount())
        } catch (e: Exception) {
            emit(mockChats.sumOf { it.unreadCount })
        }
    }

    override suspend fun searchChats(query: String): List<Chat> {
        return try {
            val localResults = chatDao.searchChats(query)
            localResults.map { it.toChat() }
        } catch (e: Exception) {
            mockChats.filter {
                it.username.contains(query, ignoreCase = true)
            }
        }
    }
}

//package com.sam.ayaana.domain.repository

//
//import com.sam.ayaana.Utils.Result
//import com.sam.ayaana.Utils.toChat
//import com.sam.ayaana.Utils.toChatEntity
//import com.sam.ayaana.Utils.toMessage
//import com.sam.ayaana.Utils.toMessageEntity
//import com.sam.ayaana.data.local.dao.ChatDao
//import com.sam.ayaana.data.remote.api.ChatApi
//import com.sam.ayaana.data.remote.model.request.MessageRequest
//import com.sam.ayaana.domain.model.Chat
//import com.sam.ayaana.domain.model.Message
//import com.sam.ayaana.domain.model.MessageStatus
//import com.sam.ayaana.domain.model.MessageType
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//import java.util.Date
//import javax.inject.Inject
//
//interface IChatRepository {
//    suspend fun getChats(): Flow<Result<List<Chat>>>
//    suspend fun getMessages(chatId: String): Flow<Result<List<Message>>>
//    suspend fun sendTextMessage(chatId: String, content: String): Result<Unit>
//    suspend fun sendMediaMessage(chatId: String, mediaUrl: String, type: String): Result<Unit>
//    suspend fun markAsRead(chatId: String): Result<Unit>
//    suspend fun getUnreadCount(): Flow<Int>
//    suspend fun searchChats(query: String): List<Chat>
//}
//
//class ChatRepositoryImpl @Inject constructor(
//    private val chatApi: ChatApi,
//    private val chatDao: ChatDao
//) : IChatRepository {
//
//    private val mockChats = listOf(
//        Chat(
//            id = "1",
//            userId = "user1",
//            username = "Shivan Wamuyu",
//            profileImage = "https://picsum.photos/id/100/200/300",
//            lastMessage = "Sent Tuesday",
//            timestamp = Date(System.currentTimeMillis() - 86400000),
//            unreadCount = 0,
//            isOnline = false,
//            messageStatus = MessageStatus.SEEN
//        ),
//        Chat(
//            id = "2",
//            userId = "user2",
//            username = "Juma Allan 💧",
//            profileImage = "https://picsum.photos/id/101/200/300",
//            lastMessage = "Sent",
//            timestamp = Date(System.currentTimeMillis() - 172800000),
//            unreadCount = 0,
//            isOnline = true,
//            messageStatus = MessageStatus.SEEN
//        ),
//        Chat(
//            id = "3",
//            userId = "user3",
//            username = "Reuben Apollo",
//            profileImage = "https://picsum.photos/id/102/200/300",
//            lastMessage = "Sent",
//            timestamp = Date(System.currentTimeMillis() - 259200000),
//            unreadCount = 0,
//            isOnline = false,
//            messageStatus = MessageStatus.SEEN
//        ),
//        Chat(
//            id = "4",
//            userId = "user4",
//            username = "Silvester mutiso",
//            profileImage = "https://picsum.photos/id/103/200/300",
//            lastMessage = "Seen",
//            timestamp = Date(System.currentTimeMillis() - 345600000),
//            unreadCount = 0,
//            isOnline = true,
//            messageStatus = MessageStatus.SEEN
//        ),
//        Chat(
//            id = "5",
//            userId = "user5",
//            username = "Finding Remote Jobs",
//            profileImage = "https://picsum.photos/id/104/200/300",
//            lastMessage = "Draft: O",
//            timestamp = Date(System.currentTimeMillis() - 432000000),
//            unreadCount = 1,
//            isOnline = false,
//            messageStatus = MessageStatus.DELIVERED
//        )
//    )
//
//    private val mockMessages = mapOf(
//        "4" to listOf(
//            Message(
//                id = "1",
//                chatId = "4",
//                senderId = "user4",
//                receiverId = "currentUser",
//                content = "Mzee mzima Silva hambari gani... Mie Sina maneno kaka",
//                timestamp = Date(System.currentTimeMillis() - 86400000),
//                messageType = MessageType.TEXT,
//                isSentByMe = false,
//                messageStatus = MessageStatus.SEEN
//            ),
//            Message(
//                id = "2",
//                chatId = "4",
//                senderId = "currentUser",
//                receiverId = "user4",
//                content = "Ulipotelea wapi mkuu kihunguro ulihama?",
//                timestamp = Date(System.currentTimeMillis() - 43200000),
//                messageType = MessageType.TEXT,
//                isSentByMe = true,
//                messageStatus = MessageStatus.SEEN
//            ),
//            Message(
//                id = "3",
//                chatId = "4",
//                senderId = "user4",
//                receiverId = "currentUser",
//                content = "Niko baba ni venye Huwa sitokei sana",
//                timestamp = Date(System.currentTimeMillis() - 21600000),
//                messageType = MessageType.TEXT,
//                isSentByMe = false,
//                messageStatus = MessageStatus.SEEN
//            ),
//            Message(
//                id = "4",
//                chatId = "4",
//                senderId = "currentUser",
//                receiverId = "user4",
//                content = "Ooh poa\nDouble tap to ▼\n\nLeo nitatokea hapo Kalabash utanipata hapo bz",
//                timestamp = Date(System.currentTimeMillis() - 10800000),
//                messageType = MessageType.TEXT,
//                isSentByMe = true,
//                messageStatus = MessageStatus.SEEN
//            )
//        )
//    )
//
//    override suspend fun getChats(): Flow<Result<List<Chat>>> = flow {
//        emit(Result.Loading)
//        try {
//            val localChats = chatDao.getChats()
//            localChats.collect { chats ->
//                if (chats.isNotEmpty()) {
//                    emit(Result.Success(chats.map { it.toChat() }))
//                }
//            }
//
//            val response = chatApi.getChats()
//            if (response.success && response.data != null) {
//                val chatEntities = response.data.map { it.toChatEntity() }
//                chatDao.insertChats(chatEntities)
//                emit(Result.Success(response.data.map { it.toChat() }))
//            } else {
//                emit(Result.Success(mockChats))
//            }
//        } catch (e: Exception) {
//            emit(Result.Success(mockChats))
//        }
//    }
//
//    override suspend fun getMessages(chatId: String): Flow<Result<List<Message>>> = flow {
//        emit(Result.Loading)
//        try {
//            val localMessages = chatDao.getMessages(chatId)
//            localMessages.collect { messages ->
//                if (messages.isNotEmpty()) {
//                    emit(Result.Success(messages.map { it.toMessage() }))
//                }
//            }
//
//            val response = chatApi.getMessages(chatId)
//            if (response.success && response.data != null) {
//                val messageEntities = response.data.map { it.toMessageEntity() }
//                chatDao.insertMessages(messageEntities)
//                emit(Result.Success(response.data.map { it.toMessage() }))
//            } else {
//                emit(Result.Success(mockMessages[chatId] ?: emptyList()))
//            }
//        } catch (e: Exception) {
//            emit(Result.Success(mockMessages[chatId] ?: emptyList()))
//        }
//    }
//
//    override suspend fun sendTextMessage(chatId: String, content: String): Result<Unit> {
//        return try {
//            val response = chatApi.sendMessage(chatId, MessageRequest(chatId, content, "TEXT"))
//            if (response.success) {
//                Result.Success(Unit)
//            } else {
//                Result.Error(response.message ?: "Failed to send message")
//            }
//        } catch (e: Exception) {
//            Result.Error(e.message ?: "Network error")
//        }
//    }
//
//    override suspend fun sendMediaMessage(chatId: String, mediaUrl: String, type: String): Result<Unit> {
//        return try {
//            val response = chatApi.sendMessage(chatId, MessageRequest(chatId, "", type, mediaUrl))
//            if (response.success) {
//                Result.Success(Unit)
//            } else {
//                Result.Error(response.message ?: "Failed to send media message")
//            }
//        } catch (e: Exception) {
//            Result.Error(e.message ?: "Network error")
//        }
//    }
//
//    override suspend fun markAsRead(chatId: String): Result<Unit> {
//        return try {
//            val response = chatApi.markAsRead(chatId)
//            if (response.success) {
//                chatDao.markChatAsRead(chatId)
//                Result.Success(Unit)
//            } else {
//                Result.Error(response.message ?: "Failed to mark as read")
//            }
//        } catch (e: Exception) {
//            Result.Error(e.message ?: "Network error")
//        }
//    }
//
//    override suspend fun getUnreadCount(): Flow<Int> = flow {
//        emit(chatDao.getTotalUnreadCount())
//    }
//
//    override suspend fun searchChats(query: String): List<Chat> {
//        return try {
//            val localResults = chatDao.searchChats(query)
//            localResults.map { it.toChat() }
//        } catch (e: Exception) {
//            mockChats.filter {
//                it.username.contains(query, ignoreCase = true)
//            }
//        }
//    }
//}
