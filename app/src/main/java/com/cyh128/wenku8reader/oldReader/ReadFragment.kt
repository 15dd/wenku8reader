package com.cyh128.wenku8reader.oldReader

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.util.GlobalConfig
import com.cyh128.wenku8reader.util.Wenku8Spider.Content
import java.io.IOException

class ReadFragment : Fragment() {
    private lateinit var view: View
    private lateinit var textView: TextView
    private lateinit var recyclerView: RecyclerView
    private var text: String? = null
    private var imgUrl: List<String>? = null
    private var fontSize: Float = 0f
    private var lineSpacing: Float = 0f
    private var readerAdapter: ReaderAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        view = inflater.inflate(R.layout.fragment_old_reader, container, false)
        textView = view.findViewById(R.id.text_frag_read)
        recyclerView = view.findViewById(R.id.recyclerView_frag_read)
        layoutManager = LinearLayoutManager(view.context)
        return view
    }

    @Throws(IOException::class)
    fun setContent() {
        val allContent: List<List<String>> = Content(
            (ReaderActivity.bookUrl)!!,
            ReaderActivity.ccss[ReaderActivity.vcssPosition][ReaderActivity.ccssPosition].url
        )
        text = allContent[0][0]
        imgUrl = allContent[1]
        if (text!!.contains("<img")) { //去除<img>html标签，防止出现绿色小方块
            text = text!!.replace("<a href[^>]*>".toRegex(), "")
            text = text!!.replace("</a>".toRegex(), "")
            text = text!!.replace("<img[^>]*>".toRegex(), "")
        }
        val msg: Message = Message()
        handler.sendMessage(msg)
    }

    fun setContentFontSize(size: Float) {
        fontSize = size
        val msg: Message = Message()
        setFontSizeHandler.sendMessage(msg)
    }

    fun setContentLineSpacing(size: Float) {
        lineSpacing = size
        val msg: Message = Message()
        setLineSpaceHandler.sendMessage(msg)
    }

    private val handler: Handler = Handler(object : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            if (!requireActivity().isFinishing) { //防止activity已销毁导致的崩溃
                textView.textSize = GlobalConfig.oldReaderFontSize
                textView.setLineSpacing(GlobalConfig.oldReaderLineSpacing, 1f)
                textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
                readerAdapter = ReaderAdapter(view.context, imgUrl)
                recyclerView.adapter = readerAdapter
                recyclerView.layoutManager = layoutManager
                return true
            } else {
                Log.w("debug", "activity destroyed")
            }
            return false
        }
    })
    private val setFontSizeHandler: Handler = Handler {
        textView.textSize = fontSize
        true
    }
    private val setLineSpaceHandler: Handler = Handler {
        textView.setLineSpacing(lineSpacing, 1f)
        true
    }
}
