package com.carecloud.carepay.practice.library.splash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.signin.adapters.PartnersAdapter;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.signinsignup.dto.Partners;

import java.util.ArrayList;
import java.util.List;

public class ChoosePracticeManagementActivity extends BasePracticeActivity {
    private RecyclerView rvPartners;
    private List<Partners> partnersList;
    private PartnersAdapter partnersAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pm);

        initializeView();
        initializeData();
    }

    private void initializeView() {
        TextView title = findViewById(R.id.titleTextView);
        title.setText(Label.getLabel("practice_management_select"));

        rvPartners = findViewById(R.id.rv_pms_partners);
        rvPartners.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void initializeData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Defs.partnersInfo)) {
            partnersList = (List<Partners>) getIntent().getExtras().get(Defs.partnersInfo);
            partnersAdapter = new PartnersAdapter(this, getEnablePartners(), partners -> {
                setResult(RESULT_OK, getIntent().putExtra(Defs.partnersInfo, partners.getPracticeMgmt()));
                finish();
            });
            rvPartners.setAdapter(partnersAdapter);
        } else {
            finish();
        }
    }

    private List<Partners> getEnablePartners() {
        List<Partners> enablePartnersList = new ArrayList<>();
        for (Partners partners : partnersList) {
            if (partners.getImplemented()) {
                enablePartnersList.add(partners);
            }
        }
        return enablePartnersList;
    }
}
