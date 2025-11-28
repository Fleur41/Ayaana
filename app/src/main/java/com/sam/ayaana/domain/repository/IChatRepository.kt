package com.sam.ayaana.domain.repository

import com.sam.ayaana.Utils.Result
import com.sam.ayaana.Utils.toChat
import com.sam.ayaana.data.local.dao.ChatDao
import com.sam.ayaana.data.remote.api.ChatApi
import com.sam.ayaana.domain.model.Chat
import com.sam.ayaana.domain.model.Message
import com.sam.ayaana.domain.model.MessageStatus
import com.sam.ayaana.domain.model.MessageType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
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
    suspend fun sendVoiceMessage(chatId: String, audioFile: File): Result<Unit>
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
            messageStatus = MessageStatus.SEEN
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
            messageStatus = MessageStatus.SEEN
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
            messageStatus = MessageStatus.SEEN
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
            messageStatus = MessageStatus.SEEN
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
            messageStatus = MessageStatus.DELIVERED
        ),
        Chat(
            id = "6",
            userId = "user6",
            username = "Cathy Waweru",
            profileImage = "https://picsum.photos/id/105/200/300",
            lastMessage = "Your note @_Lil~Timid. 💤 ...",
            timestamp = Date(System.currentTimeMillis() - 518400000),
            unreadCount = 0,
            isOnline = true,
            messageStatus = com.sam.ayaana.domain.model.MessageStatus.SEEN
        ),
        Chat(
            id = "7",
            userId = "user7",
            username = "_Lil~Timid",
            profileImage = "https://picsum.photos/id/106/200/300",
            lastMessage = "💤 ...",
            timestamp = Date(System.currentTimeMillis() - 604800000),
            unreadCount = 2,
            isOnline = false,
            messageStatus = com.sam.ayaana.domain.model.MessageStatus.DELIVERED
        ),
        Chat(
            id = "8",
            userId = "user8",
            username = "G A D G A D O",
            profileImage = "https://picsum.photos/id/107/200/300",
            lastMessage = "Draft: O",
            timestamp = Date(System.currentTimeMillis() - 691200000),
            unreadCount = 0,
            isOnline = false,
            messageStatus = com.sam.ayaana.domain.model.MessageStatus.SEEN
        ),
        Chat(
            id = "9",
            userId = "user9",
            username = "Remote Work Kenya",
            profileImage = "https://picsum.photos/id/108/200/300",
            lastMessage = "New job posted",
            timestamp = Date(System.currentTimeMillis() - 777600000),
            unreadCount = 3,
            isOnline = false,
            messageStatus = com.sam.ayaana.domain.model.MessageStatus.DELIVERED
        ),
        Chat(
            id = "10",
            userId = "user10",
            username = "Tech Community KE",
            profileImage = "https://picsum.photos/id/109/200/300",
            lastMessage = "Meetup this Saturday",
            timestamp = Date(System.currentTimeMillis() - 864000000),
            unreadCount = 0,
            isOnline = true,
            messageStatus = MessageStatus.SEEN
        )

    )

    private val mockMessages = mapOf(
        "1" to listOf( // Shivan Wamuyu
            Message(
                id = "101",
                chatId = "1",
                senderId = "user1",
                receiverId = "currentUser",
                content = "Hey, are we still meeting tomorrow?",
                timestamp = Date(System.currentTimeMillis() - 86400000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.SEEN
            ),
            Message(
                id = "102",
                chatId = "1",
                senderId = "currentUser",
                receiverId = "user1",
                content = "Yes, 2 PM at the usual spot!",
                timestamp = Date(System.currentTimeMillis() - 43200000),
                messageType = MessageType.TEXT,
                isSentByMe = true,
                messageStatus = MessageStatus.SEEN
            )
        ),
        "2" to listOf( // Juma Allan 💧
            Message(
                id = "201",
                chatId = "2",
                senderId = "user2",
                receiverId = "currentUser",
                content = "Bro, unaeza nisaidia na hiyo project?",
                timestamp = Date(System.currentTimeMillis() - 172800000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.SEEN
            )
        ),
        "3" to listOf( // Reuben Apollo
            Message(
                id = "301",
                chatId = "3",
                senderId = "currentUser",
                receiverId = "user3",
                content = "When are you coming to Nairobi?",
                timestamp = Date(System.currentTimeMillis() - 259200000),
                messageType = MessageType.TEXT,
                isSentByMe = true,
                messageStatus = MessageStatus.SEEN
            ),
            Message(
                id = "302",
                chatId = "3",
                senderId = "user3",
                receiverId = "currentUser",
                content = "Next month, tutaongea",
                timestamp = Date(System.currentTimeMillis() - 216000000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.SEEN
            )
        ),
        "4" to listOf( // Silvester mutiso - existing conversation
            Message(
                id = "401",
                chatId = "4",
                senderId = "user4",
                receiverId = "currentUser",
                content = "Mzee mzima Silva hambari gani... Mie Sina maneno kaka",
                timestamp = Date(System.currentTimeMillis() - 86400000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.SEEN
            ),
            Message(
                id = "402",
                chatId = "4",
                senderId = "currentUser",
                receiverId = "user4",
                content = "Ulipotelea wapi mkuu kihunguro ulihama?",
                timestamp = Date(System.currentTimeMillis() - 43200000),
                messageType = MessageType.TEXT,
                isSentByMe = true,
                messageStatus = MessageStatus.SEEN
            ),
            Message(
                id = "403",
                chatId = "4",
                senderId = "user4",
                receiverId = "currentUser",
                content = "Niko baba ni venye Huwa sitokei sana",
                timestamp = Date(System.currentTimeMillis() - 21600000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.SEEN
            ),
            Message(
                id = "404",
                chatId = "4",
                senderId = "currentUser",
                receiverId = "user4",
                content = "Ooh poa\nDouble tap to ▼\n\nLeo nitatokea hapo Kalabash utanipata hapo bz",
                timestamp = Date(System.currentTimeMillis() - 10800000),
                messageType = MessageType.TEXT,
                isSentByMe = true,
                messageStatus = MessageStatus.SEEN
            )
        ),
        "5" to listOf( // Finding Remote Jobs
            Message(
                id = "501",
                chatId = "5",
                senderId = "user5",
                receiverId = "currentUser",
                content = "New remote developer position available!",
                timestamp = Date(System.currentTimeMillis() - 432000000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.DELIVERED
            ),
            Message(
                id = "502",
                chatId = "5",
                senderId = "currentUser",
                receiverId = "user5",
                content = "Thanks for the update!",
                timestamp = Date(System.currentTimeMillis() - 345600000),
                messageType = MessageType.TEXT,
                isSentByMe = true,
                messageStatus = MessageStatus.DELIVERED
            )
        ),
        "6" to listOf( // Cathy Waweru
            Message(
                id = "601",
                chatId = "6",
                senderId = "user6",
                receiverId = "currentUser",
                content = "Your note @_Lil~Timid. 💤 ...",
                timestamp = Date(System.currentTimeMillis() - 518400000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.SEEN
            ),
            Message(
                id = "602",
                chatId = "6",
                senderId = "currentUser",
                receiverId = "user6",
                content = "Got it, thanks Cathy!",
                timestamp = Date(System.currentTimeMillis() - 475200000),
                messageType = MessageType.TEXT,
                isSentByMe = true,
                messageStatus = MessageStatus.SEEN
            )
        ),
        "7" to listOf( // _Lil~Timid
            Message(
                id = "701",
                chatId = "7",
                senderId = "user7",
                receiverId = "currentUser",
                content = "💤 Good night fam...",
                timestamp = Date(System.currentTimeMillis() - 604800000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.DELIVERED
            ),
            Message(
                id = "702",
                chatId = "7",
                senderId = "currentUser",
                receiverId = "user7",
                content = "Sleep well! 🌙",
                timestamp = Date(System.currentTimeMillis() - 561600000),
                messageType = MessageType.TEXT,
                isSentByMe = true,
                messageStatus = MessageStatus.DELIVERED
            )
        ),
        "8" to listOf( // G A D G A D O
            Message(
                id = "801",
                chatId = "8",
                senderId = "user8",
                receiverId = "currentUser",
                content = "Draft: New music coming soon...",
                timestamp = Date(System.currentTimeMillis() - 691200000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.SEEN
            )
        ),
        "9" to listOf( // Remote Work Kenya
            Message(
                id = "901",
                chatId = "9",
                senderId = "user9",
                receiverId = "currentUser",
                content = "New job posted: Senior Android Developer",
                timestamp = Date(System.currentTimeMillis() - 777600000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.DELIVERED
            ),
            Message(
                id = "902",
                chatId = "9",
                senderId = "user9",
                receiverId = "currentUser",
                content = "Remote position with flexible hours",
                timestamp = Date(System.currentTimeMillis() - 734400000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.DELIVERED
            )
        ),
        "10" to listOf( // Tech Community KE
            Message(
                id = "1001",
                chatId = "10",
                senderId = "user10",
                receiverId = "currentUser",
                content = "Meetup this Saturday at iHub!",
                timestamp = Date(System.currentTimeMillis() - 864000000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.SEEN
            ),
            Message(
                id = "1002",
                chatId = "10",
                senderId = "currentUser",
                receiverId = "user10",
                content = "I'll be there! What's the topic?",
                timestamp = Date(System.currentTimeMillis() - 820800000),
                messageType = MessageType.TEXT,
                isSentByMe = true,
                messageStatus = MessageStatus.SEEN
            ),
            Message(
                id = "1003",
                chatId = "10",
                senderId = "user10",
                receiverId = "currentUser",
                content = "Jetpack Compose and Modern Android Development",
                timestamp = Date(System.currentTimeMillis() - 777600000),
                messageType = MessageType.TEXT,
                isSentByMe = false,
                messageStatus = MessageStatus.SEEN
            )
        )
    )
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

    // FIXED: Now properly emitting Success with type parameters and handling unused parameters
    override suspend fun getChats(): Flow<Result<List<Chat>>> = flow {

        emit(Result.Loading)

        // SIMPLE TEST ONLY - remove all other emissions
        delay(500) // Show loading for 1 second
        emit(Result.Success<List<Chat>>(mockChats))
    }
//    override suspend fun getChats(): Flow<Result<List<Chat>>> = flow {
//        try {
//            // Try to get from local database first
//            try {
//                val localChats = chatDao.getChats()
//                localChats.collect { chats ->
//                    if (chats.isNotEmpty()) {
//                        // FIXED: Use proper type parameter
//                        emit(Result.Success<List<Chat>>(chats.map { it.toChat() }))
//                        return@collect
//                    }
//                }
//            } catch (e: Exception) {
//                // FIXED: Parameter "e" is now used
//                println("Local database error: ${e.message}")
//                // Continue to mock data
//            }
//
//            // Try API call
//            try {
//                val response = chatApi.getChats()
//                if (response.success && response.data != null) {
//                    // Convert and save to local database
//                    val chatEntities = response.data.map { it.toChatEntity() }
//                    chatDao.insertChats(chatEntities)
//                    // FIXED: Use proper type parameter
//                    emit(Result.Success<List<Chat>>(response.data.map { it.toChat() }))
//                } else {
//                    // Fallback to mock data - FIXED: Use proper type parameter
//                    emit(Result.Success<List<Chat>>(mockChats))
//                }
//            } catch (e: Exception) {
//                // FIXED: Parameter "e" is now used
//                println("API error: ${e.message}")
//                // Fallback to mock data if API fails - FIXED: Use proper type parameter
//                emit(Result.Success<List<Chat>>(mockChats))
//            }
//        } catch (e: Exception) {
//            // FIXED: Parameter "e" is now used
//            println("General error: ${e.message}")
//            // Final fallback to mock data - FIXED: Use proper type parameter
//            emit(Result.Success<List<Chat>>(mockChats))
//        }
//    }

    override suspend fun getMessages(chatId: String): Flow<Result<List<Message>>> = flow {
        emit(Result.Loading)

        delay(300) // 0.5 second delay
        val messages = mockMessages[chatId] ?: emptyList()
        emit(Result.Success<List<Message>>(messages))
    }
//    override suspend fun getMessages(chatId: String): Flow<Result<List<Message>>> = flow {
//        try {
//            // Try to get from local database first
//            try {
//                val localMessages = chatDao.getMessages(chatId)
//                localMessages.collect { messages ->
//                    if (messages.isNotEmpty()) {
//                        // FIXED: Use proper type parameter
//                        emit(Result.Success<List<Message>>(messages.map { it.toMessage() }))
//                        return@collect
//                    }
//                }
//            } catch (e: Exception) {
//                // FIXED: Parameter "e" is now used
//                println("Local messages error: ${e.message}")
//                // Continue to mock data
//            }
//
//            // Try API call
//            try {
//                val response = chatApi.getMessages(chatId)
//                if (response.success && response.data != null) {
//                    val messageEntities = response.data.map { it.toMessageEntity() }
//                    chatDao.insertMessages(messageEntities)
//                    // FIXED: Use proper type parameter
//                    emit(Result.Success<List<Message>>(response.data.map { it.toMessage() }))
//                } else {
//                    // Fallback to mock data - FIXED: Use proper type parameter
//                    emit(Result.Success<List<Message>>(mockMessages[chatId] ?: emptyList()))
//                }
//            } catch (e: Exception) {
//                // FIXED: Parameter "e" is now used
//                println("API messages error: ${e.message}")
//                // Fallback to mock data if API fails - FIXED: Use proper type parameter
//                emit(Result.Success<List<Message>>(mockMessages[chatId] ?: emptyList()))
//            }
//        } catch (e: Exception) {
//            // FIXED: Parameter "e" is now used
//            println("General messages error: ${e.message}")
//            // Final fallback to mock data - FIXED: Use proper type parameter
//            emit(Result.Success<List<Message>>(mockMessages[chatId] ?: emptyList()))
//        }
//    }

    override suspend fun sendTextMessage(chatId: String, content: String): Result<Unit> {
        return try {
            val response = chatApi.sendMessage(chatId, com.sam.ayaana.data.remote.model.request.MessageRequest(chatId, content, "TEXT"))
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Failed to send message")
            }
        } catch (e: Exception) {
            // FIXED: Parameter "e" is now used
            println("Send message error: ${e.message}")
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
            // FIXED: Parameter "e" is now used
            println("Send media error: ${e.message}")
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
            // FIXED: Parameter "e" is now used
            println("Mark as read error: ${e.message}")
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getUnreadCount(): Flow<Int> = flow {
        try {
            emit(chatDao.getTotalUnreadCount())
        } catch (e: Exception) {
            // FIXED: Parameter "e" is now used
            println("Unread count error: ${e.message}")
            emit(mockChats.sumOf { it.unreadCount })
        }
    }

    override suspend fun searchChats(query: String): List<Chat> {
        return try {
            val localResults = chatDao.searchChats(query)
            localResults.map { it.toChat() }
        } catch (e: Exception) {
            // FIXED: Parameter "e" is now used
            println("Search chats error: ${e.message}")
            mockChats.filter {
                it.username.contains(query, ignoreCase = true)
            }
        }
    }

    override suspend fun sendVoiceMessage(chatId: String, audioFile: File): Result<Unit> {
        return try {
            // For mock implementation, we'll simulate sending the voice message
            val mockVoiceMessage = Message(
                id = System.currentTimeMillis().toString(),
                chatId = chatId,
                senderId = "currentUser",
                receiverId = "otherUser",
                content = "Voice message",
                timestamp = Date(),
                messageType = MessageType.VOICE,
                mediaUrl = null, // Use mediaUrl instead of filePath for now
                filePath = audioFile.absolutePath, // This should work now
                isSentByMe = true,
                messageStatus = MessageStatus.SENT
            )

            // Simulate network delay
            delay(1000)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to send voice message")
        }
    }
//    override suspend fun sendVoiceMessage(
//        chatId: String,
//        audioFile: File
//    ): Result<Unit> {
//        return try {
//            // For mock implementation, we'll simulate sending the voice message
//            // In real implementation, you would upload the file to your server
//            val mockVoiceMessage = Message(
//                id = System.currentTimeMillis().toString(),
//                chatId = chatId,
//                senderId = "currentUser",
//                receiverId = "otherUser",
//                content = "Voice message",
//                timestamp = Date(),
//                messageType = MessageType.VOICE,
//                filePath = audioFile.absolutePath,
//                isSentByMe = true,
//               messageStatus = MessageStatus.SENT
//            )
//
//            // Simulate network delay
//           delay(1000)
//
//            Result.Success(Unit)
//        } catch (e: Exception) {
//           Result.Error(e.message ?: "Failed to send voice message")
//        }
//    }
}




