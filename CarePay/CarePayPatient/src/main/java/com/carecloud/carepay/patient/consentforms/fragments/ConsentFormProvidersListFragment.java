package com.carecloud.carepay.patient.consentforms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consent_forms_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpList(view);
    }

    private void setUpList(View view) {
        List<UserFormDTO> practiceFormsList = consentFormDto.getPayload().getUserForms();
        RecyclerView recyclerView = view.findViewById(R.id.consentFormsRecyclerView);
        if (practiceFormsList.size() == 0 && hasAllPermissionsOn(consentFormDto)) {
            view.findViewById(R.id.emptyStateScreen).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            Map<String, Boolean> permissionsMap = getPracticesPermissions(consentFormDto);
            Map<String, UserFormDTO> practiceFormsMap = getPracticesFormsMap(practiceFormsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            PracticeConsentFormsAdapter adapter = new PracticeConsentFormsAdapter(consentFormDto
                    .getPayload().getPracticesInformation(), permissionsMap, practiceFormsMap
            );
            adapter.setCallback(this);
            recyclerView.setAdapter(adapter);
        }
    }

    private Map<String, UserFormDTO> getPracticesFormsMap(List<UserFormDTO> practiceFormsList) {
        Map<String, UserFormDTO> practiceFormsMap = new HashMap<>();
        for (UserFormDTO userFormDTO : practiceFormsList) {
            practiceFormsMap.put(userFormDTO.getMetadata().getPracticeId(), userFormDTO);
        }
        return practiceFormsMap;
    }

    private Map<String, Boolean> getPracticesPermissions(ConsentFormDTO consentFormDto) {
        Map<String, Boolean> permissionsMap = new HashMap<>();
        for (UserPracticeDTO practice : consentFormDto.getPayload().getPracticesInformation()) {
            permissionsMap.put(practice.getPracticeId(),
                    consentFormDto.getPayload().canViewForms(practice.getPracticeId()));
        }
        return permissionsMap;
    }

    private boolean hasAllPermissionsOn(ConsentFormDTO consentFormDto) {
        for (UserPracticeDTO practice : consentFormDto.getPayload().getPracticesInformation()) {
            if (!consentFormDto.getPayload().canViewForms(practice.getPracticeId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onProviderSelected(UserFormDTO practiceForm, int position) {
        callback.addFragment(ConsentFormViewPagerFragment.newInstance(position), true);
    }
}
