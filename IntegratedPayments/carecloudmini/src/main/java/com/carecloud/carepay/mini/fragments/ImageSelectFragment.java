package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;
import com.carecloud.carepay.mini.utils.Defs;
import com.carecloud.carepay.mini.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lmenendez on 6/25/17
 */

public class ImageSelectFragment extends RegistrationFragment {

    private View nextButton;
    private ImageView practiceLogo;

    private UserPracticeDTO selectedPractice;

    private @Defs.ImageStyles int selectedImageStyle;
    private View lastSelectionIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_registration_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initProgressToolbar(view, getString(R.string.registration_image_title), 5);

        View practiceLogoSelect = view.findViewById(R.id.practice_logo_select);
        practiceLogo = (ImageView) view.findViewById(R.id.practice_logo);
        practiceLogo.setOnClickListener(getChoiceClickListener(Defs.IMAGE_STYLE_PRACTICE_LOGO, practiceLogoSelect));

        View practiceInitialsSelect = view.findViewById(R.id.practice_initials_select);
        View practiceInitials = view.findViewById(R.id.practice_initials);
        practiceInitials.setOnClickListener(getChoiceClickListener(Defs.IMAGE_STYLE_PRACTICE_INITIALS, practiceInitialsSelect));

        View carecloudSelect = view.findViewById(R.id.carecloud_logo_select);
        View carecloudLogo = view.findViewById(R.id.carecloud_logo);
        carecloudLogo.setOnClickListener(getChoiceClickListener(Defs.IMAGE_STYLE_CARECLOUD_LOGO, carecloudSelect));

        nextButton = view.findViewById(R.id.button_next);
        nextButton.setVisibility(View.INVISIBLE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedImageStyle();
            }
        });

        View editInitials = view.findViewById(R.id.edit_initials);
        editInitials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.replaceFragment(new EditInitialsFragment(), false);
            }
        });

        initPracticeInfo(view);
    }

    private void initPracticeInfo(View view){
        selectedPractice = callback.getRegistrationDataModel().getPayloadDTO().getUserPractices().get(0);

        final TextView practiceInitials = (TextView) view.findViewById(R.id.practice_initials_name);
        if(selectedPractice.getPracticeInitials()!=null){
            practiceInitials.setText(selectedPractice.getPracticeInitials());
        }else {
            practiceInitials.setText(StringUtil.getShortName(selectedPractice.getPracticeName()));
        }

        View practiceLogoLayout = view.findViewById(R.id.practice_logo_layout);
        final View practiceLogoUnavailable = view.findViewById(R.id.practice_logo_unavailable);
        String imageUrl = selectedPractice.getPracticePhoto();
        if(StringUtil.isNullOrEmpty(imageUrl)){
            practiceLogoLayout.setVisibility(View.GONE);
        }else{
            Picasso.with(getContext())
                    .load(imageUrl)
                    .resize(200, 200)
                    .centerCrop()
                    .transform(new CropCircleTransformation())
                    .placeholder(R.drawable.practice_no_image_bkg)
                    .into(practiceLogo, new Callback() {
                        @Override
                        public void onSuccess() {
                            practiceLogo.setClickable(true);
                            practiceLogoUnavailable.setVisibility(View.GONE);
                            //// TODO: 6/25/17 go to summary screen
                        }

                        @Override
                        public void onError() {
                            practiceLogo.setClickable(false);
                            practiceLogoUnavailable.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    private void setSelectedImageStyle(){
        getApplicationHelper().getApplicationPreferences().setImageStyle(selectedImageStyle);
        callback.replaceFragment(new ReviewInfoFragment(), true);
    }

    private View.OnClickListener getChoiceClickListener(@Defs.ImageStyles final int imageStyle, final View selectionIcon){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastSelectionIcon != null){
                    lastSelectionIcon.setVisibility(View.GONE);
                }
                selectedImageStyle = imageStyle;
                selectionIcon.setVisibility(View.VISIBLE);
                lastSelectionIcon = selectionIcon;

                nextButton.setVisibility(View.VISIBLE);
            }
        };
    }
}
