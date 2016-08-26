package com.carecloud.carepaylibray.fragments.demographics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.ScreenModel;

public class DocumentsFragment extends Fragment {


   public static final DocumentsFragment newInstance(Bundle bundle) {
        DocumentsFragment fragment = new DocumentsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("DocumentsFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        return view;
        }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        LinearLayout.LayoutParams matchWidthParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout parent = new LinearLayout(getActivity());
        parent.setLayoutParams(matchWidthParams);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(20, 20, 0, 20);
//        LinearLayout.LayoutParams LayoutParamsview = new AppBarLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        ScreenModel screenModel = ApplicationWorkflow.Instance().getDemographicsDocumentsScreenModel();

        int index = 0;
        for (ScreenComponentModel componentModel : screenModel.getComponentModels()) {
            if (componentModel.getType().equals("image")) {
                ImageView image = new ImageView(getActivity());
                image.setImageResource(R.drawable.icn_signup_illustration_step_2_documents);
                image.setLayoutParams(matchWidthParams);
                parent.addView(image,index,matchWidthParams);

            }else if (componentModel.getType().equals("ImageView")) {
                ImageView images = new ImageView(getActivity());
                images.setImageResource(R.drawable.icn_placeholder_document);
                images.setLayoutParams(matchWidthParams);
                parent.addView(images, index, matchWidthParams);

            }else if(componentModel.getType().equals("button")){

                Button select =  new Button(getActivity());
                select.setText(componentModel.getLabel());
                select.setLayoutParams(matchWidthParams);
                parent.addView(select);

            }
            else if(componentModel.getType().equals("Inputtext")) {
                EditText et1 = new EditText(getActivity());
                et1.setLayoutParams(matchWidthParams);
                et1.setHint(componentModel.getLabel());
                parent.addView(et1);
            }
            else if(componentModel.getType().equals("togglebutton")) {
                ToggleButton toggle= new ToggleButton(getActivity());
                toggle.setLayoutParams(matchWidthParams);
                toggle.setHint(componentModel.getLabel());
                parent.addView(toggle);

            }
            else if(componentModel.getType().equals("text")){
                TextView tv1 = new TextView(getActivity());
                tv1.setLayoutParams(matchWidthParams);
                tv1.setText(componentModel.getLabel());
                tv1.setGravity(Gravity.CENTER);
                parent.addView(tv1);
            }

            index++;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        viewGroup.addView(parent);
    }
}


