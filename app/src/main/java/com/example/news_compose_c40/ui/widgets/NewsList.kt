package com.example.news_compose_c40.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.news_compose_c40.data.model.article.Article
import com.example.news_compose_c40.data.model.source.Source
import com.example.news_compose_c40.ui.screens.news.PAGE_SIZE
import com.example.news_compose_c40.ui.theme.green
import com.example.news_compose_c40.ui.theme.green_50

@Composable
fun NewsList(newsList: List<Article>, shouldDisplayNoArticlesFound: Boolean, loadingState: Boolean,page:Int?=null,requestingNextPage:Boolean?=null,onBottomReached:(()->Unit)?=null, onNewsClick:(String, String)->Unit) {

        if (!shouldDisplayNoArticlesFound) {
            Box {
                LazyColumn(verticalArrangement = Arrangement.SpaceEvenly) {

                        items(newsList.size) { position->
                            //0
                            //1
                                 //2+1           1 * 3 , 6 , 9 , 12, 15
                            if (page != null) {
                                if ((position+1) >= (page*PAGE_SIZE)) {
                                    onBottomReached?.invoke()
                                }
                            }

                            NewsCard(newsList[position]) { title,sourceName ->
                                onNewsClick(title,sourceName)
                            }
                        }
                    }


                if (requestingNextPage==true){
                    LinearProgressIndicator(color = green, modifier = Modifier.fillMaxWidth())
                }
                ProgressIndicator(loadingState)
            }
        } else {
            ArticlesNotFound()
        }

}



@Preview(showSystemUi = true)
@Composable
private fun PreviewNewsList() {
    NewsList(listOf(
        Article(
        title = "Why are football's biggest clubs starting a new \n" + "tournament?",
        publishedAt = "3 hours ago"
    ), Article(
        title = "Why are football's biggest clubs starting a new \n" + "tournament?",
        publishedAt = "3 hours ago"
    )
    ), false,false, onNewsClick ={ _, _->

    })
}
