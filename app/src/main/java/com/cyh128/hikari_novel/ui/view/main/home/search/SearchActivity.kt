package com.cyh128.hikari_novel.ui.view.main.home.search

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.SearchMode
import com.cyh128.hikari_novel.data.source.local.database.search_history.SearchHistoryEntity
import com.cyh128.hikari_novel.databinding.ActivitySearchBinding
import com.cyh128.hikari_novel.ui.view.other.EmptyView
import com.cyh128.hikari_novel.ui.view.other.LoadingView
import com.cyh128.hikari_novel.ui.view.other.MessageView
import com.cyh128.hikari_novel.util.launchWithLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private lateinit var searchHistoryChipAdapter: SearchHistoryChipAdapter
    private val viewModel by lazy { ViewModelProvider(this)[SearchViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.tbASearch)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbASearch.setNavigationOnClickListener { finish() }

        intent.getStringExtra("author")?.let {//如果不为空，则说明是来自NovelInfoActivity的Intent
            binding.cASearchByAuthor.isChecked = true
            binding.tietASearch.setText(it)
            search(it)
        }

        launchWithLifecycle {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    Event.SearchResultEmptyEvent -> showEmptyScreen()
                    Event.SearchInitSuccessEvent -> showContentScreen()
                    Event.SearchInitErrorCauseByInFiveSecondEvent -> showMessageScreen(
                        getString(R.string.in_five_second_tip)
                    )

                    is Event.NetWorkErrorEvent -> {
                        MaterialAlertDialogBuilder(this@SearchActivity)
                            .setTitle(R.string.network_error)
                            .setIcon(R.drawable.ic_error)
                            .setMessage(event.msg)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                            .show()
                    }

                    Event.RefreshSearchHistoryEvent -> searchHistoryChipAdapter.notifyDataSetChanged()

                    else -> {}
                }
            }
        }

        //初始化搜索记录列表
        searchHistoryChipAdapter = SearchHistoryChipAdapter(
            list = viewModel.historyList,
            onItemClick = { text ->
                setEditText(text)
            }
        )
        binding.rvASearch.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = searchHistoryChipAdapter
        }

        initListener()

        //弹起键盘
        showSoftInput()

        viewModel.getSearchHistory() //获取搜索历史记录
    }

    private fun initListener() {
        binding.tietASearch.apply {
            setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH && event == null) {
                    search(text.toString())
                }
                true
            }
        }

        binding.bASearchClearAllHistory.setOnClickListener {
            //删除所有搜索记录
            if (viewModel.historyList.isEmpty()) {
                Snackbar.make(it, R.string.no_history_can_clear_tip, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.deleteAllSearchHistory()
        }
    }

    fun search(keyword: String) {
        binding.tlASearch.error = null
        if (keyword.isEmpty()) {
            binding.tlASearch.error = getString(R.string.not_null)
            return
        }

        showLoadingScreen()

        viewModel.keyword = keyword
        viewModel.searchMode =
            if (this.binding.cASearchByNovelTitle.isChecked) SearchMode.TITLE else SearchMode.AUTHOR
        viewModel.getData(true)

        hideSoftInput()

        //添加搜索历史记录
        viewModel.addOrReplaceSearchHistory(SearchHistoryEntity(keyword))
    }

    private fun setEditText(text: String) = binding.tietASearch.setText(text)

    private fun showSoftInput() {
        binding.tietASearch.requestFocus() //获取焦点
        //if (!imm.isActive()) //没有显示键盘，弹出
        val imm =
            binding.tietASearch.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.tietASearch, 0)
    }

    private fun hideSoftInput() {
        binding.tietASearch.clearFocus()
        val imm =
            binding.tietASearch.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            binding.tietASearch.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun showContentScreen() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fcv_a_search,
            SearchContentFragment()
        ).commit()
    }

    private fun showEmptyScreen() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fcv_a_search,
            EmptyView()
        ).commit()
    }

    private fun showLoadingScreen() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fcv_a_search,
            LoadingView()
        ).commit()
    }

    private fun showMessageScreen(msg: String?) {
        supportFragmentManager.beginTransaction().replace(
            R.id.fcv_a_search,
            MessageView().apply { arguments = Bundle().apply { putString("msg", msg) } }
        ).commit()
    }
}