package com.carecloud.carepaylibray.consentforms.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.activities.SignatureActivity;
import com.carecloud.carepaylibray.consentforms.fragments.ConsentForm1Fragment;
import com.carecloud.carepaylibray.consentforms.fragments.ConsentForm2Fragment;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.homescreen.HomeScreenActivity;
import com.carecloud.carepaylibray.intake.InTakeActivity;

import static com.carecloud.carepaylibray.utils.Utility.setTypefaceFromAssets;

public class ConsentActivity extends AppCompatActivity implements IFragmentCallback {

    public enum FORMS {
        FORM1, FORM2, FORM3
    }

    private TextView title;
    private FORMS showingForm = FORMS.FORM1;
    private View indicator0, indicator1, indicator2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.consent_activity_layout);

        indicator0 = findViewById(R.id.indicator0);
        indicator1 = findViewById(R.id.indicator1);
        indicator2 = findViewById(R.id.indicator2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.signup_toolbar);
        title = (TextView) toolbar.findViewById(R.id.signup_toolbar_title);
        setTypefaceFromAssets(this, "fonts/gotham_rounded_medium.otf", title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icn_patient_mode_nav_back));
        setSupportActionBar(toolbar);
        replaceFragment(getForm(), false);
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, fragment);

        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void signButtonClicked() {
        Intent intent = new Intent(this, SignatureActivity.class);
        intent.putExtra("consentform", showingForm);
        startActivityForResult(intent, CarePayConstants.SIGNATURE_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CarePayConstants.SIGNATURE_REQ_CODE) {
            Fragment fragment = getNextForm();
            if (fragment != null) {
                replaceFragment(fragment, true);
            } else {
                startActivity(new Intent(ConsentActivity.this, InTakeActivity.class));
            }
        }
    }

    private Fragment getForm() {
        if (showingForm == FORMS.FORM1) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.FORM_DATA, getFormData("form1"));
            ConsentForm1Fragment consentForm1Fragment = new ConsentForm1Fragment();
            consentForm1Fragment.setArguments(bundle);
            title.setText("Consent Form 1 of 3");
            updateTitle(FORMS.FORM1);

            return consentForm1Fragment;
        } else if (showingForm == FORMS.FORM2) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.FORM_DATA, getFormData("form2"));
            ConsentForm2Fragment consentForm2Fragment = new ConsentForm2Fragment();
            consentForm2Fragment.setArguments(bundle);
            updateTitle(FORMS.FORM2);
            return consentForm2Fragment;
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(CarePayConstants.FORM_DATA, getFormData("form3"));
            ConsentForm1Fragment consentForm1Fragment = new ConsentForm1Fragment();
            consentForm1Fragment.setArguments(bundle);
            updateTitle(FORMS.FORM3);
            return consentForm1Fragment;
        }
    }

    private void updateTitle(FORMS forms) {
        switch (forms) {
            case FORM1:
                indicator0.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator1.setBackgroundResource(R.drawable.circle_indicator_gray);
                indicator2.setBackgroundResource(R.drawable.circle_indicator_gray);
                break;

            case FORM2:
                title.setText("Consent Form 2 of 3");
                indicator0.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator1.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator2.setBackgroundResource(R.drawable.circle_indicator_gray);
                break;

            case FORM3:
                title.setText("Consent Form 3 of 3");
                indicator0.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator1.setBackgroundResource(R.drawable.circle_indicator_blue);
                indicator2.setBackgroundResource(R.drawable.circle_indicator_blue);
                break;
        }

    }

    private FormData getFormData(String formName) {
        FormData formData = new FormData();

        if (formName.equals("form1")) {
            formData.setTitle("Consent for Medical Care");
            formData.setDescription("Please fill in this form about general cardiac symptoms you may have had in the past.");
            formData.setContent("I, Bruno O. Barros, understand that I have a condition thar requires medical treatment. Lorem ipsum dolor sit amet. Nullam id dolor id nibh ultricies vehicula ut id elit. Donec ullamcorper nulla non metus auctor fringilla. Morbi leo risus, porta ac consectetur ac, vestibulum at eros. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nullam quis risus eget urna mollis ornare vel eu leo. Maecenas sed diam eget risus varius blandit sit amet non magna.\n" +
                                        "Etiam porta sem malesuada magna mollis euismod. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum. Sed posuere consectetur est at lobortis. Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem nec elit. Nulla vitae elit libero, a pharetra augue. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum. Sed posuere consectetur est at lobortis. Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem nec elit. Aenean lacinia bibendum nulla sed consectetur. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed posuere consectetur est at lobortis.");
            formData.setDate("August 1st, 2016");
        } else if (formName.equals("form2")) {
            formData.setTitle("Authorization Form");
            formData.setDescription("Please fill in this form about general cardiac symptoms you may have had in the past.");
            formData.setContent("This form grants temporary authority to a designated adult to provide and arrange for medical care for a minor in the event of an emergency, where the minor is not accompanied by either parents or legal guardians, and it may not be feasible or practical to contact them. This form should be given to the trip leader or shown to the trip leader and then carried by the designated adult.");
            formData.setContent2("I do hereby state that I have legal custody of the aforementioned Minor. I grant my authorization and consent for Dr. Joshua Wellington to administer general first aid treatment for any minor injuries or illnesses experienced by the Minor. If the injury or illness is life threatening or in need of emergency treatment, I authorize the Designated Adult to summon any and all professional emergency personnel to attend, transport, and treat the minor and to issue consent for any X-ray, anesthetic, blood transfusion, medication, or other medical diagnosis, treatment, or hospital care deemed advisable by, and to be rendered under the general supervision of, any licensed physician, surgeon, dentist, hospital, or other medical professional or institution duly licensed to practice in the state in which such treatment is to occur. I agree to assume financial responsibility for all expenses of such care. \n" +
                                         "It is understood that this authorization is given in advance of any such medical treatment, but is given to provide authority and power on the part of the Designated Adult in the exercise of his or her best judgment upon the advice of any such medical or emergency personnel.");
            formData.setDate("August 1st, 2016");
        } else { //form3
            formData.setTitle("HIPAA Confidentiality Agreement");
            formData.setDescription("Please read this form carefully and completely before signing it.");
            formData.setContent("Employees and partners of the practice will have access to confidential information, both written and oral, in the course of their employment and job responsibilities. It is imperative that this information is not disclosed to any unauthorized individuals to maintain the integrity of the patient information. An unauthorized individual would be any person that is not currently an employee of the practice and/or any information. Any other disclosures may only occur at the direction may only occur at the direction of the Privacy Office or by patient authorization. \n" +
                                        "I have read and understand the practice’s policies with regards to privacy and Security of personal health information. I agree to maintain confidentiality of all information obtained in the course of my employment including, but not limited to, financial, technical, or propriety information of the organization and personal and sensitive information regarding patients, employees, and vendors. I understand that inappropriate disclosure or release of patient information is grounds for termination.");
            formData.setDate("August 1st, 2016");
        }
        return formData;
    }

    private Fragment getNextForm() {
        if (showingForm == FORMS.FORM1) {
            showingForm = FORMS.FORM2;
            return getForm();
        } else if (showingForm == FORMS.FORM2) {
            showingForm = FORMS.FORM3;
            return getForm();
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            updateTitle(FORMS.FORM2);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            updateTitle(FORMS.FORM1);
        }
    }
}
