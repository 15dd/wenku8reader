package com.cyh128.hikari_novel.ui.other

import android.os.Bundle
import android.view.View
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.ViewEmptyBinding
import com.cyh128.hikari_novel.databinding.ViewLoadingBinding
import com.cyh128.hikari_novel.databinding.ViewMessageBinding

class EmptyView: BaseFragment<ViewEmptyBinding>()

class LoadingView: BaseFragment<ViewLoadingBinding>()

class MessageView: BaseFragment<ViewMessageBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvVMessage.text = requireArguments().getString("msg")
    }
}