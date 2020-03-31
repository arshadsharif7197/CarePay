package com.carecloud.carepay.patient.myhealth.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.MyHealthViewModel;
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
    private MyHealthDto myHealthDto;

    public AllergyDetailFragment() {

    }

    /**
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
        MyHealthViewModel model = ViewModelProviders.of(getActivity()).get(MyHealthViewModel.class);
        myHealthDto = model.getMyHealthDto().getValue();
        allergy = getAllergy(getArguments().getInt("allergyId"));
    }

    private AllergyDto getAllergy(int allergyId) {
        for (AllergyDto allergy : myHealthDto.getPayload().getMyHealthData().getAllergies().getAllergies()) {
            if (allergyId == allergy.getId()) {
                return allergy;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_allergy_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
        TextView allergyNameTextView = view.findViewById(R.id.allergyName);
        allergyNameTextView.setText(allergy.getName());
        TextView practiceValueTextView = view.findViewById(R.id.practiceValueTextView);
        practiceValueTextView.setText(allergy.getPractice());
        TextView dateValueTextView = view.findViewById(R.id.dateValueTextView);
        dateValueTextView.setText(DateUtil.getInstance().setDateRaw(allergy.getOnsetAt())
                .toStringWithFormatMmSlashDdSlashYyyy());
        RecyclerView reactionsRecyclerView = view.findViewById(R.id.reactionsRecyclerView);
        reactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reactionsRecyclerView.setAdapter(new ReactionsRecyclerViewAdapter(allergy.getReactions()));
    }

    private void setUpToolbar(View view) {
        callback.displayToolbar(false, null);
        Toolbar toolbar = view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(view1 -> getActivity().onBackPressed());
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("my_health_allergy_detail_title"));
    }
}
