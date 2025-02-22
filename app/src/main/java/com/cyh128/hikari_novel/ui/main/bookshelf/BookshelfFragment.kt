package com.cyh128.hikari_novel.ui.main.bookshelf

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.FragmentBookshelfBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BookshelfFragment : BaseFragment<FragmentBookshelfBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[BookshelfViewModel::class] }

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
                    //TODO
                    true
                }

                R.id.menu_f_bookshelf_sync -> {
                    //TODO 翻译
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.getAllBookshelf()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "getAll Successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }

                else -> throw IllegalArgumentException()
            }
        }
    }
}