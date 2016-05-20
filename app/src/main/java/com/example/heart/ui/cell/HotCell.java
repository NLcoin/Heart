package com.example.heart.ui.cell;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baijiahulian.common.listview.BaseListCell;
import com.baijiahulian.tianxiao.model.TXDataModel;
import com.example.heart.R;

/**
 * Created by Cheng on 16/1/23.
 */
public class HotCell implements BaseListCell<TXDataModel> {
    private Context mContext;

    private TextView mTvPublishTime;
    private TextView mTvStatus;
    private TextView mTvPartyName;
    private TextView mTvVisitorCount;
    private TextView mTvParticipateCount;
    private TextView mTvWinnerCount;
    private Button mBtnManager;

    public HotCell(Context context) {
        mContext = context;
    }

    @Override
    public void setData(final TXDataModel item, int position) {
//        mTvPublishTime.setText(TXMTimeUtil.format(TXMTimeUtil.YMD_HMS, item.updateTime.getMilliseconds()));
//        if (item.status == TXMConstant.TXMPartyStatus.Being.value()) {
//            mTvStatus.setText(R.string.txm_party_underway);
//        } else if (item.status == TXMConstant.TXMPartyStatus.NoStart.value()){
//            mTvStatus.setText(R.string.txm_party_nostart);
//        } else if (item.status == TXMConstant.TXMPartyStatus.End.value()){
//            mTvStatus.setText(R.string.txm_party_end);
//        } else if (item.status == TXMConstant.TXMPartyStatus.Close.value()){
//            mTvStatus.setText(R.string.txm_party_closed);
//        }
//
//        mTvPartyName.setText(item.name);
//        mTvVisitorCount.setText(mContext.getString(R.string.txm_visitor) + item.browseCount);
//        mTvParticipateCount.setText(mContext.getString(R.string.txm_participate) + item.userCount);
//        mTvWinnerCount.setText(mContext.getString(R.string.txm_vote_win_prize) + item.voteCount);
//        mBtnManager.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TXMPromotionVoteDetailActivity.launch(mContext, item.id);
//            }
//        });
    }

    @Override
    public int getCellResource() {
        return R.layout.activity_live_record;
    }

    @Override
    public void initialChildViews(View view) {
//        mTvPublishTime = (TextView) view.findViewById(R.id.tv_market_publish_time);
//        mTvStatus = (TextView) view.findViewById(R.id.tv_market_activity_status);
//        mTvPartyName = (TextView) view.findViewById(R.id.tv_market_activity_name);
//        mTvVisitorCount = (TextView) view.findViewById(R.id.tv_market_visitor_count);
//        mTvParticipateCount = (TextView) view.findViewById(R.id.tv_market_share_count);
//        mTvParticipateCount.setVisibility(View.VISIBLE);
//        mTvWinnerCount = (TextView) view.findViewById(R.id.tv_market_enroll_count);
//        mBtnManager = (Button) view.findViewById(R.id.btn_market_activity_manager);
    }
}
