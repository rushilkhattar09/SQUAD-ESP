package com.squad.CustomAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squad.MainActivity;
import com.squad.R;
import com.squad.questions.Constants;
import com.squad.cvh.CustomViewHolder;
import com.squad.structure.Task;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Task> list;
    private String mUserId;
    private Context context;


    public CustomAdapter(ArrayList<Task> list, String userId) {
        this.list = list;
        this.mUserId = userId;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        this.context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.single_recycler_item, null);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position < 0 || position >= list.size()) return;

        final CustomViewHolder customViewHolder = (CustomViewHolder) holder;
        Task task = list.get(position);
        customViewHolder.bindData(task, mUserId, position+1);

        customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = customViewHolder.getAdapterPosition();
                startDetailActivity(list.get(pos));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void startDetailActivity(Task task) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.DOC_ID, task.getDocID());
        context.startActivity(intent);
    }


}
