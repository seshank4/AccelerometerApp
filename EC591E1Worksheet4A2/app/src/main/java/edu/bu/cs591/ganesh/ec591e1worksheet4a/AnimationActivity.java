package edu.bu.cs591.ganesh.ec591e1worksheet4a;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.support.v4.view.GestureDetectorCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AnimationActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private LinearLayout imgLinearLayout;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private SensorManager sManager;
    private GestureDetectorCompat detector;
    private Animation animationRotateRightFast;
    private Animation animationRotateRightSlow;
    private Animation animationRotateLeftFast;
    private Animation animationRotateLeftSlow;
    private static final int THRESHOLD_VELOCITY = 10000;
    private static final int MIN_FLING_VELOCITY = 100;
    private ImageView imageView;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new GestureDetectorCompat(this, this);
        animationRotateRightFast = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right_fast);
        animationRotateRightSlow = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right_slow);
        animationRotateLeftFast = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_left_fast);
        animationRotateLeftSlow = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_left_slow);
        imageView = (ImageView)findViewById(R.id.flingMe);

        setContentView(R.layout.activity_animation);
        imgLinearLayout = (LinearLayout) findViewById(R.id.imgLinearLayout);

        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        sManager.registerListener(mSensorListener, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        //sManager.registerListener((SensorEventListener) this, accelerometer, sManager.SENSOR_DELAY_GAME);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sManager.registerListener(mSensorListener, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        sManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        imageView = (ImageView)findViewById(R.id.flingMe);
        if(e2.getX() - e1.getX() > 200) {
            if ((Math.abs(velocityX) > Math.abs(velocityY)) && Math.abs(velocityX) > THRESHOLD_VELOCITY) {
                imageView.startAnimation(animationRotateRightFast);
            } else if ((Math.abs(velocityX) > Math.abs(velocityY)) && Math.abs(velocityX) < THRESHOLD_VELOCITY && velocityX > MIN_FLING_VELOCITY) {
                imageView.startAnimation(animationRotateRightSlow);
            }
        }else if(e1.getX() - e2.getX() > 200){
            if ((Math.abs(velocityX) > Math.abs(velocityY)) && Math.abs(velocityX) > THRESHOLD_VELOCITY) {
                imageView.startAnimation(animationRotateLeftFast);
            } else if ((Math.abs(velocityX) > Math.abs(velocityY)) && Math.abs(velocityX) < THRESHOLD_VELOCITY && Math.abs(velocityX) > MIN_FLING_VELOCITY) {
                imageView.startAnimation(animationRotateLeftSlow);
            }
        }
        return true;
    }
}
