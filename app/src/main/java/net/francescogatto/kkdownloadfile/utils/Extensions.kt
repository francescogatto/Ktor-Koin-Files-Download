package net.francescogatto.kkdownloadfile.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import io.ktor.util.cio.writeChannel
import kotlinx.coroutines.io.copyAndClose
import net.francescogatto.kkdownloadfile.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import java.io.File

val globalContext: Context
    get() = GlobalContext.get().koin.rootScope.androidContext()


suspend fun HttpClient.downloadFile(file: File, url: String, callback: suspend (boolean: Boolean) -> Unit) {
    val call = call {
        url(url)
        method = HttpMethod.Get
    }
    if (!call.response.status.isSuccess()) {
        callback(false)
    }
    call.response.content.copyAndClose(file.writeChannel())
    return callback(true)
}

suspend fun HttpClient.downloadFile(file: File, url: String): Result<File> {
    val call = call {
        url(url)
        method = HttpMethod.Get
    }
    if (!call.response.status.isSuccess()) {
        Result.failure<Boolean>(Exception("File not downlaoded"))
    }
    call.response.content.copyAndClose(file.writeChannel())
    return Result.success(file)
}

fun Activity.openFile(file: File) {
    Intent(Intent.ACTION_VIEW).apply {
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        addCategory(Intent.CATEGORY_DEFAULT)
        val uri = FileProvider.getUriForFile(this@openFile, BuildConfig.APPLICATION_ID + ".provider", file)
        val mimeType = getMimeType(file)
        mimeType?.let {
            setDataAndType(uri, it)
            startActivity(this)
        }

    }
}

fun getMimeType(file: File): String? {
    val extension = file.extension
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}
