package com.sam.ayaana.Utils

import kotlin.collections.filter

// Make SearchableItem a public interface in the same package


object AISearchUtils {
    interface SearchableItem {
        val title: String
        val description: String
        val username: String
        val tags: List<String>
        val category: String
    }

    // Semantic mapping for AI-powered search
    val semanticMap = mapOf(
        "happy" to listOf("joy", "fun", "smile", "laugh", "celebration", "excited", "cheerful"),
        "sad" to listOf("cry", "emotional", "heartbreak", "melancholy", "depressed", "tearful"),
        "funny" to listOf("comedy", "joke", "laugh", "humor", "hilarious", "comic", "entertaining"),
        "amazing" to listOf("awesome", "wow", "incredible", "fantastic", "wonderful", "astonishing"),
        "beautiful" to listOf("pretty", "gorgeous", "stunning", "lovely", "attractive", "charming"),
        "exciting" to listOf("thrilling", "adventurous", "exhilarating", "action", "intense"),
        "relaxing" to listOf("calm", "peaceful", "serene", "tranquil", "soothing", "chill"),
        "educational" to listOf("learn", "teach", "tutorial", "informative", "knowledge", "study"),
        "inspiring" to listOf("motivational", "uplifting", "encouraging", "empowering", "positive"),
        "scary" to listOf("frightening", "horror", "terrifying", "spooky", "creepy", "fear")
    )

    // Category mapping for better search
    val categoryMap = mapOf(
        "car" to listOf("vehicle", "automobile", "transport", "drive", "road", "motor"),
        "lorry" to listOf("truck", "vehicle", "transport", "heavy", "cargo", "freight", "haulage"),
        "food" to listOf("cooking", "recipe", "meal", "restaurant", "delicious", "tasty", "eat", "dinner"),
        "travel" to listOf("vacation", "tourism", "adventure", "explore", "journey", "trip", "destination"),
        "fashion" to listOf("clothing", "style", "outfit", "design", "model", "runway", "trend"),
        "music" to listOf("song", "artist", "concert", "performance", "band", "melody", "rhythm"),
        "sports" to listOf("game", "athlete", "competition", "fitness", "exercise", "training", "match"),
        "comedy" to listOf("funny", "joke", "humor", "entertainment", "laughter", "standup"),
        "nature" to listOf("environment", "wildlife", "forest", "mountain", "beach", "ocean", "animal"),
        "technology" to listOf("gadget", "device", "electronic", "innovation", "digital", "tech", "software")
    )

    // Check if query matches any semantic meaning
    fun matchesSemanticMeaning(query: String, description: String, tags: List<String>): Boolean {
        val normalizedQuery = query.lowercase().trim()
        val normalizedDesc = description.lowercase()

        return semanticMap.entries.any { (key, synonyms) ->
            val queryContainsKeyOrSynonym = normalizedQuery == key ||
                    synonyms.any { synonym -> normalizedQuery.contains(synonym) }

            val contentContainsKey = normalizedDesc.contains(key) ||
                    tags.any { tag -> tag.lowercase().contains(key) }

            queryContainsKeyOrSynonym && contentContainsKey
        }
    }

    // Check if query matches a category
    fun matchesCategory(query: String, category: String): Boolean {
        val normalizedQuery = query.lowercase().trim()
        val normalizedCategory = category.lowercase()

        // Direct match
        if (normalizedQuery == normalizedCategory) return true

        // Check category map for synonyms
        return categoryMap.entries.any { (key, synonyms) ->
            (normalizedQuery == key || synonyms.any { synonym -> normalizedQuery.contains(synonym) }) &&
                    normalizedCategory == key
        }
    }

    // AI-powered search function
    fun aiSearch(query: String, items: List<SearchableItem>): List<SearchableItem> {
        val normalizedQuery = query.lowercase().trim()

        return items.filter { item ->
            // 1. Direct text match (title, description, username)
            item.title.lowercase().contains(normalizedQuery) ||
                    item.description.lowercase().contains(normalizedQuery) ||
                    item.username.lowercase().contains(normalizedQuery) ||

                    // 2. Tag match
                    item.tags.any { tag -> tag.lowercase().contains(normalizedQuery) } ||

                    // 3. Category match
                    matchesCategory(normalizedQuery, item.category) ||

                    // 4. Semantic meaning match
                    matchesSemanticMeaning(normalizedQuery, item.description, item.tags) ||

                    // 5. Partial word match (for better UX)
                    normalizedQuery.split(" ").any { word ->
                        word.length > 2 && (
                                item.title.lowercase().contains(word) ||
                                        item.description.lowercase().contains(word) ||
                                        item.tags.any { tag -> tag.lowercase().contains(word) }
                                )
                    }
        }.sortedByDescending { item ->
            // Ranking algorithm (simple version)
            var score = 0
            if (item.title.lowercase().contains(normalizedQuery)) score += 3
            if (item.description.lowercase().contains(normalizedQuery)) score += 2
            if (item.tags.any { it.lowercase().contains(normalizedQuery) }) score += 4
            if (matchesCategory(normalizedQuery, item.category)) score += 3
            if (matchesSemanticMeaning(normalizedQuery, item.description, item.tags)) score += 2
            -score // For descending sort
        }
    }
}