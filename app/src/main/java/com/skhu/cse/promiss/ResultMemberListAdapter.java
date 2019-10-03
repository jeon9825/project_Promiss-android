package com.skhu.cse.promiss;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skhu.cse.promiss.Items.UserItem;
import com.skhu.cse.promiss.Viewholder.ResultMemberListViewholder;
import com.skhu.cse.promiss.Viewholder.UserIistViewholder;

import java.util.ArrayList;

public class ResultMemberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<UserItem> arrayList=null;
    private Activity activity=null;

    public ResultMemberListAdapter(Activity activity, ArrayList<UserItem> arrayList)
    {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    ClickEvent event;

    public interface ClickEvent {
        public void afterClick(View view, int position);
    }

    public void setEvent(ClickEvent event)
    {
        this.event=event;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  activity.getLayoutInflater().inflate(R.layout.member_list_2,parent,false);

        return new ResultMemberListViewholder(view);
    }

    private int GetBackground(int position)
    {

        switch (position%4)
        {

            case 0:
                return R.drawable.member_list_1;
            case 1:
                return R.drawable.member_list_2;
            case 2:
                return R.drawable.member_list_3;
            default:
                return R.drawable.member_list_4;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ResultMemberListViewholder userIistViewholder=(ResultMemberListViewholder)holder;

        UserItem item = arrayList.get(position);

        userIistViewholder.name.setText(item.getName());
        userIistViewholder.button.setBackgroundResource(GetBackground(position));
        userIistViewholder.fine.setText(item.getFine()+"원");


    }

    @Override
    public int getItemCount() {
        if(arrayList!=null)
        return arrayList.size();
        else
            return 0;
    }
}
