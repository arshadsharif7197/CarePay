package com.carecloud.carepay.practice.library.signin;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.signin.adapters.PartnersAdapter;
import com.carecloud.carepay.practice.library.signin.fragments.ChoosePracticeLocationFragment;
import com.carecloud.carepay.practice.library.signin.interfaces.PracticeManagementSignInInterface;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.signinsignup.dto.Partners;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChoosePracticeManagementSystemFragment extends BaseDialogFragment {

    private PracticeManagementSignInInterface callback;
    private RecyclerView rvPartners;
    private List<Partners> partnersList;
    private PartnersAdapter partnersAdapter;

    public static ChoosePracticeManagementSystemFragment newInstance(Serializable partnersList) {
        Bundle args = new Bundle();
        args.putSerializable("partnersList", partnersList);

        ChoosePracticeManagementSystemFragment fragment = new ChoosePracticeManagementSystemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        partnersList = (List<Partners>) getArguments().getSerializable("partnersList");
        partnersAdapter = new PartnersAdapter(getActivity(), partnersList, partners -> {
            callback.onPracticeManagementSelected(partners.getPracticeMgmt());
            dismiss();
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PracticeManagementSignInInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PracticeManagementSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_choose_pm, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        TextView title = (TextView) view.findViewById(R.id.titleTextView);
        title.setText(Label.getLabel("practice_management_select"));

        rvPartners = view.findViewById(R.id.rv_pms_partners);
        rvPartners.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPartners.setAdapter(partnersAdapter);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        String pm = getApplicationPreferences().getStartPracticeManagement();
        setCancelable(!StringUtil.isNullOrEmpty(pm));
        if (StringUtil.isNullOrEmpty(pm)) {
            getApplicationPreferences().clearAll();
        }
    }
}
