package com.cyh128.hikari_novel.ui.main.bookshelf

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.FragmentBookshelfBinding
import com.cyh128.hikari_novel.ui.main.bookshelf.search.BookshelfSearchActivity
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import com.drake.channel.sendEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookshelfFragment : BaseFragment<FragmentBookshelfBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[BookshelfViewModel::class] }

    private lateinit var syncingDialog: AlertDialog

    private val tabTexts = listOf(
        R.string.default_bookshelf,
        R.string.first_bookshelf,
        R.string.second_bookshelf,
        R.string.third_bookshelf,
        R.string.fourth_bookshelf,
        R.string.fifth_bookshelf
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiveEvent<Event>("event_bookshelf_fragment") { event ->
            when (event) {
                Event.LoadSuccessEvent -> {
                    Toast.makeText(context, getString(R.string.sync_completed), Toast.LENGTH_SHORT).show()
                    syncingDialog.dismiss()
                }

                is Event.NetworkErrorEvent -> {
                    MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.network_error)
                        .setIcon(R.drawable.ic_error)
                        .setMessage(event.msg)
                        .setPositiveButton(R.string.ok, null)
                        .show()
                }

                else -> {}
            }
        }

        initView()
        initListener()
    }

    private fun initView() {
        binding.vpFBookshelf.adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            override fun getItemCount(): Int = tabTexts.size
            override fun createFragment(position: Int): Fragment = BookshelfContentFragment().apply {
                arguments = Bundle().apply {
                    putInt("classId", position)
                }
            }
        }

        TabLayoutMediator(binding.tlFBookshelf, binding.vpFBookshelf) { tab, position ->
            tab.text = getString(tabTexts[position])
        }.attach()
    }

    private fun initListener() {
        binding.tbFBookshelf.setOnMenuItemClickListener { menu ->
            when(menu.itemId) {
                R.id.menu_f_bookshelf_search -> {
                    startActivity<BookshelfSearchActivity>()
                    true
                }

                R.id.menu_f_bookshelf_edit -> {
                    sendEvent(Event.EditBookshelfEvent, "event_bookshelf_list_fragment")
                    true
                }

                R.id.menu_f_bookshelf_sync -> {
                    syncingDialog = MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.sync)
                        .setMessage(getString(R.string.sync_tip))
                        .setCancelable(false)
                        .show()

                    viewModel.getAllBookshelf()
                    true
                }
                else -> throw IllegalArgumentException()
            }
        }

        binding.tbFBookshelfSelection.setNavigationOnClickListener {
            setSelectionToolbar(false)
            sendEvent(Event.ExitSelectionModeEvent, "event_bookshelf_list_fragment")
        }

        binding.tbFBookshelfSelection.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.menu_f_bookshelf_select_all -> {
                    sendEvent(Event.SelectAllEvent, "event_bookshelf_list_fragment")
                    true
                }

                R.id.menu_f_bookshelf_deselect -> {
                    sendEvent(Event.DeselectEvent, "event_bookshelf_list_fragment")
                    true
                }

                R.id.menu_f_bookshelf_move -> {
                    sendEvent(Event.MoveNovelFromListEvent, "event_bookshelf_list_fragment")
                    true
                }

                R.id.menu_f_bookshelf_remove -> {
                    sendEvent(Event.RemoveNovelFromListEvent, "event_bookshelf_list_fragment")
                    true
                }

                else -> throw IllegalArgumentException()
            }
        }
    }

    fun setSelectionToolbar(value: Boolean) {
        binding.tbFBookshelfSelection.visibility = if (value) View.VISIBLE else View.INVISIBLE
        binding.tbFBookshelf.visibility = if (!value) View.VISIBLE else View.INVISIBLE
        binding.vpFBookshelf.isUserInputEnabled = !value
        for (i in 0 until binding.tlFBookshelf.tabCount) {
            val tabStrip = binding.tlFBookshelf.getChildAt(0) as ViewGroup
            val tabView = tabStrip.getChildAt(i)
            tabView.isEnabled = !value
        }
    }
}