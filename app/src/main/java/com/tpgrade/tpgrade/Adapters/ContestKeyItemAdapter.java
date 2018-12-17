package com.tpgrade.tpgrade.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tpgrade.Lib.DateUtils;
import com.tpgrade.models.Exam;
import com.tpgrade.tpgrade.R;

import java.util.List;

public class ContestKeyItemAdapter extends RecyclerView.Adapter<ContestKeyItemAdapter.ContestKeyViewHolder> {

    private Context context;

    private List<Exam> exams;

    public ContestKeyItemAdapter(List<Exam> exams) {
        this.exams = exams;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Exam> getExams() {
        return exams;
    }

    @Override
    public ContestKeyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contest_key_item, viewGroup, false);
        ContestKeyViewHolder ContestKeyViewHolder = new ContestKeyViewHolder(v);

        return ContestKeyViewHolder;
    }

    @Override
    public void onBindViewHolder(final ContestKeyViewHolder holder, final int position) {
        final Exam Exam = getExams().get(position);

        holder.Exam = Exam;
        holder.tvTestName.setText("Mã đề: " + Exam.examTitle);
        holder.tvCreated.setText(DateUtils.formatCreated(Exam.created));

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Exam dbExam = Exam.findById(Exam.class, Exam.getId());
                dbExam.delete();
                exams.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return getExams().size();
    }

    public void clear() {
        getExams().clear();
        final int size = getExams().size();
        notifyItemRangeRemoved(0, size);
    }

    public void insert(Exam Exam, int position) {
        getExams().add(position, Exam);
    }

    public void update(Exam Exam, int position) {
    }

    public static class ContestKeyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layout;

        CardView cv;

        TextView tvTestName;
        TextView tvNumbers;
        TextView tvCreated;

        LinearLayout btnRemove;

        Exam Exam;

        ContestKeyViewHolder(View itemView) {
            super(itemView);
            tvTestName = (TextView) itemView.findViewById(R.id.tvTestName);
            tvNumbers = (TextView) itemView.findViewById(R.id.tvNumbers);
            tvCreated = (TextView) itemView.findViewById(R.id.tvCreated);

            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            btnRemove = (LinearLayout) itemView.findViewById(R.id.btnRemove);

            cv = (CardView) itemView.findViewById(R.id.cv);
        }
    }
}
