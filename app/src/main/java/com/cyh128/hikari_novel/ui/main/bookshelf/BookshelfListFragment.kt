package com.cyh128.hikari_novel.ui.main.bookshelf

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.FragmentBookshelfListBinding
import com.cyh128.hikari_novel.ui.detail.NovelInfoActivity
import com.cyh128.hikari_novel.ui.main.BookshelfListAdapter
import com.cyh128.hikari_novel.util.startActivity
import kotlinx.coroutines.launch

class BookshelfListFragment: BaseFragment<FragmentBookshelfListBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireParentFragment())[BookshelfViewModel::class] }

    lateinit var adapter: BookshelfListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BookshelfListAdapter(
            viewModel.displayList,
            onItemClick = { aid ->
                startActivity<NovelInfoActivity> {
                    putExtra("aid", aid)
                }
            },
            onMultiSelectModeChange = { value ->

            }
        )

        binding.rvFBookshelfList.apply {
            layoutManager = GridLayoutManager(context, 3)
            this.adapter = this@BookshelfListFragment.adapter
        }
        
        lifecycleScope.launch { 
            
        }

        binding.tvFBookshelfList.text = "${viewModel.displayList.size}/${viewModel.getAllFlow.value?.size}/${viewModel.maxCollection}"
    }
}