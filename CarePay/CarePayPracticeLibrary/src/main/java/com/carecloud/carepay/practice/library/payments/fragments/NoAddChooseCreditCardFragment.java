package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;

/**
 * Created by lmenendez on 3/8/17.
 */

public class NoAddChooseCreditCardFragment extends ChooseCreditCardFragment {

    @Override
    public void onViewCreated(View view, Bundle icicle){
        super.onViewCreated(view, icicle);
        View addCardButton = view.findViewById(R.id.addNewCardButton);
        addCardButton.setVisibility(View.GONE);
    }
}
