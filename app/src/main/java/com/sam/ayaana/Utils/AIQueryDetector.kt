package com.sam.ayaana.Utils


object AIAssistantDetector {

    fun shouldNavigateToAi(query: String): Boolean {
        val lowerQuery = query.lowercase().trim()

        if (lowerQuery.isEmpty()) return false

        val aiKeywords = listOf(
            "how to", "what is", "why", "when", "where", "who", "explain",
            "help with", "advice", "suggestion", "tip", "recommendation",
            "caption", "hashtag", "social media", "instagram", "post",
            "story", "reel", "content", "engagement", "algorithm",
            "ai", "assistant", "help", "guide", "tutorial", "write",
            "create", "generate", "improve", "better", "best"
        )

        val aiCommands = listOf(
            "help me", "tell me", "show me", "give me", "suggest",
            "recommend", "advise", "guide me", "explain to me"
        )

        val hasQuestionMark = lowerQuery.contains("?")
        val hasAiKeyword = aiKeywords.any { lowerQuery.contains(it) }
        val hasAiCommand = aiCommands.any { lowerQuery.startsWith(it) }

        return hasQuestionMark || hasAiKeyword || hasAiCommand || lowerQuery.length > 20
    }

    // You can add more helper functions
    fun isUserSearch(query: String): Boolean {
        return !shouldNavigateToAi(query)
    }

    fun getQueryType(query: String): QueryType {
        return if (shouldNavigateToAi(query)) QueryType.AI_ASSISTANT else QueryType.USER_SEARCH
    }
}

enum class QueryType {
    AI_ASSISTANT,
    USER_SEARCH
}