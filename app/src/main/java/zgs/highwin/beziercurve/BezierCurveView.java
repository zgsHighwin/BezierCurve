package zgs.highwin.beziercurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.TimeUnit;

public class BezierCurveView extends View {
    private Paint mLeftCirclePaint;
    private Paint mRightCirclePaint;
    private Paint mBackgroundPaint;
    private Paint mLinePaint;
    private Paint mBezierPathPaint;
    private float mDownY;
    private float mDownX;
    private boolean mIsActionUp;

    public BezierCurveView(Context context) {
        this(context, null);
    }

    public BezierCurveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierCurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        mLeftCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLeftCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLeftCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mLeftCirclePaint.setStrokeJoin(Paint.Join.ROUND);
        mLeftCirclePaint.setColor(Color.GREEN);
        mLeftCirclePaint.setDither(true);

        mRightCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRightCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mRightCirclePaint.setStrokeCap(Paint.Cap.ROUND);
        mRightCirclePaint.setStrokeJoin(Paint.Join.ROUND);
        mRightCirclePaint.setColor(Color.RED);
        mRightCirclePaint.setDither(true);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackgroundPaint.setStrokeJoin(Paint.Join.ROUND);
        mBackgroundPaint.setColor(Color.BLUE);
        mBackgroundPaint.setDither(true);
        mBackgroundPaint.setStrokeWidth(10);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setColor(Color.YELLOW);
        mLinePaint.setDither(true);

        mBezierPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBezierPathPaint.setStyle(Paint.Style.STROKE);
        mBezierPathPaint.setStrokeCap(Paint.Cap.ROUND);
        mBezierPathPaint.setStrokeJoin(Paint.Join.ROUND);
        mBezierPathPaint.setColor(Color.BLACK);
        mBezierPathPaint.setDither(true);
        mBezierPathPaint.setStrokeWidth(30);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int realWidth = 0;
        int realHeight = 0;
        if (widthMode == MeasureSpec.EXACTLY) {
            realWidth = widthSize;
        } else {
            realWidth = 50;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            realHeight = heightSize;
        } else {
            realHeight = 30;
        }
        setMeasuredDimension(realWidth, realHeight);
    }

    private int radius = 50;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF bgRectF = new RectF(0, 0, getWidth(), getHeight());
        // RectF rectLine = new RectF(radius, getHeight() / 2 - radius / 2, getWidth() - radius, getHeight() / 2 + radius / 2);
        //   canvas.drawRect(rectLine, mLinePaint);
        canvas.drawRect(bgRectF, mBackgroundPaint);
        canvas.drawCircle(radius, getHeight() / 2, radius, mLeftCirclePaint);
        canvas.drawCircle(getWidth() - radius, getHeight() / 2, radius, mRightCirclePaint);
        canvas.drawPath(getPath((int) (mDownY), (int) mDownX), mBezierPathPaint);
    }

    @NonNull
    private Path getPath(int yPosition, int xPosition) {
        Path bezierPath = new Path();
        bezierPath.reset();
        bezierPath.moveTo(0, getHeight() / 2);
        bezierPath.quadTo(xPosition, yPosition, getWidth(), getHeight() / 2);
        // bezierPath.quadTo(getWidth()/2, getHeight()/2, getWidth(), getHeight() / 2);  //直线
        // bezierPath.quadTo(getWidth() / 2, yPosition, getWidth(), getHeight() / 2); //只对y轴进行处理
        // bezierPath.close();
        return bezierPath;
    }

    private int speed = 5;
    private int sleepTime = 5;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (!mIsActionUp) {
                    mDownX = event.getX();
                    mDownY = event.getY();
                    judgeBorder();
                    invalidate();
                    Log.d("BezierCurveView", "mDownY:" + mDownY);
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsActionUp = true;
                Log.d("BezierCurveView", "actionup");
                mDownX = event.getX();
                mDownY = event.getY();
                judgeBorder();
                Log.d("BezierCurveView", "left" + mDownX);
                if (mDownX > getWidth() / 2) {
                    if (mDownY > getHeight() / 2) {
                        new Thread() {
                            @Override
                            public void run() {
                                if (!isInterrupted()) {
                                    while (true) {
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(sleepTime);
                                            mDownX -= speed;
                                            mDownY -= speed;
                                            if (mDownX <= getWidth() / 2) {
                                                mDownX = getWidth() / 2;
                                            }
                                            if (mDownY <= getHeight() / 2) {
                                                mDownY = getHeight() / 2;
                                            }
                                            if (mDownX == getWidth() / 2 && mDownY == getHeight() / 2) {
                                                postInvalidate();
                                                mIsActionUp = false;
                                                synchronized (this) {
                                                    Log.d("BezierCurveView", "interrupt");
                                                    this.notify();
                                                    this.interrupt();
                                                }
                                                break;
                                            }
                                            postInvalidate();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }.start();
                    } else {    //mDownY <getHeight() / 2
                        new Thread() {
                            @Override
                            public void run() {
                                if (!isInterrupted()) {
                                    while (true) {
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(sleepTime);
                                            mDownX -= speed;
                                            mDownY += speed;
                                            if (mDownX <= getWidth() / 2) {
                                                mDownX = getWidth() / 2;
                                            }
                                            if (mDownY >= getHeight() / 2) {
                                                mDownY = getHeight() / 2;
                                            }
                                            if (mDownX == getWidth() / 2 && mDownY == getHeight() / 2) {
                                                postInvalidate();
                                                mIsActionUp = false;
                                                synchronized (this) {
                                                    Log.d("BezierCurveView", "interrupt");
                                                    this.notify();
                                                    this.interrupt();
                                                }
                                                break;
                                            }
                                            postInvalidate();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }.start();

                    }
                } else { //mDownX < getWidth() / 2
                    if (mDownY > getHeight() / 2) {
                        new Thread() {
                            @Override
                            public void run() {
                                if (!isInterrupted()) {
                                    while (true) {
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(sleepTime);
                                            mDownX += speed;
                                            mDownY -= speed;
                                            if (mDownX >= getWidth() / 2) {
                                                mDownX = getWidth() / 2;
                                            }
                                            if (mDownY <= getHeight() / 2) {
                                                mDownY = getHeight() / 2;
                                            }
                                            if (mDownX == getWidth() / 2 && mDownY == getHeight() / 2) {
                                                postInvalidate();
                                                mIsActionUp = false;
                                                synchronized (this) {
                                                    Log.d("BezierCurveView", "interrupt");
                                                    this.notify();
                                                    this.interrupt();
                                                }
                                                break;
                                            }
                                            postInvalidate();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }.start();
                    } else {    //mDownY <getHeight() / 2
                        new Thread() {
                            @Override
                            public void run() {
                                if (!isInterrupted()) {
                                    while (true) {
                                        try {
                                            TimeUnit.MILLISECONDS.sleep(sleepTime);
                                            mDownX += speed;
                                            mDownY += speed;
                                            if (mDownX >= getWidth() / 2) {
                                                mDownX = getWidth() / 2;
                                            }
                                            if (mDownY >= getHeight() / 2) {
                                                mDownY = getHeight() / 2;
                                            }
                                            if (mDownX == getWidth() / 2 && mDownY == getHeight() / 2) {
                                                postInvalidate();
                                                mIsActionUp = false;
                                                synchronized (this) {
                                                    Log.d("BezierCurveView", "interrupt");
                                                    this.notify();
                                                    this.interrupt();
                                                }
                                                break;
                                            }
                                            postInvalidate();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }.start();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 边界检测
     */
    private void judgeBorder() {
        if (mDownX < 0) {
            mDownX = 0;
        }

        if (mDownX > getWidth()) {
            mDownX = getWidth();
        }

        if (mDownY < -getHeight() / 2) {
            mDownY = -getHeight() / 2;
        } else if (mDownY > 3 * getHeight() / 2) {
            mDownY = 3 * getHeight() / 2;
        }
    }

   /* private class MoveThread extends Thread {

        private boolean mIsPause;

        *//**
     * 线程暂停
     *//*
        public synchronized void onThreadPause() {
            mIsPause = true;
        }

        *//**
     * 线程等待
     *//*
        private void onThreadWait() {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        *//**
     * 线程继续运行
     *//*
        public synchronized void onThreadResume() {
            mIsPause = false;
            this.notify();
        }

        *//**
     * 关闭线程
     *//*
        public synchronized void onThreadClose() {
            try {
                notify();
                interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            super.run();
        }
    }*/
}
