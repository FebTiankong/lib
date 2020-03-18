package com.bokecc.livemodule.live;

import com.bokecc.sdk.mobile.live.pojo.QuestionnaireInfo;
import com.bokecc.sdk.mobile.live.pojo.QuestionnaireStatisInfo;

import org.json.JSONObject;

/**
 * 直播 功能 回调监听（签到、答题卡/投票、问卷、抽奖）
 */
public interface DWLiveFunctionListener {


    //**************************** 签到 ************************************

    /**
     * 签到回调
     *
     * @param duration 签到持续时间，单位为秒
     */
    void onRollCall(int duration);

    //**************************** 投票/答题卡 ************************************

    /**
     * 开始投票
     *
     * @param voteCount 总共的选项个数2-5
     * @param VoteType  0表示单选，1表示多选，目前只有单选
     */
    void onVoteStart(int voteCount, int VoteType);

    /**
     * 结束投票
     */
    void onVoteStop();

    /**
     * 投票结果统计
     *
     * @param jsonObject 投票结果数据
     */
    void onVoteResult(JSONObject jsonObject);

    //**************************** 抽奖 ************************************

    /**
     * 开始抽奖
     *
     * @param lotteryId 本次抽奖的id
     */
    void onStartLottery(String lotteryId);

    /**
     * 抽奖结果
     *
     * @param isWin       是否中奖，true表示中奖了
     * @param lotteryCode 中奖码
     * @param lotteryId   本次抽奖的id
     * @param winnerName  中奖者的名字
     */
    void onLotteryResult(boolean isWin, String lotteryCode, String lotteryId, String winnerName);

    /**
     * 结束抽奖
     *
     * @param lotteryId 本次抽奖的id
     */
    void onStopLottery(String lotteryId);

    //**************************** 问卷 ************************************

    /**
     * 发布问卷
     *
     * @param info 问卷内容
     */
    void onQuestionnairePublish(QuestionnaireInfo info);

    /**
     * 停止问卷
     *
     * @param questionnaireId 问卷Id
     */
    void onQuestionnaireStop(String questionnaireId);

    /**
     * 问卷统计信息
     */
    void onQuestionnaireStatis(QuestionnaireStatisInfo info);

    /**
     * 发布第三方问卷
     *
     * @param title       问卷标题
     * @param externalUrl 第三方问卷链接
     */
    void onExeternalQuestionnairePublish(String title, String externalUrl);

    //**************************** SDK 功能异常 ************************************

    /**
     * 功能异常的回调
     */
    void onException(String message);

}
