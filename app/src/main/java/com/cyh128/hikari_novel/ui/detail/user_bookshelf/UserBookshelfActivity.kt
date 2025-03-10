package com.cyh128.hikari_novel.ui.detail.user_bookshelf

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.ActivityUserBookshelfBinding
import com.cyh128.hikari_novel.ui.detail.NovelInfoActivity
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserBookshelfActivity: BaseActivity<ActivityUserBookshelfBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[UserBookshelfViewModel::class.java] }
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        viewModel.uid = intent.getStringExtra("uid")!!

        setSupportActionBar(binding.tbAUserBookshelf)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbAUserBookshelf.setNavigationOnClickListener { finish() }

        val adapter = UserBookshelfListAdapter(
            list = viewModel.list,
            onItemClick = { aid ->
                startActivity<NovelInfoActivity> {
                    putExtra("aid", aid)
                }
            }
        )

        binding.rvAUserBookshelf.apply {
            layoutManager = LinearLayoutManager(this@UserBookshelfActivity)
            this.adapter = adapter
        }

        receiveEvent<Event>("event_user_bookshelf_activity") { event ->
            when (event) {
                Event.LoadSuccessEvent -> {
                    adapter.notifyDataSetChanged()
                    binding.srlAUserBookshelf.isRefreshing = false
                    if (viewModel.list.isEmpty()) {
                        Toast.makeText(this@UserBookshelfActivity, R.string.user_bookshelf_no_content, Toast.LENGTH_SHORT).show()
                    }
                }

                is Event.NetworkErrorEvent -> {
                    binding.srlAUserBookshelf.isRefreshing = false
                }

                else -> {}
            }
        }

        binding.srlAUserBookshelf.setOnRefreshListener {
            viewModel.getUserBookshelf()
        }
        binding.srlAUserBookshelf.isRefreshing = true
        viewModel.getUserBookshelf()
    }
}