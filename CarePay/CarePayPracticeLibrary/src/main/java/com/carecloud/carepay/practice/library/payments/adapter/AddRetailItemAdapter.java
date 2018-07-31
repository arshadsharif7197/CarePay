package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.retail.models.RetailItemDto;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lmenendez on 3/16/17.
 */

public class AddRetailItemAdapter extends RecyclerView.Adapter<AddRetailItemAdapter.ViewHolder> {

    public interface AddRetailItemCallback{
        void retailItemSelected(RetailItemDto retailItem);
    }

    private Context context;
    private List<RetailItemDto> retailItems;
    private AddRetailItemCallback callback;

    /**
     * Constructor
     * @param context context
     * @param retailItems list of retail items
     * @param callback callback
     */
    public AddRetailItemAdapter(Context context, @NonNull List<RetailItemDto> retailItems, AddRetailItemCallback callback){
        this.context = context;
        this.retailItems = retailItems;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_add_retail_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final RetailItemDto retailItem = retailItems.get(position);

        holder.productName.setText(retailItem.getName());
        SpannableString spannableString = new SpannableString(Html.fromHtml(retailItem.getDescription()));
        holder.subTitle.setText(spannableString);
        holder.price.setText(StringUtil.getFormattedBalanceAmount(retailItem.getPrice()));
        holder.productPlaceholder.setText(StringUtil.getShortName(retailItem.getName()));

        int size = context.getResources().getDimensionPixelSize(R.dimen.dimen_60dp);
        Picasso.with(context).load(retailItem.getThumbnailUrl())
                .resize(size, size)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(holder.productThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.productThumbnail.setVisibility(View.VISIBLE);
                        holder.productPlaceholder.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.productThumbnail.setVisibility(View.GONE);
                        holder.productPlaceholder.setVisibility(View.VISIBLE);
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.retailItemSelected(retailItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return retailItems.size();
    }

    /**
     * add more retail items
     * @param retailItems retail items
     */
    public void addRetailItems(List<RetailItemDto> retailItems) {
        this.retailItems.addAll(retailItems);
        notifyDataSetChanged();
    }

    /**
     * Set the retail items
     * @param retailItems retail items
     */
    public void setRetailItems(List<RetailItemDto> retailItems) {
        this.retailItems = retailItems;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView productName;
        TextView subTitle;
        TextView productPlaceholder;
        ImageView productThumbnail;
        TextView price;

        ViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.product_name);
            subTitle = (TextView) itemView.findViewById(R.id.product_subtitle);
            productPlaceholder = (TextView) itemView.findViewById(R.id.product_placeholder);
            price = (TextView) itemView.findViewById(R.id.product_price);
            productThumbnail = (ImageView) itemView.findViewById(R.id.product_thumbnail);
        }

    }
}
