


package com.carecloud.carepaylibray.appointments.createappointment.visittype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.SchedulerAnswerTally;
import com.carecloud.carepaylibray.appointments.models.VisitTypeQuestions;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

import java.util.List;

/**
 * Created by Muhammad Noman on 16/sep/22
 */

public class QuestionAnswerTallyListAdapter extends RecyclerView.Adapter<QuestionAnswerTallyListAdapter.ViewHolder> {

    private final boolean showSeparator;
    private Context context;
    private List<SchedulerAnswerTally> itemsList;
    private ViewHolder lastHighlightedView = null;

    private VisitTypeQuestions selectedItem;

    public QuestionAnswerTallyListAdapter(Context context,
                                          List<SchedulerAnswerTally> itemsList,
                                          boolean showSeparator) {
        this.context = context;
        this.itemsList = itemsList;
        this.showSeparator = showSeparator;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_question_answer_tally, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SchedulerAnswerTally optionInfo = itemsList.get(position);
        holder.tvTallyQuestion.setText(optionInfo.getQuestion());
        holder.tvTallyAnswer.setText(optionInfo.getAnswer());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View itemCheck;
        CarePayTextView tvTallyQuestion;
        CarePayTextView tvTallyAnswer;
        View divider;

        ViewHolder(View itemView) {
            super(itemView);
            itemCheck = itemView.findViewById(R.id.item_check);
            tvTallyQuestion = itemView.findViewById(R.id.tv_question_tally);
            tvTallyAnswer = itemView.findViewById(R.id.tv_answer_tally);
            divider = itemView.findViewById(R.id.divider);
            if (showSeparator) {
                divider.setVisibility(View.VISIBLE);
            }
        }
    }
}
