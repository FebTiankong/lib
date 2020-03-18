package com.bokecc.livemodule.replay.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bokecc.livemodule.R;
import com.bokecc.livemodule.replay.DWReplayCoreHandler;
import com.bokecc.sdk.mobile.live.replay.DWReplayPlayer;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * CC 回放视频展示控件
 */
public class ReplayVideoView extends RelativeLayout {

    Context mContext;

    View mRootView;

    TextureView mViewContainer;

    TextView mVideoNoplayTip;

    ProgressBar mVideoProgressBar;

    DWReplayPlayer player;

    Surface surface;

    public ReplayVideoView(Context context) {
        super(context);
        this.mContext = context;
        inflateViews();
        initPlayer();
    }

    public ReplayVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        inflateViews();
        initPlayer();
    }

    public ReplayVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        inflateViews();
        initPlayer();
    }

    private void inflateViews() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.live_video_view, this);
        mViewContainer = mRootView.findViewById(R.id.live_video_container);
        mVideoNoplayTip = mRootView.findViewById(R.id.tv_video_no_play_tip);
        mVideoProgressBar = mRootView.findViewById(R.id.video_progressBar);
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        mViewContainer.setSurfaceTextureListener(surfaceTextureListener);
        player = new DWReplayPlayer(mContext);
        player.setOnPreparedListener(preparedListener);
        player.setOnInfoListener(infoListener);
        player.setOnBufferingUpdateListener(bufferingUpdateListener);
        DWReplayCoreHandler dwReplayCoreHandler = DWReplayCoreHandler.getInstance();
        if (dwReplayCoreHandler != null) {
            dwReplayCoreHandler.setPlayer(player);
        }
    }

    /**
     * 添加此字段的意义在于：
     * 部分手机HOME到桌面回来时不触发onSurfaceTextureAvailable，需要由onResume来触发一次调用逻辑。
     */
    boolean hasCallStartPlay = false;

    /**
     * 开始播放
     */
    public void start() {
        if (hasCallStartPlay) {
            return;
        }
        DWReplayCoreHandler dwReplayCoreHandler = DWReplayCoreHandler.getInstance();
        if (dwReplayCoreHandler != null) {
            dwReplayCoreHandler.start(surface);
        }
        hasCallStartPlay = true;
    }

    long currentPosition;

    /**
     * 停止播放
     */
    public void stop() {
        if (player != null) {
            player.pause();
            if (player.getCurrentPosition() != 0) {
                currentPosition = player.getCurrentPosition();
            }
        }
        DWReplayCoreHandler dwReplayCoreHandler = DWReplayCoreHandler.getInstance();
        if (dwReplayCoreHandler != null) {
            dwReplayCoreHandler.stop();
        }
    }

    public void destory() {
        if (player != null) {
            player.pause();
            player.stop();
        }

        DWReplayCoreHandler dwReplayCoreHandler = DWReplayCoreHandler.getInstance();
        if (dwReplayCoreHandler != null) {
            dwReplayCoreHandler.destory();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            // 使用新的surfaceTexture生成surface
            surface = new Surface(surfaceTexture);
            // 正在播放中或者暂停中，只需要将新的surface给player即可
            if (player.isPlaying() || (player.isPlayable() && !TextUtils.isEmpty(player.getDataSource()))) {
                player.setSurface(surface);
            } else {
                // 如果不是播放中或者暂停中，就触发开始播放的操作（此操作是从头开始播放）
                if (hasCallStartPlay) {
                    return;
                }
                start();
                hasCallStartPlay = true;
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
    };


    /******************************************* 播放器相关监听 ***********************************/

    IMediaPlayer.OnPreparedListener preparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            mVideoNoplayTip.post(new Runnable() {
                @Override
                public void run() {
                    player.start();
                    hasCallStartPlay = false;  // 准备正常播放了，将字段回归为false
                    if (currentPosition > 0) {
                        player.seekTo(currentPosition);
                    }
                    mVideoNoplayTip.setVisibility(GONE);
                    DWReplayCoreHandler dwReplayCoreHandler = DWReplayCoreHandler.getInstance();
                    if (dwReplayCoreHandler != null) {
                        dwReplayCoreHandler.replayVideoPrepared();
                    }
                }
            });
        }
    };

    IMediaPlayer.OnInfoListener infoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            switch (what) {
                // 缓冲开始
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    mVideoProgressBar.setVisibility(VISIBLE);
                    break;
                // 缓冲结束
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    mVideoProgressBar.setVisibility(GONE);
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    mVideoProgressBar.setVisibility(GONE);
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    IMediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            DWReplayCoreHandler dwReplayCoreHandler = DWReplayCoreHandler.getInstance();
            if (dwReplayCoreHandler != null) {
                dwReplayCoreHandler.updateBufferPercent(percent);
            }
        }
    };
}
