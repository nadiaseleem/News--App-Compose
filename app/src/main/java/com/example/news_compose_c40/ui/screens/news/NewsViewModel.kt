package com.example.news_compose_c40.ui.screens.news

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news_compose_c40.R
import com.example.news_compose_c40.data.model.article.Article
import com.example.news_compose_c40.data.model.source.Source
import com.example.news_compose_c40.data.model.source.SourcesResponse
import com.example.news_compose_c40.util.UIMessage
import com.example.news_compose_c40.util.fromJson
import com.example.news_compose_c40.data.api.NewsService
import com.example.news_compose_c40.data.connectivity.NetworkHandler
import com.example.news_compose_c40.data.repository.NewsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject

const val PAGE_SIZE = 3

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepo: NewsRepo, networkHandler: NetworkHandler
) : ConnectivityViewModel(networkHandler) {
    private val _sourcesList = mutableStateOf<List<Source>>(listOf())
    val sourcesList: List<Source> get() = _sourcesList.value

    private var _articlesList = mutableStateOf<List<Article>>(listOf())
    val articlesList: List<Article> get() = _articlesList.value

    private val _uiMessage = mutableStateOf(UIMessage())
    val uiMessage: UIMessage get() = _uiMessage.value

    private val _isErrorDialogVisible = mutableStateOf(false)
    val isErrorDialogVisible: Boolean get() = _isErrorDialogVisible.value

    private val _selectedSourceId = mutableStateOf("")
    fun setSelectedSourceId(sourceId: String) {
        _selectedSourceId.value = sourceId
    }

    private fun showErrorDialog() {
        _isErrorDialogVisible.value = true
    }

    fun hideErrorDialog() {
        _isErrorDialogVisible.value = false
    }

    fun getSources(categoryId: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiMessage.value = UIMessage(isLoading = true)
                val sources = newsRepo.getSources(categoryId = categoryId)
                _uiMessage.value = UIMessage(isLoading = false)

                _sourcesList.value = sources


            } catch (e: HttpException) {
                val sourcesResponse = e.response()?.errorBody()?.string()?.fromJson(
                    SourcesResponse::class.java
                )
                _uiMessage.value = UIMessage(
                    isLoading = false,
                    errorMessage = sourcesResponse?.message,
                    retryAction = {
                        getSources(categoryId)
                    })

                showErrorDialog()
            } catch (e: UnknownHostException) {

                _uiMessage.value = UIMessage(
                    isLoading = false,
                    errorMessageId = R.string.connection_error,
                    retryAction = {
                        getSources(categoryId)
                    })
                showErrorDialog()

            } catch (e: Exception) {
                _uiMessage.value = UIMessage(
                    isLoading = false,
                    errorMessage = e.localizedMessage,
                    retryAction = {
                        getSources(categoryId)
                    })
                showErrorDialog()

            }
        }
    }


    private val _retry = mutableStateOf<(() -> Unit)?>(null)
    val retry: (() -> Unit)? get() = _retry.value

    var page = 1
    fun getNewsBySource(sourceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                page = 1
                _uiMessage.value = UIMessage(isLoading = true)
                val articles = newsRepo.getArticles(sourceId = sourceId, 1, PAGE_SIZE)
                _uiMessage.value = UIMessage(isLoading = false)

                if (articles.isNotEmpty())
                    _articlesList.value = articles
                else
                    _uiMessage.value = UIMessage(shouldDisplayNoArticlesFound = true)


                _retry.value = {
                    getNewsBySource(sourceId)
                }

            } catch (e: HttpException) {
                val sourcesResponse = e.response()?.errorBody()?.string()?.fromJson(
                    SourcesResponse::class.java
                )
                _uiMessage.value = UIMessage(
                    isLoading = false,
                    errorMessage = sourcesResponse?.message,
                    retryAction = {
                        getNewsBySource(sourceId)
                    })

                showErrorDialog()

            } catch (e: UnknownHostException) {

                _uiMessage.value = UIMessage(
                    isLoading = false,
                    errorMessageId = R.string.connection_error,
                    retryAction = {
                        getNewsBySource(sourceId)
                    })
                showErrorDialog()

            } catch (e: Exception) {
                _uiMessage.value = UIMessage(
                    isLoading = false,
                    errorMessage = e.localizedMessage,
                    retryAction = {
                        getNewsBySource(sourceId)
                    })
                showErrorDialog()

            }
        }

    }

    fun nextPage() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                page++
                _uiMessage.value = UIMessage(requestingNextPage = true)
                val articles =
                    newsRepo.getArticles(sourceId = _selectedSourceId.value, page, PAGE_SIZE)
                _uiMessage.value = UIMessage(requestingNextPage = false)
                appendArticlesList(articles)

            } catch (e: HttpException) {
                val sourcesResponse = e.response()?.errorBody()?.string()?.fromJson(
                    SourcesResponse::class.java
                )
                _uiMessage.value = UIMessage(
                    isLoading = false,
                    errorMessage = sourcesResponse?.message,
                    retryAction = {
                        nextPage()
                    })

                showErrorDialog()

            } catch (e: UnknownHostException) {

                _uiMessage.value = UIMessage(
                    isLoading = false,
                    errorMessageId = R.string.connection_error,
                    retryAction = {
                        nextPage()
                    })
                showErrorDialog()

            } catch (e: Exception) {
                _uiMessage.value = UIMessage(
                    isLoading = false,
                    errorMessage = e.localizedMessage,
                    retryAction = {
                        nextPage()
                    })
                showErrorDialog()

            }
        }

    }

    private fun appendArticlesList(articles: List<Article>) {
        val content = ArrayList(_articlesList.value)
        content.addAll(articles)
        _articlesList.value = content
    }
}
