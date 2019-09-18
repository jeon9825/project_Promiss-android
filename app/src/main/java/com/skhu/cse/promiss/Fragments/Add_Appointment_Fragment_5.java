package com.skhu.cse.promiss.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.skhu.cse.promiss.AddAppointmentActivity;
import com.skhu.cse.promiss.Items.UserItem;
import com.skhu.cse.promiss.R;
import com.skhu.cse.promiss.UserListAdapter;
import com.skhu.cse.promiss.server.GetJson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import me.gujun.android.taggroup.TagGroup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Add_Appointment_Fragment_5 extends Fragment {


    RecyclerView recyclerView;
    UserListAdapter adapter;
    ArrayList<UserItem> arrayList=new ArrayList<>();
    TagGroup tagGroup;

    ImageButton text_delete;
    EditText search;

    ArrayList<Integer> ids=new ArrayList<>();
    ArrayList<String> names=new ArrayList<>();
    public Add_Appointment_Fragment_5() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__appointment__fragment_5, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.frg_appointment_5_recycler);
        adapter=new UserListAdapter(getActivity(),arrayList);
        adapter.setEvent(new UserListAdapter.ClickEvent() {
            @Override
            public void afterClick(View view, int position) {
                UserItem item=arrayList.get(position);

                String name = item.getName();

                names.add(name);
                tagGroup.setTags(names);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        tagGroup=view.findViewById(R.id.frg_appointment_5_tagGroup);
        tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                names.remove(tag);
            }
        });
       tagGroup.setTags(names);


        view.findViewById(R.id.frg_appointment_5_search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //입력한 값으로 검색

                arrayList.clear();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        GetJson json=GetJson.getInstance();

                        json.requestPost("api/User/search",callback,"ID",search.getText().toString());
                    }
                }.run();
            }
        });

        text_delete=view.findViewById(R.id.frg_appointment_5_search_delete);
        search = view.findViewById(R.id.frg_appointment_5_search);

        text_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_delete.setVisibility(View.GONE);
                search.setText("");
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>1)
                    text_delete.setVisibility(View.VISIBLE);
                else
                    text_delete.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.frg_appointment_5_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //넘어가는 부분
                ((AddAppointmentActivity)getActivity()).setAppointment_user(ids);
                ((AddAppointmentActivity)getActivity()).Next();
            }
        });
    }

    Callback callback = new Callback(){

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            String result = response.body().string();

            try{
                JSONObject object = new JSONObject(result);
                JSONArray array = object.getJSONArray("data");


                for(int i=0;i<array.length();i++)
                {
                    JSONObject user = array.getJSONObject(i);

                    String name = user.getString("user_id");
                    int id = user.getInt("id");
                    boolean invite =false;
                    if(ids.contains(id))
                        invite=true;


                    arrayList.add(new UserItem(id,name,invite));

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

            }catch (JSONException e)
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"현재 서버 문제로 이용할수 없습니다.",Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                });
            }
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"현재 서버 문제로 이용할수 없습니다.",Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }
                    });
        }
    };
}
