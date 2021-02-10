package net.trejj.talk.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import net.trejj.talk.Audio;
import net.trejj.talk.CallRunningService;
import net.trejj.talk.Function;
import net.trejj.talk.R;
import net.trejj.talk.activities.BaseActivity;
import net.trejj.talk.model.realms.FireCall;
import net.trejj.talk.model.realms.User;
import net.trejj.talk.services.SinchService;
import net.trejj.talk.utils.RealmHelper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallDetails;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.skyfishjy.library.RippleBackground;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/** Created by AwsmCreators * */
public class CallScreenActivity extends BaseActivity implements SensorEventListener {

    static final String TAG = CallScreenActivity.class.getSimpleName();

    private DatabaseReference mDatabase;
    FirebaseUser currentUser;
    Double previousPoints;
    Double callrate;
    Double finalcost;
    Handler handler;

    private Audio mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;
    RippleBackground rippleBackground;

    private String mCallId;
    String callerNumber;
    String callername = "";

    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;
    ImageButton speaker, record;
    Boolean isLoudSpeaker = false;
    private Boolean callAttended = false;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    PowerManager.WakeLock wlOff = null, wlOn = null;

    private boolean isCallInProgress = false;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float distance = sensorEvent.values[0];
        if (!isLoudSpeaker) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (distance < 4) {
                if (wlOn != null && wlOn.isHeld()) {
                    wlOn.release();
                }
                if (pm != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        if (wlOff == null)
                            wlOff = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag");
                        if (!wlOff.isHeld()) wlOff.acquire();
                    }
                }
            } else {
                if (wlOff != null && wlOff.isHeld()) {
                    wlOff.release();
                }
                if (pm != null) {
                    if (wlOn == null)
                        wlOn = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
                    if (!wlOn.isHeld()) wlOn.acquire();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean enablePresence() {
        return false;
    }

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    updateCallDuration();
                }
            });
        }
    }

    @Override
    public void onSinchConnected() {

        if(!isCallInProgress) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Call call1 = getSinchServiceInterface().callPhoneNumber(callerNumber);
                        mCallId = call1.getCallId();

                        Call call = getSinchServiceInterface().getCall(mCallId);
                        if (call != null) {
                            call.addCallListener(new SinchCallListener());
                            //mCallerName.setText(call.getRemoteUserId());
                            mCallerName.setText(callername);
                            mCallState.setText(call.getState().toString());
                        } else {
                            Log.e(TAG, "Started with invalid callId, aborting.");
                            finish();
                        }
                    }catch (Exception e){
                        Toast.makeText(CallScreenActivity.this, "Calling service is not available in your area at the moment", Toast.LENGTH_LONG).show();
                        CallScreenActivity.super.onBackPressed();
                    }
                }
            }, 3000);
        }else{
            Call call1 = getSinchServiceInterface().callPhoneNumber(callerNumber);
            mCallId = call1.getCallId();
            endCall();
            runTimer2(30);
        }

    }

    @Override
    public void onSinchDisconnected() {

    }

    private Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);

        mAudioPlayer = new Audio(this);

        mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallDuration.setText("Duration");
        mCallerName = (TextView) findViewById(R.id.remoteUser);
        mCallState = (TextView) findViewById(R.id.callState);
        ImageButton endCallButton = (ImageButton) findViewById(R.id.hangupButton);
        speaker = findViewById(R.id.speaker);
        record = findViewById(R.id.record);
        rippleBackground=(RippleBackground)findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        bindService();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        callerNumber = getIntent().getStringExtra("number");
        callername = getIntent().getStringExtra("callername");

        if(getIntent().hasExtra("isCallInProgress")){
            callerNumber = getIntent().getStringExtra("number");
            callername = getIntent().getStringExtra("callername");
            isCallInProgress = getIntent().getBooleanExtra("isCallInProgress",false);
            mCallState.setText("Established");
        }

        mCallerName.setText(callername);
        handler = new Handler();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        RetriveData();

        speaker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoudSpeaker) {
                    isLoudSpeaker = false;
                    AudioController audioController = getSinchServiceInterface().getAudioController();
                    audioController.disableSpeaker();
                    speaker.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_new));
                }else {
                    isLoudSpeaker = true;
                    AudioController audioController = getSinchServiceInterface().getAudioController();
                    audioController.enableSpeaker();
                    speaker.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_volume_off_24));
                }
            }
        });


        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                endCall();
            }
        });

        mCallId = getIntent().getStringExtra(SinchService.callId);
        String number = getIntent().getStringExtra("number");
        callrate = Double.valueOf(Function.checkCountry(number));

//        mCallId = UUID.randomUUID().toString();
        finalcost = 20d;

        //runTimer();
    }

    private void RetriveData() {
        mDatabase.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
//                    User user = dataSnapshot.getValue(User.class);
                    previousPoints = Double.parseDouble(dataSnapshot.child("credits").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mDurationTask.cancel();
        mTimer.cancel();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void endCall() {

        SharedPreferences preferences = getSharedPreferences("net.trejj.talk",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isCallInProgress",true);
        editor.apply();

        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
            stopTimer();
        }
        rippleBackground.stopRippleAnimation();
        finish();
    }

    private String formatTimespan(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            mCallDuration.setText(formatTimespan(call.getDetails().getDuration()));
        }
    }
    public void startService() {
        Intent serviceIntent = new Intent(this, CallRunningService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, CallRunningService.class);
        stopService(serviceIntent);
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            stopService();
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            //String endMsg = "Call ended: " + call.getDetails().toString();
            //Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();
            stopTimer();
            try {
                chronometer.stop();
            }catch (Exception e){
                e.printStackTrace();
            }
            CallDetails details = call.getDetails();
            RealmHelper.getInstance().updateCallInfoOnCallEnded(call.getCallId(), details.getDuration());
            endCall();
            if(call.getDetails().getEndCause().toString().toLowerCase().equals("failure")){
                Toast.makeText(CallScreenActivity.this, "Call failed", Toast.LENGTH_SHORT).show();
            }
//            saveCallHistory(System.currentTimeMillis(),call.getDetails().getDuration(),call.getDetails().getEndCause());
        }

        @Override
        public void onCallEstablished(Call call) {

            startService();

            runTimer(call);
            //Toast.makeText(CallScreenActivity.this, "call attended", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Call established");
            chronometer = findViewById(R.id.chronometer);
            callAttended = true;
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            mAudioPlayer.stopProgressTone();
            mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.disableSpeaker();
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");

            String uid = call.getRemoteUserId();
            User user = new User();
            String phoneNumber = call.getHeaders().get("phoneNumber");
            user.setPhone(uid);
            user.setUserName(callername);
            Log.d("PhoneNumber", "onCallProgressing: " + phoneNumber);

            long startedTime = call.getDetails().getStartedTime();
            FireCall fireCall = new FireCall(call.getCallId(), user, 1, startedTime, phoneNumber, call.getDetails().isVideoOffered(),1,"voice");

            RealmHelper.getInstance().saveObjectToRealm(fireCall);

            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }

//    private void saveCallHistory(long currentTimeMillis, int duration, CallEndCause endCause) {
//        SaveCallHistory saveCallHistory = new SaveCallHistory();
//        saveCallHistory.Save(callerNumber,endCause.toString(),System.currentTimeMillis(),duration,callAttended,callername,"phone_call","OUT");
//    }
private void runTimer2(int duration) {
    if (previousPoints >= finalcost)
    {
        Double TotalPoints = previousPoints-=finalcost;
        mDatabase.child("users").child(currentUser.getUid()).child("credits").setValue(TotalPoints);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (previousPoints >= finalcost){
                    if (duration >= 60){
                        Double TotalPoints = previousPoints-=finalcost;
                        mDatabase.child("users").child(currentUser.getUid()).child("credits").setValue(TotalPoints);
                    }
                }else {
                    stopTimer();
                    endCall();
                }
                //Do something after 20 seconds
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }else {
        stopTimer();
        endCall();
    }
}
    private void runTimer(Call call) {
        if (previousPoints >= finalcost)
        {
            Double TotalPoints = previousPoints-=finalcost;
            mDatabase.child("users").child(currentUser.getUid()).child("credits").setValue(TotalPoints);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (previousPoints >= finalcost){
                        if (call.getDetails().getDuration() >= 60){
                            Double TotalPoints = previousPoints-=finalcost;
                            mDatabase.child("users").child(currentUser.getUid()).child("credits").setValue(TotalPoints);
                        }
                    }else {
                        stopTimer();
                        endCall();
                    }
                    //Do something after 20 seconds
                    handler.postDelayed(this, 1000);
                }
            }, 1000);
        }else {
            stopTimer();
            endCall();
        }
    }

    private void stopTimer(){
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (wlOff != null && wlOff.isHeld()) {
                wlOff.release();
            } else if (wlOn != null && wlOn.isHeld()) {
                wlOn.release();
            }
        } catch (RuntimeException ex) {
        }
    }

    private void bindService() {
        Intent serviceIntent = new Intent(this, SinchService.class);

        getApplicationContext().bindService(serviceIntent, this, BIND_AUTO_CREATE);
    }
}
