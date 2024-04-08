package org.example.newsarticles

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.example.newsarticles.adapter.NewsArticleAdapter
import org.example.newsarticles.constants.ARTICLES
import org.example.newsarticles.constants.AUTHOR
import org.example.newsarticles.constants.CONTENT
import org.example.newsarticles.constants.DESCRIPTION
import org.example.newsarticles.constants.Json_URL
import org.example.newsarticles.constants.NAME
import org.example.newsarticles.constants.PUBLISHED_AT
import org.example.newsarticles.constants.REQUEST_METHOD
import org.example.newsarticles.constants.SOURCE
import org.example.newsarticles.constants.TITLE
import org.example.newsarticles.constants.URL
import org.example.newsarticles.constants.URL_TO_IMAGE
import org.example.newsarticles.databinding.ActivityMainBinding
import org.example.newsarticles.model.Article
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsArticleAdapter
    private var articlesList = listOf<Article>()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.newsListRV.layoutManager = LinearLayoutManager(this)

        GlobalScope.launch(Dispatchers.IO) {
            val jsonData = fetchJsonData(Json_URL)
            articlesList = parseJsonToList(jsonData)

            // Update UI on the main (UI) thread
            runOnUiThread {
                newsAdapter = NewsArticleAdapter(articlesList)
                binding.newsListRV.adapter = newsAdapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.oldToNew -> sortArticlesOldToNew()
            R.id.newToOld -> sortArticlesNewToOld()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchJsonData(urlString: String?): String? {
        var result: String? = null

        try {
            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = REQUEST_METHOD

            val responseCode = urlConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = urlConnection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                reader.forEachLine {
                    stringBuilder.append(it)
                }
                result = stringBuilder.toString()
                inputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    // Function to parse JSON string into a list of NewsArticle objects
    private fun parseJsonToList(jsonString: String?): List<Article> {
        val articlesList = mutableListOf<Article>()

        try {
            val jsonObject = JSONObject(jsonString)
            val articlesArray = jsonObject.getJSONArray(ARTICLES)

            for (i in 0 until articlesArray.length()) {
                val articleObject = articlesArray.getJSONObject(i)
                val sourceObject = articleObject.getJSONObject(SOURCE)
                val sourceName = sourceObject.getString(NAME)
                val author = articleObject.getString(AUTHOR)
                val title = articleObject.getString(TITLE)
                val description = articleObject.getString(DESCRIPTION)
                val url = articleObject.getString(URL)
                val urlToImage = articleObject.getString(URL_TO_IMAGE)
                val publishedAt = articleObject.getString(PUBLISHED_AT)
                val content = articleObject.getString(CONTENT)

                val article = Article(
                    author,
                    content,
                    description,
                    publishedAt,
                    sourceName,
                    title,
                    url,
                    urlToImage
                )
                articlesList.add(article)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return articlesList
    }

    private fun sortArticlesOldToNew() {
        articlesList.sortedBy { it.publishedAt }
        newsAdapter.notifyDataSetChanged()
    }

    private fun sortArticlesNewToOld() {
        articlesList.sortedByDescending { it.publishedAt }
        newsAdapter.notifyDataSetChanged()
    }
}
