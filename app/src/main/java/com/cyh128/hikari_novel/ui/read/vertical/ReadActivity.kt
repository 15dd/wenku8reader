package com.cyh128.hikari_novel.ui.read.vertical

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.ReadParcel
import com.cyh128.hikari_novel.databinding.ActivityVerticalReadBinding
import com.cyh128.hikari_novel.databinding.ViewVerticalReadConfigBinding
import com.cyh128.hikari_novel.ui.other.LoadingView
import com.cyh128.hikari_novel.ui.read.SelectColorActivity
import com.cyh128.hikari_novel.util.getIsInDarkMode
import com.drake.channel.receiveEvent
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.gyf.immersionbar.ImmersionBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

@AndroidEntryPoint
class ReadActivity : BaseActivity<ActivityVerticalReadBinding>() {
    private lateinit var bottomViewBinding: ViewVerticalReadConfigBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val viewModel by lazy { ViewModelProvider(this)[ReadViewModel::class.java] }
    private var showBarColor by Delegates.notNull<Int>()
    private var saveReadHistoryJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getParcelableExtra<ReadParcel>("data")!!.also {
            viewModel.novel = it.novel
            viewModel.curVolumePos = it.curVolumePos
            viewModel.curChapterPos = it.curChapterPos
            viewModel.goToLatest = it.goToLatest
        }

        setSupportActionBar(binding.tbAVRead)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbAVRead.setNavigationOnClickListener { finish() }


        receiveEvent<Event>("event_vertical_read_activity") { event ->
            when (event) {
                Event.LoadSuccessEvent -> {
                    lifecycle.withStarted { //在ui可见的情况下执行，否则先挂起
                        supportActionBar?.title = viewModel.chapterTitle
                        showContent()
                        if (viewModel.curImages.isNotEmpty()) {
                            Snackbar.make(
                                binding.root,
                                R.string.have_image_tip,
                                Snackbar.LENGTH_INDEFINITE
                            ).apply {
                                setAnchorView(binding.llAVReadBottomBar) //使它出现在bottomAppBar的上面，避免遮挡内容
                                setAction(R.string.ok) {}
                                show()
                            }
                        }
                        setBottomBarIsEnable(true)
                        hideBar()
                    }
                }

                else -> {}
            }
        }

        initView()
        initListener()

        viewModel.getNovelContent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_act_v_h_read, menu)
        return true
    }

    private fun initView() {
        TypedValue().also {  //上下栏颜色初始化
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorSurfaceContainer,
                it,
                true
            )
            showBarColor = it.resourceId
        }

        bottomViewBinding = ViewVerticalReadConfigBinding.inflate(LayoutInflater.from(this))
        bottomSheetDialog = BottomSheetDialog(this).apply {
            setContentView(bottomViewBinding.root)
            dismissWithAnimation = true
        }

        bottomViewBinding.sVVReadConfigRestoreChapterReadHistory.isChecked = viewModel.getIsShowChapterReadHistory()
        bottomViewBinding.sVVReadConfigFontSize.value = viewModel.getFontSize()
        bottomViewBinding.sVVReadConfigLineSpacing.value = viewModel.getLineSpacing()
        bottomViewBinding.sVVReadConfigKeepScreenOn.isChecked = viewModel.getKeepScreenOn()

        bottomViewBinding.sVVReadConfigRestoreChapterReadHistoryWithoutConfirm.isChecked = viewModel.getIsShowChapterReadHistoryWithoutConfirm()
        bottomViewBinding.sVVReadConfigRestoreChapterReadHistoryWithoutConfirm.isEnabled = viewModel.getIsShowChapterReadHistory()

        if (viewModel.getKeepScreenOn()) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //保持屏幕常亮
        else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setBottomBarIsEnable(false)
        showBar()
        setProgressTipColor()
        showLoading()
    }

    private fun initListener() {
        binding.tbAVRead.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_hide_bar -> {
                    hideBar()
                    true
                }

                else -> false
            }
        }

        viewModel.progressText.observe(this) {
            binding.tvAVReadProgress.text = it

            saveReadHistoryJob?.cancel()
            saveReadHistoryJob = lifecycleScope.launch { viewModel.saveReadHistory() }
        }

        //设置底栏菜单按钮监听
        binding.bAVReadPreviousChapter.setOnClickListener {
            toPreviousChapter()
        }

        binding.bAVReadNextChapter.setOnClickListener {
            toNextChapter()
        }

        binding.bAVReadConfig.setOnClickListener {
            bottomSheetDialog.show()
        }

        bottomViewBinding.sVVReadConfigFontSize.addOnSliderTouchListener(
            object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}
                override fun onStopTrackingTouch(slider: Slider) {
                    viewModel.setFontSize(bottomViewBinding.sVVReadConfigFontSize.value)
                }
            }
        )

        bottomViewBinding.sVVReadConfigLineSpacing.addOnSliderTouchListener(
            object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}
                override fun onStopTrackingTouch(slider: Slider) {
                    viewModel.setLineSpacing(bottomViewBinding.sVVReadConfigLineSpacing.value)
                }
            }
        )

        bottomViewBinding.sVVReadConfigKeepScreenOn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setKeepScreenOn(isChecked)
            if (isChecked) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //保持屏幕常亮
            else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        bottomViewBinding.sVVReadConfigRestoreChapterReadHistory.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsShowChapterReadHistory(isChecked)
            bottomViewBinding.sVVReadConfigRestoreChapterReadHistoryWithoutConfirm.isEnabled = isChecked
        }

        bottomViewBinding.sVVReadConfigRestoreChapterReadHistoryWithoutConfirm.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsShowChapterReadHistoryWithoutConfirm(isChecked)
        }

        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                //当SelectColorActivity退出后所要做的语句体
                if (result.resultCode == RESULT_OK) { //刷新界面，使设置的文字颜色和背景颜色生效
                    setProgressTipColor()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fcv_a_v_read, ReadFragment())
                        .commit()
                }
            }

        bottomViewBinding.bVVReadConfigChangeColor.setOnClickListener {
            activityResultLauncher.launch(
                Intent(
                    this,
                    SelectColorActivity::class.java
                )
            )
        }
    }

    fun toPreviousChapter() { //切换至上一章
        lifecycleScope.launch {
            if (viewModel.curChapterPos == 0) { //判断是不是该卷的第一章
                if (viewModel.curVolumePos == 0) {
                    Snackbar.make(
                        binding.root,
                        R.string.no_previous_chapter,
                        Snackbar.LENGTH_INDEFINITE
                    ).apply {
                        setAnchorView(binding.llAVReadBottomBar) //使它出现在bottomAppBar的上面，避免遮挡内容
                        setAction(R.string.ok) {}
                        show()
                    }
                    return@launch
                }
                viewModel.curVolumePos--
                viewModel.curChapterPos = viewModel.curVolume.chapters.size - 1
                showLoading()
                viewModel.getNovelContent()
                setBottomBarIsEnable(false)
                return@launch
            }
            showLoading()
            viewModel.curChapterPos--
            viewModel.getNovelContent()
            setBottomBarIsEnable(false)
        }
    }

    fun toNextChapter() { //切换至下一章
        lifecycleScope.launch {
            if (viewModel.curChapterPos == viewModel.curVolume.chapters.size - 1) { //判断是不是该卷的最后一章
                if (viewModel.curVolumePos == viewModel.novel.volume.size - 1) {
                    Snackbar.make(binding.root, R.string.no_next_chapter, Snackbar.LENGTH_INDEFINITE)
                        .apply {
                            setAnchorView(binding.llAVReadBottomBar) //使它出现在bottomAppBar的上面，避免遮挡内容
                            setAction(R.string.ok) {}
                            show()
                        }
                    return@launch
                }
                viewModel.curVolumePos++
                viewModel.curChapterPos = 0
                showLoading()
                viewModel.getNovelContent()
                setBottomBarIsEnable(false)
                return@launch
            }
            showLoading()
            viewModel.curChapterPos++
            viewModel.getNovelContent()
            setBottomBarIsEnable(false)
        }
    }

    private fun showContent() { //显示小说正文内容
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fcv_a_v_read, ReadFragment())
            .commit()
    }

    private fun showLoading() { //显示正在加载
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fcv_a_v_read,
                LoadingView().apply {
                    arguments = Bundle().also {
                        it.putString("color", getHideBarColor())
                    }
                }
            )
            .commit()
        binding.tvAVReadProgress.text = "0%"
    }

    fun showBar() { //显示上下栏
        binding.tbAVRead.animate().translationY(0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    ImmersionBar.with(this@ReadActivity)
                        .statusBarColor(showBarColor)
                        .navigationBarColor(showBarColor)
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init()
                }

                override fun onAnimationEnd(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        binding.llAVReadBottomBar.animate().translationY(0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
    }

    fun hideBar() { //隐藏上下栏
        binding.tbAVRead.animate().translationY(-(binding.tbAVRead.height.toFloat()))
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    ImmersionBar.with(this@ReadActivity)
                        .statusBarColor(getHideBarColor())
                        .navigationBarColor(getHideBarColor())
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        binding.llAVReadBottomBar.animate().translationY(binding.llAVReadBottomBar.height.toFloat())
            .setInterpolator(AccelerateDecelerateInterpolator())
    }

    fun setRestoreChapterReadHistoryDisable() {
        bottomViewBinding.sVVReadConfigRestoreChapterReadHistory.isChecked = false
    }

    private fun getHideBarColor() = getIsInDarkMode().let {
        if (it) { //夜间模式
            "#" + viewModel.getBgColorNight()
        } else {
            "#" + viewModel.getBgColorDay()
        }
    }

    private fun getTextColor() = getIsInDarkMode().let {
        if (it) { //夜间模式
            "#" + viewModel.getTextColorNight()
        } else {
            "#" + viewModel.getTextColorDay()
        }
    }

    private fun setProgressTipColor() { //设置阅读提示的字体颜色及其背景颜色
        binding.llAVReadBottomBar2.setBackgroundColor(Color.parseColor(getHideBarColor()))
        binding.tvAVReadProgress.setTextColor(Color.parseColor(getTextColor()))
    }

    private fun setBottomBarIsEnable(isEnable: Boolean) {
        binding.bAVReadConfig.isEnabled = isEnable
        binding.bAVReadNextChapter.isEnabled = isEnable
        binding.bAVReadPreviousChapter.isEnabled = isEnable
    }
}