package com.carecloud.carepaylibray.keyboard;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.List;

/**
 * Created by lsoco_user on 9/2/2016.
 * Activity that supports a custom keyboard
 * It has two fragment holders : one for content, one for keyboard
 */
public abstract class KeyboardHolderActivity extends AppCompatActivity {

    private static final String LOG_TAG = KeyboardHolderActivity.class.getSimpleName();
    public static final String KEY_LANG_ID = "language";
    public static String KB_FRAG_TAG    = "keyboard";
    public static String KB_CONTENT_TAG = "content";
    protected FragmentManager fm;
    protected int             langId;

    /**
     * Repalces a fragment in content holder
     *
     * @param fragClass
     */
    public abstract void replaceFragment(Class fragClass);

    /**
     * Creates and add the fragment with contents
     */
    public abstract void placeInitContentFragment();

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        fm = getSupportFragmentManager();

        // add the keyboard
        MyKeyboardFragment kbrdFrag = (MyKeyboardFragment) fm.findFragmentByTag(KB_FRAG_TAG);
        if (kbrdFrag == null) {
            kbrdFrag = new MyKeyboardFragment();
            // bind the edits from all fragments with edits here
        }
        fm.beginTransaction().replace(getKeyboardHolderId(), kbrdFrag, KB_FRAG_TAG).commit();

        // abstract part
        placeInitContentFragment();

        // hide the keyboard
        fm.beginTransaction().hide(kbrdFrag).commit();
    }

    @Override
    public void onBackPressed() {
        if (isKeyboardVisible()) {
            toggleKeyboardVisible(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.v(LOG_TAG, "onConfigChanged()");
        super.onConfigurationChanged(newConfig);
        // restart the contents fragment
        Fragment contentsFragment = fm.findFragmentByTag(KB_CONTENT_TAG);
        if(contentsFragment != null) {
            fm.beginTransaction().detach(contentsFragment).commit();
            fm.beginTransaction().attach(contentsFragment).commit();
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
        // bind the keyboard to the mEdits
        MyKeyboard keyboard = ((MyKeyboardFragment) fm.findFragmentByTag(KB_FRAG_TAG)).getKeyboard();
        if(keyboard != null) {
            (new KeyboardBinderHelper(this, edits)).bindEditsToKeyboard();
        } else {
            Log.v(LOG_TAG, "keyboard null");
        }
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
        if(kf != null) {
            return kf.getKeyboard();
        }
        return null;
    }
}