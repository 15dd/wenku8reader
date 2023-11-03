package com.cyh128.wenku8reader.newReader;

import android.animation.Animator;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.activity.ContentsActivity;
import com.cyh128.wenku8reader.activity.SelectColorActivity;
import com.cyh128.wenku8reader.bean.ContentsCcssBean;
import com.cyh128.wenku8reader.bean.ContentsVcssBean;
import com.cyh128.wenku8reader.util.DatabaseHelper;
import com.cyh128.wenku8reader.util.GlobalConfig;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {
    public MaterialToolbar toolbar;
    public View bottombar;
    public static List<ContentsVcssBean> vcss = new ArrayList<>();
    public static List<List<ContentsCcssBean>> ccss = new ArrayList<>();
    public static String bookUrl = null;
    public static int ccssPosition = 0;
    public static int vcssPosition = 0;
    public static ReaderActivity readerActivity; //状态栏和导航栏沉浸所使用的
    private int showBarColor;
    private String hideBarColor;
    public static Slider readProgress;
    public PageView pageView;
    private Dialog dialog;
    private MaterialAlertDialogBuilder builder;
    private BottomSheetDialog bottomSheetDialog;
    private Slider fontSize, lineSpacing, bottomTextSize;
    public static boolean isNigntMode;
    private MaterialButtonToggleGroup readDirection;
    private MaterialSwitch switchChapter;
    public static int showCount = 0;
    private int historyPosition = -1;//历史记录
    private View bottomSheetView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //我已经逐渐看不懂这个阅读器是怎么写出来的了，好像想整个重写一遍。。。。。。
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reader);
        readerActivity = this;

        historyPosition = getIntent().getIntExtra("position", -1);//是否有阅读记录

        vcss = ContentsActivity.vcss;
        ccss = ContentsActivity.ccss;
        bookUrl = ContentsActivity.bookUrl;
        ccssPosition = ContentsActivity.ccssPosition;
        vcssPosition = ContentsActivity.vcssPosition;

        pageView = findViewById(R.id.readerView);
        bottombar = findViewById(R.id.bottombar_act_read);
        toolbar = findViewById(R.id.toolbar_act_read);
        readProgress = findViewById(R.id.progress_act_read);

        Button previousChapter = findViewById(R.id.button_act_read_previousChapter);
        Button nextChapter = findViewById(R.id.button_act_read_nextChapter);
        Button setting = findViewById(R.id.button_act_read_Setting);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });
        toolbar.setTitle(ccss.get(vcssPosition).get(ccssPosition).ccss);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorSurfaceContainer, typedValue, true);
        showBarColor = typedValue.resourceId;
        toolbar.setBackgroundResource(showBarColor);
        bottombar.setBackgroundResource(showBarColor);

        //getSystemService导致的内存泄漏问题 https://blog.csdn.net/xiabing082/article/details/53993298
        UiModeManager uiModeManager = (UiModeManager) getApplicationContext().getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            isNigntMode = true;
        } else {
            isNigntMode = false;
        }

        setBackgroundAndTextColor();//初始化背景颜色的字符串，以便hideBar()调用

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetView = LayoutInflater.from(this).inflate(R.layout.view_bottom_sheet_act_new_reader, null, false);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setDismissWithAnimation(true);
        //保存设置
        bottomSheetDialog.setOnDismissListener(dialog -> {
            hideBar();
            GlobalConfig.newReaderFontSize = fontSize.getValue() + 20f;
            GlobalConfig.newReaderLineSpacing = lineSpacing.getValue();
            GlobalConfig.readerBottomTextSize = bottomTextSize.getValue();
            DatabaseHelper.SaveReaderSetting();
        });


        fontSize = bottomSheetView.findViewById(R.id.slider_bottom_sheet_act_reader_fontsize);
        lineSpacing = bottomSheetView.findViewById(R.id.slider_bottom_sheet_act_reader_linespacing);
        bottomTextSize = bottomSheetView.findViewById(R.id.slider_bottom_sheet_act_reader_bottomfontsize);
        readDirection = bottomSheetView.findViewById(R.id.toggleGroup_bottom_sheet_act_read);
        switchChapter = bottomSheetView.findViewById(R.id.switch_bottom_sheet_act_read);

        fontSize.setValue(GlobalConfig.newReaderFontSize - 20f); //slider上显示的数字比实际字体大小要小20f
        lineSpacing.setValue(GlobalConfig.newReaderLineSpacing);
        bottomTextSize.setValue(GlobalConfig.readerBottomTextSize);
        readDirection.check(GlobalConfig.isUpToDown ? R.id.button_bottom_sheet_act_read_utd : R.id.button_bottom_sheet_act_read_ltr);
        switchChapter.setChecked(GlobalConfig.canSwitchChapterByScroll);

        fontSize.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                pageView.setTextSize(slider.getValue() + 20f); //实际字体大小比slider上显示的数字要大20f
            }
        });

        lineSpacing.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                pageView.setRowSpace(slider.getValue());
            }
        });

        bottomTextSize.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                pageView.setBottomTextSize(slider.getValue());
            }
        });

        readDirection.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.button_bottom_sheet_act_read_ltr && isChecked) {
                pageView.setDirection(Orientation.horizontal);
                GlobalConfig.isUpToDown = false;
            } else if (checkedId == R.id.button_bottom_sheet_act_read_utd && isChecked) {
                pageView.setDirection(Orientation.vertical);
                GlobalConfig.isUpToDown = true;
            }
        });

        readProgress.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                pageView.setPageNum((int) slider.getValue());
            }
        });

        switchChapter.setOnCheckedChangeListener((buttonView, isChecked) -> GlobalConfig.canSwitchChapterByScroll = isChecked);

        previousChapter.setOnClickListener(v -> {
            toPreviousChapter();
        });

        nextChapter.setOnClickListener(v -> {
            toNextChapter();
        });

        setting.setOnClickListener(v -> {
            bottomSheetDialog.show();
        });


        showLoadingDialog();
        loadContent();
        showBar();

        //进入阅读界面2秒后隐藏appbar和bottomAppbar
        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                hideBar();
            }
        }.start();

        NewbieGuide.with(ReaderActivity.this)//新手引导
                .setLabel("imageLongShow")
                .addGuidePage(GuidePage.newInstance().setLayoutRes(R.layout.view_image_long_show))
                .show();

        bottomSheetView.findViewById(R.id.button_select_rgb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReaderActivity.this, SelectColorActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {//防止windowLeaked
            dialog.dismiss();
        }
        //防止内存泄露
        readerActivity = null;
        readProgress = null;

        DatabaseHelper.SaveNewReaderReadHistory(
                bookUrl,
                ccss.get(vcssPosition).get(ccssPosition).url,
                ccss.get(vcssPosition).get(ccssPosition).ccss,
                pageView.getPageNum()
        );
    }

    public void setBackgroundAndTextColor() {
        if (isNigntMode) {
            pageView.setTextColor(Color.parseColor(GlobalConfig.textColorNight));
            pageView.setBackgroundcolor(Color.parseColor(GlobalConfig.backgroundColorNight));
            hideBarColor = GlobalConfig.backgroundColorNight;
        } else {
            pageView.setTextColor(Color.parseColor(GlobalConfig.textColorDay));
            pageView.setBackgroundcolor(Color.parseColor(GlobalConfig.backgroundColorDay));
            hideBarColor = GlobalConfig.backgroundColorDay;
        }
    }

    private void loadContent() {
        new Thread(() -> {
            try {
                List<List<String>> allContent = Wenku8Spider.Content(bookUrl, ccss.get(vcssPosition).get(ccssPosition).url);
                String title = ccss.get(vcssPosition).get(ccssPosition).ccss;
                String text = title + "|" + Html.fromHtml(allContent.get(0).get(0), Html.FROM_HTML_MODE_COMPACT);

                runOnUiThread(() -> {
                    pageView.removeAllViews();//删除上一章加载的内容
                    pageView.setImgUrlList(new ArrayList<>());
                    readProgress.setValue(1);//进度条重置
                    showCount = 0;

                    setBackgroundAndTextColor();
                    if (allContent.get(1) != null && allContent.get(1).size() != 0) {
                        pageView.setImgUrlList((ArrayList<String>) allContent.get(1));
                    }
                    pageView.setText(text);
                    pageView.setTextSize(GlobalConfig.newReaderFontSize);
                    pageView.setBottomTextSize(GlobalConfig.readerBottomTextSize);
                    pageView.setRowSpace(GlobalConfig.newReaderLineSpacing);
                    pageView.setDirection(GlobalConfig.isUpToDown ? Orientation.vertical : Orientation.horizontal);
                    if (historyPosition != -1) { //如果有传递历史记录，就执行
                        pageView.setPageNum(historyPosition);
                        historyPosition = -1;
                    }

                    if (dialog != null) dialog.dismiss();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> new MaterialAlertDialogBuilder(ReaderActivity.this)
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

    public void showLoadingDialog() {
        dialog = new Dialog(this);
        View contentView = LayoutInflater.from(this).inflate(R.layout.view_alert_progress_dialog, null);
        builder = new MaterialAlertDialogBuilder(this);
        builder.setView(contentView);
        builder.setCancelable(false);
        dialog = builder.show();
    }

    public enum Direction {Next, Previous}
    public void switchChapter(Direction direction) {
        if (direction == Direction.Next) {
            runOnUiThread(() -> new MaterialAlertDialogBuilder(ReaderActivity.this)
                    .setTitle("切换下一章")
                    .setMessage("是否继续？")
                    .setIcon(R.drawable.info2)
                    .setCancelable(false)
                    .setPositiveButton("继续", (dialog, which) -> toNextChapter())
                    .setNegativeButton("取消", null)
                    .show());
        } else if (direction == Direction.Previous) {
            runOnUiThread(() -> new MaterialAlertDialogBuilder(ReaderActivity.this)
                    .setTitle("切换上一章")
                    .setMessage("是否继续？")
                    .setIcon(R.drawable.info2)
                    .setCancelable(false)
                    .setPositiveButton("继续", (dialog, which) -> toPreviousChapter())
                    .setNegativeButton("取消", null)
                    .show());
        }
    }

    public void toNextChapter() {
        runOnUiThread(() -> {
            if (ccssPosition == ccss.get(vcssPosition).size() - 1) {//判断是不是该卷的最后一章
                Snackbar snackbar = Snackbar.make(toolbar, "没有下一章，已经是这一卷的最后一章了", Snackbar.LENGTH_SHORT);
                snackbar.setAnchorView(bottombar);//使它出现在bottomAppBar的上面，避免遮挡内容
                snackbar.show();
                return;
            }

            showLoadingDialog();

            ccssPosition++;
            toolbar.setTitle(ccss.get(vcssPosition).get(ccssPosition).ccss);
            loadContent();
        });
    }

    public void toPreviousChapter() {
        runOnUiThread(() -> {
            if (ccssPosition == 0) { //判断是不是该卷的第一章
                Snackbar snackbar = Snackbar.make(toolbar, "没有上一章，已经是这一卷的第一章了", Snackbar.LENGTH_SHORT);
                snackbar.setAnchorView(bottombar);//使它出现在bottomAppBar的上面，避免遮挡内容
                snackbar.show();
                return;
            }

            showLoadingDialog();

            ccssPosition--;
            toolbar.setTitle(ccss.get(vcssPosition).get(ccssPosition).ccss);
            loadContent();
        });
    }

    public void showBar() {
        //顶栏显示动画
        toolbar.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                Log.d("tag","readeractivity hide:"+hideBarColor);
                ImmersionBar.with(ReaderActivity.this)
                        .statusBarColor(showBarColor)
                        .navigationBarColor(showBarColor)
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init();
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
            }
        }).setInterpolator(new AccelerateDecelerateInterpolator());

        //底栏显示动画
        bottombar.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                ImmersionBar.with(ReaderActivity.this)
                        .statusBarColor(showBarColor)
                        .navigationBarColor(showBarColor)
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init();
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
            }
        }).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public void hideBar() {
        //顶栏隐藏动画
        toolbar.animate().translationY(-toolbar.getHeight()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                ImmersionBar.with(ReaderActivity.this)
                        .statusBarColor(hideBarColor)
                        .navigationBarColor(hideBarColor)
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
            }
        }).setInterpolator(new AccelerateDecelerateInterpolator());

        //底栏隐藏动画
        bottombar.animate().translationY(bottombar.getHeight()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                ImmersionBar.with(ReaderActivity.this)
                        .statusBarColor(hideBarColor)
                        .navigationBarColor(hideBarColor)
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {
            }
        }).setInterpolator(new AccelerateDecelerateInterpolator());
    }
}
