package com.cyh128.wenku8reader.activity

import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.fragment.SearchFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class SearchActivity : AppCompatActivity() {
    private lateinit var editText: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        editText = findViewById(R.id.editText_act_search)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_act_search)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        editText.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        editText.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!searchFlag) {
                    Snackbar.make(editText, "两次搜索间隔必须大于5秒", Snackbar.LENGTH_SHORT).show()
                    return@setOnEditorActionListener true
                } else if (editText.text.toString().trim { it <= ' ' }.isEmpty()) {
                    Snackbar.make(editText, "内容不能为空", Snackbar.LENGTH_SHORT).show()
                    return@setOnEditorActionListener true
                }
                val searchFragment = SearchFragment()
                val bundle = Bundle()
                bundle.putString("searchText", editText.text.toString())
                searchFragment.arguments = bundle
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_act_search, searchFragment)
                    .commit()
                searchFlag = false
                object : CountDownTimer(5500, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        searchFlag = true
                    }
                }.start()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(
                    editText.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                editText.clearFocus()
                return@setOnEditorActionListener false
            }
            true
        }
    }

    companion object {
        var searchFlag = true
    }
}
