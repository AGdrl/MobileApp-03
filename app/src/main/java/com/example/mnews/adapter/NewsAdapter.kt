package com.example.mnews.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mnews.R
import com.example.mnews.models.Article
import com.example.mnews.util.ReadTracker
import com.example.mnews.util.ShareCounter

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleImage: ImageView = itemView.findViewById(R.id.articleItemImage)
        val siteSource: TextView = itemView.findViewById(R.id.siteSource)
        val newsHead: TextView = itemView.findViewById(R.id.newsHead)
        val newsContent: TextView = itemView.findViewById(R.id.newsContent)
        val publishDate: TextView = itemView.findViewById(R.id.publishDate)
        val ivShare: ImageView = itemView.findViewById(R.id.ivShare)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article_preview, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        Glide.with(holder.itemView).load(article.urlToImage).into(holder.articleImage)

        val sourceName = article.source?.name ?: ""
        holder.newsHead.text = article.title
        holder.newsContent.text = article.description
        holder.publishDate.text = article.publishedAt

        // 🔵 OKUNDU MU? + READ BADGE
        val url = article.url ?: ""
        val isRead = url.isNotBlank() && ReadTracker.isRead(holder.itemView.context, url)

        holder.itemView.alpha = if (isRead) 0.6f else 1.0f
        holder.siteSource.text = if (isRead) "READ • $sourceName" else sourceName

        // 📰 HABERE TIKLANINCA (detay)
        holder.itemView.setOnClickListener {
            if (url.isNotBlank()) {
                ReadTracker.markRead(holder.itemView.context, url)
            }
            onItemClickListener?.let { it(article) }
        }

        // 🌐 OPEN IN BROWSER (kartı uzun bas)
        holder.itemView.setOnLongClickListener {
            val link = article.url ?: ""
            if (link.isBlank()) {
                Toast.makeText(holder.itemView.context, "No link to open", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            holder.itemView.context.startActivity(intent)
            true
        }

        // 📤 SHARE (tıkla) + 🔢 SHARE COUNTER
        holder.ivShare.setOnClickListener {
            val desc = article.description ?: ""

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "${article.title}\n\n$desc\n\n${article.url}"
                )
                type = "text/plain"
            }

            ShareCounter.increment(holder.itemView.context)
            val total = ShareCounter.getTotal(holder.itemView.context)

            Toast.makeText(
                holder.itemView.context,
                "Total shares: $total",
                Toast.LENGTH_SHORT
            ).show()

            val chooser = Intent.createChooser(sendIntent, "Haberi Paylaş")
            holder.itemView.context.startActivity(chooser)
        }

        // 📋 COPY LINK (share ikonuna uzun bas)
        holder.ivShare.setOnLongClickListener {
            val link = article.url ?: ""
            if (link.isBlank()) {
                Toast.makeText(holder.itemView.context, "No link to copy", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }

            val clipboard = holder.itemView.context
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("News link", link))

            Toast.makeText(holder.itemView.context, "Link copied!", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun getItemCount() = differ.currentList.size

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}
