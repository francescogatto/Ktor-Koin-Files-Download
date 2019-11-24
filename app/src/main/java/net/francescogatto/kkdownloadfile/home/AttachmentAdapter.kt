package net.francescogatto.kkdownloadfile.home

import android.os.Bundle
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
            textProgress.isVisible = dummy.isDownloading
            setOnClickListener {
                listener(dummy)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.firstOrNull() != null) {
            with(holder.itemView) {
                (payloads.first() as Bundle).getInt("progress").also {
                    progressBarDocument.progress = it
                    progressBarDocument.isVisible = it < 99
                    textProgress.isVisible = it < 99
                    textProgress.text = "$it %"
                }
            }
        }
    }

    fun setDownloading(dummy: DummyData, isDownloading: Boolean) {
        getDummy(dummy)?.isDownloading = isDownloading
        notifyItemChanged(dummyes.indexOf(dummy))
    }

    fun setProgress(dummy: DummyData, progress: Int) {
        getDummy(dummy)?.progress = progress
        notifyItemChanged(dummyes.indexOf(dummy), Bundle().apply { putInt("progress", progress) })
    }

    private fun getDummy(dummy: DummyData) = dummyes.find { dummy.id == it.id }


}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
