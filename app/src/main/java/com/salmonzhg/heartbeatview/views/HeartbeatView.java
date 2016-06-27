package com.salmonzhg.heartbeatview.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.salmonzhg.heartbeatview.R;
import com.salmonzhg.heartbeatview.utils.DisplayUtils;


/**
 * Created by Salmon on 2016/5/25 0025.
 */
public class HeartbeatView extends View {
    private Paint mPaint;
    private Paint mBallPaint;
    private Paint mRingPaint;
    private Paint mRingAnimPaint;
    private Path mPath;
    private RectF mRectf;
    private float[] yPos;
    private int mRingRadius;
    private int mRingStrokeWid = 25;
    private int mBallRadius = 15;
    private int mAnimationMargin = 55;
    private int mOffset = 0;
    private int mCycle;
    private int mAngle = 0;
    private boolean isAnimating = false;
    private HeartBeatAnimImpl mListener;
    private boolean isRepeat = false;
    private AnimatorSet mAnimatorSet;

    public HeartbeatView(Context context) {
        this(context, null);
    }

    public HeartbeatView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setColor(ContextCompat.getColor(context, R.color.heartbeat));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);

        mBallPaint = new Paint(mPaint);
        mBallPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(ContextCompat.getColor(context, R.color.heartbeat));

        mRingPaint = new Paint();
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStrokeWidth(mRingStrokeWid);
        mRingPaint.setColor(ContextCompat.getColor(context, R.color.heart_default));

        mRingAnimPaint = new Paint(mRingPaint);
        mRingAnimPaint.setColor(ContextCompat.getColor(context, R.color.white));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < 360; i += 3) {
            canvas.drawArc(mRectf, -90 + i, 1, false, mRingPaint);
        }

        if (isAnimating) {
            resetPath();
            canvas.drawPath(mPath, mPaint);
            for (int i = -90; i < mAngle - 90; i += 3) {
                canvas.drawArc(mRectf, i, 1, false, mRingAnimPaint);
            }
        }

        if (mOffset > mAnimationMargin)
            canvas.drawCircle(getMeasuredWidth() - mBallRadius - mAnimationMargin, yPos[mOffset - mAnimationMargin], mBallRadius, mBallPaint);
        else if (mOffset != 0)
            canvas.drawCircle(getMeasuredWidth() - mBallRadius - mAnimationMargin, getMeasuredHeight() / 2, mBallRadius, mBallPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredSpec = Math.min(measure(widthMeasureSpec), measure(heightMeasureSpec));
        setMeasuredDimension(measuredSpec, measuredSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath = new Path();
        mPath.moveTo(0, getMeasuredHeight() / 2);

        w = getMeasuredWidth();
        h = getMeasuredHeight();

        mRectf = new RectF(mRingStrokeWid, mRingStrokeWid, w - mRingStrokeWid, h - mRingStrokeWid);

        int amplitude = h / 5;
        mCycle = w * 3 / 2;

        yPos = new float[mCycle];

        for (int i = 0; i < w / 2; i++) {
            yPos[i] = (float) Math.sin(Math.PI * 4 * i / w) * amplitude + h / 2;
        }
        for (int i = w / 2; i < mCycle; i++) {
            yPos[i] = h / 2;
        }
    }

    private void resetPath() {
        mPath.reset();
        int start = getMeasuredWidth() - mOffset;

        if (!isRepeat) {
            mPath.moveTo(start > mAnimationMargin ? start - mAnimationMargin : mAnimationMargin,
                    start > mAnimationMargin ? getMeasuredHeight() / 2 : yPos[mOffset - getMeasuredWidth() + mAnimationMargin]);
        } else {
            mPath.moveTo(mAnimationMargin, start > mAnimationMargin ? getMeasuredHeight() / 2 : yPos[mOffset - getMeasuredWidth() + mAnimationMargin]);
        }

        if (mOffset < getMeasuredWidth() - mAnimationMargin) {
            if (mOffset < mAnimationMargin) {
                mPath.lineTo(getMeasuredWidth() - mAnimationMargin, getMeasuredHeight() / 2);
            } else {
                for (int i = start, j = 0; i < getMeasuredWidth() - mAnimationMargin; i++, j++) {
                    mPath.lineTo(i, yPos[j]);
                }
            }
        } else {
            for (int i = mAnimationMargin, j = mOffset - getMeasuredWidth() + mAnimationMargin;
                 i < getMeasuredWidth() - mAnimationMargin; i++, j++) {
                mPath.lineTo(i, yPos[j]);
            }
        }
    }

    private int measure(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        if (mode == MeasureSpec.AT_MOST) {
            size = dp2px(300);
        }
        return size;
    }

    public void startAnim() {
        if (isAnimating)
            return;
        isAnimating = true;
        ValueAnimator heartBeatAnim = ValueAnimator.ofInt(0, mCycle - 1);
        heartBeatAnim.setDuration(2000);
        heartBeatAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        heartBeatAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                isRepeat = false;
                mAngle = 0;
                mOffset = 0;
                if (mListener != null)
                    mListener.onAnimFinished();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isRepeat = true;
            }
        });
        heartBeatAnim.setRepeatCount(3);
        heartBeatAnim.setInterpolator(new LinearInterpolator());

        ValueAnimator circleAnim = ValueAnimator.ofInt(0, 360);
        circleAnim.setDuration(2000 * 4);
        circleAnim.setInterpolator(new LinearInterpolator());
        circleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAngle = (int) animation.getAnimatedValue();
            }
        });

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(heartBeatAnim).with(circleAnim);
        mAnimatorSet.start();
    }

    public void stopAnim() {
        if (!isAnimating)
            return;
        mAnimatorSet.end();
    }

    public void setHeartBeatAnimListener(HeartBeatAnimImpl listener) {
        mListener = listener;
    }

    private int dp2px(int pxValue) {
        return DisplayUtils.dip2px(getContext(), pxValue);
    }

    public interface HeartBeatAnimImpl {
        void onAnimFinished();
    }
}
