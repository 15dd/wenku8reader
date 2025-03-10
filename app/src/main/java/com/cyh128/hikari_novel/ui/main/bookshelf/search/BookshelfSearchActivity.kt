package com.cyh128.hikari_novel.ui.main.bookshelf.search

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.NovelCover
import com.cyh128.hikari_novel.databinding.ActivityBookshelfSearchBinding
import com.cyh128.hikari_novel.ui.other.EmptyView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BookshelfSearchActivity: BaseActivity<ActivityBookshelfSearchBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[BookshelfSearchViewModel::class] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.tbABookshelfSearch)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbABookshelfSearch.setNavigationOnClickListener { finish() }

        binding.tietABookshelfSearch.apply {
            setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH && event == null) {
                    search(text.toString())
                    hideSoftInput()
                }
                true
            }
        }

        showSoftInput()
    }

    private fun search(keyword: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.searchList.clear()
            viewModel.searchList.addAll(
                viewModel.getAllFlow.take(1).first()?.mapNotNull {
                    if (it.title.contains(keyword)) {
                        NovelCover(
                            aid = it.aid,
                            img = it.img,
                            detailUrl = it.detailUrl,
                            title = it.title
                        )
                    } else null
                } ?: emptyList()
            )
            withContext(Dispatchers.Main) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fcv_a_bookshelf_search,
                    if (viewModel.searchList.isEmpty()) EmptyView() else BookshelfSearchContentFragment()
                ).commit()
            }
        }
    }

    private fun showSoftInput() {
        binding.tietABookshelfSearch.requestFocus() //获取焦点
        //if (!imm.isActive()) //没有显示键盘，弹出
        val imm = binding.tietABookshelfSearch.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.tietABookshelfSearch, 0)
    }

    private fun hideSoftInput() {
        binding.tietABookshelfSearch.clearFocus()
        val imm = binding.tietABookshelfSearch.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.tietABookshelfSearch.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}