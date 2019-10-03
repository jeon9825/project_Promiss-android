package com.skhu.cse.promiss.Viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skhu.cse.promiss.R;

public class ResultMemberListViewholder extends RecyclerView.ViewHolder {

    public Button button;
    public TextView name;
    public TextView fine;

    public ResultMemberListViewholder(@NonNull View itemView) {
        super(itemView);

        button  = itemView.findViewById(R.id.member_list_btn);
        name = itemView.findViewById(R.id.member_list_name);
        fine = itemView.findViewById(R.id.member_list_fine);
    }
}
