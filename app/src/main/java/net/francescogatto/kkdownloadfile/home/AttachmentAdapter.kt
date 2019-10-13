package net.francescogatto.kkdownloadfile.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list.view.*
import net.francescogatto.kkdownloadfile.R
import net.francescogatto.kkdownloadfile.model.DummyData

class AttachmentAdapter(var dummyes: Array<DummyData>, val listener: (DummyData) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false))

    override fun getItemCount(): Int = dummyes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dummy = dummyes[position]
        with(holder.itemView) {
            title.text = dummy.title
            downloadIcon.isVisible = !dummy.file.exists()
            documentTypeIcon.isVisible = dummy.file.exists()
            progressBarDocument.isVisible = dummy.isDownloading
            setOnClickListener {
                listener(dummy)
            }
        }
    }

    fun setDownload(dummy: DummyData, isDownloading: Boolean) {
        dummyes.find { dummy.id == it.id }?.isDownloading = isDownloading
        notifyDataSetChanged()
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
