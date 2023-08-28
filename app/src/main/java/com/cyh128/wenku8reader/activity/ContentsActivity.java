package com.cyh128.wenku8reader.activity;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.ctetin.expandabletextviewlibrary.ExpandableTextView;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.adapter.ContentsListAdapter;
import com.cyh128.wenku8reader.bean.BookcaseBean;
import com.cyh128.wenku8reader.bean.ContentsCcssBean;
import com.cyh128.wenku8reader.bean.ContentsVcssBean;
import com.cyh128.wenku8reader.fragment.BookCaseFragment;
import com.cyh128.wenku8reader.newReader.ReaderActivity;
import com.cyh128.wenku8reader.util.GlobalConfig;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import chen.you.expandable.ExpandableRecyclerView;

public class ContentsActivity extends AppCompatActivity {

    private List<Object> contentsList = new ArrayList<>();
    private List<String> novelDetail = new ArrayList<>();
    public static List<ContentsVcssBean> vcss = new ArrayList<>();
    public static List<List<ContentsCcssBean>> ccss = new ArrayList<>();
    public static String bookUrl = null;
    public static int ccssPosition = 0;
    public static int vcssPosition = 0;
    private TextView title, author, status, update , popular , tag , anime , warning;
    private ExpandableTextView introduce;
    private ImageView imageView;
    private ExpandableRecyclerView expandableRecyclerView;
    private ContentsListAdapter contentsListAdapter;
    private NestedScrollView myNestedScrollView;
    private View mainLayout, warningCardView;
    private LinearProgressIndicator linearProgressIndicator;
    private MaterialButton addToBookcase;
    private Button commentButton;
    private Button webButton;
    private Button recommendButton;
    private boolean inBookcase;
    private int bid;
    private int aid;
    private ExtendedFloatingActionButton fab;
    private Drawable removeDraw, addDraw;
    private boolean isAddOrRemoveDone = true;//防止在添加或移出书架的操作在未完成的情况下再次点击按钮


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        Intent intent = getIntent();
        bookUrl = intent.getStringExtra("bookUrl");

        Resources resources = getResources();
        removeDraw = resources.getDrawable(R.drawable.remove, null);
        addDraw = resources.getDrawable(R.drawable.add, null);

        fab = findViewById(R.id.fab_act_contents);
        fab.hide();
        mainLayout = findViewById(R.id.linearLayout_act_contents);
        mainLayout.setVisibility(View.INVISIBLE);
        linearProgressIndicator = findViewById(R.id.progress_act_contents);
        addToBookcase = findViewById(R.id.button_act_contents_addToBookcase);
        commentButton = findViewById(R.id.button_act_contents_comment);
        webButton = findViewById(R.id.button_act_contents_web);
        recommendButton = findViewById(R.id.button_act_contents_recommend);
        myNestedScrollView = findViewById(R.id.myscrollview_act_contents);
        introduce = findViewById(R.id.text_act_contents_introduce);

        warningCardView = findViewById(R.id.cardView_act_contents_warning);
        warningCardView.setVisibility(View.GONE);

        title = findViewById(R.id.text_act_contents_title);
        author = findViewById(R.id.text_act_contents_author);
        status = findViewById(R.id.text_act_contents_status);
        update = findViewById(R.id.text_act_contents_update);
        imageView = findViewById(R.id.image_act_contents);
        expandableRecyclerView = findViewById(R.id.recyclerView_act_contents);
        tag = findViewById(R.id.text_act_contents_tag);
        popular = findViewById(R.id.text_act_contents_popular);
        anime = findViewById(R.id.text_act_contents_anime);
        warning = findViewById(R.id.text_act_contents_warning);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_act_contents);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });

        webButton.setOnClickListener(v -> {
            Uri uri = Uri.parse(bookUrl);    //设置跳转的网站
            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent1);
        });

        addToBookcase.setOnClickListener(v -> { //加入书架或移出书架
            if (!isAddOrRemoveDone) {
                Snackbar snackbar = Snackbar.make(title, "点击间隔过短，请稍后重试", Snackbar.LENGTH_SHORT);
                snackbar.setAnchorView(fab);
                snackbar.show();
                return;
            }

            isAddOrRemoveDone = false;
            boolean isChecked = addToBookcase.getText().toString().equals("加入书架");
            if (isChecked) {
                new Thread(() -> {
                    try {
                        if (!Wenku8Spider.addBook(aid)) {
                            runOnUiThread(() -> {
                                new MaterialAlertDialogBuilder(ContentsActivity.this)
                                        .setTitle("添加失败")
                                        .setMessage("可能的原因有:\n1 -> 您已经超过了最大收藏限制，无法添加\n2 -> 该书已经在书架内，请退出页面刷新书架然后重试")
                                        .setIcon(R.drawable.warning)
                                        .setCancelable(false)
                                        .setPositiveButton("明白", null)
                                        .show();
                            });

                            isAddOrRemoveDone = true;
                            return;
                        }
                        BookCaseFragment.bookcaseList = Wenku8Spider.getBookcase();

                        runOnUiThread(() -> {
                            addToBookcase.setIcon(removeDraw);
                            addToBookcase.setText("移出书架");
                            TypedValue typedValue = new TypedValue(); //获取 [?attr/] 的颜色
                            getTheme().resolveAttribute(com.google.android.material.R.attr.colorError, typedValue, true);
                            addToBookcase.setBackgroundColor(typedValue.data);

                            isAddOrRemoveDone = true;
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            new MaterialAlertDialogBuilder(ContentsActivity.this)
                                    .setTitle("网络超时")
                                    .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                                    .setIcon(R.drawable.timeout)
                                    .setCancelable(false)
                                    .setPositiveButton("明白", (dialog, which) -> finish())
                                    .show();

                            isAddOrRemoveDone = true;
                        });
                    }
                }).start();
            } else {
                new Thread(() -> {
                    try {
                        getBid();//因为移出过书架，书的delid(bid)会变，比如之前是delid=8843659，删除过就变成了delid=8843661，所以在删除之前需要先重新获取一下书架，然后读取新的delid(bid)
                        Wenku8Spider.removeBook(bid);
                        BookCaseFragment.bookcaseList = Wenku8Spider.getBookcase();

                        runOnUiThread(() -> {
                            addToBookcase.setIcon(addDraw);
                            addToBookcase.setText("加入书架");
                            TypedValue typedValue = new TypedValue(); //获取 [?attr/] 的颜色
                            getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
                            addToBookcase.setBackgroundColor(typedValue.data);

                            isAddOrRemoveDone = true;
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            new MaterialAlertDialogBuilder(ContentsActivity.this)
                                    .setTitle("网络超时")
                                    .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                                    .setIcon(R.drawable.timeout)
                                    .setCancelable(false)
                                    .setPositiveButton("明白", (dialog, which) -> finish())
                                    .show();

                            isAddOrRemoveDone = true;
                        });
                    }
                }).start();
            }
        });

        new Thread(() -> {
            try {
                novelDetail = Wenku8Spider.getNovelDetail(bookUrl);//小说详情
                contentsList = Wenku8Spider.getContents(bookUrl);//小说目录

                //获取这本书的aid和bid====================================================================================================
                try { //https://www.wenku8.net/book/xxxx.htm
                    this.aid = Integer.parseInt(bookUrl.substring(bookUrl.indexOf("book/") + 5, bookUrl.indexOf(".htm")));
                } catch (Exception e) {
                    //https://www.wenku8.net/modules/article/readbookcase.php?aid=xxxx&bid=xxxxxxx
                    this.aid = Integer.parseInt(bookUrl.substring(bookUrl.indexOf("aid=") + 4, bookUrl.indexOf("&")));
                }
                try {//https://www.wenku8.net/modules/article/readbookcase.php?aid=xxxx&bid=xxxxxxx
                    this.bid = Integer.parseInt(bookUrl.substring(bookUrl.indexOf("bid=") + 4));
                } catch (
                        NumberFormatException e) {//https://www.wenku8.net/book/xxxx.htm 如果url没有bid,只有aid,那么就根据它的aid在书架中找到它对应的bid。删除必须用bid
                    BookCaseFragment.bookcaseList = Wenku8Spider.getBookcase();
                    for (BookcaseBean bcc : BookCaseFragment.bookcaseList) {
                        if (Integer.parseInt(bcc.aid) == this.aid) {
                            this.bid = Integer.parseInt(bcc.bid);
                            break;
                        }
                    }
                }
                //end===================================================================================================================

                isInBookcase();//判断这本书是否已在书架中
                otherButtonListener();//按钮监听

                Message msg = new Message();
                setNovelInfo.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> new MaterialAlertDialogBuilder(ContentsActivity.this)
                        .setTitle("网络超时")
                        .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                        .setIcon(R.drawable.timeout)
                        .setCancelable(false)
                        .setPositiveButton("明白", (dialog, which) -> finish())
                        .show());
            }
        }).start();

        fab.setOnClickListener(v -> isHaveHistory());
        myNestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY - oldScrollY > 0) {
                    if (fab.isExtended()) {
                        fab.shrink();
                    }
                } else if (scrollY == myNestedScrollView.getBottom()) {
                    if (fab.isExtended()) {
                        fab.shrink();
                    }
                } else if (scrollY == myNestedScrollView.getTop()) {
                    if (!fab.isExtended()) {
                        fab.extend();
                    }
                } else {
                    if (!fab.isExtended()) {
                        fab.extend();
                    }
                }
            }
        });
    }

    private void getBid() throws IOException {
        //https://www.wenku8.net/book/xxxx.htm 如果url没有bid,只有aid,那么就根据它的aid在书架中找到它对应的bid。因为删除必须用bid
        BookCaseFragment.bookcaseList = Wenku8Spider.getBookcase();
        for (BookcaseBean bcc : BookCaseFragment.bookcaseList) {
            if (Integer.parseInt(bcc.aid) == this.aid) {
                this.bid = Integer.parseInt(bcc.bid);
                break;
            }
        }
        Log.d("debug", "当前bookUrl:" + bookUrl + " bid:" + bid);
    }

    private void otherButtonListener() {
        commentButton.setOnClickListener(v -> {
            Intent intent = new Intent(ContentsActivity.this, CommentActivity.class);
            intent.putExtra("url", novelDetail.get(6));
            startActivity(intent);
        });

        recommendButton.setOnClickListener(v -> {
            new Thread(()->{
                try {
                    String result = Wenku8Spider.bookVote(aid);
                    runOnUiThread(() -> new MaterialAlertDialogBuilder(ContentsActivity.this)
                            .setMessage(result)
                            .setCancelable(false)
                            .setPositiveButton("明白", null)
                            .show());
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> new MaterialAlertDialogBuilder(ContentsActivity.this)
                            .setTitle("网络超时")
                            .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                            .setIcon(R.drawable.timeout)
                            .setCancelable(false)
                            .setPositiveButton("明白", null)
                            .show());
                }
            }).start();
        });

        imageView.setOnClickListener(v -> {
            Intent intent1 = new Intent(ContentsActivity.this, PhotoViewActivity.class);
            intent1.putExtra("url",novelDetail.get(4));
            startActivity(intent1);
        });
    }



    private void isInBookcase() { //判断是否已在书架中，根据这本书的aid是否跟书架中的任意一本的aid相同，如果相同，那么这本书就已经在书架了
        if (BookCaseFragment.bookcaseList.size() == 0) {//如果书架为空时，这种情况一般发生在用户没有点击书架页或者没有收藏的情况下
            try {
                BookCaseFragment.bookcaseList = Wenku8Spider.getBookcase();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> new MaterialAlertDialogBuilder(ContentsActivity.this)
                        .setTitle("网络超时")
                        .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                        .setIcon(R.drawable.timeout)
                        .setCancelable(false)
                        .setPositiveButton("明白", (dialog, which) -> finish())
                        .show());
                return;
            }

            if (BookCaseFragment.bookcaseList.size() == 0) { //如果再一次获取目录，但size还是为0时，那么返回
                Log.d("debug", "bookcaselist size -> 0");
                inBookcase = false;
                return;
            }
        }
        for (BookcaseBean bcc : BookCaseFragment.bookcaseList) {
            if (Integer.parseInt(bcc.aid) == this.aid) {
                inBookcase = true;
                return;
            }
        }
        inBookcase = false;
    }

    private void isHaveHistory() {
        String sql = null;
        if (GlobalConfig.readerMode == 1) {
            sql = String.format("select * from new_reader_read_history where bookUrl='%s'", bookUrl);
        } else {
            sql = String.format("select * from old_reader_read_history where bookUrl='%s'", bookUrl);
        }

        Cursor cursor = GlobalConfig.db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                String indexUrl = cursor.getString(1);

                //这是为了获得该章节在目录中第几卷第几章，供ReaderActivity使用
                for (int j = 0; j < ccss.size(); j++) {
                    for (int k = 0; k < ccss.get(j).size(); k++) {
                        if (ccss.get(j).get(k).url.equals(indexUrl)) {
                            Intent toContent;
                            if (GlobalConfig.readerMode == 1) {
                                toContent = new Intent(ContentsActivity.this, ReaderActivity.class);
                            } else {
                                toContent = new Intent(ContentsActivity.this, com.cyh128.wenku8reader.oldReader.ReaderActivity.class);
                            }

                            vcssPosition = j;
                            ccssPosition = k;
                            int position = cursor.getInt(3);
                            toContent.putExtra("position",position);
                            startActivity(toContent);
                        }
                    }
                }
            }
            cursor.close();
        } else {
            runOnUiThread(() -> {
                Snackbar snackbar = Snackbar.make(mainLayout, "没有阅读记录", Snackbar.LENGTH_SHORT);
                snackbar.setAnchorView(fab);
                snackbar.show();
            });
        }
    }

    private final Handler setNovelInfo = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            title.setText(novelDetail.get(0));
            author.setText(novelDetail.get(1));
            status.setText(novelDetail.get(2));
            update.setText(novelDetail.get(3));
            tag.setText(novelDetail.get(7));
            if (novelDetail.get(8).contains("本书公告")) { //判断当前小说是否无版权
                popular.setVisibility(View.GONE);
                warningCardView.setVisibility(View.VISIBLE);
                warning.setText(novelDetail.get(8));
                introduce.setVisibility(View.GONE);
            } else {
                popular.setText(novelDetail.get(8));
            }

            anime.setText("动画化情况：" + novelDetail.get(9));
            if (!ContentsActivity.this.isFinishing()) {//https://blog.csdn.net/wjj1996825/article/details/80280109 防止在未加载完成的情况下返回导致的奔溃问题
                Glide.with(ContentsActivity.this)
                        .load(novelDetail.get(4))
                        .centerCrop()
                        .into(imageView);
            }

            introduce.setContent(String.valueOf(Html.fromHtml(novelDetail.get(5), FROM_HTML_MODE_COMPACT)));

            vcss = (List<ContentsVcssBean>) contentsList.get(0);//卷list
            ccss = (List<List<ContentsCcssBean>>) contentsList.get(1);//章节list

            contentsListAdapter = new ContentsListAdapter(ContentsActivity.this, vcss, ccss);
            expandableRecyclerView.setAdapter(contentsListAdapter);
            expandableRecyclerView.setLayoutManager(new LinearLayoutManager(ContentsActivity.this));


            //加载完成，显示界面======================================================================
            if (inBookcase) { //如果这本书在书架，那么将加入书架的按钮样式变为移出书架的按钮样式
                addToBookcase.setIcon(removeDraw);
                addToBookcase.setText("移出书架");
                TypedValue typedValue = new TypedValue(); //获取 [?attr/] 的颜色
                getTheme().resolveAttribute(com.google.android.material.R.attr.colorError, typedValue, true);
                addToBookcase.setBackgroundColor(typedValue.data);
            }
            mainLayout.setVisibility(View.VISIBLE);
            fab.show();
            linearProgressIndicator.hide();
        }
    };
}
