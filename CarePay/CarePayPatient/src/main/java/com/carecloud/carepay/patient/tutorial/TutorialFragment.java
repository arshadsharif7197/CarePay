package com.carecloud.carepay.patient.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.squareup.picasso.Picasso;

public class TutorialFragment extends Fragment {
    private static final String ARG_TUTORIAL_ITEM = "arg_tut_item";
    private static final String ARG_PAGE = "arg_page";

    public static TutorialFragment newInstance(TutorialItem tutorialItem, int page) {
        TutorialFragment helpTutorialImageFragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TUTORIAL_ITEM, tutorialItem);
        args.putInt(ARG_PAGE, page);
        helpTutorialImageFragment.setArguments(args);
        return helpTutorialImageFragment;
    }

    private TutorialItem tutorialItem;
    int page;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        tutorialItem = b.getParcelable(ARG_TUTORIAL_ITEM);
        page = b.getInt(ARG_PAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tutorial_image, container, false);
        view.setTag(page);

        ImageView imageViewFront = (ImageView) view.findViewById(R.id.fragment_tutorial_imageview);
        TextView textViewSubTitle = (TextView) view.findViewById(R.id.fragment_tutorial_subtitle_text);

        TextView textView = (TextView) view.findViewById(R.id.fragment_tutorial_text);
        if (!TextUtils.isEmpty(tutorialItem.getTitleText())) {
            textView.setText(tutorialItem.getTitleText());
        } else if (tutorialItem.getTitleTextRes() != -1) {
            textView.setText(tutorialItem.getTitleTextRes());
        }
        if (!TextUtils.isEmpty(tutorialItem.getSubTitleText())) {
            textViewSubTitle.setText(tutorialItem.getSubTitleText());
        } else if (tutorialItem.getSubTitleTextRes() != -1) {
            textViewSubTitle.setText(tutorialItem.getSubTitleTextRes());
        }
        if (tutorialItem.getForegroundImageRes() != -1) {
            Picasso.with(getContext()).load(tutorialItem.getForegroundImageRes()).into(imageViewFront);
        }

        return view;
    }
}

