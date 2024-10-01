package com.cyh128.hikarinovel.ui.view.other

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.cyh128.hikarinovel.base.BaseFragment
import com.cyh128.hikarinovel.databinding.ViewEmptyBinding
import com.cyh128.hikarinovel.databinding.ViewLoadingBinding
import com.cyh128.hikarinovel.databinding.ViewMessageBinding

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