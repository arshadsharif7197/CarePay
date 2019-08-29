package com.carecloud.carepay.patient.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carecloud.carepay.patient.BaseTest
import com.carecloud.carepay.patient.pageObjects.LoginScreen
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author pjohnson on 2019-08-28.
 */
@RunWith(AndroidJUnit4::class)
class LoginTest : BaseTest() {

    @Test
    fun loginPatientTest() {
        LoginScreen()
                .typeUser("dev_emails+dev.rsanchez@carecloud.com")
                .typePassword("Test123!")
                .pressLoginButton()
    }


}