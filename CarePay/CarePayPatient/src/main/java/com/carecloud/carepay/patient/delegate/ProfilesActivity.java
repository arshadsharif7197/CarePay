package com.carecloud.carepay.patient.delegate;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.delegate.fragments.ProfileListFragment;
import com.carecloud.carepay.patient.delegate.interfaces.ProfileManagementInterface;
import com.carecloud.carepay.patient.delegate.model.DelegateDto;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.profile.UserLinks;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2019-06-13.
 */
public class ProfilesActivity extends BasePatientActivity implements ProfileManagementInterface {

    public static final int CHANGES_DONE = 103;
    private DelegateDto delegateDto;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_profiles);
        delegateDto = getConvertedDTO(DelegateDto.class);
        replaceFragment(ProfileListFragment.newInstance(), false);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public DTO getDto() {
        return delegateDto;
    }

    @Override
    public void updateProfiles(UserLinks userLinks) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        DtoHelper.bundleDto(bundle, userLinks);
        intent.putExtras(bundle);
        setResult(CHANGES_DONE, intent);
        if (userLinks.getRepresentedUsers().isEmpty()) {
            finish();
        } else {
            getSupportFragmentManager().popBackStackImmediate();
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            if (fragment instanceof ProfileListFragment) {
                ((ProfileListFragment) fragment).refreshList(userLinks);
            }
        }
    }
}
