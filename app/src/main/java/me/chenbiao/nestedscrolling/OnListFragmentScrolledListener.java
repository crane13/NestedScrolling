package me.chenbiao.nestedscrolling;

/*
 * Interface to provide callback to activity when a child fragment is scrolled
 */
public interface OnListFragmentScrolledListener {
    void onListFragmentScrollStateChange(int scrollState);

    void onListFragmentScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount);
}
