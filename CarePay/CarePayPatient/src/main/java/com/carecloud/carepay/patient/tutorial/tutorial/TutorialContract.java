package com.carecloud.carepay.patient.tutorial.tutorial;

import java.util.List;

import com.carecloud.carepay.patient.tutorial.TutorialFragment;
import com.carecloud.carepay.patient.tutorial.TutorialItem;

interface TutorialContract {

    interface View {
        void showEndTutorial();

        void showDoneButton();

        void showSkipButton();

        void setViewPagerFragments(List<TutorialFragment> tutorialFragments);
    }

    interface UserActionsListener {
        void loadViewPagerFragments(List<TutorialItem> tutorialItems);

        void doneOrSkipClick();

        void onPageSelected(int pageNo);
    }

}
