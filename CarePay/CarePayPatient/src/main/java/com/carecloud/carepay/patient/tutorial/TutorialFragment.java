package com.carecloud.carepay.patient.tutorial;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
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

    /**
     * @param tutorialItem item to be displayed
     * @param page the item belongs to
     * @return Tutorial Fragment
     */
    public static TutorialFragment newInstance(TutorialItem tutorialItem, int page) {
        TutorialFragment helpTutorialImageFragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TUTORIAL_ITEM, tutorialItem);
        args.putInt(ARG_PAGE, page);
        helpTutorialImageFragment.setArguments(args);
        return helpTutorialImageFragment;
    }

    private TutorialItem item;
    int page;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        item = bundle.getParcelable(ARG_TUTORIAL_ITEM);
        page = bundle.getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tutorial_image, container, false);
        view.setTag(page);

        ((TextView) view.findViewById(R.id.tutorial_title_textview)).setText(item.getTitle());
        ((TextView) view.findViewById(R.id.tutorial_subtitle_textview)).setText(item.getSubTitle());

        if (item.getImageRes() != -1) {
            ImageView imageViewFront = (ImageView) view.findViewById(R.id.fragment_tutorial_imageview);
            Picasso.with(getContext()).load(item.getImageRes()).into(imageViewFront);
        }

        return view;
    }
}

