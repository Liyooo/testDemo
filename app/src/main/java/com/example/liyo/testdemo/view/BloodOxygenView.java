package com.example.liyo.testdemo.view;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.liyo.testdemo.R;

/**
 * Created by liyo on 2017/10/21.
 */

public class BloodOxygenView extends View {


    private Paint mPaint;   //画笔
    private Paint mPaintText;   //文字画笔
    private Path mPath; //路径
    private int mRadius;    //画布边缘半径（去除padding后的半径）
    private final int mStartAngle = 0;  //起始角度
    private final int mSweepAngle = 360;  //绘制角度
    private int mMinValue = 0; //最小值
    private int mMaxValue = 150; //最大值
    private int blood_oxygen = 0; //血氧值
    private int pulse_rate = 0; //脉率值
    private int flat, slightLow, normal, slightlyHigher, polarAltitude;//检测结果描述偏低到极高
    private int mSparkleWidth;  //外圈指示器宽度
    private int mProgressWidth; // 进度圆弧宽度
    private final int mSliceCount = 60;    //内圈虚线刻度个数
    private final float mDegree = 3f; //刻度的角度
    private float mLength1; // 刻度顶部相对边缘的长度
    private float mLength2; // 压力值指示器顶部相对边缘的长度
    private int mPadding;   //内外圈间距
    private float mCenterX, mCenterY; // 圆心坐标
    private RectF mRectFProgressArc;
    private Rect mRectText;

    public int getmMinValue() {
        return mMinValue;
    }

    public void setmMinValue(int mMinValue) {
        this.mMinValue = mMinValue;
    }

    public int getmMaxValue() {
        return mMaxValue;
    }

    public void setmMaxValue(int mMaxValue) {
        this.mMaxValue = mMaxValue;
    }

    public int getBlood_oxygen() {
        return blood_oxygen;
    }

    public void setBlood_oxygen(int blood_oxygen) {
        setSystolicPressureValue(blood_oxygen);
        this.blood_oxygen = blood_oxygen;
    }

    public int getPulse_rate() {
        return pulse_rate;
    }

    public void setPulse_rate(int pulse_rate) {
        setDiastolicPressureValue(pulse_rate);
        this.pulse_rate = pulse_rate;
    }

    public int getFlat() {
        return flat;
    }

    public void setFlat(int flat) {
        this.flat = flat;
    }

    public int getSlightLow() {
        return slightLow;
    }

    public void setSlightLow(int slightLow) {
        this.slightLow = slightLow;
    }

    public int getNormal() {
        return normal;
    }

    public void setNormal(int normal) {
        this.normal = normal;
    }

    public int getSlightlyHigher() {
        return slightlyHigher;
    }

    public void setSlightlyHigher(int slightlyHigher) {
        this.slightlyHigher = slightlyHigher;
    }

    public int getPolarAltitude() {
        return polarAltitude;
    }

    public void setPolarAltitude(int polarAltitude) {
        this.polarAltitude = polarAltitude;
    }

    public BloodOxygenView(Context context) {
        this(context, null);
    }

    public BloodOxygenView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BloodOxygenView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    /**
     * 初始化
     */
    private void initPaint() {
        mSparkleWidth = dp2px(8);
        mProgressWidth = 40;

        mPaintText = new Paint();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setColor(getResources().getColor(R.color.yyy_color));

        mRectFProgressArc = new RectF();
        mPadding = 40;

        mRectText = new Rect();
        mPath = new Path();


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mLength1 = mPadding + mSparkleWidth / 2f + dp2px(8);
        mLength2 = mLength1 + mProgressWidth + dp2px(4);

        int width = resolveSize(dp2px(220), widthMeasureSpec);
        mRadius = (width - mPadding * 2) / 2;

        //设置当前自定义view的大小
//        setMeasuredDimension(width, width - dp2px(34));
        setMeasuredDimension(width, width);

        mCenterX = mCenterY = getMeasuredWidth() / 2f;
        mRectFProgressArc.set(
                mPadding + mSparkleWidth / 2f,
                mPadding + mSparkleWidth / 2f,
                getMeasuredWidth() - mPadding - mSparkleWidth / 2f,
                getMeasuredWidth() - mPadding - mSparkleWidth / 2f
        );
        mPaint.setTextSize(sp2px(10));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(getResources().getColor(R.color.yyy_touming));
        Log.e("hehe","test");
        /**
         * 画进度圆弧背景
         */
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
//        canvas.drawCircle(mCenterX, mCenterY, mSweepAngle, mPaint);
        canvas.drawArc(mRectFProgressArc, mStartAngle, mSweepAngle, false, mPaint);

        /**
         * 画进度圆弧(起始到压力值)
         */
        canvas.drawArc(mRectFProgressArc, mStartAngle,
                getResources().getColor(R.color.yyy_color), false, mPaint);


        /**
         * 画刻度
         */

        float degree = mDegree;   //刻度线角度
        float a = calculateRelativeAngleWithValue(pulse_rate);
        float b = mSweepAngle / 2f;
        mPaint.setShader(null);
        mPaint.setStrokeWidth(4);
        canvas.save();  //保存画布
        canvas.drawLine(mCenterX, mPadding + mLength1, mCenterX, mPadding + mLength1 - 1, mPaint);
        // 逆时针旋转
        for (int i = 0; i < mSliceCount; i++) {
            canvas.rotate(-degree, mCenterX, mCenterY);
            b -= degree;
            canvas.drawLine(mCenterX, mPadding + mLength1, mCenterX, mPadding + mLength1 - 1, mPaint);
        }

        canvas.restore();   //还原画布
        canvas.save();
        // 顺时针旋转
        b = mSweepAngle / 2f;
        for (int i = 0; i < mSliceCount; i++) {
            canvas.rotate(degree, mCenterX, mCenterY);
            b += degree;
            canvas.drawLine(mCenterX, mPadding + mLength1, mCenterX, mPadding + mLength1 - 1, mPaint);
        }
        canvas.restore();

        /**
         * 画内圈值指示器
         */
        canvas.save();
        b = mSweepAngle / 2f;
        System.out.println("aaaaaaaaaaaa:"+a+",bbbbbbbbbbb:"+b);
        canvas.rotate(a, mCenterX, mCenterY);
        mPaint.setStyle(Paint.Style.FILL);
        mPath.reset();
        mPath.moveTo(mCenterX, mPadding + mLength2);
        mPath.rLineTo(-dp2px(2), dp2px(5));
        mPath.rLineTo(dp2px(4), 0);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
        mPaint.setStrokeWidth(dp2px(1));
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mCenterX, mPadding + mLength2 + dp2px(6) + 1, dp2px(2), mPaint);
        canvas.restore();


        float[] point = getCoordinatePoint(
                mRadius - mSparkleWidth / 2f,
                mStartAngle + calculateRelativeAngleWithValue(pulse_rate)
        );

        //画结果数值
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(sp2px(40));
        mPaint.setTextAlign(Paint.Align.CENTER);
        String value = String.valueOf(pulse_rate);
        canvas.drawText(value, mCenterX, mCenterY, mPaint);

        //画数值单位
        float wid = mPaint.measureText(value);  //获取度数值文本宽度
        mPaint.setTextSize(sp2px(14));
        canvas.drawText("bpm", mCenterX, mCenterY + 60, mPaint);

        mPaint.setTextSize(sp2px(16));
        canvas.drawText("脉率", mCenterX, mCenterY + 120, mPaint);

    }


    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     *
     * @param radius    绘制角度
     * @param angle     起始角度
     * @return
     */
    private float[] getCoordinatePoint(float radius, float angle) {
        float[] point = new float[2];

        double arcAngle = Math.toRadians(angle); //将角度转换为弧度
        if (angle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (angle > 90 && angle < 180) {
            arcAngle = Math.PI * (180 - angle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (angle > 180 && angle < 270) {
            arcAngle = Math.PI * (angle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (angle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - angle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }

        return point;
    }


    /**
     * 检测结果描述
     * 暂未拿到计算公式
     *
     * @return
     */
    private String calculateCreditDescription() {

        if (blood_oxygen >= polarAltitude) {
            return "极高";
        } else if (blood_oxygen > slightlyHigher) {
            return "偏高";
        } else if (blood_oxygen > normal) {
            return "正常";
        } else if (blood_oxygen > slightLow) {
            return "略低";
        } else if (blood_oxygen > flat) {
            return "偏低";
        }
        return "未测量";

    }


    /**
     * 相对起始角度计算检测值所对应的角度大小
     */
    private float calculateRelativeAngleWithValue(int value) {
        if (value > mMaxValue)
            return mMaxValue;
        return mSweepAngle * value * 1f / mMaxValue;
    }


    /**
     * 设置外圈检测值
     *
     * @param pressure
     */
    private void setSystolicPressureValue(int pressure) {
        if (blood_oxygen == pressure || pressure < mMinValue || pressure > mMaxValue) {
            return;
        }

        blood_oxygen = pressure;
        invalidate();
    }

    /**
     * 设置内圈检测值
     *
     * @param pressure
     */
    private void setDiastolicPressureValue(int pressure) {
        if (pulse_rate == pressure || pressure < mMinValue || pressure > mMaxValue) {
            return;
        }
        pulse_rate = pressure;
        postInvalidate();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }
}
