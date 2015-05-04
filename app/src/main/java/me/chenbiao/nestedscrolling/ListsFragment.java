package me.chenbiao.nestedscrolling;

import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

import me.chenbiao.nestedscrolling.OverlappingPaneLayout.PanelSlideCallbacks;

public class ListsFragment extends Fragment implements ViewPager.OnPageChangeListener {

    public static final int TAB_INDEX_COUNT = 3;

    private ActionBar mActionBar;
    private ViewPager mViewPager;
    private ViewPagerTabs mViewPagerTabs;
    private ViewPagerAdapter mViewPagerAdapter;
    private ScrollFragment scrollFragment;
    private BlankFragment blankFragment;
    private ArrayList<OnPageChangeListener> mOnPageChangeListeners = new ArrayList<>();
    private ListView mCardsListView;
    private CardsAdapter mMergedAdapter;
    private OverlappingPaneLayout mOverlappingPaneLayout;

    private String[] mTabTitles;
    private boolean mIsPanelOpen = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMergedAdapter = new CardsAdapter(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionBar = getActivity().getActionBar();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View parentView = inflater.inflate(R.layout.lists_fragment, container, false);
        mViewPager = (ViewPager) parentView.findViewById(R.id.lists_pager);
        mViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(0);

        mTabTitles = new String[TAB_INDEX_COUNT];
        mTabTitles[0] = getResources().getString(R.string.tab);
        mTabTitles[1] = getResources().getString(R.string.tab);
        mTabTitles[2] = getResources().getString(R.string.tab);

        mViewPagerTabs = (ViewPagerTabs) parentView.findViewById(R.id.lists_pager_header);
        mViewPagerTabs.setViewPager(mViewPager);
        addOnPageChangeListener(mViewPagerTabs);

        mCardsListView = (ListView) parentView.findViewById(R.id.shortcut_card_list);
        mCardsListView.setAdapter(mMergedAdapter);

        setupPaneLayout((OverlappingPaneLayout) parentView);
        mOverlappingPaneLayout = (OverlappingPaneLayout) parentView;

        return parentView;
    }

    private void setupPaneLayout(OverlappingPaneLayout paneLayout) {
        // TODO: Remove the notion of a capturable view. The entire view be slideable, once
        // the framework better supports nested scrolling.
        paneLayout.setCapturableView(mViewPagerTabs);
        paneLayout.openPane();
        paneLayout.setPanelSlideCallbacks(mPanelSlideCallbacks);
        paneLayout.setIntermediatePinnedOffset(
                ((HostInterface) getActivity()).getActionBarController().getActionBarHeight());

        LayoutTransition transition = paneLayout.getLayoutTransition();
        // Turns on animations for all types of layout changes so that they occur for
        // height changes.
        transition.enableTransitionType(LayoutTransition.CHANGING);
    }

    public boolean shouldShowActionBar() {
        return mIsPanelOpen && mActionBar != null;
    }

    public boolean isPaneOpen() {
        return mIsPanelOpen;
    }

    private PanelSlideCallbacks mPanelSlideCallbacks = new PanelSlideCallbacks() {

        @Override
        public void onPanelSlide(View panel, float slideOffset) {
            // For every 1 percent that the panel is slid upwards, clip 1 percent off the top
            // edge of the shortcut card, to achieve the animated effect of the shortcut card
            // being pushed out of view when the panel is slid upwards. slideOffset is 1 when
            // the shortcut card is fully exposed, and 0 when completely hidden.
            float ratioCardHidden = (1 - slideOffset);
            if (mCardsListView.getChildCount() > 0) {
                /*final SwipeableShortcutCard v = (SwipeableShortcutCard) mCardsListView.getChildAt(0);
                v.clipCard(ratioCardHidden);*/
            }

            if (mActionBar != null) {
                // Amount of available space that is not being hidden by the bottom pane
                final int topPaneHeight = (int) (slideOffset * mCardsListView.getHeight());

                final int availableActionBarHeight =
                        Math.min(mActionBar.getHeight(), topPaneHeight);
                final ActionBarController controller =
                        ((HostInterface) getActivity()).getActionBarController();
                controller.setHideOffset(mActionBar.getHeight() - availableActionBarHeight);

                if (!mActionBar.isShowing()) {
                    mActionBar.show();
                }
            }
        }

        @Override
        public void onPanelOpened(View panel) {
            mIsPanelOpen = true;
        }

        @Override
        public void onPanelClosed(View panel) {
            mIsPanelOpen = false;
        }

        @Override
        public void onPanelFlingReachesEdge(int velocityY) {
            if (getCurrentListView() != null) {
                getCurrentListView().fling(velocityY);
            }
        }

        @Override
        public boolean isScrollableChildUnscrolled() {
            final AbsListView listView = getCurrentListView();
            return listView != null && (listView.getChildCount() == 0
                    || listView.getChildAt(0).getTop() == listView.getPaddingTop());
        }
    };

    public void addOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        if (!mOnPageChangeListeners.contains(onPageChangeListener)) {
            mOnPageChangeListeners.add(onPageChangeListener);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        final int count = mOnPageChangeListeners.size();
        for (int i = 0; i < count; i++) {
            mOnPageChangeListeners.get(i).onPageScrolled(position, positionOffset,
                    positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        final int count = mOnPageChangeListeners.size();
        for (int i = 0; i < count; i++) {
            mOnPageChangeListeners.get(i).onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        final int count = mOnPageChangeListeners.size();
        for (int i = 0; i < count; i++) {
            mOnPageChangeListeners.get(i).onPageScrollStateChanged(state);
        }
    }

    private AbsListView getCurrentListView() {
        final int position = mViewPager.getCurrentItem();
        switch (position) {
            case 0:
                return scrollFragment == null ? null : scrollFragment.getListView();
            case 1:
                return null;
            case 2:
                return null;
        }
        throw new IllegalStateException("No fragment at position " + position);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    scrollFragment = new ScrollFragment();
                    return scrollFragment;
                case 1:
                    blankFragment = new BlankFragment();
                    return blankFragment;
                case 2:
                    blankFragment = new BlankFragment();
                    return blankFragment;
            }
            throw new IllegalStateException("No fragment at position " + position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // On rotation the FragmentManager handles rotation. Therefore getItem() isn't called.
            // Copy the fragments that the FragmentManager finds so that we can store them in
            // instance variables for later.
            final Fragment fragment =
                    (Fragment) super.instantiateItem(container, position);
            if (fragment instanceof ScrollFragment) {
                scrollFragment = (ScrollFragment) fragment;
            } else if (fragment instanceof BlankFragment) {
                blankFragment = (BlankFragment) fragment;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_INDEX_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }

    public interface HostInterface {
        ActionBarController getActionBarController();
    }
}
