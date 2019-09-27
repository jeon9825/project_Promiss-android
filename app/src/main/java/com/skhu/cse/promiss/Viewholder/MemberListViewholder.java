package com.skhu.cse.promiss.Viewholder;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skhu.cse.promiss.R;

public class MemberListViewholder extends RecyclerView.ViewHolder {

    public Button button;

    public MemberListViewholder(@NonNull View itemView) {
        super(itemView);
        button = itemView.findViewById(R.id.member_list_btn);
    }
}
