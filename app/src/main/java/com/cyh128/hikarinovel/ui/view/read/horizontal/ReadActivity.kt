package com.cyh128.hikarinovel.ui.view.read.horizontal

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import com.cyh128.hikarinovel.R
import com.cyh128.hikarinovel.base.BaseActivity
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.model.HorizontalRead
import com.cyh128.hikarinovel.data.model.ReadParcel
import com.cyh128.hikarinovel.databinding.ActivityHorizontalReadBinding
import com.cyh128.hikarinovel.databinding.ViewHorizontalReadConfigBinding
import com.cyh128.hikarinovel.ui.view.other.PhotoViewActivity
import com.cyh128.hikarinovel.ui.view.read.SelectColorActivity
import com.cyh128.hikarinovel.util.getIsInDarkMode
import com.cyh128.hikarinovel.util.startActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.gyf.immersionbar.ImmersionBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@AndroidEntryPoint
class ReadActivity : BaseActivity<ActivityHorizontalReadBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[ReadViewModel::class.java] }
    private lateinit var bottomViewBinding: ViewHorizontalReadConfigBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var showBarColor by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.tbAHRead)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbAHRead.setNavigationOnClickListener { finish() }
        supportActionBar?.setTitle(R.string.loading)

        intent.getParcelableExtra<ReadParcel>("data")!!.also {
            viewModel.novel = it.novel
            viewModel.curVolumePos = it.curVolumePos
            viewModel.curChapterPos = it.curChapterPos
            viewModel.goToLatest = it.goToLatest
        }

        //不能使用launchWithLifecycle，否则会因为某些操作执行太快，并且没有达到Lifecycle.State.STARTED，导致漏event
        lifecycleScope.launch {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    Event.LoadSuccessEvent -> {
                        lifecycle.withStarted {
                            binding.pvAHRead.content = HorizontalRead(
                                viewModel.chapterTitle,
                                viewModel.curNovelContent,
                                viewModel.curImages
                            )
                            supportActionBar?.title = viewModel.chapterTitle

                            lifecycleScope.launch {
                                viewModel.getByCid.take(1).last()?.let {
                                    if (viewModel.goToLatest) {
                                        binding.pvAHRead.pageNum = it.location
                                    } else {
                                        if (viewModel.getIsShowChapterReadHistory()) {
                                            MaterialAlertDialogBuilder(this@ReadActivity)
                                                .setTitle(R.string.history)
                                                .setIcon(R.drawable.ic_history)
                                                .setMessage(R.string.history_restore_tip)
                                                .setCancelable(false)
                                                .setNeutralButton(
                                                    R.string.not_restore_and_close_forever
                                                ) { _, _ ->
                                                    viewModel.setIsShowChapterReadHistory(false)
                                                    bottomViewBinding.sVHReadConfigRestoreChapterReadHistory.isChecked = false
                                                }
                                                .setNegativeButton(R.string.not_restore) { _, _ -> }
                                                .setPositiveButton(R.string.restore_chapter_read_history) { _, _ ->
                                                    binding.pvAHRead.pageNum = it.location
                                                }
                                                .show()
                                        }
                                    }
                                }
                            }

                            if (viewModel.curImages.isNotEmpty()) {
                                Snackbar.make(
                                    binding.root,
                                    R.string.have_image_tip,
                                    Snackbar.LENGTH_INDEFINITE
                                ).apply {
                                    setAnchorView(binding.llAHReadBottomBar) //使它出现在bottomAppBar的上面，避免遮挡内容
                                    setAction(R.string.ok) {}
                                    show()
                                }
                            }
                            setBottomBarIsEnable(true)
                            hideBar()
                        }
                    }

                    is Event.NetWorkErrorEvent -> {
                        MaterialAlertDialogBuilder(this@ReadActivity)
                            .setTitle(R.string.network_error)
                            .setIcon(R.drawable.ic_error)
                            .setMessage(event.msg)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                            .show()
                        setBottomBarIsEnable(true)
                    }

                    else -> {}
                }
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

    override fun onDestroy() {
        viewModel.saveReadHistory(binding.pvAHRead.pageNum, binding.pvAHRead.maxPageNum)
        super.onDestroy()
    }

    //音量上下键拦截
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (viewModel.getKeyDownSwitchChapter()) {
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    binding.pvAHRead.pageToPrevious()
                    return true
                }

                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    binding.pvAHRead.pageToNext()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun initView() {
        setBottomBarIsEnable(false)

        if (viewModel.isHorizontalReadFirstLaunch) {
            binding.iAHReadGuide.root.setOnClickListener {
                binding.iAHReadGuide.root.visibility = View.GONE
                viewModel.setIsHorizontalReadFirstLaunch(false)
            }
        } else binding.iAHReadGuide.root.visibility = View.GONE

        TypedValue().also {  //上下栏颜色初始化
            theme.resolveAttribute(
                com.google.android.material.R.attr.colorSurfaceContainer,
                it,
                true
            )
            showBarColor = it.resourceId
        }

        bottomViewBinding = ViewHorizontalReadConfigBinding.inflate(LayoutInflater.from(this))
        bottomSheetDialog = BottomSheetDialog(this).apply {
            setContentView(bottomViewBinding.root)
            dismissWithAnimation = true
        }

        bottomViewBinding.sVHReadConfigFontSize.value = viewModel.getFontSize()
        bottomViewBinding.sVHReadConfigBottomTextSize.value = viewModel.getBottomFontSize()
        bottomViewBinding.sVHReadConfigLineSpacing.value = viewModel.getLineSpacing()
        bottomViewBinding.sVHReadConfigKeyDownSwitchChapter.isChecked = viewModel.getKeyDownSwitchChapter()
        bottomViewBinding.sVHReadConfigSwitchAnimation.isChecked = viewModel.getSwitchAnimation()
        bottomViewBinding.sVHReadConfigRestoreChapterReadHistory.isChecked = viewModel.getIsShowChapterReadHistory()
        bottomViewBinding.sVHReadConfigKeepScreenOn.isChecked = viewModel.getKeepScreenOn()

        if (viewModel.getKeepScreenOn()) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //保持屏幕常亮
        else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.pvAHRead.rowSpace = viewModel.getLineSpacing()
        binding.pvAHRead.textSize = viewModel.getFontSize()
        binding.pvAHRead.bottomTextSize = viewModel.getBottomFontSize()
        binding.pvAHRead.switchAnimation = viewModel.getSwitchAnimation()

        setColor()
        showBar()
    }

    private fun setColor() {
        if (getIsInDarkMode()) { //夜间模式
            binding.pvAHRead.textColor = Color.parseColor("#" + viewModel.getTextColorNight())
            binding.pvAHRead.backgroundcolor = Color.parseColor("#" + viewModel.getBgColorNight())
        } else {
            binding.pvAHRead.textColor = Color.parseColor("#" + viewModel.getTextColorDay())
            binding.pvAHRead.backgroundcolor = Color.parseColor("#" + viewModel.getBgColorDay())
        }
    }

    private fun initListener() {
        binding.tbAHRead.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_hide_bar -> {
                    hideBar()
                    true
                }

                else -> false
            }
        }

        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                //当SelectColorActivity退出后所要做的语句体
                if (result.resultCode == RESULT_OK) { //刷新界面，使设置的文字颜色和背景颜色生效
                    setColor()
                }
            }

        binding.pvAHRead.setImageClickListener { imageUrl ->
            startActivity<PhotoViewActivity> {
                putExtra("url", imageUrl)
            }
        }

        binding.pvAHRead.onCenterClick = object : IPageView.OnCenterClick {
            override fun onCenterClick() {
                if (viewModel.isBarShown) {
                    hideBar()
                    viewModel.isBarShown = false
                } else {
                    showBar()
                    viewModel.isBarShown = true
                }
            }
        }

        binding.pvAHRead.onNextChapter = object : IPageView.OnNextChapter {
            override fun onNextChapter() = skipToNextChapter()
        }

        binding.pvAHRead.onPreviousChapter = object : IPageView.OnPreviousChapter {
            override fun onPreviousChapter() = skipToPreviousChapter()
        }

        binding.pvAHRead.onPageChange = object : IPageView.OnPageChange {
            override fun onPageChange(index: Int) {
                Log.d("r a", "index:$index count:${binding.pvAHRead.maxPageNum}")
                binding.sAHReadProgress.valueTo = binding.pvAHRead.maxPageNum.toFloat()
                binding.sAHReadProgress.value = index.toFloat()
            }
        }

        binding.sAHReadProgress.addOnSliderTouchListener(
            object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}
                override fun onStopTrackingTouch(slider: Slider) {
                    binding.pvAHRead.pageNum = slider.value.toInt()
                }
            }
        )

        bottomViewBinding.sVHReadConfigFontSize.addOnSliderTouchListener(
            object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    binding.pvAHRead.textSize = slider.value
                    viewModel.setFontSize(slider.value)
                }
            }
        )

        bottomViewBinding.sVHReadConfigBottomTextSize.addOnSliderTouchListener(
            object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    binding.pvAHRead.bottomTextSize = slider.value
                    viewModel.setBottomFontSize(slider.value)
                }
            }
        )

        bottomViewBinding.sVHReadConfigLineSpacing.addOnSliderTouchListener(
            object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    binding.pvAHRead.rowSpace = slider.value
                    viewModel.setLineSpacing(slider.value)
                }
            }
        )

        bottomViewBinding.bVHReadConfigColor.setOnClickListener {
            activityResultLauncher.launch(
                Intent(
                    this,
                    SelectColorActivity::class.java
                )
            )
        }

        binding.bAHReadConfig.setOnClickListener { bottomSheetDialog.show() }

        binding.bAHReadNextChapter.setOnClickListener { skipToNextChapter() }

        binding.bAHReadPreviousChapter.setOnClickListener { skipToPreviousChapter() }

        bottomViewBinding.sVHReadConfigKeyDownSwitchChapter.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setKeyDownSwitchChapter(isChecked)
        }

        bottomViewBinding.sVHReadConfigKeepScreenOn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setKeepScreenOn(isChecked)
            if (isChecked) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //保持屏幕常亮
            else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        bottomViewBinding.sVHReadConfigSwitchAnimation.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSwitchAnimation(isChecked)
            binding.pvAHRead.switchAnimation = isChecked
        }

        bottomViewBinding.sVHReadConfigRestoreChapterReadHistory.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsShowChapterReadHistory(isChecked)
        }
    }

    private fun skipToPreviousChapter() {
        lifecycleScope.launch {
            viewModel.saveReadHistory(binding.pvAHRead.pageNum, binding.pvAHRead.maxPageNum).join()
            if (viewModel.curChapterPos == 0) { //判断是不是该卷的第一章
                if (viewModel.curVolumePos == 0) {
                    Snackbar.make(
                        binding.root,
                        R.string.no_previous_chapter,
                        Snackbar.LENGTH_INDEFINITE
                    ).apply {
                        setAnchorView(binding.llAHReadBottomBar) //使它出现在bottomAppBar的上面，避免遮挡内容
                        setAction(R.string.ok) {}
                        show()
                    }
                    return@launch
                }
                viewModel.curVolumePos--
                viewModel.curChapterPos = viewModel.curVolume.chapters.size - 1
                binding.pvAHRead.showLoadingTip()
                viewModel.getNovelContent()
                setBottomBarIsEnable(false)
                return@launch
            }
            binding.pvAHRead.showLoadingTip()
            viewModel.curChapterPos--
            viewModel.getNovelContent()
            setBottomBarIsEnable(false)
        }
    }

    private fun skipToNextChapter() {
        lifecycleScope.launch {
            viewModel.saveReadHistory(binding.pvAHRead.pageNum, binding.pvAHRead.maxPageNum).join()
            if (viewModel.curChapterPos == viewModel.curVolume.chapters.size - 1) { //判断是不是该卷的最后一章
                if (viewModel.curVolumePos == viewModel.novel.volume.size - 1) {
                    Snackbar.make(binding.root, R.string.no_next_chapter, Snackbar.LENGTH_INDEFINITE)
                        .apply {
                            setAnchorView(binding.llAHReadBottomBar) //使它出现在bottomAppBar的上面，避免遮挡内容
                            setAction(R.string.ok) {}
                            show()
                        }
                    return@launch
                }
                viewModel.curVolumePos++
                viewModel.curChapterPos = 0
                binding.pvAHRead.showLoadingTip()
                viewModel.getNovelContent()
                setBottomBarIsEnable(false)
                return@launch
            }
            binding.pvAHRead.showLoadingTip()
            viewModel.curChapterPos++
            viewModel.getNovelContent()
            setBottomBarIsEnable(false)
        }
    }

    private fun showBar() { //显示上下栏
        binding.tbAHRead.animate().translationY(0f)
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
        binding.llAHReadBottomBar.animate().translationY(0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
    }

    private fun hideBar() { //隐藏上下栏
        binding.tbAHRead.animate().translationY(-(binding.tbAHRead.height.toFloat()))
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
        binding.llAHReadBottomBar.animate().translationY(binding.llAHReadBottomBar.height.toFloat())
            .setInterpolator(AccelerateDecelerateInterpolator())
    }

    private fun getHideBarColor() = getIsInDarkMode().let {
        if (it) { //夜间模式
            "#" + viewModel.getBgColorNight()
        } else {
            "#" + viewModel.getBgColorDay()
        }
    }

    private fun setBottomBarIsEnable(value: Boolean) {
        binding.bAHReadConfig.isEnabled = value
        binding.bAHReadNextChapter.isEnabled = value
        binding.bAHReadPreviousChapter.isEnabled = value
        binding.sAHReadProgress.isEnabled = value
    }
}