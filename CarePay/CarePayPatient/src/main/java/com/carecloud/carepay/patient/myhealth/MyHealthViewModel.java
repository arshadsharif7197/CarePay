package com.carecloud.carepay.patient.myhealth;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepay.patient.myhealth.dtos.MedicationDto;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 2019-08-12.
 */
public class MyHealthViewModel extends BaseViewModel {

    private MutableLiveData<MyHealthDto> myHealthDto;
    private MutableLiveData<MyHealthDto> medicationDto = new MutableLiveData<>();

    public MyHealthViewModel(Application application) {
        super(application);
    }

    public MutableLiveData<MyHealthDto> getMyHealthDto() {
        return getMyHealthDto(null, false);
    }

    public MutableLiveData<MyHealthDto> getMyHealthDto(TransitionDTO myHealthTransition, boolean refresh) {
        if (myHealthDto == null || refresh) {
            myHealthDto = new MutableLiveData<>();
            loadDto(myHealthTransition);
        }
        return myHealthDto;
    }

    private void loadDto(TransitionDTO myHealthTransition) {
        Map<String, String> queryMap = new HashMap<>();
        ((CarePayApplication) getApplication()).getWorkflowServiceHelper().execute(myHealthTransition,
                new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {
                        setSkeleton(true);
                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        setSkeleton(false);
                        myHealthDto.setValue(DtoHelper.getConvertedDTO(MyHealthDto.class, workflowDTO));
                    }

                    @Override
                    public void onFailure(String exceptionMessage) {
                        setSkeleton(false);
                        setErrorMessage(exceptionMessage);
                    }
                }, queryMap);
    }

    public MutableLiveData<MyHealthDto> getMedicationDto() {
        return medicationDto;
    }

    public void callMedicationBreezeService(MedicationDto medication, String code, String codeSystem) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("code", code);
        queryMap.put("code_system", codeSystem);
        queryMap.put("term", medication.getDrugName());
        TransitionDTO transitionDTO = myHealthDto.getValue().getMetadata()
                .getLinks().getEducationMaterial();
        ((CarePayApplication) getApplication()).getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                setLoading(false);
                medicationDto.setValue(DtoHelper.getConvertedDTO(MyHealthDto.class, workflowDTO));
            }

            @Override
            public void onFailure(String exceptionMessage) {
                setLoading(false);
                setErrorMessage(exceptionMessage);
            }
        }, queryMap);
    }
}
