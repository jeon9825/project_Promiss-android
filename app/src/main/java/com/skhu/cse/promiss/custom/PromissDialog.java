package com.skhu.cse.promiss.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.skhu.cse.promiss.R;

public class PromissDialog extends Dialog {

    TextView title;
    TextView message;
    Button cancel;
    Button okay;

    String titleS;
    String messageS;
    String cancelS;
    String okayS;

    View.OnClickListener cancelClick;
    View.OnClickListener okayClick;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams IpWindow =new WindowManager.LayoutParams();
        IpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        IpWindow.dimAmount = 0.6f;
        getWindow().setAttributes(IpWindow);
        setContentView(R.layout.dialog_custom_view);


        title=findViewById(R.id.custom_dialog_title);
        message=findViewById(R.id.custom_dialog_message);
        cancel = findViewById(R.id.custom_dialog_cancel);
        okay=findViewById(R.id.custom_dialog_okay);

        if(titleS!=null){
            title.setText(titleS);
        }
        if(messageS!=null)
        {
            message.setText(messageS);
        }
        if(cancelS!=null)
        {
            cancel.setText(cancelS);
            cancel.setOnClickListener(cancelClick);
        }
        if(okayS!=null)
        {
            okay.setText(okayS);
            okay.setOnClickListener(okayClick);
        }

    }

    public void setTitleDialog(String s)
    {
        //title.setText(s);
        titleS=s;
    }

    public void setMessageDialog(String s)
    {
//        message.setText(s);
        messageS=s;
    }

    public void setCancelDialog(String s, View.OnClickListener clickListener)
    {
       // cancel.setText(s);
        cancelS=s;
        //cancel.setOnClickListener(clickListener);
        cancelClick = clickListener;

    }

    public void setOkayDialog(String s, View.OnClickListener clickListener)
    {
        //okay.setText(s);
        okayS=s;
       // okay.setOnClickListener(clickListener);
        okayClick=clickListener;
    }
    private PromissDialog(Context context) {
        super(context);
    }

    public static class Builder{

        PromissDialog dialog;


        public PromissDialog build()
        {
            return dialog;
        }
        public Builder(Context context)
        {
            dialog=new PromissDialog(context);
        }

        public  Builder setTitle(String title)
        {
            dialog.setTitleDialog(title);
            return this;
        }
        public  Builder setMessage(String message)
        {
            dialog.setMessageDialog(message);
            return this;
        }

        public Builder addCancelListener(String cancel,View.OnClickListener clickListener)
        {
            dialog.setCancelDialog(cancel,clickListener);
            return this;
        }
        public Builder addOkayListener(String okay,View.OnClickListener clickListener)
        {
            dialog.setOkayDialog(okay,clickListener);
            return this;
        }
    }
}
