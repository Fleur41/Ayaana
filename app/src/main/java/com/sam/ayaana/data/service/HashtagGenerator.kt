package com.sam.ayaana.data.service

import com.sam.ayaana.domain.model.Hashtag

class HashtagGenerator {

    fun generateHashtagsFromText(text: String): List<Hashtag> {
        val keywords = extractKeywords(text)
        val hashtags = mutableListOf<Hashtag>()

        keywords.forEach { keyword ->
            hashtags.addAll(getHashtagsForKeyword(keyword))
        }

        // Add trending hashtags
        hashtags.addAll(getTrendingHashtags())

        // Remove duplicates and limit
        return hashtags.distinctBy { it.tag }.take(15)
    }

    private fun extractKeywords(text: String): List<String> {
        val words = text.lowercase().split(" ", ",", ".", "!", "?")
        return words.filter { it.length > 3 } // Filter out short words
    }

    private fun getHashtagsForKeyword(keyword: String): List<Hashtag> {
        val keywordMap = mapOf(
            "beach" to listOf("BeachLife", "OceanView", "Sunset", "Vacation", "Summer"),
            "food" to listOf("Foodie", "Yummy", "Delicious", "FoodPorn", "InstaFood"),
            "travel" to listOf("Travel", "Wanderlust", "Adventure", "Explore", "TravelGram"),
            "fashion" to listOf("OOTD", "Fashion", "Style", "Outfit", "Fashionista"),
            "fitness" to listOf("Fitness", "Workout", "Gym", "Healthy", "FitFam"),
            "art" to listOf("Art", "Artist", "Creative", "Design", "Artwork"),
            "love" to listOf("Love", "Relationship", "Couple", "Romance", "Together"),
            "nature" to listOf("Nature", "Landscape", "Scenic", "Outdoors", "Natural"),
            "party" to listOf("Party", "Fun", "Celebration", "NightOut", "GoodTimes"),
            "family" to listOf("Family", "FamilyTime", "Moments", "Togetherness", "Home"),
            "photo" to listOf("Photography", "PhotoOfTheDay", "InstaPhoto", "ShotOnPhone"),
            "sunset" to listOf("Sunset", "GoldenHour", "Sky", "Evening"),
            "friends" to listOf("Friends", "Squad", "Besties", "Memories"),
            "music" to listOf("Music", "Playlist", "Vibes", "Tunes"),
            "city" to listOf("CityLife", "Urban", "Downtown", "Metropolis")
        )

        return keywordMap[keyword]?.map {
            Hashtag("#$it", relevanceScore = 0.8, category = keyword)
        } ?: emptyList()
    }

    fun getTrendingHashtags(): List<Hashtag> {
        return listOf(
            Hashtag("#InstaGood", 0.9, "Trending"),
            Hashtag("#PhotoOfTheDay", 0.8, "Trending"),
            Hashtag("#Trending", 0.7, "Trending"),
            Hashtag("#LikeForLike", 0.6, "Community"),
            Hashtag("#FollowForFollow", 0.6, "Community"),
            Hashtag("#Love", 0.8, "Popular"),
            Hashtag("#Beautiful", 0.7, "Popular"),
            Hashtag("#Happy", 0.7, "Popular")
        )
    }
}
//import com.sam.ayaana.domain.model.Hashtag
//
//class HashtagGeneratorService {
//
//    // Mock AI - Phase 1: Simple keyword matching
//    fun generateHashtagsFromText(text: String): List<Hashtag> {
//        val keywords = extractKeywords(text)
//        val hashtags = mutableListOf<Hashtag>()
//
//        keywords.forEach { keyword ->
//            hashtags.addAll(getHashtagsForKeyword(keyword))
//        }
//
//        // Add trending hashtags
//        hashtags.addAll(getTrendingHashtags())
//
//        // Remove duplicates and limit
//        return hashtags.distinctBy { it.tag }.take(15)
//    }
//
//    private fun extractKeywords(text: String): List<String> {
//        val words = text.lowercase().split(" ", ",", ".", "!", "?")
//        return words.filter { it.length > 3 } // Filter out short words
//    }
//
//    private fun getHashtagsForKeyword(keyword: String): List<Hashtag> {
//        val keywordMap = mapOf(
//            // Categories from Instagram trends
//            "beach" to listOf("BeachLife", "OceanView", "Sunset", "Vacation", "Summer"),
//            "food" to listOf("Foodie", "Yummy", "Delicious", "FoodPorn", "InstaFood"),
//            "travel" to listOf("Travel", "Wanderlust", "Adventure", "Explore", "TravelGram"),
//            "fashion" to listOf("OOTD", "Fashion", "Style", "Outfit", "Fashionista"),
//            "fitness" to listOf("Fitness", "Workout", "Gym", "Healthy", "FitFam"),
//            "art" to listOf("Art", "Artist", "Creative", "Design", "Artwork"),
//            "love" to listOf("Love", "Relationship", "Couple", "Romance", "Together"),
//            "nature" to listOf("Nature", "Landscape", "Scenic", "Outdoors", "Natural"),
//            "party" to listOf("Party", "Fun", "Celebration", "NightOut", "GoodTimes"),
//            "family" to listOf("Family", "FamilyTime", "Moments", "Togetherness", "Home")
//        )
//
//        return keywordMap[keyword]?.map {
//            Hashtag("#$it", relevanceScore = 0.8, category = keyword)
//        } ?: emptyList()
//    }
//
//    private fun getTrendingHashtags(): List<Hashtag> {
//        // Mock trending hashtags (Phase 1)
//        return listOf(
//            Hashtag("#InstaGood", 0.9, "Trending"),
//            Hashtag("#PhotoOfTheDay", 0.8, "Trending"),
//            Hashtag("#Trending", 0.7, "Trending"),
//            Hashtag("#LikeForLike", 0.6, "Community"),
//            Hashtag("#FollowForFollow", 0.6, "Community")
//        )
//    }
//
//    // Phase 2: Can integrate with real AI API here
//    suspend fun generateWithAI(text: String): List<Hashtag> {
//        // TODO: Integrate with Gemini API or similar
//        return generateHashtagsFromText(text) // Fallback to mock for now
//    }
//}