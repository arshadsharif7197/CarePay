# shamrock-android


## UI Automated Testing

We are using Espresso for automation, more info can be found [here](https://developer.android.com/training/testing/espresso). Our testing framework code is structured using Page Object Model(POM).

The test code can be found in the `src/androidTest` folder of each project being tested.

Practice app: 

`shamrock_android/CarePay/CarePayPracticeTablet/src/androidTest/java/com/carecloud/carepay/practice/tablet`

Patient app: 

`shamrock_android/CarePay/CarePayPatient/src/androidTest/java/com/carecloud/carepay/patient`

Testing related code being shared between the apps/frameworks can be found in the `src/main` folder in CarePayLibrary.

`shamrock_android/CarePay/CarePayLibrary/src/main/java/com/carecloud/carepaylibray/androidTest`

### Running the tests

To run the test you can use the following command from the CarePay project root. This will run the test using the debug QA variant. You will need to have the Android emulator running already.

Practice app: ```./gradlew -p CarePayPracticeTablet connectedQaDebugAndroidTest```

Patient app: ```./gradlew -p CarePayPatient connectedQaDebugAndroidTest```

If running the tests in Firebase Test lab, you need to create two different APKs, one for the actual app and the other for the tests. Use the following commands to generate the APKs for Practice app, using the debug QA variant.

App APK: ```./gradlew -p CarePayPracticeTablet assembleQaDebug```

Test APK: ```./gradlew -p CarePayPracticeTablet assembleQaDebugAndroidTest```

Afterwards you can use the command provided by Firebase along with the files created to run the tests.

### Writing Tests

Page Object Model(POM) allows us to encapsulate the actions we will be performing on the app and resuse the automation code written for similar views/flows. The goal is to create a different class for each view we see with multiple actions on it.

You can read about POM [here](https://martinfowler.com/bliki/PageObject.html). The article talks about web applications and uses a different testing framework, Selenium, but the structure of the framework can be used for mobile apps as well.

The testing folder within each app is broken down into the `pageObjects` and `tests` subfolders. Lets look at the `pageObjects` and see how we create a simple one.

If we look at the login screen for patient app, we can see that we can perform multiple actions on it. The following are some of the possibilites, the view contains a lot more but we will just focus on these:
* Enter username
* Enter password
* Press login button

When mapping the view, we create a class that contains a method for each action. We are currently using the content description as a selector for the elements, but we can also use index, text selector, or a combination of all, when dealing with complicated views. Lets look at the page object for patient Login screen: 

`shamrock_android/CarePay/CarePayPatient/src/androidTest/java/com/carecloud/carepay/patient/pageObjects/LoginScreen.kt`

``` kotlin
class LoginScreen : CustomViewActions() {
    fun typeUser(user: String): LoginScreen {
        type(appContext.getString(R.string.content_description_signin_user), user)
        return this
    }
    fun typePassword(password: String): LoginScreen {
        type(appContext.getString(R.string.content_description_signin_password), password)
        return this
    }
    fun pressLoginButton(): AppointmentScreen {
        click(appContext.getString(R.string.content_description_signin_button))
        return AppointmentScreen()
    }
}
```
Each method should do the necessary steps to achieve the action, you can add multiple steps such as click and type. If the action keeps you in the same view, such as type username, you should return the same class you are working on. In the case the action changes the view, we return a new class which should be mapped to the view where the action takes you to. For example, once you press the login button in patient you end up in the Appointments screen, so we return AppointmentScreen page object:
``` kotlin
fun pressLoginButton(): AppointmentScreen {
    click(appContext.getString(R.string.content_description_signin_button))
    return AppointmentScreen()
}
```
The actions are provided by CustomActionsViews, which is a wrapper around the Espresso actions, to help us customize the action we use for our specific purposes.

Once we have our POM created we can write a test. The tests are located within the testing folder of each app, in the `tests` subfolder. The following is an example test which used the page object methods created above:

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginTest : BaseTest() {
    @Test
    fun loginPatientTest() {
        LoginScreen()
                .typeUser("email@carecloud.com")
                .typePassword("Password")
                .pressLoginButton()
    }
}
```
If we run the test above, it would type the username, password and click on the Login button, which would take us directly to the Appointments screen. Once we create a page object for the Appointments screen, we would be able to continue performing actions until our test is done. 

At some points during our test we want to verify data on the screen, for example, we might want to check if the appointment created is displayed on the UI. In this example, we check that the "Add Appointment" button is displayed:

```kotlin
fun verifyAppointmentButtonIsDisplayed() : AppointmentScreen {
    verifyViewVisible(appContext.getString(R.string.content_description_appointments_list))
    return this
}
```
This check lets you know that Appointment screen was reached, meaning login was successful. Now we can add this step at the end of the test written above:
```kotlin
@Test
fun loginPatientTest() {
    LoginScreen()
            .typeUser("email@carecloud.com")
            .typePassword("Password")
            .pressLoginButton()
            .verifyAppointmentButtonIsDisplayed()
}
```