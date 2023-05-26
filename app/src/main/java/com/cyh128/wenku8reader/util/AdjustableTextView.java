package com.cyh128.wenku8reader.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class AdjustableTextView {
    //https://blog.csdn.net/z912662880/article/details/104373357 点击展开textview
    private String string;

//控件id

    private final TextView textView;

//设置要显示的行数

    private final int length;

    private String hiddenString;

    private String displayString;
    private Context context;

    public AdjustableTextView(TextView textView, int length, Context context) {

        this.textView = textView;

        this.length = length;

        this.context = context;
    }

    public void hiddenText() {

        string = textView.getText().toString();

        //设置textView可点击

        textView.setMovementMethod(LinkMovementMethod.getInstance());

        //注册一个需要隐藏文本的Text的观察者来监听视图树，防止报错

        ViewTreeObserver viewTreeObserver = textView.getViewTreeObserver();

   /*interface ViewTreeObserver.OnGlobalLayoutListener

   当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时，所要调用的回调函数的接口类  */

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override

            public void onGlobalLayout() {

                //注册一个需要隐藏文本的Text的观察者来监听视图树

                ViewTreeObserver viewTreeObserver = textView.getViewTreeObserver();

                //移除监听，防止后续继续监听

                viewTreeObserver.removeOnGlobalLayoutListener(this);

                //如果文本的行数大于要求的行数

                if (textView.getLineCount() > length) {

                    //得到文本前三行最后一个字符的位置

                    int lineEnd = textView.getLayout().getLineEnd(length - 1);

                    //拼接需要显示的字符串

                    hiddenString = string.substring(0, lineEnd - 5) + "...展开";

                    textView.setText(getClickableSpan(lineEnd));

                }

            }

        });

    }

    private SpannableString getClickableSpan(int count) {

        class Listener extends ClickableSpan implements View.OnClickListener {

            //文字点击事件

            @Override

            public void onClick(@NonNull View widget) {

                textView.setText(string);

                //调用显示文本的拼接方法

                displayText();

            }

            @Override

            public void updateDrawState(@NonNull TextPaint ds) {

                super.updateDrawState(ds);

                ds.setUnderlineText(false);

            }

        }

        SpannableString spannableString = new SpannableString(hiddenString);

        //设置前景色

        TypedValue typedValue = new TypedValue(); //获取 [?attr/] 的颜色
        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimaryVariant, typedValue, true);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(typedValue.data);

        //设置字体大小

        spannableString.setSpan(new RelativeSizeSpan(1.1f), count - 5, count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置字体可点击

        spannableString.setSpan(new Listener(), count - 5, count, Spanned.SPAN_MARK_MARK);

        //添加前景色
        spannableString.setSpan(foregroundColorSpan, count - 5, count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;

    }

    public void displayText() {

        string = textView.getText().toString();

        //设置textView可点击

        textView.setMovementMethod(LinkMovementMethod.getInstance());

        //注册一个需要隐藏文本的Text的观察者来监听视图树，防止报错

        ViewTreeObserver viewTreeObserver = textView.getViewTreeObserver();

    /*interface ViewTreeObserver.OnGlobalLayoutListener

   当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时，所要调用的回调函数的接口类  */

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override

            public void onGlobalLayout() {

                //注册一个需要隐藏文本的Text的观察者来监听视图树，防止报错

                ViewTreeObserver viewTreeObserver = textView.getViewTreeObserver();

                //移除监听，防止后续继续监听

                viewTreeObserver.removeOnGlobalLayoutListener(this);

                //得到最后一个字符串的位置

                int lineEnd2 = textView.getLayout().getLineEnd(textView.getLineCount() - 1);

                //在原字符串后面加上“隐藏”

                displayString = string.substring(0, lineEnd2) + "隐藏";

                textView.setText(getClickableSpan2(lineEnd2));

            }

        });

    }

    private SpannableString getClickableSpan2(int count) {

        //指定部分监听

        class Listener extends ClickableSpan implements View.OnClickListener {

            //点击事件

            @Override

            public void onClick(@NonNull View widget) {

                textView.setText(string);

                //调用隐藏文本的方法

                hiddenText();

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {

                super.updateDrawState(ds);

                ds.setUnderlineText(false);

            }

        }

        //新建一个spannableString,把拼接好的字符串传进去（设置字体颜色，监听点击，字体大小）

        SpannableString spannableString = new SpannableString(displayString);

        //设置前景色
        TypedValue typedValue = new TypedValue(); //获取 [?attr/] 的颜色
        context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimaryVariant, typedValue, true);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(typedValue.data);

        spannableString.setSpan(new RelativeSizeSpan(1.1f), count, count + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new Listener(), count, count + 2, Spanned.SPAN_MARK_MARK);

        //添加前景色

        spannableString.setSpan(foregroundColorSpan, count, count + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //返回 spannableString

        return spannableString;

    }

}

