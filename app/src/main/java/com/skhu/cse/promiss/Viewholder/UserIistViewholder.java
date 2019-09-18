package com.skhu.cse.promiss.Viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skhu.cse.promiss.R;

public class UserIistViewholder extends RecyclerView.ViewHolder {

    public TextView name;

    public ImageButton add;
    public TextView invite;

    public UserIistViewholder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.user_list_name);
        add = itemView.findViewById(R.id.user_list_add);
        invite = itemView.findViewById(R.id.user_list_check);
    }
}
