package me.chenbiao.nestedscrolling;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends Activity implements ActionBarController.ActivityUi,
        ListsFragment.HostInterface, OnListFragmentScrolledListener {

    private FrameLayout mParentLayout;
    private ListsFragment mListsFragment;
    private ActionBarController mActionBarController;
    private int mActionBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Resources resources = getResources();
        mActionBarHeight = resources.getDimensionPixelSize(R.dimen.action_bar_height_large);

        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);

        final ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setCustomView(R.layout.search_edittext);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(null);

        mActionBarController = new ActionBarController(this, actionBar.getCustomView());

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.frame, new ListsFragment())
                    .commit();
        } else {
            mActionBarController.restoreInstanceState(savedInstanceState);
        }

        mParentLayout = (FrameLayout) findViewById(R.id.mainlayout);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ListsFragment) {
            mListsFragment = (ListsFragment) fragment;
        }
    }

    @Override
    public boolean shouldShowActionBar() {
        return mListsFragment.shouldShowActionBar();
    }

    @Override
    public int getActionBarHeight() {
        return mActionBarHeight;
    }

    @Override
    public int getActionBarHideOffset() {
        return getActionBar().getHideOffset();
    }

    @Override
    public void setActionBarHideOffset(int offset) {
        getActionBar().setHideOffset(offset);
    }

    @Override
    public void onListFragmentScrollStateChange(int scrollState) {
    }

    @Override
    public void onListFragmentScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    public ActionBarController getActionBarController() {
        return mActionBarController;
    }
}
