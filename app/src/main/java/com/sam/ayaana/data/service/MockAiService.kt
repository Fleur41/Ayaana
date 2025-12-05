package com.sam.ayaana.data.service

import com.sam.ayaana.domain.model.AiCommand

class MockAiService {

    fun detectCommand(userMessage: String): AiCommand {
        val message = userMessage.lowercase()

        return when {
            message.contains("hello") || message.contains("hi") || message.contains("hey") -> AiCommand.GREETING
            message.contains("help") -> AiCommand.HELP
            message.contains("what is") && message.contains("app") -> AiCommand.ABOUT_APP
            message.contains("weather") -> AiCommand.WEATHER
            message.contains("joke") || message.contains("funny") -> AiCommand.JOKE
            else -> AiCommand.UNKNOWN
        }
    }

    fun generateResponse(command: AiCommand): String {
        return when (command) {
            AiCommand.GREETING -> listOf(
                "Hello! I'm Ayaana AI. How can I help you today? 👋",
                "Hi there! Ready to assist with your social media journey!",
                "Hey! What's on your mind today?"
            ).random()

            AiCommand.HELP -> listOf(
                "I can help you with:\n• Writing captions\n• Suggesting hashtags\n• Answering questions\n• Giving tips\nJust ask!",
                "Here's what I can do:\n- Generate captions for your posts\n- Suggest trending hashtags\n- Answer questions about the app\n- Provide social media tips"
            ).random()

            AiCommand.ABOUT_APP -> "Ayaana is a social media app for connecting with friends, sharing moments, and discovering content. I'm here to make your experience better!"

            AiCommand.WEATHER -> "I'm a social media AI, but I suggest checking your weather app for accurate forecasts! ☀️⛈️"

            AiCommand.JOKE -> listOf(
                "Why don't programmers like nature? It has too many bugs! 🐛",
                "Why did the Instagrammer cross the road? To get to the filter on the other side! 📸",
                "What's a social media manager's favorite drink? Follow-er-ccino! ☕"
            ).random()

            AiCommand.UNKNOWN -> listOf(
                "I'm still learning! Try asking me about:\n• Writing a caption\n• Hashtag suggestions\n• App features",
                "That's interesting! As an AI assistant, I'm best at helping with social media content. Try asking for help with captions or hashtags!",
                "I'm here to help with your social media journey. Ask me about captions, hashtags, or app tips!"
            ).random()
        }
    }

    fun processMessage(userMessage: String): String {
        val command = detectCommand(userMessage)
        return generateResponse(command)
    }
}
