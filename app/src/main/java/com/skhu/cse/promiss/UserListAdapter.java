package com.skhu.cse.promiss;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.skhu.cse.promiss.Items.UserItem;
import com.skhu.cse.promiss.Viewholder.UserIistViewholder;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<UserItem> arrayList=null;
    private Activity activity=null;

    public UserListAdapter(Activity activity,ArrayList<UserItem> arrayList)
    {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    ClickEvent event;

    public interface ClickEvent {
        public void afterClick(View view,int position);
    }

    public void setEvent(ClickEvent event)
    {
        this.event=event;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  activity.getLayoutInflater().inflate(R.layout.user_list,parent,false);

        return new UserIistViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        UserIistViewholder userIistViewholder=(UserIistViewholder)holder;

        UserItem item = arrayList.get(position);

        userIistViewholder.name.setText(item.getName());

        if(item.isInvite()) {
            userIistViewholder.invite.setVisibility(View.VISIBLE);
            userIistViewholder.add.setVisibility(View.GONE);
        }else
        {
            userIistViewholder.add.setVisibility(View.VISIBLE);

            userIistViewholder.add.setOnClickListener(view->{

                    item.setInvite(true);
                    userIistViewholder.invite.setVisibility(View.VISIBLE);
                    userIistViewholder.add.setVisibility(View.GONE);

                    if(event !=null) event.afterClick(userIistViewholder.add,position);

            });
            userIistViewholder.invite.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if(arrayList!=null)
        return arrayList.size();
        else
            return 0;
    }
}
