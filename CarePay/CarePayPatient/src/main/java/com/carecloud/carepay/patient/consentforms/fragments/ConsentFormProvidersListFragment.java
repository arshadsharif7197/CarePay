package com.carecloud.carepay.patient.consentforms.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.consentforms.adapters.PracticeConsentFormsAdapter;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormActivityInterface;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormsProviderInterface;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.UserFormDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 8/03/18.
 */

public class ConsentFormProvidersListFragment extends BaseFragment implements ConsentFormsProviderInterface {


    private ConsentFormActivityInterface callback;
    private ConsentFormDTO consentFormDto;

    public static ConsentFormProvidersListFragment newInstance() {
        return new ConsentFormProvidersListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ConsentFormActivityInterface) {
            callback = (ConsentFormActivityInterface) context;
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        consentFormDto = (ConsentFormDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consent_forms_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpList(view);
    }

    protected void setUpList(View view) {
        List<UserFormDTO> practiceList = consentFormDto.getPayload().getUserForms();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.consentFormsRecyclerView);
        if (practiceList.size() > 0) {
            Map<String, UserPracticeDTO> practicesInformation = getPracticesInformation(consentFormDto.getPayload()
                    .getPracticesInformation());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            PracticeConsentFormsAdapter adapter = new PracticeConsentFormsAdapter(practiceList,
                    practicesInformation);
            adapter.setCallback(this);
            recyclerView.setAdapter(adapter);
        } else {
            view.findViewById(R.id.emptyStateScreen).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private Map<String, UserPracticeDTO> getPracticesInformation(List<UserPracticeDTO> practicesInformation) {
        Map<String, UserPracticeDTO> practicesMap = new HashMap<>();
        for (UserPracticeDTO practice : practicesInformation) {
            practicesMap.put(practice.getPracticeId(), practice);
        }
        return practicesMap;
    }

    @Override
    public void onProviderSelected(UserFormDTO practiceForm, int position) {
        callback.onProviderSelected(practiceForm, position);
    }
}
