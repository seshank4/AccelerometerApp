package edu.bu.cs591.ganesh.ec591e1worksheet4a;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.support.v4.view.GestureDetectorCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;

public class AnimationActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private LinearLayout imgLinearLayout;
    private SensorManager sManager;
    private GestureDetectorCompat detector;
    private Animation animationRotateRightFast;
    private Animation animationRotateRightSlow;
    private Animation animationRotateLeftFast;
    private Animation animationRotateLeftSlow;
    private Animation animationSlideRight;
    private Animation animationSlideLeft;
    private Animation animationSlideUp;
    private Animation animationSlideDown;
    private Animation animationShake;
    private static final int THRESHOLD_VELOCITY = 10000;
    private static final int MIN_FLING_VELOCITY = 100;
    private ImageView imageView;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private float deltaX,deltaY,deltaZ;
    private static final int SIGNIFICANT_SHAKE = 15;
    private static final int MIN_ACC = 5;

    private float[] gravity = new float[3];

    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

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
        animationSlideRight = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_right);
        animationSlideLeft = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_left);
        animationSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
        animationSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);
        animationShake = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
        imageView = (ImageView)findViewById(R.id.flingMe);

        setContentView(R.layout.activity_animation);
        imgLinearLayout = (LinearLayout) findViewById(R.id.imgLinearLayout);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(mSensorListener, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // initialize acceleration values
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            Sensor mySensor = event.sensor;
            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                final float alpha = 0.9f;

                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                float linearX = x - gravity[0];
                float linearY = y - gravity[1];
                float linearZ = z - gravity[2];

                deltaX = linearX - last_x;
                deltaY = linearX - last_y;
                deltaZ = linearX - last_z;

                long curTime = System.currentTimeMillis();

                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
                float deltaAccl = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + deltaAccl;
                double deltaTime = curTime - lastUpdate;
                if ((deltaTime) > 100) {
                    imageView = (ImageView)findViewById(R.id.flingMe);
                    if(Math.abs(linearX) > Math.abs((linearY+9.8)) ) {
                        if (linearX > 0 && Math.abs(linearX) > 5 && deltaX>5 && mAccelCurrent<15) {
                            imageView.startAnimation(animationSlideLeft);
                            lastUpdate = curTime;
                            Log.i("deltas ",String.valueOf(mAccel));
                            Log.i("acc","mAcc : "+mAccelCurrent);
                        } else if (linearX < 0 && Math.abs(linearX) > 5 && deltaX<-5 && mAccelCurrent<15) {
                            imageView.startAnimation(animationSlideRight);
                            lastUpdate = curTime;
                            Log.i("deltas ",String.valueOf(mAccel));
                            Log.i("acc","mAcc : "+mAccelCurrent);
                        }
                    }else if(Math.abs(linearY) > Math.abs((linearX))) {
                        if (linearY >0 && Math.abs(linearY) > 5 && deltaY<-5 && mAccelCurrent>15) {
                            imageView.startAnimation(animationSlideDown);
                            lastUpdate = curTime;
                            Log.i("acc","mAcc : " +mAccelCurrent);
                        } else if (linearY < 0 && Math.abs(linearY) > 5 && deltaY>2 && mAccelCurrent>5) {
                            imageView.startAnimation(animationSlideUp);
                            Log.i("acc","mAcc : "+mAccelCurrent);
                            lastUpdate = curTime;
                        }
                    }
                    last_x = linearX;
                    last_y = linearY;
                    last_z = linearZ;

                    if(mAccel > SIGNIFICANT_SHAKE){
                        imageView.startAnimation(animationShake);
                        Log.i("deltas ",String.valueOf(mAccel));
                    }
                    }
                }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(mSensorListener, senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        senSensorManager.unregisterListener(mSensorListener);
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
