package com.carecloud.carepay.practice.library.signin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.signinsignup.fragments.ResetPasswordFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 03/12/2017
 */
public class ResetPasswordActivity extends BasePracticeActivity implements FragmentActivityInterface {

    private SignInDTO signInDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setSystemUiVisibility();
        Bundle bundle = getIntent().getExtras();
        signInDTO = DtoHelper.getConvertedDTO(SignInDTO.class, bundle);
        if (savedInstanceState == null) {
            replaceFragment(ResetPasswordFragment.newInstance(), false);
        }
    }

    @Override
    public DTO getDto() {
        return signInDTO;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }
}
