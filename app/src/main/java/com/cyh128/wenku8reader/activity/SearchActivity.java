package com.cyh128.wenku8reader.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cyh128.wenku8reader.fragment.SearchFragment;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.util.NavbarStatusbarInit;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class SearchActivity extends AppCompatActivity {
    private TextInputEditText editText;
    public static boolean searchFlag = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        NavbarStatusbarInit.allTransparent(SearchActivity.this);
        editText = findViewById(R.id.editText_act_search);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_act_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });

        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!searchFlag) {
                    Snackbar.make(editText, "两次搜索间隔必须大于5秒", BaseTransientBottomBar.LENGTH_SHORT).show();
                    return true;
                }
                SearchFragment searchFragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("searchText", String.valueOf(editText.getText()));
                searchFragment.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_act_search, searchFragment)
                        .commit();
                searchFlag = false;

                new CountDownTimer(5500, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        searchFlag = true;
                    }
                }.start();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                editText.clearFocus();
                return false;
            }
            return true;
        });
    }
}
