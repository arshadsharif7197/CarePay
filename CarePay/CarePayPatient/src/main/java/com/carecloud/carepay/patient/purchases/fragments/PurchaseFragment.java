package com.carecloud.carepay.patient.purchases.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;

/**
 * Created by lmenendez on 2/8/17.
 */

public class PurchaseFragment extends Fragment {

    private TextView noPurchaseTitle;
    private TextView noPurchaseDesc;
    private View noPurchaseLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_purchase, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        noPurchaseTitle = (TextView) view.findViewById(R.id.no_purchase_message_title);
        noPurchaseDesc = (TextView) view.findViewById(R.id.no_purchase_message_desc);
        noPurchaseLayout = view.findViewById(R.id.no_purchase_layout);

        //TODO this is temporary and must be removed when this class is implemented
        noPurchaseTitle.setText("No retail options available");
        noPurchaseDesc.setText("You are not connected to a practice that offers online retail options");
        noPurchaseLayout.setVisibility(View.VISIBLE);

    }
}
