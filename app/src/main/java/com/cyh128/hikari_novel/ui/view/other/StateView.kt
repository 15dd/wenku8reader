package com.cyh128.hikari_novel.ui.view.other

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.ViewEmptyBinding
import com.cyh128.hikari_novel.databinding.ViewLoadingBinding
import com.cyh128.hikari_novel.databinding.ViewMessageBinding

class EmptyView: BaseFragment<ViewEmptyBinding>()

class LoadingView: BaseFragment<ViewLoadingBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            binding.root.setBackgroundColor(Color.parseColor(it.getString("color")))
        }
    }
}

class MessageView: BaseFragment<ViewMessageBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvVMessage.text = requireArguments().getString("msg")
    }
}