package com.skhu.cse.promiss;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skhu.cse.promiss.Items.UserItem;
import com.skhu.cse.promiss.Viewholder.MemberListViewholder;

import java.util.ArrayList;

public class MemberAdapter extends RecyclerView.Adapter<MemberListViewholder> {

    ArrayList<UserItem> arrayList ;
    Activity activity;

    ClickEvent event;
    public interface ClickEvent{
        public void OnClick(View view,int position);
    }
    public MemberAdapter(Activity activity,ArrayList<UserItem> arrayList)
    {
        this.activity = activity;
        this.arrayList = arrayList;
    }
    public void setClickEvent(ClickEvent event)
    {
        this.event = event;
    }
    @NonNull
    @Override
    public MemberListViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = activity.getLayoutInflater().inflate(R.layout.member_list,parent,false);

        return new MemberListViewholder(view);
    }
    private int GetBackground(int position)
    {

        switch (position%5)
        {
            case 0:
                return R.drawable.flag_icon;
            case 1:
                return R.drawable.member_list_1;
            case 2:
                return R.drawable.member_list_2;
            case 3:
                return R.drawable.member_list_3;
                default:
                    return R.drawable.member_list_4;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MemberListViewholder holder, int position) {

        UserItem item = arrayList.get(position);

        String name= item.getName();
        if(name.length()>10)
        name = name.substring(0,9);

        holder.button.setBackgroundResource(GetBackground(position));
        if(position!=0)
        holder.button.setText(name);
        else
            holder.button.setText("");

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.OnClick(view,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
