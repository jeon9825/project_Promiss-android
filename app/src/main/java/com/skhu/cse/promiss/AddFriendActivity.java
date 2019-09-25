package com.skhu.cse.promiss;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.skhu.cse.promiss.Items.UserItem;
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

public class AddFriendActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserListAdapter adapter;
    private ArrayList<UserItem> arrayList;
    private TagGroup tagGroup;

    private ImageButton text_delete;
    private EditText search;

    private ArrayList<Integer> ids=new ArrayList<>();
    private ArrayList<String> names=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add__appointment__fragment_5);
        findViewById(R.id.frg_appointment_5_t1).setVisibility(View.GONE);

        Intent data = getIntent();
        arrayList = data.getParcelableArrayListExtra("data");
        recyclerView=findViewById(R.id.frg_appointment_5_recycler);
        adapter=new UserListAdapter(this,arrayList);

        adapter.setEvent(new UserListAdapter.ClickEvent() {
            @Override
            public void afterClick(View view, int position) {
                UserItem item=arrayList.get(position);

                String name = item.getName();

                ids.add(item.getId());
                names.add(name);
                tagGroup.setTags(names);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        tagGroup=findViewById(R.id.frg_appointment_5_tagGroup);
        tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                names.remove(tag);
            }
        });
        tagGroup.setTags(names);


        findViewById(R.id.frg_appointment_5_search_btn).setOnClickListener(new View.OnClickListener() {
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

        text_delete=findViewById(R.id.frg_appointment_5_search_delete);
        search = findViewById(R.id.frg_appointment_5_search);

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

        findViewById(R.id.frg_appointment_5_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                    String name = user.getString("user_name");
                    int id = user.getInt("id");
                    boolean invite =false;
                    if(ids.contains(id))
                        invite=true;


                    arrayList.add(new UserItem(id,name,invite));

                    AddFriendActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

            }catch (JSONException e)
            {
                AddFriendActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"현재 서버 문제로 이용할수 없습니다.",Toast.LENGTH_LONG).show();
                        AddFriendActivity.this.finish();
                    }
                });
            }
        }

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {

            AddFriendActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"현재 서버 문제로 이용할수 없습니다.",Toast.LENGTH_LONG).show();
                    AddFriendActivity.this.finish();
                }
            });
        }
    };
}
