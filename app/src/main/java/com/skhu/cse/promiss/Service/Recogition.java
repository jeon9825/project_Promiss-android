package com.skhu.cse.promiss.Service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ResultReceiver;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import android.util.Base64;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;


//import com.minsudongP.MainActivity;
//import com.minsudongP.R;
//import com.minsudongP.Singletone.UrlConnection;
//import com.minsudongP.Singletone.UserInfor;
//import com.minsudongP.SiriActivity;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.skhu.cse.promiss.Items.UserData;
import com.skhu.cse.promiss.MainActivity;
import com.skhu.cse.promiss.R;
import com.skhu.cse.promiss.SiriActivity;
import com.skhu.cse.promiss.server.GetJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.speech.SpeechRecognizer.ERROR_AUDIO;
import static android.speech.SpeechRecognizer.ERROR_CLIENT;
import static android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS;
import static android.speech.SpeechRecognizer.ERROR_NETWORK;
import static android.speech.SpeechRecognizer.ERROR_NO_MATCH;
import static android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY;
import static android.speech.SpeechRecognizer.ERROR_SERVER;
import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;
import static android.speech.tts.TextToSpeech.ERROR_NETWORK_TIMEOUT;
import static com.skhu.cse.promiss.App.CHAANEL_ID;


public class Recogition extends RecognitionService {

    public static final int MSG_VOICE_RECO_READY = 0;
    public static final int MSG_VOICE_RECO_END = 1;
    public static final int MSG_VOICE_RECO_RESTART = 2;
    private SpeechRecognizer mSrRecognizer;
    boolean mBoolVoiceRecoStarted;
    boolean hasQuestion; // 프로미스라고 사용자가 말했을 때
    protected AudioManager mAudioManager;
    MediaPlayer mediaPlayer;
    Intent intent; //Siri Activity 실행할 Activity
    Intent itIntent;//음성인식 Intent
    boolean end = false;
    ResultReceiver receiver;


    @Override
    public void onCreate() {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        startListening();
        hasQuestion = false;
        intent = new Intent(this, SiriActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    }

    private Handler mHdrVoiceRecoState = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_VOICE_RECO_READY:
                    break;
                case MSG_VOICE_RECO_END: {
                    stopListening();
                    sendEmptyMessageDelayed(MSG_VOICE_RECO_RESTART, 1000);
                    break;
                }
                case MSG_VOICE_RECO_RESTART:
                    startListening();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this
                , 0, notificationIntent, 0); //알람을 눌렀을 때 해당 엑티비티로

        Notification notification = new NotificationCompat.Builder(this, CHAANEL_ID)
                .setContentTitle("Promiss Service")
                .setContentText("프로미스를 불러주세요")
                .setSmallIcon(R.drawable.flag_icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        return START_STICKY;
    }

    private void sendMessage(String send, String message) {

        Intent intent = new Intent("Promiss-event-name");
        intent.putExtra("send", send);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        end = true;
        mSrRecognizer.destroy();
        mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_READY); //음성인식 서비스 다시 시작

    }


    @Override
    protected void onStartListening(Intent recognizerIntent, Callback listener) {

    }


    public void startListening() {
        if (end)
            return;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) { //현재 아나운서가 말하고 있다면
            mHdrVoiceRecoState.sendEmptyMessageDelayed(MSG_VOICE_RECO_RESTART, 500);
        } else {
//
            //음성인식을 시작하기 위해 Mute
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC)) {

                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
                }
            } else {
                mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }


            if (mBoolVoiceRecoStarted == false) { // 최초의 실행이거나 인식이 종료된 후에 다시 인식을 시작하려 할 때
                if (mSrRecognizer == null) {

                    mSrRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                    mSrRecognizer.setRecognitionListener(mClsRecoListener);


                }
                if (mSrRecognizer.isRecognitionAvailable(getApplicationContext())) { //시스템에서 음성인식 서비스 실행이 가능하다면
                    itIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    itIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
                    itIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN.toString());
                    itIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
                    itIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500);
                    itIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

                }
                mSrRecognizer.startListening(itIntent);
            }
            mBoolVoiceRecoStarted = true;  //음성인식 서비스 실행 중
        }
    }

    public void stopListening() //Override 함수가 아닌 한번만 호출되는 함수 음성인식이 중단될 때
    {
        try {
            if (mSrRecognizer != null && mBoolVoiceRecoStarted == true) {
                mSrRecognizer.stopListening(); //음성인식 Override 중단을 호출
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mBoolVoiceRecoStarted = false;  //음성인식 종료
    }


    @Override
    protected void onCancel(Callback listener) {
        mSrRecognizer.cancel();
    }

    @Override
    protected void onStopListening(Callback listener) { //음성인식 Override 함수의 종료부분
        mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_RESTART); //음성인식 서비스 다시 시작
    }

    private RecognitionListener mClsRecoListener = new RecognitionListener() {
        @Override
        public void onRmsChanged(float rmsdB) {
            if (rmsdB >= 8.5 && hasQuestion) {
                sendMessage("start", "");
            } else if (rmsdB >= 5.5 && hasQuestion) {
                sendMessage("pause", "");
            }

        }


        @Override
        public void onResults(Bundle results) {


            //Recognizer KEY를 사용하여 인식한 결과값을 가져오는 코드
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            final String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            Log.d("key", Arrays.toString(rs));


            //Background에서 인식은 항상 하고 있지만 특정 키워드이후에 동작하기 위해 if문으로 동작
            if (rs[0].contains("프로미스") || rs[0].contains("프루미스") || rs[0].contains("포로미스") || rs[0].contains("프로미쓰") || rs[0].contains("프롬있스") || hasQuestion) {
                if (!hasQuestion) {
                    hasQuestion = true;
                    startActivity(intent);
                }

                sendMessage("my", rs[0]); //사용자의 질문을 보냄
                new Thread() {

                    @Override
                    public void run() {
                        UserData userInfor = UserData.shared;
                        GetJson connection = GetJson.getInstance();

                        connection.PostSpeekRequest(rs[0], userInfor.getId(), new

                                okhttp3.Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {

                                        String s = response.body().string();
                                        Log.d("code", "" + response.code());
                                        if (response.code() == 200) {

                                            try {
                                                JSONObject jsonObject = new JSONObject(s);


                                                JSONObject data = jsonObject.getJSONObject("data");

                                                sendMessage("chatbot", data.getString("message"));
                                                //Chatbot에서 전달한 Message를 UI에 띄어주기 위하여
                                                // response.body().string()은 한번밖에 호출 못함

                                                if (jsonObject.getInt("result") == 1000) {
                                                    hasQuestion = false;
                                                    sendMessage("reset", "");

                                                    //Chatbot이 사용자의 질문을 이해하지 못했을 때 종료
                                                }

                                                byte[] bytes = Base64.decode(data.getString("voice"), 0);
                                                Log.d("code", "" + bytes.length);
                                                File f = new File(getApplicationContext().getFilesDir().getPath().toString() + "speek" + ".mp3");

                                                FileOutputStream outputStream = new FileOutputStream(f);

                                                Log.d("result", "" + bytes.length);
                                                outputStream.write(bytes, 0, bytes.length);
                                                outputStream.flush();
                                                outputStream.close();

                                                FileInputStream fs = new FileInputStream(f);

                                                //Chatbot의 음성을 들려주기 위하여 Mute했던 소리를 킴
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);

                                                } else {
                                                    // Note that this must be the same instance of audioManager that mutes
                                                    // http://stackoverflow.com/questions/7908962/setstreammute-never-unmutes?rq=1
                                                    mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                                                }

                                                mediaPlayer = new MediaPlayer();
                                                mediaPlayer.setDataSource(fs.getFD());
                                                //mediaPlayer.prepare();
                                                mediaPlayer.prepare();
                                                fs.close();
                                                // mediaPlayer.prepareAsync();
                                                mediaPlayer.start();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                    }
                }.start();
            }

            mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END); //음성인식 종료
            //((TextView)(findViewById(R.id.text))).setText("" + rs[index]);
        }


        @Override
        public void onReadyForSpeech(Bundle params) {
        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int intError) {

            switch (intError) {

                case ERROR_NETWORK_TIMEOUT:
                    //네트워크 타임아웃
                    break;


                case ERROR_NETWORK:
                    break;

                case ERROR_AUDIO:
                    //녹음 에러
                    break;
                case ERROR_SERVER:
                    //서버에서 에러를 보냄
                    break;
                case ERROR_CLIENT:
                    //클라이언트 에러
                    break;
                case ERROR_SPEECH_TIMEOUT:
                    //아무 음성도 듣지 못했을 때
                    mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END);
                    hasQuestion = false;
                    break;
                case ERROR_NO_MATCH:
                    //적당한 결과를 찾지 못했을 때
                    mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END);
                    hasQuestion = false;
                    break;
                case ERROR_RECOGNIZER_BUSY:
                    //RecognitionService가 바쁠 때
                    break;
                case ERROR_INSUFFICIENT_PERMISSIONS:
                    //uses - permission(즉 RECORD_AUDIO) 이 없을 때
                    break;

            }
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onPartialResults(Bundle partialResults) { //부분 인식을 성공 했을 때

        }
    };
}

