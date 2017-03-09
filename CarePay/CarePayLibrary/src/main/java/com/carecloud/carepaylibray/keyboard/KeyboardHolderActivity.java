package com.carecloud.carepaylibray.keyboard;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.WindowManager;
import android.widget.EditText;

import com.carecloud.carepaylibray.base.BaseActivity;

import java.util.List;

/**
 * Created by lsoco_user on 9/2/2016.
 * Activity that supports a custom keyboard
 * It has two fragment holders : one for content, one for keyboard
 */
public abstract class KeyboardHolderActivity extends BaseActivity {

    public static final String LOG_TAG     = KeyboardHolderActivity.class.getSimpleName();
    public static final String KEY_LANG_ID = "language";
    public static       String KB_FRAG_TAG = "keyboard";
    protected FragmentManager fm;
    protected int             langId;

    /**
     * Helper to replace a fragment
     * @param fragClass The class of the fragment
     * @param addToBackStack Whether to add to back stack or not
     */
    public void replaceFragment(Class fragClass, boolean addToBackStack) {

    }

    /**
     * Creates and add the fragment with contents
     */
    public void placeInitContentFragment() {

    }

    /**
     * Provides the activity layout id
     *
     * @return The activity layout
     */
    public abstract int getLayoutRes();

    /**
     * Provides the id for the contents fragment
     *
     * @return The id
     */
    public abstract int getContentsHolderId();

    /**
     * Provides the id of the keyboard holder
     *
     * @return The id
     */
    public abstract int getKeyboardHolderId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());

        // hide sys keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        fm = getSupportFragmentManager();

        // add the keyboard
        MyKeyboardFragment kbrdFrag = null;
        if (getKeyboardHolderId() != 0) { // add keyboard only if there a holder for it
            kbrdFrag = (MyKeyboardFragment) fm.findFragmentByTag(KB_FRAG_TAG);
            if (kbrdFrag == null) {
                kbrdFrag = new MyKeyboardFragment();
                // bind the edits from all fragments with edits here
            }
            fm.beginTransaction().replace(getKeyboardHolderId(), kbrdFrag, KB_FRAG_TAG).commit();
        }
        // abstract part
        placeInitContentFragment();

        // hide the keyboard
        if (kbrdFrag != null) {
            fm.beginTransaction().hide(kbrdFrag).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (isKeyboardVisible()) {
            toggleKeyboardVisible(false);
        } else {
            super.onBackPressed();
        }
    }

    public boolean isKeyboardVisible() {
        MyKeyboardFragment keyboardFragment = (MyKeyboardFragment) fm.findFragmentByTag(KB_FRAG_TAG);
        if (keyboardFragment != null) {
            return keyboardFragment.isVisible();
        }
        return false;
    }

    public void bindKeyboardToEdits(List<EditText> edits) {
//        // bind the keyboard to the mEdits
//        MyKeyboard keyboard = ((MyKeyboardFragment) fm.findFragmentByTag(KB_FRAG_TAG)).getKeyboard();
//        if (keyboard != null) {
//            (new KeyboardBinderHelper(this, edits)).bindEditsToKeyboard();
//        } else {
//            Log.v(LOG_TAG, "keyboard null");
//        }
    }

    public void toggleKeyboardVisible(boolean visible) {
        MyKeyboardFragment keyboardFragment = (MyKeyboardFragment) fm.findFragmentByTag(KB_FRAG_TAG);
        if (keyboardFragment != null) {
            if (keyboardFragment.isVisible()) {
                if (!visible) {
                    fm.beginTransaction().hide(keyboardFragment).commit();
                }
            } else { // keyboard invisible
                if (visible) { // should make it visible
                    fm.beginTransaction().show(keyboardFragment).commit();
                }
            }
        }
    }

    /**
     * @return
     */
    public int getLangId() {
        return langId;
    }

    /**
     * @param langId
     */
    public void setLangId(int langId) {
        this.langId = langId;
        MyKeyboardFragment keyboardFragment = (MyKeyboardFragment) fm.findFragmentByTag(KB_FRAG_TAG);
        if (keyboardFragment != null) {
            keyboardFragment.createKeyboard(langId);
        }
    }

    public MyKeyboard getKeyboard() {
        MyKeyboardFragment kf = (MyKeyboardFragment) fm.findFragmentByTag(KB_FRAG_TAG);
        if (kf != null) {
            return kf.getKeyboard();
        }
        return null;
    }
}
