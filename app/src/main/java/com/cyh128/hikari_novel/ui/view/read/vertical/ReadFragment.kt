package com.cyh128.hikari_novel.ui.view.read.vertical

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.FragmentVerticalReadBinding
import com.cyh128.hikari_novel.ui.view.other.PhotoViewActivity
import com.cyh128.hikari_novel.util.getIsInDarkMode
import com.cyh128.hikari_novel.util.launchWithLifecycle
import com.cyh128.hikari_novel.util.startActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class ReadFragment : BaseFragment<FragmentVerticalReadBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[ReadViewModel::class.java] }
    private lateinit var gestureDetector: GestureDetector //https://juejin.cn/post/7032900181519515685
    private var readAdapter: ReadAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchWithLifecycle {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is Event.ChangeFontSizeEvent -> {
                        binding.tvFVRead.textSize = event.value
                    }

                    is Event.ChangeLineSpacingEvent -> {
                        binding.tvFVRead.setLineSpacing(event.value, 1f)
                    }

                    else -> {}
                }
            }
        }

        initView()
        initListener()
    }

    override fun onDestroyView() {
        readAdapter = null //防止内存泄漏
        super.onDestroyView()
    }

    private fun initView() {
        readAdapter = ReadAdapter(
            requireContext(),
            viewModel.curImages
        ) { imageUrl ->
            startActivity<PhotoViewActivity> {
                putExtra("url", imageUrl)
            }
        }
        binding.tvFVRead.text = viewModel.curNovelContent
        binding.rvFVRead.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = readAdapter
        }

        //初始化阅读器参数
        binding.tvFVRead.textSize = viewModel.getFontSize()
        binding.tvFVRead.setLineSpacing(viewModel.getLineSpacing(), 1f)

        if (getIsInDarkMode()) { //夜间模式
            binding.tvFVRead.setTextColor(Color.parseColor("#" + viewModel.getTextColorNight()))
            binding.tvFVReadEndTip.setTextColor(Color.parseColor("#" + viewModel.getTextColorNight()))
            binding.root.setBackgroundColor(Color.parseColor("#" + viewModel.getBgColorNight()))
        } else {
            binding.tvFVRead.setTextColor(Color.parseColor("#" + viewModel.getTextColorDay()))
            binding.tvFVReadEndTip.setTextColor(Color.parseColor("#" + viewModel.getTextColorDay()))
            binding.root.setBackgroundColor(Color.parseColor("#" + viewModel.getBgColorDay()))
        }

        binding.nsvFVRead.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.nsvFVRead.viewTreeObserver.removeOnGlobalLayoutListener(this) //确保只调用一次
                    refreshProgressText()

                    lifecycleScope.launch {
                        viewModel.getByCid.take(1).last()?.let {
                            if (viewModel.goToLatest) {
                                binding.nsvFVRead.scrollTo(0, it.location) //滚动到指定位置
                            } else {
                                if (viewModel.getIsShowChapterReadHistory()) {
                                    if (viewModel.getIsShowChapterReadHistoryWithoutConfirm()) {
                                        binding.nsvFVRead.scrollTo(0, it.location) //滚动到指定位置
                                    } else {
                                        MaterialAlertDialogBuilder(requireContext())
                                            .setTitle(R.string.history)
                                            .setIcon(R.drawable.ic_history)
                                            .setMessage(R.string.history_restore_tip)
                                            .setCancelable(false)
                                            .setNeutralButton(
                                                R.string.not_restore_and_close_forever
                                            ) { _, _ ->
                                                viewModel.setIsShowChapterReadHistory(false)
                                                (requireActivity() as ReadActivity).setRestoreChapterReadHistoryDisable()
                                            }
                                            .setNegativeButton(R.string.not_restore) { _, _ -> }
                                            .setPositiveButton(R.string.restore_chapter_read_history_with_confirm) { _, _ ->
                                                binding.nsvFVRead.scrollTo(0, it.location) //滚动到指定位置
                                            }
                                            .show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        gestureDetector = GestureDetector(requireActivity(), ReaderBarGestureListener())
        binding.nsvFVRead.setOnTouchListener { _: View?, event: MotionEvent? ->
            gestureDetector.onTouchEvent(
                (event)!!
            )
        }

        binding.nsvFVRead.setOnScrollChangeListener { _, _, _, _, _ ->
            refreshProgressText()
        }

        binding.bFVReadPreviousChapter.setOnClickListener {
            (requireActivity() as ReadActivity).skipToPreviousChapter()
        }

        binding.bFVReadNextChapter.setOnClickListener {
            (requireActivity() as ReadActivity).skipToNextChapter()
        }
    }

    private fun refreshProgressText() {
        val start: Int = binding.nsvFVRead.height + binding.nsvFVRead.scrollY
        val bottom: Int = binding.nsvFVRead.getChildAt(0).height
        val df = DecimalFormat("0.00")
        var result: String = df.format((start.toFloat() / bottom * 100).toDouble())
        viewModel.curReadPos = binding.nsvFVRead.scrollY
        result = result.substring(0, result.indexOf("."))
        if (result.toInt() > 100) result = "100"
        viewModel.progressText.value = "$result%"
    }

    private inner class ReaderBarGestureListener : GestureDetector.OnGestureListener {
        override fun onDown(e: MotionEvent): Boolean = false
        override fun onShowPress(e: MotionEvent) {}
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (viewModel.isBarShown) {
                (requireActivity() as ReadActivity).hideBar()
                viewModel.isBarShown = false
            } else {
                (requireActivity() as ReadActivity).showBar()
                viewModel.isBarShown = true
            }
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean = false

        override fun onLongPress(e: MotionEvent) {}
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean = false
    }
}
