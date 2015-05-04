package me.chenbiao.nestedscrolling;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Bundle;
import android.view.View;

/**
 * Controls the various animated properties of the actionBar: showing/hiding, fading/revealing,
 * and collapsing/expanding, and assigns suitable properties to the actionBar based on the
 * current state of the UI.
 */
public class ActionBarController {
    public static final String TAG = "ActionBarController";
    private static final String KEY_IS_SLID_UP = "key_actionbar_is_slid_up";
    private static final String KEY_IS_FADED_OUT = "key_actionbar_is_faded_out";
    private static final String KEY_IS_EXPANDED = "key_actionbar_is_expanded";

    private ActivityUi mActivityUi;
    private View mSearchBox;

    private boolean mIsActionBarSlidUp;

    private final AnimUtils.AnimationCallback mFadeOutCallback = new AnimUtils.AnimationCallback() {
        @Override
        public void onAnimationEnd() {
            slideActionBar(true /* slideUp */, false /* animate */);
        }

        @Override
        public void onAnimationCancel() {
            slideActionBar(true /* slideUp */, false /* animate */);
        }
    };

    public ActionBarController(ActivityUi activityUi, View searchBox) {
        mActivityUi = activityUi;
        mSearchBox = searchBox;
    }

    /**
     * @return Whether or not the action bar is currently showing (both slid down and visible)
     */
    public boolean isActionBarShowing() {
        return !mIsActionBarSlidUp;
    }

    public void slideActionBar(boolean slideUp, boolean animate) {
        if (animate) {
            ValueAnimator animator = slideUp ? ValueAnimator.ofFloat(0, 1) : ValueAnimator.ofFloat(1, 0);
            animator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float value = (float) animation.getAnimatedValue();
                    setHideOffset((int) (mActivityUi.getActionBarHeight() * value));
                }
            });
            animator.start();
        } else {
            setHideOffset(slideUp ? mActivityUi.getActionBarHeight() : 0);
        }
        mIsActionBarSlidUp = slideUp;
    }

    public void setAlpha(float alphaValue) {
        mSearchBox.animate().alpha(alphaValue).start();
    }

    /**
     * @return The offset the action bar is being translated upwards by
     */
    public int getHideOffset() {
        return mActivityUi.getActionBarHideOffset();
    }

    public void setHideOffset(int offset) {
        mIsActionBarSlidUp = offset >= mActivityUi.getActionBarHeight();
        mActivityUi.setActionBarHideOffset(offset);
    }

    public int getActionBarHeight() {
        return mActivityUi.getActionBarHeight();
    }

    /**
     * Saves the current state of the action bar into a provided {@link Bundle}
     */
    public void saveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_SLID_UP, mIsActionBarSlidUp);
    }

    /**
     * Restores the action bar state from a provided {@link Bundle}.
     */
    public void restoreInstanceState(Bundle inState) {
        mIsActionBarSlidUp = inState.getBoolean(KEY_IS_SLID_UP);
    }

    /**
     * This should be called after onCreateOptionsMenu has been called, when the actionbar has
     * been laid out and actually has a height.
     */
    public void restoreActionBarOffset() {
        slideActionBar(mIsActionBarSlidUp /* slideUp */, false /* animate */);
    }

    public boolean getIsActionBarSlidUp() {
        return mIsActionBarSlidUp;
    }

    public interface ActivityUi {
        boolean shouldShowActionBar();

        int getActionBarHeight();

        int getActionBarHideOffset();

        void setActionBarHideOffset(int offset);
    }
}