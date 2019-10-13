package net.francescogatto.kkdownloadfile.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.ktor.client.HttpClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.francescogatto.kkdownloadfile.R
import net.francescogatto.kkdownloadfile.model.DummyData
import net.francescogatto.kkdownloadfile.utils.downloadFile
import net.francescogatto.kkdownloadfile.utils.openFile
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    companion object {
        val data = arrayOf(
            DummyData("1", "Title1.pdf", "https://css4.pub/2015/icelandic/dictionary.pdf"),
            DummyData("2", "Title2.pdf", "https://css4.pub/2015/icelandic/dictionary.pdf"),
            DummyData("3", "Title3.pdf", "https://css4.pub/2015/icelandic/dictionary.pdf"),
            DummyData("4", "Title4.pdf", "https://css4.pub/2017/newsletter/drylab.pdf"),
            DummyData("5", "Title5.pdf", "https://css4.pub/2017/newsletter/drylab.pdf"),
            DummyData("6", "Title6.pdf", "https://css4.pub/2017/newsletter/drylab.pdf")
        )
    }

    lateinit var myAdapter: AttachmentAdapter
    val ktor: HttpClient by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation).apply {
                addItemDecoration(this)
            }
            myAdapter = AttachmentAdapter(data) { dummy ->
                manageClickAdapter(dummy)
            }
            adapter = myAdapter
        }
    }

    fun manageClickAdapter(dummy: DummyData) {
        when {
            dummy.file.exists() -> openFile(dummy.file) 
            else -> {
                myAdapter.setDownload(dummy, true)
                CoroutineScope(Dispatchers.IO).launch {
                    ktor.downloadFile(dummy.file, dummy.url) {
                        if (it) {
                            Log.d("TAG", "File downloaded!")
                        } else {
                            dummy.file.delete()
                            Log.d("TAG", "File not downlaoded")
                        }
                        runOnUiThread {
                            myAdapter.setDownload(dummy, false)
                        }
                    }
                }
            }
        }
    }

}

