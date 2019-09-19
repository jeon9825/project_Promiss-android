package com.skhu.cse.promiss.Viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skhu.cse.promiss.R;

public class SearchAddressViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewName;
    public TextView textViewDetail;

    public SearchAddressViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewName = itemView.findViewById(R.id.search_address_name);
        textViewDetail = itemView.findViewById(R.id.search_address_detail);
    }
}
