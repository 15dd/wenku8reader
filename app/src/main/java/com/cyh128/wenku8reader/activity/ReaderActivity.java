package com.cyh128.wenku8reader.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.cyh128.wenku8reader.classLibrary.ContentsCcssClass;
import com.cyh128.wenku8reader.classLibrary.ContentsVcssClass;
import com.cyh128.wenku8reader.util.VarTemp;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.fragment.ReadFragment;
import com.cyh128.wenku8reader.util.NavbarStatusbarInit;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {
    private String text;
    private BottomAppBar bottomAppBar;
    private BottomSheetDialog bottomSheetDialog;
    private androidx.appcompat.widget.Toolbar toolbar;
    private AppBarLayout appBarLayout;
    public static List<ContentsVcssClass> vcss = new ArrayList<>();
    public static List<List<ContentsCcssClass>> ccss = new ArrayList<>();
    public static String bookUrl = null;
    public static int ccssPosition = 0;
    public static int vcssPosition = 0;
    private ReadFragment readFragment;
    private Dialog dialog;
    private NestedScrollView nestedScrollView;
    private Slider fontSizeSlider, lineSpacingSlider;
    private TextView progressText;
    private GestureDetector gestureDetector; //https://juejin.cn/post/7032900181519515685
    private MyGestureListener myGestureListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        //textView = findViewById(R.id.text_act_reader);
        toolbar = findViewById(R.id.toolbar_act_reader);
        bottomAppBar = findViewById(R.id.bottomAppBar_act_reader);
        nestedScrollView = findViewById(R.id.scrollView_act_reader);
        progressText = findViewById(R.id.text_act_reader_progress);
        appBarLayout = findViewById(R.id.appbarlayout_act_reader);
        NavbarStatusbarInit.allTransparent(this);

        dialog = new Dialog(ReaderActivity.this);
        View contentView = LayoutInflater.from(ReaderActivity.this).inflate(R.layout.alert_progress_dialog, null);
        dialog.setContentView(contentView);
        dialog.setCancelable(false);
        dialog.show();

        vcss = ContentsActivity.vcss;
        ccss = ContentsActivity.ccss;
        bookUrl = ContentsActivity.bookUrl;
        ccssPosition = ContentsActivity.ccssPosition;//设置为在目录点击的章节的索引值(childPosition)，以便跳转上一章节或者下一章节
        vcssPosition = ContentsActivity.vcssPosition;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });
        toolbar.setTitle(ccss.get(vcssPosition).get(ccssPosition).ccss);


        bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(ReaderActivity.this).inflate(R.layout.bottom_sheet_act_reader, null, false);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setDismissWithAnimation(true);

        fontSizeSlider = bottomSheetView.findViewById(R.id.slider_bottom_sheet_act_reader);
        fontSizeSlider.setValue(VarTemp.readerFontSize);//设置为之前字体大小的值
        fontSizeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {//字体大小滑动条监听
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                readFragment.setContentFontSize(slider.getValue());
            }
        });
        lineSpacingSlider = bottomSheetView.findViewById(R.id.slider_bottom_sheet_act_reader_linespacing);
        lineSpacingSlider.setValue(VarTemp.readerLineSpacing);
        lineSpacingSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                readFragment.setContentLineSpacing(slider.getValue());
            }
        });

        readFragment = new ReadFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_act_reader, readFragment)
                .commit();

        new Thread(() -> {
            try {
                List<List<String>> allContent = Wenku8Spider.Content(bookUrl, ccss.get(vcssPosition).get(ccssPosition).url);
                this.text = allContent.get(0).get(0);

            } catch (Exception e) {
                runOnUiThread(() -> new MaterialAlertDialogBuilder(ReaderActivity.this)
                        .setTitle("网络超时")
                        .setMessage("连接超时，可能是服务器出错了或者您正在连接VPN或代理服务器，请稍后再试")
                        .setIcon(R.drawable.timeout)
                        .setCancelable(false)
                        .setPositiveButton("明白", (dialog, which) -> finish())
                        .show());
                return;
            }
            Message msg = new Message();
            handler.sendMessage(msg);
        }).start();

        setBottomAppBarListener();//设置底栏菜单按钮监听

        nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int start = nestedScrollView.getHeight() + scrollY;
                int bottom = nestedScrollView.getChildAt(0).getHeight();
                DecimalFormat df = new DecimalFormat("0.00");
                String result = df.format((float) start / bottom * 100);
                result = result.substring(0, result.indexOf("."));
                progressText.setText("阅读进度:" + result + "%");
            }
        });

        //隐藏appbar和bottomAppbar
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                appBarLayout.setVisibility(View.GONE);
                bottomAppBar.setVisibility(View.GONE);
            }
        }.start();

        myGestureListener = new MyGestureListener();
        gestureDetector = new GestureDetector(this,myGestureListener);
        findViewById(R.id.scrollView_act_reader).setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { //https://juejin.cn/post/7032900181519515685
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() { //销毁时，保存设置的字体大小,保存阅读进度
        super.onDestroy();
        new Thread(() -> {
            if (VarTemp.readerFontSize != fontSizeSlider.getValue() || VarTemp.readerLineSpacing != lineSpacingSlider.getValue()) {//判断是否有改动
                VarTemp.readerFontSize = fontSizeSlider.getValue();
                VarTemp.readerLineSpacing = lineSpacingSlider.getValue();

                ContentValues values = new ContentValues();
                values.put("_id", 1);
                values.put("fontSize", fontSizeSlider.getValue());
                values.put("lineSpacing",lineSpacingSlider.getValue());
                VarTemp.db.replace("setting", null, values);
            }

            ContentValues values = new ContentValues();
            values.put("bookUrl", bookUrl);
            values.put("indexUrl", ccss.get(vcssPosition).get(ccssPosition).url);
            values.put("title", ccss.get(vcssPosition).get(ccssPosition).ccss);
            values.put("location", nestedScrollView.getScrollY());
            VarTemp.db.replace("readHistory", null, values);
        }).start();
    }

    private void setBottomAppBarListener() {//设置底栏菜单按钮监听
        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.setting) {
                bottomSheetDialog.show();
                return true;
            } else if (item.getItemId() == R.id.previous) {
                if (ccssPosition == 0) { //判断是不是该卷的第一章
                    Snackbar snackbar = Snackbar.make(toolbar, "没有上一章，已经是这一卷的第一章了", Snackbar.LENGTH_SHORT);
                    snackbar.setAnchorView(bottomAppBar);//使它出现在bottomAppBar的上面，避免遮挡内容
                    snackbar.show();
                    return true;
                }
                ccssPosition--;

                dialog.show();
                toolbar.setTitle(ccss.get(vcssPosition).get(ccssPosition).ccss);
                new Thread(() -> {
                    try {
                        readFragment.setContent();
                        dialog.dismiss();
                        runOnUiThread(() -> nestedScrollView.post(() -> nestedScrollView.scrollTo(0, 0)));
                    } catch (Exception e) {
                        runOnUiThread(() -> new MaterialAlertDialogBuilder(ReaderActivity.this)
                                .setTitle("网络超时")
                                .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                                .setIcon(R.drawable.timeout)
                                .setCancelable(false)
                                .setPositiveButton("明白", (dialog, which) -> finish())
                                .show());
                    }
                }).start();

                return true;
            } else if (item.getItemId() == R.id.next) {
                if (ccssPosition == ccss.get(vcssPosition).size() - 1) {//判断是不是该卷的最后一章
                    Snackbar snackbar = Snackbar.make(toolbar, "没有下一章，已经是这一卷的最后一章了", Snackbar.LENGTH_SHORT);
                    snackbar.setAnchorView(bottomAppBar);//使它出现在bottomAppBar的上面，避免遮挡内容
                    snackbar.show();
                    return true;
                }
                ccssPosition++;

                dialog.show();
                toolbar.setTitle(ccss.get(vcssPosition).get(ccssPosition).ccss);
                new Thread(() -> {
                    try {
                        readFragment.setContent();
                        dialog.dismiss();
                        runOnUiThread(() -> nestedScrollView.post(() -> nestedScrollView.scrollTo(0, 0)));
                    } catch (Exception e) {
                        runOnUiThread(() -> new MaterialAlertDialogBuilder(ReaderActivity.this)
                                .setTitle("网络超时")
                                .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                                .setIcon(R.drawable.timeout)
                                .setCancelable(false)
                                .setPositiveButton("明白", (dialog, which) -> finish())
                                .show());
                    }
                }).start();

                return true;
            }
            return false;
        });
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            new Thread(() -> {
                try {
                    readFragment.setContent();
                    dialog.dismiss();

                    Message msg2 = new Message();
                    scrollToTarget.sendMessage(msg2);
                } catch (Exception e) {
                    runOnUiThread(() -> new MaterialAlertDialogBuilder(ReaderActivity.this)
                            .setTitle("网络超时")
                            .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                            .setIcon(R.drawable.timeout)
                            .setCancelable(false)
                            .setPositiveButton("明白", (dialog, which) -> finish())
                            .show());
                }
            }).start();
            return false;
        }
    });

    private final Handler scrollToTarget = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            String sql = String.format("select * from readHistory where bookUrl='%s'", bookUrl);
            Cursor cursor = VarTemp.db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.move(i);
                    int position = cursor.getInt(3);
                    if (cursor.getString(2).equals(ccss.get(vcssPosition).get(ccssPosition).ccss)) {
                        Log.d("debug", String.valueOf(position));
                        //nestedScrollView.fling(0);
                        nestedScrollView.post(() -> nestedScrollView.scrollTo(0, position));
                    }
                }
                cursor.close();
            } else {
                nestedScrollView.post(() -> nestedScrollView.scrollTo(0, 0));
            }

            return false;
        }
    });

    private class MyGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(@NonNull MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent e) {
            if (appBarLayout.isShown() && bottomAppBar.isShown()) {
                appBarLayout.setVisibility(View.GONE);
                bottomAppBar.setVisibility(View.GONE);
                return true;
            }
            appBarLayout.setVisibility(View.VISIBLE);
            bottomAppBar.setVisibility(View.VISIBLE);

            return true;
        }

        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {

        }

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

}
