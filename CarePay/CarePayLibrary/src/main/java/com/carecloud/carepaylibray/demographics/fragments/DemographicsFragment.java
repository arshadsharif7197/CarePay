package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityPersDetailsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.utils.AddressUtil;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DemographicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DemographicsFragment extends CheckInDemographicsBaseFragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DemographicDTO demographicDTO;
    private DemographicLabelsDTO globalLabelsMetaDTO;
    private DemographicMetadataEntityPersDetailsDTO persDetailsMetaDTO;
    private DemographicPersDetailsPayloadDTO demographicPersDetailsPayloadDTO;
    private DemographicsListener demographicsListener;

    private View.OnClickListener genderListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            showDialog(
                    getOptionsFrom(persDetailsMetaDTO.properties.gender.options) ,
                    globalLabelsMetaDTO.getDemographicsTitleSelectGender(),
                    globalLabelsMetaDTO.getDemographicsCancelLabel(),
                    (TextView) findViewById(R.id.chooseGenderTextView));
        }
    };
    private View.OnClickListener raceListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            showDialog(
                    getOptionsFrom(persDetailsMetaDTO.properties.primaryRace.options) ,
                    globalLabelsMetaDTO.getDemographicsTitleSelectRace(),
                    globalLabelsMetaDTO.getDemographicsCancelLabel(),
                    (TextView) findViewById(R.id.raceListDataTextView));
        }
    };
    private View.OnClickListener ethnicityListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            showDialog(
                    getOptionsFrom(persDetailsMetaDTO.properties.ethnicity.options) ,
                    globalLabelsMetaDTO.getDemographicsTitleSelectEthnicity(),
                    globalLabelsMetaDTO.getDemographicsCancelLabel(),
                    (TextView) findViewById(R.id.ethnicityListDataTextView));
        }
    };

    public DemographicsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View mainView = inflater.inflate(R.layout.fragment_review_demographic_demographics, container, false);


        View mainView  = super.onCreateView(inflater, container, savedInstanceState);


        initializeDemographicsDTO();

        initialiseUIFields(mainView);

        setTypefaces(mainView);

        //formatEditText(view);

        initUiFromModels(mainView);

        checkIfEnableButton(mainView);
        /*

         initialiseUIFields(view);
        setTypefaces(view);
        formatEditText(view);
        initViewFromModels(view);
         */


        stepProgressBar.setCurrentProgressDot(2);

        return mainView;
    }


    public void setTypefaces(View view){

        setProximaNovaRegularTypeface(getActivity(), ((TextView) view.findViewById(R.id.raceDataTextView)));
        setProximaNovaSemiboldTypeface(getActivity(), ((TextView) view.findViewById(R.id.raceListDataTextView)));

        setProximaNovaRegularTypeface(getActivity(), ((TextView) view.findViewById(R.id.genderTextView)));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.chooseGenderTextView));

        setProximaNovaRegularTypeface(getActivity(), ((TextView) view.findViewById(R.id.ethnicityDataTextView)));
        setProximaNovaSemiboldTypeface(getActivity(), ((TextView) view.findViewById(R.id.ethnicityListDataTextView)));
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DemographicsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DemographicsFragment newInstance(String param1, String param2) {
        DemographicsFragment fragment = new DemographicsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }




    @Override
    protected DemographicDTO updateDemographicDTO(View view) {

        String gender = ((TextView)findViewById(R.id.chooseGenderTextView)).getText().toString();
        if (!StringUtil.isNullOrEmpty(gender)) {
            demographicPersDetailsPayloadDTO.setGender(gender);
        }

        String race = ((TextView) findViewById(R.id.raceListDataTextView)).getText().toString();
        if (!StringUtil.isNullOrEmpty(race)) {
            demographicPersDetailsPayloadDTO.setPrimaryRace(race);
        }

        String ethnicity = ((TextView) findViewById(R.id.ethnicityListDataTextView)).getText().toString();
        if (!StringUtil.isNullOrEmpty(ethnicity)) {
            demographicPersDetailsPayloadDTO.setEthnicity(ethnicity);
        }

        demographicDTO.getPayload().getDemographics().getPayload().setPersonalDetails(demographicPersDetailsPayloadDTO);

        return demographicDTO;

    }

    private void initialiseUIFields(View view) {

        //globalLabelsMetaDTO.getDemographicsAddressHeader()
        setHeaderTitle("Demographics", view);
        initNextButton(globalLabelsMetaDTO.getDemographicsReviewNextButton(), null, view);

        ((TextView) view.findViewById(R.id.raceListDataTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        ((TextView) view.findViewById(R.id.raceDataTextView)).setText(persDetailsMetaDTO.properties.primaryRace.getLabel());
        view.findViewById(R.id.raceListDataTextView).setOnClickListener(raceListener);

        ((TextView) view.findViewById(R.id.ethnicityListDataTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        ((TextView) view.findViewById(R.id.ethnicityDataTextView)).setText(persDetailsMetaDTO.properties.ethnicity.getLabel());
        view.findViewById(R.id.ethnicityListDataTextView).setOnClickListener(ethnicityListener);

        ((TextView) view.findViewById(R.id.chooseGenderTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
        ((TextView) view.findViewById(R.id.genderTextView)).setText(persDetailsMetaDTO.properties.gender.getLabel());
        view.findViewById(R.id.chooseGenderTextView).setOnClickListener(genderListener);

    }

    private void initUiFromModels(View view){

        /*
         String getGender = demographicPersDetailsPayloadDTO.getGender();
            if (SystemUtil.isNotEmptyString(getGender)) {
                selectGender.setText(getGender);
            } else {
                selectGender.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());

            }
            String getRace = demographicPersDetailsPayloadDTO.getPrimaryRace();
            if (SystemUtil.isNotEmptyString(getRace)) {
                raceDataTextView.setText(getRace);
            } else {
                raceDataTextView.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }
            String getethnicity = demographicPersDetailsPayloadDTO.getEthnicity();
            if (SystemUtil.isNotEmptyString(getethnicity)) {
                ethnicityDataTextView.setText(getethnicity);
            } else {
                ethnicityDataTextView.setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }
         */



        if (demographicPersDetailsPayloadDTO != null) {

            if (SystemUtil.isNotEmptyString(demographicPersDetailsPayloadDTO.getGender())) {
                ((TextView) view.findViewById(R.id.chooseGenderTextView)).setText(demographicPersDetailsPayloadDTO.getGender());
            } else {
                ((TextView) view.findViewById(R.id.chooseGenderTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }

            if (SystemUtil.isNotEmptyString(demographicPersDetailsPayloadDTO.getPrimaryRace())) {
                ((TextView) view.findViewById(R.id.raceListDataTextView)).setText(demographicPersDetailsPayloadDTO.getPrimaryRace());
            } else {
                ((TextView) view.findViewById(R.id.raceListDataTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }

            if (SystemUtil.isNotEmptyString(demographicPersDetailsPayloadDTO.getEthnicity())) {
                ((TextView) view.findViewById(R.id.ethnicityListDataTextView)).setText(demographicPersDetailsPayloadDTO.getEthnicity());
            } else {
                ((TextView) view.findViewById(R.id.ethnicityListDataTextView)).setText(globalLabelsMetaDTO.getDemographicsChooseLabel());
            }

        }
    }


    public void onButtonPressed(Uri uri) {
        if (demographicsListener != null) {
            demographicsListener.onFragmentInteraction(uri);
        }
    }

    public void navigateToIdentificationFragment() {
        if (demographicsListener != null) {
            demographicsListener.navigateToIdentificationFragment();
        }
    }


    public void navigateToAddressFragment() {
        if (demographicsListener != null) {
            demographicsListener.navigateToAddressFragment();
        }
    }

    public void updateDto() {
        if (demographicsListener != null) {
            demographicsListener.updateDTO(demographicDTO);
        }
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DemographicsListener) {
            demographicsListener = (DemographicsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DemographicsListener");
        }
    }

    /**
     * Init data
     *
     * */
    private void initializeDemographicsDTO() {
        demographicDTO = DtoHelper.getConvertedDTO(DemographicDTO.class, getArguments());
        globalLabelsMetaDTO = demographicDTO.getMetadata().getLabels();
        persDetailsMetaDTO = demographicDTO.getMetadata().getDataModels().demographic.personalDetails;

        if (demographicDTO.getPayload().getDemographics() != null) {
            demographicPersDetailsPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        demographicsListener = null;
    }

    private String[] getOptionsFrom(List<MetadataOptionDTO> options){
        List<String> strOptions = new ArrayList<>();
        for (MetadataOptionDTO o : options) {
            strOptions.add(o.getLabel());
        }
        return strOptions.toArray(new String[0]);
    }

    /**
     *
     * Show dialog
     *
     * */
    private void showDialog(final String[] dataArray, String title, String cancelLabel, final TextView editText){
        SystemUtil.showChooseDialog(getActivity(),
                dataArray, title, cancelLabel,
                editText,
                new SystemUtil.OnClickItemCallback() {
                    @Override
                    public void executeOnClick(TextView destination, String selectedOption) {
                        editText.setText(selectedOption);
                    }
                });
    }






    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface DemographicsListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void navigateToAddressFragment();

        void navigateToIdentificationFragment();

        void updateDTO(DemographicDTO model);


    }


    @Override
    protected boolean passConstraints(View view) {

        boolean isGenderValid = !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(  ((TextView) view.findViewById(R.id.chooseGenderTextView)).getText().toString());
        boolean isEthnicityValid = !globalLabelsMetaDTO.getDemographicsChooseLabel().equals( ((TextView) view.findViewById(R.id.ethnicityListDataTextView)).getText().toString());
        boolean isRaceValid = !globalLabelsMetaDTO.getDemographicsChooseLabel().equals(  ((TextView) view.findViewById(R.id.raceListDataTextView)).getText().toString());

        return isGenderValid && isEthnicityValid && isRaceValid;

    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_review_demographic_demographics;
    }



}
