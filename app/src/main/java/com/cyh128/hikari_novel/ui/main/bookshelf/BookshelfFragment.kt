package com.cyh128.hikari_novel.ui.main.bookshelf

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.FragmentBookshelfBinding
import com.cyh128.hikari_novel.ui.other.EmptyView
import com.cyh128.hikari_novel.util.launchWithLifecycle
import com.drake.channel.receiveEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookshelfFragment : BaseFragment<FragmentBookshelfBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[BookshelfViewModel::class] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiveEvent<Event>("event_bookshelf_fragment") { event ->
            when(event) {
                is Event.NetworkErrorEvent -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.network_error)
                        .setIcon(R.drawable.ic_error)
                        .setMessage(event.msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .show()
                }
                Event.SearchBookshelfFailureEvent -> toEmptyScreen()
                else -> {}
            }
        }

        initListener()

        //防止activity重建导致bookshelf的数据重新获取
        //if (childFragmentManager.findFragmentById(R.id.fcv_f_bookshelf) !is BookshelfContentFragment) toBookshelfScreen()
    }

    private fun initListener() {
        launchWithLifecycle {
            viewModel.bookshelfList.collect {
                if (it.isEmpty()) {
                    toEmptyScreen()
                    binding.tbFBookshelf.menu.findItem(R.id.menu_f_bookshelf_search).isEnabled = false
                } else {
                    toBookshelfScreen() //重建页面，强制刷新书架数据
                    binding.tbFBookshelf.menu.findItem(R.id.menu_f_bookshelf_search).isEnabled = true
                }
            }
        }

        binding.tbFBookshelf.menu.findItem(R.id.menu_f_bookshelf_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = SearchView(requireContext()).apply {
                queryHint = getString(R.string.search)
                isIconified = false

                //去除下划线
                findViewById<LinearLayout>(androidx.appcompat.R.id.submit_area).background = null
                findViewById<LinearLayout>(androidx.appcompat.R.id.search_plate).background = null

                setOnQueryTextListener(
                    object : OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            toSearchScreen(query!!)
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if (newText.isNullOrEmpty()) toBookshelfScreen()
                            return false
                        }
                    }
                )
            }
        }
    }

    private fun toSearchScreen(keyword: String) {
        binding.ablFBookshelf.liftOnScrollTargetViewId = R.id.rv_f_novel_list
        childFragmentManager.beginTransaction().replace(
            R.id.fcv_f_bookshelf,
            BookshelfSearchFragment().apply {
                arguments = Bundle().apply { putString("keyword", keyword) }
            }
        ).commit()
    }

    private fun toBookshelfScreen() {
        binding.ablFBookshelf.liftOnScrollTargetViewId = R.id.rv_f_novel_list
        childFragmentManager.beginTransaction().replace(
            R.id.fcv_f_bookshelf,
            BookshelfContentFragment(),
        ).commit()
    }

    private fun toEmptyScreen() {
        childFragmentManager.beginTransaction().replace(
            R.id.fcv_f_bookshelf,
            EmptyView()
        ).commit()
    }
}