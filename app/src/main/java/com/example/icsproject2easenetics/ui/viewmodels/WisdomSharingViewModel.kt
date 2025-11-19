package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.WisdomPost
import com.example.icsproject2easenetics.data.models.WisdomComment
import com.example.icsproject2easenetics.data.models.WisdomCategory
import com.example.icsproject2easenetics.data.repositories.WisdomRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class WisdomSharingViewModel : ViewModel() {
    private val wisdomRepository = WisdomRepository()

    private val _wisdomPosts = MutableStateFlow<List<WisdomPost>>(emptyList())
    val wisdomPosts: StateFlow<List<WisdomPost>> = _wisdomPosts.asStateFlow()

    private val _selectedPostComments = MutableStateFlow<List<WisdomComment>>(emptyList())
    val selectedPostComments: StateFlow<List<WisdomComment>> = _selectedPostComments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _postSuccess = MutableStateFlow(false)
    val postSuccess: StateFlow<Boolean> = _postSuccess.asStateFlow()

    // Track active listeners
    private var postsJob: Job? = null
    private var commentsJob: Job? = null

    init {
        startRealTimePostsListener()
    }

    private fun startRealTimePostsListener() {
        // Cancel existing listener if any
        postsJob?.cancel()

        postsJob = viewModelScope.launch {
            println("🚀 WisdomSharingViewModel: Starting REAL-TIME posts listener...")
            wisdomRepository.getWisdomPostsRealTime()
                .onEach { posts ->
                    println("📡 WisdomSharingViewModel: Real-time update - ${posts.size} posts received")
                    _wisdomPosts.value = posts
                }
                .collect()
        }
    }

    fun startCommentsListener(postId: String) {
        // Cancel existing comments listener if any
        commentsJob?.cancel()

        commentsJob = viewModelScope.launch {
            println("🚀 WisdomSharingViewModel: Starting REAL-TIME comments listener for post $postId")
            wisdomRepository.getCommentsRealTime(postId)
                .onEach { comments ->
                    println("📡 WisdomSharingViewModel: Real-time comments - ${comments.size} comments received")
                    _selectedPostComments.value = comments
                }
                .collect()
        }
    }

    fun stopCommentsListener() {
        println("🛑 WisdomSharingViewModel: Stopping comments listener")
        commentsJob?.cancel()
        _selectedPostComments.value = emptyList()
    }

    fun refreshPosts() {
        println("🔄 WisdomSharingViewModel: Manual refresh requested")
        startRealTimePostsListener()
    }

    // Keep the old load function for backward compatibility if needed
    fun loadAllWisdomPosts() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                println("🔄 WisdomSharingViewModel: Manually loading posts...")
                val posts = wisdomRepository.getAllWisdomPosts()
                println("✅ WisdomSharingViewModel: Manually loaded ${posts.size} posts")
                _wisdomPosts.value = posts
            } catch (e: Exception) {
                val errorMsg = "Failed to load wisdom posts: ${e.message}"
                println("❌ WisdomSharingViewModel: $errorMsg")
                _errorMessage.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserPosts(userId: String) {
        viewModelScope.launch {
            try {
                val posts = wisdomRepository.getWisdomPostsByUser(userId)
                // You might want to store this in a separate state if needed
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load your posts: ${e.message}"
            }
        }
    }

    fun createWisdomPost(
        userId: String,
        userName: String,
        userEmail: String,
        title: String,
        content: String,
        category: WisdomCategory
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        _postSuccess.value = false

        viewModelScope.launch {
            try {
                println("🔄 WisdomSharingViewModel: Creating post for user $userName")

                val post = WisdomPost(
                    userId = userId,
                    userName = userName,
                    userEmail = userEmail,
                    title = title,
                    content = content,
                    category = category,
                    isPublic = true  // Make sure this is set correctly
                )

                val result = wisdomRepository.createWisdomPost(post)

                if (result.isSuccess) {
                    println("✅ WisdomSharingViewModel: Post created successfully")
                    _postSuccess.value = true
                } else {
                    val errorMsg = "Failed to create post: ${result.exceptionOrNull()?.message}"
                    println("❌ WisdomSharingViewModel: $errorMsg")
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                val errorMsg = "Failed to create post: ${e.message}"
                println("❌ WisdomSharingViewModel: $errorMsg")
                _errorMessage.value = errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun likePost(postId: String, userId: String) {
        viewModelScope.launch {
            try {
                wisdomRepository.likePost(postId, userId)
                // No need to manually refresh - real-time listener will update automatically
            } catch (e: Exception) {
                _errorMessage.value = "Failed to like post: ${e.message}"
            }
        }
    }

    fun addComment(postId: String, userId: String, userName: String, content: String) {
        viewModelScope.launch {
            try {
                val comment = WisdomComment(
                    postId = postId,
                    userId = userId,
                    userName = userName,
                    content = content
                )

                wisdomRepository.addComment(comment)
                // No need to manually refresh - real-time listener will update automatically
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add comment: ${e.message}"
            }
        }
    }

    // Keep for backward compatibility, but real-time is preferred
    fun loadCommentsForPost(postId: String) {
        viewModelScope.launch {
            try {
                val comments = wisdomRepository.getCommentsForPost(postId)
                _selectedPostComments.value = comments
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load comments: ${e.message}"
            }
        }
    }

    fun deletePost(postId: String, userId: String) {
        viewModelScope.launch {
            try {
                val result = wisdomRepository.deletePost(postId, userId)
                if (result.isSuccess) {
                    // No need to manually refresh - real-time listener will update automatically
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete post: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _postSuccess.value = false
    }
}