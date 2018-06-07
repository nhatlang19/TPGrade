package com.tpgrade.tpgrade.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tpgrade.Lib.DateUtils;
import com.tpgrade.contants.ContantContest;
import com.tpgrade.models.Topic;
import com.tpgrade.tpgrade.ContestActivity;
import com.tpgrade.tpgrade.Fragments.Home.EditTopicDialogFragment;
import com.tpgrade.tpgrade.HomeActivity;
import com.tpgrade.tpgrade.R;

import java.util.List;

public class ContestKeyItemAdapter extends RecyclerView.Adapter<ContestKeyItemAdapter.ContestKeyViewHolder> {

    private Context context;

    private List<Topic> topics;

    public ContestKeyItemAdapter(List<Topic> topics) {
        this.topics = topics;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    @Override
    public ContestKeyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contest_key_item, viewGroup, false);
        ContestKeyViewHolder ContestKeyViewHolder = new ContestKeyViewHolder(v);

        return ContestKeyViewHolder;
    }

    @Override
    public void onBindViewHolder(final ContestKeyViewHolder holder, final int position) {
        final Topic topic = getTopics().get(position);
        topic.position = position;
        holder.topic = topic;
        holder.tvTestName.setText(topic.testName);
        holder.tvNumbers.setText("Số câu: " + topic.numbers);
        holder.tvCreated.setText(DateUtils.formatCreated(topic.created));

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Topic dbTopic = Topic.findById(Topic.class, topic.getId());
                dbTopic.delete();
                topics.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ContestActivity.class);
                intent.putExtra(ContantContest.CONTEST_KEY__TOPIC_ID, topic.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return getTopics().size();
    }

    public void clear() {
        getTopics().clear();
        final int size = getTopics().size();
        notifyItemRangeRemoved(0, size);
    }

    public void insert(Topic topic, int position) {
        getTopics().add(position, topic);
    }

    public void update(Topic topic, int position) {
    }

    public static class ContestKeyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layout;

        CardView cv;

        TextView tvTestName;
        TextView tvNumbers;
        TextView tvCreated;

        LinearLayout btnRemove;

        Topic topic;

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
