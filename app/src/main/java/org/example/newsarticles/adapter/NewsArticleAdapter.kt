package org.example.newsarticles.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.example.newsarticles.R
import org.example.newsarticles.databinding.NewsListItemBinding
import org.example.newsarticles.model.Article
import java.text.SimpleDateFormat
import java.util.Locale

class NewsArticleAdapter(private val newsArticle: List<Article>): RecyclerView.Adapter<NewsArticleAdapter.ViewHolder>() {

    private lateinit var binding: NewsListItemBinding

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = NewsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int = newsArticle.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = newsArticle[position]

        binding.newsTitleTV.text = item.title.toString()
        binding.newsAuthorTV.text = item.author.toString()
        binding.newsUrlTV.text = item.url.toString()

        binding.newsPublishedTV.text = getDateFromDateTime(item.publishedAt.toString())

        // Fetch Image
        Glide.with(holder.itemView.context)
            .load(Uri.parse(item.urlToImage))
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.newsImageIV)

        // Open Article into Browser window
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(binding.newsUrlTV.text.toString()))
        binding.newsTitleTV.setOnClickListener {
            holder.itemView.context.startActivity(intent)
        }

        binding.newsUrlTV.setOnClickListener {
            holder.itemView.context.startActivity(intent)
        }
    }

    // Fetch Only Date
    private fun getDateFromDateTime(dateTimeString: String?): String {
        if (dateTimeString.isNullOrEmpty()) return ""
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateTimeString)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}
