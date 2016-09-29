package com.carecloud.carepaylibray.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.util.Log;
import android.widget.EditText;

import com.carecloud.carepaylibrary.R;


/**
 * Implements custom keyboard functionality
 */
public class MyKeyboard implements KeyboardView.OnKeyboardActionListener {

    private static final String LOG_TAG = "MyKeyboard";

    private Keyboard     mKeyboard;
    private KeyboardView mKv;
    private KeyboardHolderActivity      mKeyboardHolderActivity;
    private boolean mCaps = false;
    private TargetEditor mTargetEditor;

    public MyKeyboard(KeyboardHolderActivity activity, KeyboardView keyView, int langId) {
        mKeyboardHolderActivity = activity;
        mKv = keyView;
        mKeyboard = new Keyboard(activity, getKeyResource(langId));
        mKv.setKeyboard(mKeyboard);
        mKv.setOnKeyboardActionListener(this);
        mTargetEditor = new TargetEditor(null);
    }

    /**
     * Updates the reference to the edit text that the keyboard is used to
     * @param edit The edit text
     */
    public void setTargetEdit(EditText edit) {
        if(mTargetEditor != null) {
            mTargetEditor.setEditTarget(edit);
        }
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
            mTargetEditor.deleteChar();
            Log.v(LOG_TAG, "del");
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
            mCaps = !mCaps;
            mKeyboard.setShifted(mCaps);
            mKv.invalidateAllKeys();
        } else if (primaryCode == Keyboard.KEYCODE_DONE) {
            EditText currentEdit = mTargetEditor.getTargetEdit();
            ((EditTextTag)currentEdit.getTag()).getKeyboardBinder().moveToNextEditInSet(currentEdit);
            if (mTargetEditor.getTargetEdit() == null) {
                mKeyboardHolderActivity.toggleKeyboardVisible(false);
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
        AudioManager am = (AudioManager) mKeyboardHolderActivity.getSystemService(Context.AUDIO_SERVICE);
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

    /**
     *
     */
    private class TargetEditor {

        private EditText      mTargetEdit;
        private StringBuilder mTargetEditBuffer;

        public TargetEditor(EditText edit) {
            mTargetEdit = edit;
            mTargetEditBuffer = new StringBuilder();
        }

        EditText getTargetEdit() {
            return mTargetEdit;
        }

        void setEditTarget(EditText edit) {
            mTargetEdit = edit;
            if (edit != null) {
                // trigger an update of the buffer
                initTargetEditBuffer();
            }
        }

        /**
         * Init the edit buffer
         */
        public void initTargetEditBuffer() {
            if (mTargetEdit != null) {
                int lastIndex = mTargetEditBuffer.length();
                mTargetEditBuffer.delete(0, lastIndex); // clear the old text in the buffer
                String text = mTargetEdit.getText().toString();
                mTargetEditBuffer.append(text); // set the new text
            }
        }

        /**
         * Deletes the last char or the selection
         */
        public void deleteChar() {
            if (mTargetEdit == null) {
                return;
            }
            int beginSel = mTargetEdit.getSelectionStart();
            int endSel = mTargetEdit.getSelectionEnd();
            int lastIndex = mTargetEdit.getText().length() - 1;
            if (lastIndex >= 0) {
                if(endSel - beginSel <= 0) { // no selection
                    mTargetEditBuffer.deleteCharAt(lastIndex); // just delete the last char
                } else {
                    // delete selection
                    mTargetEditBuffer.replace(beginSel, endSel, "");
                }
                // update edit
                mTargetEdit.setText(mTargetEditBuffer.toString());
                // place the cursor at the end
                mTargetEdit.setSelection(mTargetEditBuffer.length());
            }
        }

        /**
         * Add a char
         *
         * @param code The code of the char
         */
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