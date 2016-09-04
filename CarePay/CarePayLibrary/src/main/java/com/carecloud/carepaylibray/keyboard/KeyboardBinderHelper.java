package com.carecloud.carepaylibray.keyboard;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Binds edit text to a custom keyboard
 */
public class KeyboardBinderHelper {

    private KeyboardHolderActivity mActivity;
    private List<EditText>         mEdits;
    private List<EditTextWrapper>  mWrappers;
    private MyKeyboard             mKeyboard;

    public KeyboardBinderHelper(KeyboardHolderActivity activity, List<EditText> edits) {
        mActivity = activity;
        mKeyboard = activity.getKeyboard();
        mEdits = edits;
        mWrappers = new ArrayList<>();
    }

    public void bindEditsToKeyboard() {
        for (int i = 0; i < mEdits.size(); i++) {
            EditText ed = mEdits.get(i);
            // set this as keyboard binder
            EditTextTag tag = ((EditTextTag)ed.getTag());
            tag.setKeyboardBinder(this);
            ed.setTag(tag);
            // create the wrapper
            mWrappers.add(new EditTextWrapper(this, ed));
        }
    }

    public void addEdit(EditText editText) {
        EditTextTag tag = (EditTextTag) editText.getTag();
        tag.setIndex(mEdits.size());
        editText.setTag(tag);
        mEdits.add(editText);
        mWrappers.add(new EditTextWrapper(this, editText));
    }

    public void removeEdit(EditText editText) {
        int index = ((EditTextTag) editText.getTag()).getIndex();
        mEdits.remove(index);
        mWrappers.remove(index);
    }

    public void setEditAsCurrent(EditText editText) {
        mKeyboard.setTargetEdit(editText);
    }

    public void moveToNextEditInSet(EditText currentEdit) {
        // clear the selection
        currentEdit.setFocusableInTouchMode(true);
        currentEdit.clearFocus();
        currentEdit.setFocusableInTouchMode(false);

        // increment the index
        int index = ((EditTextTag) currentEdit.getTag()).getIndex();
        int noOfEdits = mEdits.size();
        ++index;

        // find the next edit
        EditText nextEdit = null;
        if (index < noOfEdits) {
            nextEdit = mEdits.get(index);
        }

        if(nextEdit != null) {
            // set active
            nextEdit.setFocusableInTouchMode(true);
            nextEdit.requestFocus();
            nextEdit.setSelection(0, nextEdit.getText().length());
            nextEdit.setFocusableInTouchMode(false);
        }
        mKeyboard.setTargetEdit(nextEdit);
    }

    public KeyboardHolderActivity getKeyboardHolderActivity() {
        return mActivity;
    }
}