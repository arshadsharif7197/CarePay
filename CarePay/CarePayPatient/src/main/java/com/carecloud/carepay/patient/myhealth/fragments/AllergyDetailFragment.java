package com.carecloud.carepay.patient.myhealth.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.adapters.ReactionsRecyclerViewAdapter;
import com.carecloud.carepay.patient.myhealth.dtos.AllergyDto;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.myhealth.interfaces.MyHealthInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.DateUtil;

/**
 * @author pjohnson on 19/07/17.
 */

public class AllergyDetailFragment extends BaseFragment {

    private MyHealthInterface callback;
    private AllergyDto allergy;

    public AllergyDetailFragment() {

    }

    /**
     *
     * @param id the allergy id
     * @return a new instance of AllergyDetailFragment
     */
    public static AllergyDetailFragment newInstance(Integer id) {
        Bundle args = new Bundle();
        args.putInt("allergyId", id);
        AllergyDetailFragment fragment = new AllergyDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (MyHealthInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the Activity must implement MyHealthInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        allergy = getAllergy(getArguments().getInt("allergyId"));
    }

    private AllergyDto getAllergy(int allergyId) {
        MyHealthDto myHealthDto = (MyHealthDto) callback.getDto();
        for (AllergyDto allergy : myHealthDto.getPayload().getMyHealthData().getAllergies().getAllergies()) {
            if (allergyId == allergy.getId()) {
                return allergy;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_allergy_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        TextView allergyNameTextView = (TextView) view.findViewById(R.id.allergyName);
        allergyNameTextView.setText(allergy.getName());
        TextView practiceValueTextView = (TextView) view.findViewById(R.id.practiceValueTextView);
        practiceValueTextView.setText(allergy.getPractice());
        TextView dateValueTextView = (TextView) view.findViewById(R.id.dateValueTextView);
        dateValueTextView.setText(DateUtil.getInstance().setDateRaw(allergy.getOnsetAt())
                .toStringWithFormatMmSlashDdSlashYyyy());
        RecyclerView reactionsRecyclerView = (RecyclerView) view.findViewById(R.id.reactionsRecyclerView);
        reactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reactionsRecyclerView.setAdapter(new ReactionsRecyclerViewAdapter(allergy.getReactions()));
    }

    private void setUpToolbar(View view) {
        callback.displayToolbar(false, null);
        Toolbar toolbar = (Toolbar) view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager().getBackStackEntryCount() < 2) {
                    callback.displayToolbar(true, null);
                }
                getActivity().onBackPressed();
            }
        });
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("my_health_allergy_detail_title"));
    }
}
