package com.example.heart.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baijiahulian.common.listview.AbsListDataAdapter;
import com.baijiahulian.common.listview.AbsListView;
import com.baijiahulian.common.listview.BaseListCell;
import com.baijiahulian.common.listview.BaseListDataAdapter;
import com.baijiahulian.tianxiao.model.TXDataModel;
import com.baijiahulian.tianxiao.ui.TXBaseListFragment;
import com.example.heart.R;
import com.example.heart.ui.cell.HotCell;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class HotFragment extends TXBaseListFragment {
    public static final String INTENT_IN_STATUS_ID = "intent-in-status-id";
    int mStatus;

    public static HotFragment newInstance(int status) {
        HotFragment f = new HotFragment();
        Bundle b = new Bundle();
        b.putInt(INTENT_IN_STATUS_ID, status);
        f.setArguments(b);
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStatus = getArguments().getInt(INTENT_IN_STATUS_ID);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hot, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerListView.setOnLoadMoreListener(new AbsListView.IOnLoadMore() {
            @Override
            public void onLoadMore() {
//                if (!mHasMore) {
//                    return;
//                }
//                loadData();
            }
        });
        TextView mTvEmptyText = (TextView) mListEmptyView.findViewById(R.id.layout_listview_empty_note_tv);
        mTvEmptyText.setText("空空热门");
    }
    @Override
    protected int getListViewId() {
        return R.id.txm_fragment_hot_list;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected AbsListDataAdapter getAdapter(Context context) {
        return new HotAdapter(context);
    }

    @Override
    protected void loadFirstPage() {
        initFirstPage();
        loadData();
    }

    @Override
    public void onListRefresh() {
        initFirstPage();
        loadData();
    }

    private void initFirstPage() {
//        mLastVoteId = 0;
//        mHasMore = true;
    }

    private void loadData() {
        mAdapter.addAll(new ArrayList().toArray());
//        if (mService == null) {
//            mService = (TXMVoteDataService) TXDataServiceManager.getService(TXMVoteDataService.SERVICE_KEY);
//        }
//
//        mService.getVoteList(this, mStatus.value(), mLastVoteId,
//                new TXMBaseDataService.TXMDataServiceArrayListener<TXMVoteItemModel>() {
//                    @Override
//                    public void onDataBack(TXServiceResultModel result, List<TXMVoteItemModel> list, Object param) {
//                        if (mLastVoteId == 0) {// 第一页
//                            mAdapter.clearData();
//                        }
//                        if (null == list || list.size() == 0) {
//                            mHasMore = false;
//                        } else {
//                            mLastVoteId = list.get(list.size() - 1).id;
//                        }
//                        mAdapter.addAll(list.toArray());
//                    }
//                }, null);
    }

//    public void onEventMainThread(TXMVoteRefreshEvent event) {
//        onListRefresh();
//    }
//
//    public static void notifyVoteListRefresh() {
//        EventBus.getDefault().post(new TXMVoteRefreshEvent());
//    }

    static class HotAdapter extends BaseListDataAdapter<TXDataModel> {
        private Context mContext;

        public HotAdapter(Context context) {
            mContext = context;
        }

        @Override
        protected BaseListCell<TXDataModel> createCell(int type) {
            return new HotCell(mContext);
        }
    }
}
