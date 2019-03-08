package com.example.qd.douyinwu;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qd.douyinwu.interfaces.OnViewPagerListener;
import com.example.qd.douyinwu.utils.GetScreenWinth;
import com.example.qd.douyinwu.utils.MyVideoPlayer;
import com.example.qd.douyinwu.utils.PagerLayoutManager;
import com.example.qd.douyinwu.utils.SoftKeyBoardListener;
import com.example.qd.douyinwu.utils.SoftKeyHideShow;
import com.example.qd.douyinwu.utils.VideoAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private List<String> myData;
    private RecyclerView recyclerView;
    private PagerLayoutManager mLayoutManager;
    private MyVideoPlayer jzVideo;
    private SoftKeyBoardListener softKeyBoardListener;//软键盘监听
    private CommitAdapter commitAdapter;
    private RecyclerView recyclerViewCommit;
    private RelativeLayout rl_bottom;
    private View commit;
    private TextView tv_shape, tv_shape2, tv_send, tv_context;
    private LinearLayout ll_cancel;
    private RelativeLayout rl_all;
    private EditText et_context;
    private Animation showAction;
    private VideoAdapter adapter;

    /**
     * 默认从第一个开始播放
     */
    private int positionClick = 0;

    /**
     * 是否可以自动滑动
     * 当现实评论列表，说明用户想评论，不可以自动滑动
     */
    private boolean isScroll = true;

    @Override
    protected void onResume() {
        super.onResume();
        //home back
        if (jzVideo != null) {
            jzVideo.goOnPlayOnResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 视频回去的时候要暂停
        ((AudioManager) getSystemService(
                Context.AUDIO_SERVICE)).requestAudioFocus(
                new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                    }
                }, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        //home back
//        if (jzVideo != null) {
//            jzVideo.goOnPlayOnPause();
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInit();
        setContentView(R.layout.activity_main);
        setView();
        addData();
        setAdapter();
        setSoftKeyBoardListener();
    }

    private void setInit() {
        //沉浸状态栏
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //Android 系统5.0一下
        } else {
            //Android 系统5.0一上
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0xff000000);
        }
    }

    private void setView() {
        rl_bottom = findViewById(R.id.rl_bottom);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewCommit = findViewById(R.id.recyclerViewCommit);
        commit = findViewById(R.id.commit);
        tv_shape = findViewById(R.id.tv_shape);
        tv_shape2 = findViewById(R.id.tv_shape2);
        tv_send = findViewById(R.id.tv_send);
        tv_context = findViewById(R.id.tv_context);
        ll_cancel = findViewById(R.id.ll_cancel);
        rl_all = findViewById(R.id.rl_all);
        et_context = findViewById(R.id.et_context);
        myData = new ArrayList<>();
    }

    private void addData() {
        //添加数据
        myData.add("http://static.gamemm.com/upload/video/20180914/14039_1536897206.mp4");
        myData.add("http://static.gamemm.com/upload/video/20180810/14078_1533870358.mp4");
        myData.add("http://static.gamemm.com/upload/video/20180914/14039_1536897206.mp4");
        myData.add("http://static.gamemm.com/upload/video/20180810/14078_1533870358.mp4");
        myData.add("http://static.gamemm.com/upload/video/20180914/14039_1536897206.mp4");
        myData.add("http://static.gamemm.com/upload/video/20180810/14078_1533870358.mp4");
    }

    private void setAdapter() {
        //设置adapter
        adapter = new VideoAdapter(MainActivity.this, myData);
        recyclerView.setAdapter(adapter);
        mLayoutManager = new PagerLayoutManager(this, OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete(View view) {
                //点击进入 0
                playVideo(view, false);
            }

            @Override
            public void onPageSelected(int position, boolean isBottom, View view) {
                positionClick = position;
                //滑动选择 1
                playVideo(view, isBottom);
            }

            @Override
            public void onPageRelease(boolean isNext, int position, View view) {
                //暂停上一个播放
//                releaseVideo(view);
            }
        });

        adapter.setOnItemClickListerer(new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String type, View view, View view1, View view2) {
                if (type.equals("back")) {
                    //返回
                    if (jzVideo.backPress()) {
                        return;
                    }
                    finish();
                } else if (type.equals("commit")) {
                    //评论
                    showCommitDialog();
                }
            }
        });
    }

    /**
     * 开始播放 & 监听播放完成
     */
    private void playVideo(View view, boolean isBottom) {
        if (view != null) {
            jzVideo = view.findViewById(R.id.jzVideo);
            jzVideo.startVideo();
            if (isBottom) {
                //到最后一个加载第二页
                myData.add("http://static.gamemm.com/upload/video/20180914/14039_1536897206.mp4");
                myData.add("http://static.gamemm.com/upload/video/20180810/14078_1533870358.mp4");
                myData.add("http://static.gamemm.com/upload/video/20180914/14039_1536897206.mp4");
                myData.add("http://static.gamemm.com/upload/video/20180810/14078_1533870358.mp4");
                myData.add("http://static.gamemm.com/upload/video/20180914/14039_1536897206.mp4");
                myData.add("http://static.gamemm.com/upload/video/20180810/14078_1533870358.mp4");
                adapter.notifyDataSetChanged();
            }
            jzVideo.setFinishListerer(new MyVideoPlayer.OnItemClickListener() {
                @Override
                public void onItemClick() {
                    //播放完成自动播放下一个,用户没有看评论列表可以播放下一个
                    if (isScroll) {
                        smoothMoveToPosition(recyclerView, positionClick++);
                    }
                }
            });
        }
    }

    /**
     * 平滑的滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            //滑动指定高度
            mRecyclerView.smoothScrollBy(0, GetScreenWinth.getHeight(this) -
                    (Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"))));
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    /**
     * 评论布局
     */
    public void showCommitDialog() {
        if (commitAdapter == null) {
            commitAdapter = new CommitAdapter(this);
            recyclerViewCommit.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewCommit.setAdapter(commitAdapter);
        } else {
            commitAdapter.notifyDataSetChanged();
        }

        //为布局设置显示的动画
        showAction = AnimationUtils.loadAnimation(this, R.anim.actionsheet_dialog_in);
        commit.startAnimation(showAction);

        //显示布局和阴影
        commit.setVisibility(View.VISIBLE);
        tv_shape.setVisibility(View.VISIBLE);

        //不可以滑动
        isScroll = false;

        //关闭评论
        ll_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commit.setVisibility(View.GONE);
                tv_shape.setVisibility(View.GONE);
                //可以滑动
                isScroll = true;
            }
        });
        //阴影点击,隐藏评论列表和阴影
        tv_shape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commit.setVisibility(View.GONE);
                tv_shape.setVisibility(View.GONE);
                //可以滑动
                isScroll = true;
            }
        });
        //输入评论点击
        tv_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoftKeyHideShow.HideShowSoftKey(MainActivity.this);//隐藏软键盘
            }
        });
        //第二层阴影点击，隐藏输入评论框和软键盘
        tv_shape2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoftKeyHideShow.HideShowSoftKey(MainActivity.this);//隐藏软键盘
            }
        });
        //发送评论
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                SoftKeyHideShow.HideShowSoftKey(MainActivity.this);//隐藏软键盘
            }
        });
    }

    /**
     * 软键盘监听
     */
    private void setSoftKeyBoardListener() {
        softKeyBoardListener = new SoftKeyBoardListener(this);
        //软键盘状态监听
        softKeyBoardListener.setListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                //动态设置控件宽高
                ViewGroup.LayoutParams params = rl_bottom.getLayoutParams();
                rl_bottom.setPadding(0, 0, 0, height);
                rl_bottom.setLayoutParams(params);
                //当软键盘显示的时候
                rl_bottom.setVisibility(View.VISIBLE);
                tv_shape2.setVisibility(View.VISIBLE);

                et_context.setFocusable(true);
                et_context.setFocusableInTouchMode(true);
                et_context.setCursorVisible(true);
                et_context.requestFocus();
            }

            @Override
            public void keyBoardHide(int height) {
                //当软键盘隐藏的时候
                rl_bottom.setVisibility(View.GONE);
                tv_shape2.setVisibility(View.GONE);
                et_context.setFocusable(false);
                et_context.setFocusableInTouchMode(false);
                et_context.setCursorVisible(false);
            }
        });
        //设置点击事件,显示软键盘
        et_context.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View view) {
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        //防止EditText点击两次才获取到焦点
        et_context.setOnTouchListener(new View.OnTouchListener() {
            //按住和松开的标识
            int flag = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                flag++;
                if (flag == 2) {
                    flag = 0;//不要忘记这句话
                    //处理逻辑
                    et_context.setFocusable(true);
                    et_context.setFocusableInTouchMode(true);
                    et_context.setCursorVisible(true);
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (jzVideo != null) {
            if (jzVideo.backPress()) {
                return;
            }
        }
        super.onBackPressed();
    }
}
