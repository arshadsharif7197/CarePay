package com.carecloud.carepay.practice.library.homescreen.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenOfficeNewsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenOfficeNewsPayloadDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

import java.util.List;

public class OfficeNewsListAdapter extends RecyclerView.Adapter<OfficeNewsListAdapter.OfficeNewsViewHolder> {

    private static final int MAX_NEWS_TO_SHOW = 3;
    private Context context;
    private List<HomeScreenOfficeNewsDTO> officeNewsList;
    private OnOfficeNewsClickedListener listener;

    /**
     * Constructor
     *
     * @param context    context
     * @param officeNews list of office news
     */
    public OfficeNewsListAdapter(Context context, List<HomeScreenOfficeNewsDTO> officeNews,
                                 OnOfficeNewsClickedListener listener) {
        this.context = context;
        this.officeNewsList = officeNews;
        this.listener = listener;
    }

    @Override
    public OfficeNewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View officeNewsListItemView = LayoutInflater.from(context).inflate(
                R.layout.office_news_list_item, parent, false);
        return new OfficeNewsViewHolder((officeNewsListItemView));
    }

    @Override
    public void onBindViewHolder(OfficeNewsViewHolder holder, int position) {
        HomeScreenOfficeNewsPayloadDTO newsPayload = officeNewsList.get(position).getPayload();
        holder.newsTitle.setText(newsPayload.getHeadline());
    }

    @Override
    public int getItemCount() {
        return Math.min(officeNewsList.size(), MAX_NEWS_TO_SHOW);
    }

    class OfficeNewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CarePayTextView newsTitle;

        OfficeNewsViewHolder(View itemView) {
            super(itemView);

            newsTitle = itemView.findViewById(R.id.office_news_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onOfficeNewsSelected(officeNewsList, getAdapterPosition());
            }
        }
    }

    public interface OnOfficeNewsClickedListener {
        void onOfficeNewsSelected(List<HomeScreenOfficeNewsDTO> officeNewsList, int position);
    }
}
