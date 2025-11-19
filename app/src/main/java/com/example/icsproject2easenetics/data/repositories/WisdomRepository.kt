package com.example.icsproject2easenetics.data.repositories

import com.example.icsproject2easenetics.data.models.WisdomPost
import com.example.icsproject2easenetics.data.models.WisdomComment
import com.example.icsproject2easenetics.data.models.WisdomCategory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class WisdomRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val wisdomPostsCollection = firestore.collection("wisdom_posts")
    private val wisdomCommentsCollection = firestore.collection("wisdom_comments")

    // Real-time listener for wisdom posts
    fun getWisdomPostsRealTime(): Flow<List<WisdomPost>> = callbackFlow {
        println("🔄 WisdomRepository: Starting real-time listener for posts...")

        val listener = wisdomPostsCollection
            .whereEqualTo("isPublic", true)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ WisdomRepository: Real-time listener error - ${error.message}")
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    println("📡 WisdomRepository: Real-time update - ${querySnapshot.documents.size} posts")

                    // DEBUG: Log all document IDs and titles
                    querySnapshot.documents.forEach { document ->
                        val data = document.data ?: emptyMap<String, Any>()
                        val title = data["title"] as? String ?: "No Title"
                        val isPublic = data["isPublic"] as? Boolean ?: false
                        val publicField = data["public"] as? Boolean ?: false
                        println("📄 Document: ${document.id} - '$title' (isPublic: $isPublic, public: $publicField)")
                    }

                    val posts = querySnapshot.documents.map { document ->
                        val data = document.data ?: emptyMap<String, Any>()
                        WisdomPost(
                            postId = document.id,
                            userId = data["userId"] as? String ?: "",
                            userName = data["userName"] as? String ?: "",
                            userEmail = data["userEmail"] as? String ?: "",
                            title = data["title"] as? String ?: "",
                            content = data["content"] as? String ?: "",
                            category = when (data["category"] as? String) {
                                "ADVICE" -> WisdomCategory.ADVICE
                                "EXPERIENCE" -> WisdomCategory.EXPERIENCE
                                "HISTORICAL_EVENT" -> WisdomCategory.HISTORICAL_EVENT
                                "ENCOURAGEMENT" -> WisdomCategory.ENCOURAGEMENT
                                "LIFE_LESSON" -> WisdomCategory.LIFE_LESSON
                                else -> WisdomCategory.ADVICE
                            },
                            timestamp = data["timestamp"] as? com.google.firebase.Timestamp ?: com.google.firebase.Timestamp.now(),
                            likes = (data["likes"] as? List<String>) ?: emptyList(),
                            // FIX: Handle both "isPublic" and "public" field names
                            isPublic = (data["isPublic"] as? Boolean) ?: (data["public"] as? Boolean) ?: true
                        )
                    }

                    println("✅ WisdomRepository: Sending ${posts.size} posts to UI")
                    trySend(posts)
                }
            }

        awaitClose {
            println("🛑 WisdomRepository: Stopping real-time listener")
            listener.remove()
        }
    }

    // Real-time listener for comments
    fun getCommentsRealTime(postId: String): Flow<List<WisdomComment>> = callbackFlow {
        println("🔄 WisdomRepository: Starting real-time listener for comments on post $postId...")

        val listener = wisdomCommentsCollection
            .whereEqualTo("postId", postId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("❌ WisdomRepository: Comments real-time listener error - ${error.message}")
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    println("📡 WisdomRepository: Real-time comments update - ${querySnapshot.documents.size} comments")

                    // DEBUG: Log all comments
                    querySnapshot.documents.forEach { document ->
                        val data = document.data ?: emptyMap<String, Any>()
                        val userName = data["userName"] as? String ?: "Unknown"
                        val content = data["content"] as? String ?: "No content"
                        println("💬 Comment: '$content' by $userName")
                    }

                    val comments = querySnapshot.documents.map { document ->
                        val data = document.data ?: emptyMap<String, Any>()
                        WisdomComment(
                            commentId = document.id,
                            postId = data["postId"] as? String ?: "",
                            userId = data["userId"] as? String ?: "",
                            userName = data["userName"] as? String ?: "",
                            content = data["content"] as? String ?: "",
                            timestamp = data["timestamp"] as? com.google.firebase.Timestamp ?: com.google.firebase.Timestamp.now()
                        )
                    }

                    println("✅ WisdomRepository: Sending ${comments.size} comments to UI")
                    trySend(comments)
                }
            }

        awaitClose {
            println("🛑 WisdomRepository: Stopping comments real-time listener")
            listener.remove()
        }
    }

    suspend fun createWisdomPost(post: WisdomPost): Result<WisdomPost> {
        return try {
            println("🔄 WisdomRepository: Creating new post...")

            val postWithId = if (post.postId.isBlank()) {
                post.copy(postId = UUID.randomUUID().toString())
            } else {
                post
            }

            println("📝 WisdomRepository: Post data - User: ${postWithId.userName}, Title: ${postWithId.title}")

            // Convert to map with explicit field names to ensure consistency
            val postData = mapOf(
                "postId" to postWithId.postId,
                "userId" to postWithId.userId,
                "userName" to postWithId.userName,
                "userEmail" to postWithId.userEmail,
                "title" to postWithId.title,
                "content" to postWithId.content,
                "category" to postWithId.category.name,
                "timestamp" to postWithId.timestamp,
                "likes" to postWithId.likes,
                "isPublic" to postWithId.isPublic  // Ensure consistent field name
            )

            wisdomPostsCollection.document(postWithId.postId)
                .set(postData)
                .await()

            println("✅ WisdomRepository: Post created successfully with ID: ${postWithId.postId}")
            Result.success(postWithId)
        } catch (e: Exception) {
            println("❌ WisdomRepository: Error creating post - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getAllWisdomPosts(): List<WisdomPost> {
        return try {
            println("🔄 WisdomRepository: Fetching all wisdom posts...")

            val snapshot = wisdomPostsCollection
                .whereEqualTo("isPublic", true)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            println("📄 WisdomRepository: Firestore returned ${snapshot.documents.size} documents")

            val posts = snapshot.documents.map { document ->
                val data = document.data ?: emptyMap<String, Any>()
                println("📝 WisdomRepository: Processing document ${document.id}")

                WisdomPost(
                    postId = document.id,
                    userId = data["userId"] as? String ?: "",
                    userName = data["userName"] as? String ?: "",
                    userEmail = data["userEmail"] as? String ?: "",
                    title = data["title"] as? String ?: "",
                    content = data["content"] as? String ?: "",
                    category = when (data["category"] as? String) {
                        "ADVICE" -> WisdomCategory.ADVICE
                        "EXPERIENCE" -> WisdomCategory.EXPERIENCE
                        "HISTORICAL_EVENT" -> WisdomCategory.HISTORICAL_EVENT
                        "ENCOURAGEMENT" -> WisdomCategory.ENCOURAGEMENT
                        "LIFE_LESSON" -> WisdomCategory.LIFE_LESSON
                        else -> WisdomCategory.ADVICE
                    },
                    timestamp = data["timestamp"] as? com.google.firebase.Timestamp ?: com.google.firebase.Timestamp.now(),
                    likes = (data["likes"] as? List<String>) ?: emptyList(),
                    // FIX: Handle both "isPublic" and "public" field names
                    isPublic = (data["isPublic"] as? Boolean) ?: (data["public"] as? Boolean) ?: true
                )
            }

            println("✅ WisdomRepository: Successfully parsed ${posts.size} posts")
            posts
        } catch (e: Exception) {
            println("❌ WisdomRepository: Error fetching posts - ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getWisdomPostsByUser(userId: String): List<WisdomPost> {
        return try {
            val snapshot = wisdomPostsCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.map { document ->
                val data = document.data ?: emptyMap<String, Any>()
                WisdomPost(
                    postId = document.id,
                    userId = data["userId"] as? String ?: "",
                    userName = data["userName"] as? String ?: "",
                    userEmail = data["userEmail"] as? String ?: "",
                    title = data["title"] as? String ?: "",
                    content = data["content"] as? String ?: "",
                    category = when (data["category"] as? String) {
                        "ADVICE" -> WisdomCategory.ADVICE
                        "EXPERIENCE" -> WisdomCategory.EXPERIENCE
                        "HISTORICAL_EVENT" -> WisdomCategory.HISTORICAL_EVENT
                        "ENCOURAGEMENT" -> WisdomCategory.ENCOURAGEMENT
                        "LIFE_LESSON" -> WisdomCategory.LIFE_LESSON
                        else -> WisdomCategory.ADVICE
                    },
                    timestamp = data["timestamp"] as? com.google.firebase.Timestamp ?: com.google.firebase.Timestamp.now(),
                    likes = (data["likes"] as? List<String>) ?: emptyList(),
                    // FIX: Handle both "isPublic" and "public" field names
                    isPublic = (data["isPublic"] as? Boolean) ?: (data["public"] as? Boolean) ?: true
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun likePost(postId: String, userId: String): Result<Boolean> {
        return try {
            val postDoc = wisdomPostsCollection.document(postId).get().await()
            val data = postDoc.data ?: emptyMap<String, Any>()

            // Create WisdomPost from data to handle both field names
            val wisdomPost = WisdomPost(
                postId = postId,
                userId = data["userId"] as? String ?: "",
                userName = data["userName"] as? String ?: "",
                userEmail = data["userEmail"] as? String ?: "",
                title = data["title"] as? String ?: "",
                content = data["content"] as? String ?: "",
                category = when (data["category"] as? String) {
                    "ADVICE" -> WisdomCategory.ADVICE
                    "EXPERIENCE" -> WisdomCategory.EXPERIENCE
                    "HISTORICAL_EVENT" -> WisdomCategory.HISTORICAL_EVENT
                    "ENCOURAGEMENT" -> WisdomCategory.ENCOURAGEMENT
                    "LIFE_LESSON" -> WisdomCategory.LIFE_LESSON
                    else -> WisdomCategory.ADVICE
                },
                timestamp = data["timestamp"] as? com.google.firebase.Timestamp ?: com.google.firebase.Timestamp.now(),
                likes = (data["likes"] as? List<String>) ?: emptyList(),
                isPublic = (data["isPublic"] as? Boolean) ?: (data["public"] as? Boolean) ?: true
            )

            val updatedLikes = if (userId in wisdomPost.likes) {
                wisdomPost.likes - userId
            } else {
                wisdomPost.likes + userId
            }

            wisdomPostsCollection.document(postId)
                .update("likes", updatedLikes)
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addComment(comment: WisdomComment): Result<WisdomComment> {
        return try {
            val commentWithId = if (comment.commentId.isBlank()) {
                comment.copy(commentId = UUID.randomUUID().toString())
            } else {
                comment
            }

            wisdomCommentsCollection.document(commentWithId.commentId)
                .set(commentWithId)
                .await()

            println("✅ WisdomRepository: Comment added successfully")
            Result.success(commentWithId)
        } catch (e: Exception) {
            println("❌ WisdomRepository: Error adding comment - ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getCommentsForPost(postId: String): List<WisdomComment> {
        return try {
            val snapshot = wisdomCommentsCollection
                .whereEqualTo("postId", postId)
                .orderBy("timestamp")
                .get()
                .await()

            snapshot.documents.map { document ->
                val data = document.data ?: emptyMap<String, Any>()
                WisdomComment(
                    commentId = document.id,
                    postId = data["postId"] as? String ?: "",
                    userId = data["userId"] as? String ?: "",
                    userName = data["userName"] as? String ?: "",
                    content = data["content"] as? String ?: "",
                    timestamp = data["timestamp"] as? com.google.firebase.Timestamp ?: com.google.firebase.Timestamp.now()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deletePost(postId: String, userId: String): Result<Boolean> {
        return try {
            val postDoc = wisdomPostsCollection.document(postId).get().await()
            val data = postDoc.data ?: emptyMap<String, Any>()

            val postUserId = data["userId"] as? String ?: ""

            if (postUserId == userId) {
                wisdomPostsCollection.document(postId).delete().await()

                // Also delete associated comments
                val commentsSnapshot = wisdomCommentsCollection
                    .whereEqualTo("postId", postId)
                    .get()
                    .await()

                commentsSnapshot.documents.forEach { document ->
                    wisdomCommentsCollection.document(document.id).delete().await()
                }

                Result.success(true)
            } else {
                Result.failure(Exception("You can only delete your own posts"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}