package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.VisitTypeListAdapter;
import com.carecloud.carepaylibray.appointments.fragments.BaseAppointmentDialogFragment;
import com.carecloud.carepaylibray.appointments.interfaces.VisitTypeInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VisitTypeFragmentDialog extends BaseAppointmentDialogFragment
        implements VisitTypeListAdapter.VisitTypeSelectionCallback {

    private VisitTypeInterface callback;
    private List<VisitTypeDTO> visitTypeList;
    private AppointmentsSettingDTO appointmentSettings;
    private AppointmentResourcesDTO model;
    private AppointmentsResultModel appointmentsResultModel;

    /**
     * Creates a VisitTypeFragmentDialog fragment
     *
     * @param appointmentResourcesDTO The appointment Resource DTO
     * @param appointmentsResultModel The appointment resource model
     */
    public static VisitTypeFragmentDialog newInstance(AppointmentResourcesDTO appointmentResourcesDTO,
                                                      AppointmentsResultModel appointmentsResultModel,
                                                      AppointmentsSettingDTO appointmentsSettings) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, appointmentResourcesDTO);
        DtoHelper.bundleDto(args, appointmentsResultModel);
        DtoHelper.bundleDto(args, appointmentsSettings);

        VisitTypeFragmentDialog dialog = new VisitTypeFragmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attachCallback(context);
    }

    @Override
    protected void attachCallback(Context context) {
        try {
            if (context instanceof AppointmentViewHandler) {
                callback = ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (VisitTypeInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement VisitTypeInterface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        model = DtoHelper.getConvertedDTO(AppointmentResourcesDTO.class, arguments);
        appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, arguments);
        visitTypeList = model.getResource().getVisitReasons();
        appointmentSettings = DtoHelper.getConvertedDTO(AppointmentsSettingDTO.class, arguments);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_visit_type, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        sortVisitTypeListByName();
        initializeViews(view);
    }

    protected void initializeViews(View view) {
        hideKeyboardOnViewTouch(view);

        View closeView = view.findViewById(R.id.closeViewLayout);
        if (closeView != null) {
            view.findViewById(R.id.closeViewLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        setCancelable(closeView == null);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
        if (toolbar != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
            title.setText(Label.getLabel("visit_type_heading"));
            toolbar.setTitle("");
            ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            title.setLayoutParams(layoutParams);
            title.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        RecyclerView visitTypeListView = (RecyclerView) view.findViewById(R.id.visitTypeList);
        if (visitTypeList.size() > 0) {
            // Load and display list
            visitTypeListView.setLayoutManager(new LinearLayoutManager(getContext()));
            visitTypeListView.setAdapter(new VisitTypeListAdapter(view.getContext(), visitTypeList,
                    appointmentSettings.getPrePayments(), this));
        } else if (view.findViewById(R.id.emptyStateScreen) != null) {
            visitTypeListView.setVisibility(View.GONE);
            view.findViewById(R.id.emptyStateScreen).setVisibility(View.VISIBLE);
            if (view.findViewById(R.id.visit_type_header_title) != null) {
                view.findViewById(R.id.visit_type_header_title).setVisibility(View.GONE);
            }
        }
    }

    private void sortVisitTypeListByName() {
        Collections.sort(visitTypeList, new Comparator<VisitTypeDTO>() {
            @Override
            public int compare(VisitTypeDTO lhs, VisitTypeDTO rhs) {
                if (lhs != null && rhs != null) {
                    return lhs.getName().compareTo(rhs.getName());
                }
                return -1;
            }
        });
    }

    @Override
    public void onVisitTypeSelected(VisitTypeDTO visitTypeDTO) {
        dismiss();
        callback.onVisitTypeSelected(visitTypeDTO, model, appointmentsResultModel);
    }
}
