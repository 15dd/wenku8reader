package com.cyh128.wenku8reader.activity;

import android.animation.Animator;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.bean.ContentsCcssBean;
import com.cyh128.wenku8reader.bean.ContentsVcssBean;
import com.cyh128.wenku8reader.reader.PageView;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.gyf.immersionbar.ImmersionBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends AppCompatActivity {
    public static MaterialToolbar toolbar;
    public static View bottombar;
    public static List<ContentsVcssBean> vcss = new ArrayList<>();
    public static List<List<ContentsCcssBean>> ccss = new ArrayList<>();
    public static String bookUrl = null;
    public static int ccssPosition = 0;
    public static int vcssPosition = 0;
    public static ReadActivity readActivity; //状态栏和导航栏沉浸所使用的
    public static int showBarColor;
    public static int hideBarColor;
    public static Slider readProgress;
    private Button previousChapter, nextChapter;
    private PageView pageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        vcss = ContentsActivity.vcss;
        ccss = ContentsActivity.ccss;
        bookUrl = ContentsActivity.bookUrl;
        ccssPosition = ContentsActivity.ccssPosition;
        vcssPosition = ContentsActivity.vcssPosition;

        pageView = findViewById(R.id.readerView);
        bottombar = findViewById(R.id.bottombar_act_read);
        toolbar = findViewById(R.id.toolbar_act_read);
        readProgress = findViewById(R.id.progress_act_read);
        previousChapter = findViewById(R.id.button_act_read_previousChapter);
        nextChapter = findViewById(R.id.button_act_read_nextChapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });
        toolbar.setTitle(ccss.get(vcssPosition).get(ccssPosition).ccss);

        readActivity = this;

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorSurfaceContainer, typedValue, true);
        showBarColor = typedValue.resourceId;
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true);
        hideBarColor = typedValue.resourceId;

        showBar();
        loadContent();

        readProgress.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                pageView.setPageNum((int) slider.getValue());
                pageView.displayView();
            }
        });

        previousChapter.setOnClickListener(v -> {
            if (ccssPosition == 0) { //判断是不是该卷的第一章
                Snackbar snackbar = Snackbar.make(toolbar, "没有上一章，已经是这一卷的第一章了", Snackbar.LENGTH_SHORT);
                snackbar.setAnchorView(bottombar);//使它出现在bottomAppBar的上面，避免遮挡内容
                snackbar.show();
                return;
            }
            ccssPosition--;
            toolbar.setTitle(ccss.get(vcssPosition).get(ccssPosition).ccss);
            loadContent();
        });

        nextChapter.setOnClickListener(v -> {
            if (ccssPosition == ccss.get(vcssPosition).size() - 1) {//判断是不是该卷的最后一章
                Snackbar snackbar = Snackbar.make(toolbar, "没有下一章，已经是这一卷的最后一章了", Snackbar.LENGTH_SHORT);
                snackbar.setAnchorView(bottombar);//使它出现在bottomAppBar的上面，避免遮挡内容
                snackbar.show();
                return;
            }
            ccssPosition++;
            toolbar.setTitle(ccss.get(vcssPosition).get(ccssPosition).ccss);
            loadContent();
        });
    }

    private void loadContent() {
        new Thread(() -> {
            try {
                List<List<String>> allContent = Wenku8Spider.Content(bookUrl, ccss.get(vcssPosition).get(ccssPosition).url);
                String title = ccss.get(vcssPosition).get(ccssPosition).ccss;
                String text = title + "|" + Html.fromHtml(allContent.get(0).get(0), Html.FROM_HTML_MODE_COMPACT);

                runOnUiThread(() -> {
                    pageView.removeAllViews();//删除上一章加载的内容
                    pageView.setPageNum(1);
                    pageView.setMaxTextPageNum(0);
                    pageView.setImgUrlList(new ArrayList<>());
                    readProgress.setValue(1);//进度条重置

                    if (allContent.get(1) != null && allContent.get(1).size() != 0) {
                        pageView.setImgList((ArrayList<String>) allContent.get(1));
                    }
                    pageView.setText(text);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> new MaterialAlertDialogBuilder(ReadActivity.this)
                        .setTitle("网络超时")
                        .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                        .setIcon(R.drawable.timeout)
                        .setCancelable(false)
                        .setPositiveButton("好的", null)
                        .show());
                ccssPosition--;
            }
        }).start();
    }

    public static void showBar() {
        //顶栏显示动画
        toolbar.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                ImmersionBar.with(readActivity)
                        .statusBarColor(showBarColor)
                        .navigationBarColor(showBarColor)
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init();
            }
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {}
            @Override
            public void onAnimationCancel(@NonNull Animator animation) {}
            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {}
        }).setInterpolator(new AccelerateDecelerateInterpolator());

        //底栏显示动画
        bottombar.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                ImmersionBar.with(readActivity)
                        .statusBarColor(showBarColor)
                        .navigationBarColor(showBarColor)
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init();
            }
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {}
            @Override
            public void onAnimationCancel(@NonNull Animator animation) {}
            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {}
        }).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public static void hideBar() {
        //顶栏隐藏动画
        toolbar.animate().translationY(-toolbar.getHeight()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {}
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                ImmersionBar.with(ReadActivity.readActivity)
                        .statusBarColor(ReadActivity.hideBarColor)
                        .navigationBarColor(ReadActivity.hideBarColor)
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animation) {}
            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {}
        }).setInterpolator(new AccelerateDecelerateInterpolator());

        //底栏隐藏动画
        bottombar.animate().translationY(bottombar.getHeight()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {}
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                ImmersionBar.with(ReadActivity.readActivity)
                        .statusBarColor(ReadActivity.hideBarColor)
                        .navigationBarColor(ReadActivity.hideBarColor)
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init();
            }
            @Override
            public void onAnimationCancel(@NonNull Animator animation) {}
            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {}
        }).setInterpolator(new AccelerateDecelerateInterpolator());
    }
}
