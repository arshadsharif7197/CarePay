package com.carecloud.carepay.patient.appointments.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;

import java.util.List;

/**
 * @author pjohnson on 10/22/18.
 */
public class PracticesAdapter extends RecyclerView.Adapter<PracticesAdapter.ViewHolder> {

    private final List<UserPracticeDTO> userPractices;
    private UserPracticeDTO selectedPractice;
    private ViewHolder selectedHolder;
    private PracticeSelectInterface callback;

    public PracticesAdapter(List<UserPracticeDTO> userPractices) {
        this.userPractices = userPractices;
       // selectedPractice = userPractices.get(0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_practices, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserPracticeDTO userPracticeDTO = userPractices.get(position);
        holder.practiceTextView.setText(userPracticeDTO.getPracticeName());
        if (selectedPractice != null && userPracticeDTO.getPracticeId().equals(selectedPractice.getPracticeId())) {
            holder.practiceTextView.setSelected(true);
            selectedHolder = holder;
        }
        holder.practiceTextView.setOnClickListener(v -> {
            if (selectedHolder != null) {
                selectedHolder.practiceTextView.setSelected(false);
            }
            selectedHolder = holder;
            selectedHolder.practiceTextView.setSelected(true);
            if (selectedPractice==null){
                selectedPractice=userPractices.get(position);
                callback.onPracticeSelected(userPracticeDTO,position);
            }

            if (selectedPractice!=null && !userPracticeDTO.getPracticeId().equals(selectedPractice.getPracticeId())) {
                callback.onPracticeSelected(userPracticeDTO,position);
            }
            selectedPractice = userPracticeDTO;
        });
    }

    @Override
    public int getItemCount() {
        return userPractices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView practiceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            practiceTextView = itemView.findViewById(R.id.practiceTextView);
        }
    }

    public interface PracticeSelectInterface {
        void onPracticeSelected(UserPracticeDTO userPracticeDTO, int position);
    }

    public void setCallback(PracticeSelectInterface callback) {
        this.callback = callback;
    }
}
