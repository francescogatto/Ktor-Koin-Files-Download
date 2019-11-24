package net.francescogatto.kkdownloadfile.home

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.ktor.client.HttpClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.francescogatto.kkdownloadfile.R
import net.francescogatto.kkdownloadfile.model.DownloadResult
import net.francescogatto.kkdownloadfile.model.DummyData
import net.francescogatto.kkdownloadfile.utils.downloadFile
import net.francescogatto.kkdownloadfile.utils.openFile
import org.koin.android.ext.android.inject
import java.lang.Exception

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
            DividerItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            ).apply {
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
            dummy.isDownloading -> {
                //Do nothing
            }
            dummy.file.exists() -> openFile(dummy.file)
            else -> {
                try {
                    downloadWithFlow(dummy)
                } catch (e: Exception) {
                    //generic error while downloading
                }
            }
        }
    }

    private fun downloadWithFlow(dummy: DummyData) {
        CoroutineScope(Dispatchers.IO).launch {
            ktor.downloadFile(dummy.file, dummy.url).collect {
                withContext(Dispatchers.Main) {
                    when (it) {
                        is DownloadResult.Success -> {
                            myAdapter.setDownloading(dummy, false)
                        }
                        is DownloadResult.Error -> {
                            myAdapter.setDownloading(dummy, false)
                            Toast.makeText(this@MainActivity, "Error while downloading ${dummy.title}", Toast.LENGTH_LONG).show()
                        }
                        is DownloadResult.Progress -> {
                            myAdapter.setProgress(dummy, it.progress)
                        }
                    }
                }
            }
        }
    }

}

