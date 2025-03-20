package com.cyh128.hikari_novel.ui.main.bookshelf

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.ListViewType
import com.cyh128.hikari_novel.databinding.FragmentBookshelfListBinding
import com.cyh128.hikari_novel.ui.detail.NovelInfoActivity
import com.cyh128.hikari_novel.ui.main.BookshelfListAdapter
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class BookshelfListFragment : BaseFragment<FragmentBookshelfListBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireParentFragment())[BookshelfViewModel::class] }
    lateinit var adapter: BookshelfListAdapter
    private var currentBookshelfId by Delegates.notNull<Int>()
    private lateinit var displayList: List<BookshelfNovelInfo>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBookshelfId = requireArguments().getInt("classId")
        displayList = (requireParentFragment() as BookshelfContentFragment).displayList

        adapter = BookshelfListAdapter(
            list = displayList,
            listViewType = viewModel.listViewType,
            onItemClick = { aid ->
                startActivity<NovelInfoActivity> {
                    putExtra("aid", aid)
                }
            },
            onSelected = { count ->
                (requireParentFragment().requireParentFragment() as BookshelfFragment).binding.tbFBookshelfSelection.title = count.toString()
            },
            onMultiSelectModeChange = { value ->
                (requireParentFragment().requireParentFragment() as BookshelfFragment).setSelectionToolbar(value)
                if (!value) (requireParentFragment().requireParentFragment() as BookshelfFragment).binding.tbFBookshelfSelection.title = null
            }
        )

        binding.rvFBookshelfList.apply {
            layoutManager = if (viewModel.listViewType == ListViewType.Linear) LinearLayoutManager(context) else GridLayoutManager(context, 3)
            this.adapter = this@BookshelfListFragment.adapter
        }

        receiveEvent<Event>("event_bookshelf_list_fragment") { event ->
            if (!isVisible) return@receiveEvent //防止与其它在栈内的fragment互相冲突

            when(event) {
                Event.EditBookshelfEvent -> {
                    adapter.setMultiSelectMode(true)
                }

                Event.ExitSelectionModeEvent -> {
                    adapter.setMultiSelectMode(false)
                }

                Event.SelectAllEvent -> {
                    adapter.setSelectAll(true)
                }

                Event.DeselectEvent -> {
                    adapter.setSelectAll(false)
                }

                Event.MoveNovelFromListEvent -> {
                    MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.move_to_other_bookshelf)
                        .setIcon(R.drawable.ic_drive_file_move)
                        .setNegativeButton(R.string.cancel, null)
                        .setItems(
                            arrayOf(
                                getString(R.string.default_bookshelf),
                                getString(R.string.first_bookshelf),
                                getString(R.string.second_bookshelf),
                                getString(R.string.third_bookshelf),
                                getString(R.string.fourth_bookshelf),
                                getString(R.string.fifth_bookshelf)
                            ),
                        ) { dialog: DialogInterface, which: Int ->
                            if (which == currentBookshelfId) {
                                adapter.setMultiSelectMode(false)
                                dialog.dismiss()
                                return@setItems
                            }

                            viewModel.moveNovelToOther(
                                adapter.getSelectedList(),
                                currentBookshelfId,
                                which
                            )
                            adapter.setMultiSelectMode(false)
                            dialog.dismiss()
                        }
                        .show()
                }

                Event.RemoveNovelFromListEvent -> {
                    viewModel.removeNovelFromList(adapter.getSelectedList(), currentBookshelfId)
                    adapter.setMultiSelectMode(false)
                }

                else -> {}
            }
        }
    }
}