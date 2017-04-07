package com.carecloud.carepay.patient.tutorial.tutorial;

import com.carecloud.carepay.patient.tutorial.TutorialFragment;
import com.carecloud.carepay.patient.tutorial.TutorialItem;

import java.util.ArrayList;
import java.util.List;

class TutorialPresenter implements TutorialContract.UserActionsListener {
    private TutorialContract.View tutorialView;
    private List<TutorialFragment> fragments;

    TutorialPresenter(TutorialContract.View tutorialView) {
        this.tutorialView = tutorialView;
    }

    @Override
    public void loadViewPagerFragments(List<TutorialItem> tutorialItems) {
        fragments = new ArrayList<>();
        for (int i = 0; i < tutorialItems.size(); i++) {
            TutorialFragment helpTutorialImageFragment;
            helpTutorialImageFragment = TutorialFragment.newInstance(tutorialItems.get(i), i);
            fragments.add(helpTutorialImageFragment);
        }
        tutorialView.setViewPagerFragments(fragments);
    }

    @Override
    public void doneOrSkipClick() {
        tutorialView.showEndTutorial();
    }

    @Override
    public void onPageSelected(int pageNo) {
        if (pageNo >= fragments.size() - 1) {
            tutorialView.showDoneButton();
        } else {
            tutorialView.showSkipButton();
        }
    }
}
