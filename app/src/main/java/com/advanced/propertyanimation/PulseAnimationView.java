package com.advanced.propertyanimation;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

class PulseAnimationView extends View {

    private float mRadius;

    private final Paint mPaint = new Paint();
    private static final int COLOR_ADJUSTER = 5;

    private float mX;
    private float mY;

    private static final int ANIMATION_DURATION = 4000;
    private static final long ANIMATION_DELAY = 1000;

    private final AnimatorSet mPulseAnimatorSet = new AnimatorSet();

    public PulseAnimationView(Context context, AttributeSet attrs) {
        super(context, null);
    }

    // SOS: The animators change this property, thus I have to provide a method of the form setProperty
    // Android Studio is not smart enough to see that this is used so I suppress the warning
    @SuppressWarnings("unused")
    void setRadius(float radius) {
        mRadius = radius;
        mPaint.setColor(Color.GREEN + (int) radius / COLOR_ADJUSTER);
        // SOS: For my own views, it's not enough to change a property, I have to call invalidate for
        // the view to be redrawn.
        invalidate();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        ObjectAnimator growAnimator = ObjectAnimator.ofFloat(this,
                "radius", 0, getWidth() / 2f);
        growAnimator.setDuration(ANIMATION_DURATION);
        growAnimator.setInterpolator(new LinearInterpolator());

        ObjectAnimator shrinkAnimator = ObjectAnimator.ofFloat(this,
                "radius", getWidth() / 2f, 0);
        shrinkAnimator.setDuration(ANIMATION_DURATION);
        shrinkAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        shrinkAnimator.setStartDelay(ANIMATION_DELAY);

        ObjectAnimator repeatAnimator = ObjectAnimator.ofFloat(this,
                "radius", 0, getWidth() / 2f);
        repeatAnimator.setStartDelay(ANIMATION_DELAY);
        repeatAnimator.setDuration(ANIMATION_DURATION);
        repeatAnimator.setRepeatCount(1);
        repeatAnimator.setRepeatMode(ValueAnimator.REVERSE);

        mPulseAnimatorSet.play(growAnimator).before(shrinkAnimator);
        mPulseAnimatorSet.play(repeatAnimator).after(shrinkAnimator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mX, mY, mRadius, mPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mX = event.getX();
            mY = event.getY();

            if (mPulseAnimatorSet.isRunning()) {
                mPulseAnimatorSet.cancel();
            }

            mPulseAnimatorSet.start();
        }
        return super.onTouchEvent(event);
    }
}
