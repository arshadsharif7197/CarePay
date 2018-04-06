package com.carecloud.carepay.patient.consentforms;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 8/03/18.
 */

public class ConsentFormsListFragment extends BaseFragment {


    private FragmentActivityInterface callback;
    private ConsentFormDTO consentFormDto;

    public static ConsentFormsListFragment newInstance() {
        return new ConsentFormsListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInterface) {
            callback = (FragmentActivityInterface) context;
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
        Map<String, UserPracticeDTO> practicesInformation = getPracticesInformation(consentFormDto.getPayload()
                .getPracticesInformation());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.consentFormsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ConsentFormsRecyclerAdapter(consentFormDto.getPayload().getForms(), practicesInformation));
    }

    private Map<String, UserPracticeDTO> getPracticesInformation(List<UserPracticeDTO> practicesInformation) {
        Map<String, UserPracticeDTO> practicesMap = new HashMap<>();
        for (UserPracticeDTO practice : practicesInformation) {
            practicesMap.put(practice.getPracticeId(), practice);
        }
        return practicesMap;
    }
}
