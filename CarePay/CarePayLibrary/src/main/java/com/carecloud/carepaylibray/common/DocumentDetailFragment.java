package com.carecloud.carepaylibray.common;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.carecloud.carepaylibrary.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * @author pjohnson on 24/01/18.
 */

public class DocumentDetailFragment extends DialogFragment {

    public DocumentDetailFragment() {

    }

    public static DocumentDetailFragment newInstance(String imageUrl) {
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        DocumentDetailFragment fragment = new DocumentDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_document_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = (ImageView) view.findViewById(R.id.documentImageView);
        String imageUrl = getArguments().getString("imageUrl");
        if (imageUrl.startsWith("/")) {
            File f = new File(imageUrl);
            Picasso.with(getContext())
                    .load(f)
                    .into(imageView);
        } else {
            Picasso.with(getContext())
                    .load(imageUrl)
                    .into(imageView);
        }
        if (view.findViewById(R.id.exitImageView) != null) {
            view.findViewById(R.id.exitImageView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    @Override
    public int getTheme() {
        return R.style.Base_Dialog_FullScreen;
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
