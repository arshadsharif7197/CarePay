package com.carecloud.carepay.patient.retail.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.retail.fragments.RetailFragment;
import com.carecloud.carepay.patient.retail.fragments.RetailListFragment;
import com.carecloud.carepay.patient.retail.interfaces.RetailInterface;
import com.carecloud.carepay.patient.retail.models.RetailModel;
import com.carecloud.carepay.patient.retail.models.RetailPracticeDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;

/**
 * Created by lmenendez on 2/8/17
 */

public class RetailActivity extends MenuPatientActivity implements RetailInterface {

    private RetailModel retailModel;

    private RetailFragment retailFragment;
    private String title;
    private boolean hideToolbar = false;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        retailModel = getConvertedDTO(RetailModel.class);

        Fragment fragment;
        if(retailModel.getPayload().getRetailPracticeList().size() == 1) {
            RetailPracticeDTO retailPracticeDTO = retailModel.getPayload().getRetailPracticeList().get(0);
            retailFragment = RetailFragment.newInstance(retailModel, retailPracticeDTO, lookupUserPractice(retailPracticeDTO));
            fragment = retailFragment;
        }else{
            fragment = RetailListFragment.newInstance(retailModel);
        }

        replaceFragment(fragment, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.nav_purchase);
        menuItem.setChecked(true);
        title = menuItem.getTitle().toString();
        if(!hideToolbar) {
            displayToolbar(true);
        }
    }

    @Override
    public void onBackPressed(){
        if(retailFragment == null || !retailFragment.handleBackButton()){
            displayToolbar(true);
            super.onBackPressed();
        }
    }

    @Override
    public void displayRetailStore(RetailModel retailModel, RetailPracticeDTO retailPractice, UserPracticeDTO userPracticeDTO) {
        retailFragment = RetailFragment.newInstance(retailModel, retailPractice, userPracticeDTO);
        replaceFragment(retailFragment, true);
        displayToolbar(false);
    }

    @Override
    public void displayToolbar(boolean visibility) {
        displayToolbar(visibility, title);
        hideToolbar = !visibility;
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack){
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    private UserPracticeDTO lookupUserPractice(RetailPracticeDTO retailPracticeDTO){
        for(UserPracticeDTO userPracticeDTO : retailModel.getPayload().getUserPractices()){
            if(retailPracticeDTO.getPracticeId()!=null && retailPracticeDTO.getPracticeId().equals(userPracticeDTO.getPracticeId())){
                return userPracticeDTO;
            }
        }
        return new UserPracticeDTO();
    }

}
