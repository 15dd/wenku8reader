package com.cyh128.hikarinovel.ui.view.main.main.home.category

import android.os.Bundle
import android.view.View
import com.cyh128.hikarinovel.R
import com.cyh128.hikarinovel.base.BaseFragment
import com.cyh128.hikarinovel.databinding.FragmentCategoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>() {
    private var selectSort: String? = "0"
    private var tempCategory: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cgFCategory.setOnCheckedStateChangeListener { chipGroup, _ ->
            if (tempCategory.isNullOrEmpty()) {
                return@setOnCheckedStateChangeListener
            }

            when(chipGroup.checkedChipId) {
                R.id.c_f_category_sort_by_update -> {
                    selectSort = "0"
                    showContentScreen(tempCategory!!)
                }
                R.id.c_f_category_sort_by_heat -> {
                    selectSort = "1"
                    showContentScreen(tempCategory!!)
                }
                R.id.c_f_category_sort_by_completion -> {
                    selectSort = "2"
                    showContentScreen(tempCategory!!)
                }
                R.id.c_f_category_sort_by_animated -> {
                    selectSort = "3"
                    showContentScreen(tempCategory!!)
                }
            }
        }

        binding.actFCategory.setOnItemClickListener { parent, _, position, _ ->
            showContentScreen(parent.getItemAtPosition(position) as String)
        }
    }

    private fun showContentScreen(category: String) {
        childFragmentManager.beginTransaction().replace(
            R.id.fcv_f_category,
            CategoryContentFragment().apply {
                arguments = Bundle().apply {
                    tempCategory = category
                    putString("category", tempCategory)
                    putString("sort",selectSort)
                }
            }
        ).commit()
    }
}