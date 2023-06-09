package com.carecloud.carepay.practice.library.appointments.createappointment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.provider.BaseProviderListFragment;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 1/15/19.
 */
public class ProviderListFragment extends BaseProviderListFragment {

    public static ProviderListFragment newInstance(UserPracticeDTO selectedPractice,
                                                   VisitTypeDTO selectedVisitType,
                                                   LocationDTO selectedLocation) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, selectedPractice);
        DtoHelper.bundleDto(args, selectedVisitType);
        DtoHelper.bundleDto(args, selectedLocation);
        ProviderListFragment fragment = new ProviderListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_provider_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(Label.getLabel("add_appointment_provider"));
        view.findViewById(R.id.closeViewLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        if(getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE){
            ImageView close = view.findViewById(R.id.cancel_img);
            close.setImageResource(R.drawable.icn_close);
        }else  if(getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE){
            ImageView close = view.findViewById(R.id.cancel_img);
            close.setImageResource(R.drawable.icn_close);
        }
    }
}
