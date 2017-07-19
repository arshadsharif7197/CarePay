package com.carecloud.carepay.patient.myhealth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.myhealth.fragments.MyHealthMainFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthActivity extends MenuPatientActivity implements FragmentActivityInterface {

    private MyHealthDto myHealthDto;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        myHealthDto = getConvertedDTO(MyHealthDto.class);
        if (icicle == null) {
            replaceFragment(MyHealthMainFragment.newInstance(), false);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MenuItem menuItem = navigationView.getMenu().findItem(com.carecloud.carepaylibrary.R.id.nav_my_health);
        menuItem.setChecked(true);
        displayToolbar(true, menuItem.getTitle().toString());
    }

    @Override
    public DTO getDto() {
        return myHealthDto;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.container_main, fragment, addToBackStack);
    }
}
