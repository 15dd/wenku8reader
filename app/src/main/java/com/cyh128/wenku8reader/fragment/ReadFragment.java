package com.cyh128.wenku8reader.fragment;

import static android.app.Activity.RESULT_OK;
import static android.text.Html.FROM_HTML_MODE_COMPACT;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cyh128.wenku8reader.activity.ReaderActivity;
import com.cyh128.wenku8reader.util.VarTemp;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.adapter.ReaderAdapter;
import com.cyh128.wenku8reader.util.Wenku8Spider;

import java.io.IOException;
import java.util.List;

public class ReadFragment extends Fragment {
    ;
    private View view;
    private TextView textView;
    private RecyclerView recyclerView;
    private String text;
    private List<String> imgUrl;
    private float fontSize,lineSpacing;
    private ReaderAdapter readerAdapter;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_read, container, false);
        textView = view.findViewById(R.id.text_frag_read);
        recyclerView = view.findViewById(R.id.recyclerView_frag_read);
        layoutManager = new LinearLayoutManager(view.getContext());
        return view;
    }

    public void setContent() throws IOException {
        Message msg = new Message();
        msg.what = RESULT_OK;
        List<List<String>> allContent = Wenku8Spider.Content(ReaderActivity.bookUrl, ReaderActivity.ccss.get(ReaderActivity.vcssPosition).get(ReaderActivity.ccssPosition).url);
        this.text = allContent.get(0).get(0);
        this.imgUrl = allContent.get(1);

        handler.sendMessage(msg);
    }

    public void setContentFontSize(float size) {
        this.fontSize = size;
        Message msg = new Message();
        setFontSizeHandler.sendMessage(msg);
    }

    public void setContentLineSpacing(float size) {
        this.lineSpacing = size;
        Message msg = new Message();
        setLineSpaceHandler.sendMessage(msg);
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (!getActivity().isFinishing()) { //防止activity已销毁导致的崩溃
                textView.setTextSize(VarTemp.readerFontSize);
                textView.setLineSpacing(VarTemp.readerLineSpacing,1f);
                textView.setText(Html.fromHtml(text, FROM_HTML_MODE_COMPACT));
                readerAdapter = new ReaderAdapter(view.getContext(), imgUrl);
                recyclerView.setAdapter(readerAdapter);
                recyclerView.setLayoutManager(layoutManager);
                return true;
            } else {
                Log.w("debug", "activity destroyed");
            }
            return false;
        }
    });

    private final Handler setFontSizeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            textView.setTextSize(fontSize);
            return true;
        }
    });

    private final Handler setLineSpaceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            textView.setLineSpacing(lineSpacing,1f);
            return true;
        }
    });

}
