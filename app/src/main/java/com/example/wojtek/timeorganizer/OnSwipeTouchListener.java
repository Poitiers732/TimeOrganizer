package com.example.wojtek.timeorganizer;


import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Detects left and right swipes across a view.
 */
public class OnSwipeTouchListener implements View.OnTouchListener {

    AdapterView<?> arg0;
    View arg1;
    int arg2;
    long id;

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft(AdapterView<?> arg0, View arg1, int arg2,
                            long id) {
    }

    public void onSwipeRight(AdapterView<?> arg0, View arg1, int arg2,
                             long id) {
    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight(arg0,arg1,arg2,id);
                else
                    onSwipeLeft(arg0,arg1,arg2,id);
                return true;
            }
            return false;
        }
    }
}
