package com.carecloud.carepaylibray.common;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.PicassoRoundedCornersExifTransformation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 24/01/18.
 */

public class DocumentDetailFragment extends BaseDialogFragment {

    public DocumentDetailFragment() {

    }

    public static DocumentDetailFragment newInstance(String imageUrl, boolean needAuth) {
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        args.putBoolean("needAuth", needAuth);
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
        final ImageView documentImageView = view.findViewById(R.id.documentImageView);
        String imageUrl = getArguments().getString("imageUrl");
        boolean needAuth = getArguments().getBoolean("needAuth");
        final ProgressBar attachmentProgress = view.findViewById(R.id.attachmentProgress);
        if (imageUrl.startsWith("/")) {
            File f = new File(imageUrl);
            Picasso.with(getContext())
                    .load(f)
                    .into(documentImageView);
        } else if (needAuth) {
            Map<String, String> headers = new HashMap<>();
            headers.put("x-api-key", HttpConstants.getApiStartKey());
            headers.put("username", getAppAuthorizationHelper().getCurrUser());
            headers.put("Authorization", getAppAuthorizationHelper().getIdToken());
            attachmentProgress.setVisibility(View.VISIBLE);
            PicassoHelper.setHeaders(headers);
            PicassoHelper.getPicassoInstance(getContext())
                    .load(imageUrl)
                    .transform(new PicassoRoundedCornersExifTransformation(0, 0, imageUrl, headers))
                    .into(documentImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            attachmentProgress.setVisibility(View.GONE);
                            documentImageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            attachmentProgress.setVisibility(View.GONE);
                        }
                    });
        } else {
            attachmentProgress.setVisibility(View.VISIBLE);
            Picasso.with(getContext())
                    .load(imageUrl)
                    .into(documentImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            attachmentProgress.setVisibility(View.GONE);
                            documentImageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            attachmentProgress.setVisibility(View.GONE);
                        }
                    });
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
