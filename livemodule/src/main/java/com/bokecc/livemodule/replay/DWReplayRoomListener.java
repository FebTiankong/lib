package com.bokecc.livemodule.replay;

/**
 * 回放直播间信息监听
 */
public interface DWReplayRoomListener {

    /**
     * 回放播放初始化已经完成
     */
    void videoPrepared();

    /**
     * 更新缓冲进度
     * @param percent 缓冲百分比
     */
    void updateBufferPercent(int percent);

    /**
     * 展示当前直播回放时长
     * @param playerDuration 播放时长
     */
    void showVideoDuration(long playerDuration);
}
