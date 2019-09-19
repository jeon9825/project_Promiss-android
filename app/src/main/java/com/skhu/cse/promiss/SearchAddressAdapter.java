package com.skhu.cse.promiss;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skhu.cse.promiss.Items.SearchAddressItem;
import com.skhu.cse.promiss.Viewholder.SearchAddressViewHolder;

import java.util.ArrayList;

public class SearchAddressAdapter extends RecyclerView.Adapter<SearchAddressViewHolder> {
    public interface clickEvent {
        public void onClick(View v, int position);

    }

    public ArrayList<SearchAddressItem> arrayList;
    Activity activity;
    clickEvent clickEvent;

    public void setClickEvent(clickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    SearchAddressAdapter(Activity activity, ArrayList<SearchAddressItem> addressItems) {
        this.arrayList = addressItems;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SearchAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = activity.getLayoutInflater().inflate(R.layout.search_address, parent, false);
        return new SearchAddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAddressViewHolder holder, int position) {
        SearchAddressItem item = arrayList.get(position);
        holder.textViewName.setText(item.getName());
        holder.textViewDetail.setText(item.getDetail());
        if(clickEvent==null) return;
        holder.textViewDetail.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEvent.onClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
