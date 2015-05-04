package me.chenbiao.nestedscrolling;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

public class ScrollFragment extends Fragment {

    private OnListFragmentScrolledListener mActivityScrollListener;
    private View mParentView;
    private ListView mListView;
    private final ScrollListener mScrollListener = new ScrollListener();
    private NestedScrollAdapter mListAdapter;

    private class ScrollListener implements ListView.OnScrollListener {
        @Override
        public void onScroll(AbsListView view,
                             int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (mActivityScrollListener != null) {
                mActivityScrollListener.onListFragmentScroll(firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mActivityScrollListener.onListFragmentScrollStateChange(scrollState);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListAdapter = new NestedScrollAdapter(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mParentView = inflater.inflate(R.layout.scroll_fragment, container, false);
        mListView = (ListView) mParentView.findViewById(R.id.list);
        mListView.setAdapter(mListAdapter);
        mListView.setOnScrollListener(mScrollListener);
        mListView.setFastScrollEnabled(false);
        mListView.setFastScrollAlwaysVisible(false);

        return mParentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final Activity activity = getActivity();
        try {
            mActivityScrollListener = (OnListFragmentScrolledListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnListFragmentScrolledListener");
        }
    }

    public AbsListView getListView() {
        return mListView;
    }
}
