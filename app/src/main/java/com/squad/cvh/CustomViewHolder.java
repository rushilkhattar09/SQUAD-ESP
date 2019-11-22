package com.squad.cvh;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squad.R;
import com.squad.structure.Task;

public class CustomViewHolder extends RecyclerView.ViewHolder  {


    private TextView taskTextView;

    public CustomViewHolder(@NonNull View itemView ) {
        super(itemView);

        taskTextView = itemView.findViewById(R.id.task);


    }

    public void bindData(Task task, String userId, int taskNum) {

        if (userId.equals(task.getPlayer1())) {
            taskTextView.setText("Game "+taskNum+"vs "+ task.getPlayer2());
        } else {
            taskTextView.setText("Game "+taskNum+"vs "+ task.getPlayer1());
        }

    }



}
