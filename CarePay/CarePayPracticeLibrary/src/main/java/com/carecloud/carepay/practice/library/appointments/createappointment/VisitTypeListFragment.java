package com.carecloud.carepay.practice.library.appointments.createappointment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.visittype.BaseVisitTypeListFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 1/15/19.
 */
public class VisitTypeListFragment extends BaseVisitTypeListFragment {

    public static VisitTypeListFragment newInstance(UserPracticeDTO selectedPractice,
                                                    LocationDTO selectedLocation,
                                                    AppointmentResourcesItemDTO selectedResource) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, selectedPractice);
        DtoHelper.bundleDto(args, selectedLocation);
        DtoHelper.bundleDto(args, selectedResource);
        VisitTypeListFragment fragment = new VisitTypeListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visit_type_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpToolbar(view);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(Label.getLabel("visit_type_heading"));
        view.findViewById(R.id.closeViewLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        if(getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE){
            ImageView close = view.findViewById(R.id.cancel_img);
            close.setImageResource(R.drawable.icn_close);
        }
    }

}
