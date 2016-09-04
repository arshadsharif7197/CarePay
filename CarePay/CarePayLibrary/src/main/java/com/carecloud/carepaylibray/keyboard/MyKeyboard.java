package com.carecloud.carepaylibray.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.carecloud.carepaylibrary.R;

import java.util.List;

/**
 * Custom keyboard
 */
public class MyKeyboard implements KeyboardView.OnKeyboardActionListener {

    private static final String LOG_TAG = "MyKeyboard";
    private Keyboard     mKeyboard;
    private KeyboardView mKv;
    private EditText     mTargetEdit;
    private Context      mContext;
    private boolean mCaps = false;
    private TargetEditor   mTargetEditor;
    private List<EditText> mEdits;
    private int            mTargetEditIndex;

    public MyKeyboard(Context context, KeyboardView keyView, int langId, List<EditText> editTexts) {
        mContext = context;
        mKv = keyView;
        mKeyboard = new Keyboard(context, getKeyResource(langId));
        mKv.setKeyboard(mKeyboard);
        mKv.setOnKeyboardActionListener(this);
        mTargetEditor = new TargetEditor();
        mEdits = editTexts;
    }

    /**
     * Return the id of the keyboard layout
     *
     * @param langId The language id
     * @return The id of the keyboard layout
     */
    private int getKeyResource(int langId) {
        switch (langId) {
            case Constants.LANG_ES:
                return R.xml.qwerty_es;
            default:
                return R.xml.qwerty;
        }
    }

    @Override
    public void onPress(int i) {
        Log.v(LOG_TAG, "onPress()");
    }

    @Override
    public void onRelease(int i) {
        Log.v(LOG_TAG, "onRelease()");
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        playClick(primaryCode);

        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            // delete last char
            mTargetEditor.delLastChar();
            Log.v(LOG_TAG, "del");
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            mCaps = !mCaps;
            mKeyboard.setShifted(mCaps);
            mKv.invalidateAllKeys();
        } else if (primaryCode == Keyboard.KEYCODE_DONE) {
            moveTargetToNextEdit();
            if (mTargetEdit == null) {
                ((KeyboardHolder) mContext).toggleKeyboardVisible(false);
//                ((AppCompatActivity)mContext).getWindow().getDecorView().getRootView().requestFocus();
            }
        } else { // all captured characters
            char code = (char) primaryCode;
            if (Character.isLetter(code) && mCaps) {
                code = Character.toUpperCase(code);
            }
            // display char in the editor
            mTargetEditor.addChar(code);
        }
    }

    /**
     * @param keyCode The code of the pressed key
     */
    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onText(CharSequence charSequence) {
        Log.v(LOG_TAG, "onText()");
    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeUp() {
    }

    public void setTargetEditIndex(int index) {
        if (index != -1) {
            mTargetEditIndex = index;
            mTargetEdit = mEdits.get(mTargetEditIndex);
            mTargetEditor.initTargetEditBuffer();
        } else {
            mTargetEdit = null;
        }
    }

    public void setEdits(List<EditText> edits) {
        this.mEdits = edits;
    }

    private void moveTargetToNextEdit() {
        mTargetEdit.setFocusableInTouchMode(true);
        mTargetEdit.clearFocus();
        mTargetEdit.setFocusableInTouchMode(false);
        ++mTargetEditIndex;
        if (mTargetEditIndex == mEdits.size()) {
            mTargetEditIndex = -1;
            mTargetEdit = null;
        } else {
            mTargetEdit = mEdits.get(mTargetEditIndex);
            mTargetEdit.setFocusableInTouchMode(true);
            mTargetEdit.requestFocus();
            mTargetEdit.setSelection(0, mTargetEdit.getText().length());
            mTargetEdit.setFocusableInTouchMode(false);
            mTargetEditor.initTargetEditBuffer();
        }
    }

    /**
     *
     */
    private class TargetEditor {

        private StringBuilder mTargetEditBuffer;

        public TargetEditor() {
            mTargetEditBuffer = new StringBuilder();
        }

        public void initTargetEditBuffer() {
            if (mTargetEdit != null) {
                int lastIndex = mTargetEditBuffer.length();
                String text = mTargetEdit.getText().toString();
                mTargetEditBuffer.delete(0, lastIndex);
                mTargetEditBuffer.append(text);
            }
        }

        public void delLastChar() {
            if (mTargetEdit == null) {
                return;
            }

            int lastIndex = mTargetEdit.getText().length() - 1;
            if (lastIndex >= 0) {
                mTargetEditBuffer.deleteCharAt(lastIndex);
                // update edit
                mTargetEdit.setText(mTargetEditBuffer.toString());
                // set cursor position
                mTargetEdit.setSelection(lastIndex);
            }
        }

        public void addChar(int code) {
            if (mTargetEdit == null) {
                return;
            }

            // remove selection
            int startSel = mTargetEdit.getSelectionStart();
            int endSel = mTargetEdit.getSelectionEnd();
            mTargetEditBuffer.replace(startSel, endSel, "");
            // add the character
            mTargetEditBuffer.append((char) code);
            Log.v(LOG_TAG, "" + code);
            // update the edit
            mTargetEdit.setText(mTargetEditBuffer.toString());
            // set char on the last position
            mTargetEdit.setSelection(mTargetEdit.getText().length());
        }
    }
}
