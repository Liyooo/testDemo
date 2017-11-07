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
import android.util.TypedValue;
import android.view.View;

import com.example.liyo.testdemo.R;

/**
 * Created by liyo on 2017/10/21.
 */

public class CircleView extends View {

    public enum Type{
        BloodSurge,
        BloodPressure
    }


    private Paint mPaint;   //画笔
    private Paint mPaintText;   //文字画笔
    private Path mPath; //路径
    private int mRadius;    //画布边缘半径（去除padding后的半径）
    private final int mStartAngle = 130;  //起始角度
    private final int mSweepAngle = 280;  //绘制角度
    private int mMinValue = 0; //最小值
    private int mMaxValue = 150; //最大值
    private int mOuterRing = 0; //外圈值
    private int mInsideTrack = 0; //内圈值
    private Type checkType = Type.BloodPressure;    //健康检查类型
    private int flat, slightLow, normal, slightlyHigher, polarAltitude;//检测结果描述偏低到极高
    private int mSparkleWidth;  //外圈指示器宽度
    private int mProgressWidth; // 进度圆弧宽度
    private final int mSliceCount = 47;    //内圈虚线刻度个数
    private final float mDegree = 3f; //刻度的角度
    private float mLength1; // 刻度顶部相对边缘的长度
    private float mLength2; // 压力值指示器顶部相对边缘的长度
    private int mPadding;
    private float mCenterX, mCenterY; // 圆心坐标
    private RectF mRectFProgressArc;
    private Rect mRectText;


    public Type getCheckType() {
        return checkType;
    }

    public void setCheckType(Type checkType) {
        this.checkType = checkType;
    }

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

    public int getmOuterRing() {
        return mOuterRing;
    }

    public void setmOuterRing(int mOuterRing) {
        this.mOuterRing = mOuterRing;
        setSystolicPressureValue(mOuterRing);
    }

    public int getmInsideTrack() {
        return mInsideTrack;
    }

    public void setmInsideTrack(int mInsideTrack) {
        this.mInsideTrack = mInsideTrack;
        setDiastolicPressureValue(mInsideTrack);
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

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    /**
     * 初始化
     */
    private void initPaint() {
        mSparkleWidth = dp2px(8);
        mProgressWidth = 4;

        mPaintText = new Paint();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setColor(getResources().getColor(R.color.yyy_color));

        mRectFProgressArc = new RectF();
        mPadding = 20;

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
        setMeasuredDimension(width, width - dp2px(34));

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


        /**
         * 画进度圆弧背景
         */
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mProgressWidth);
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
        float a = calculateRelativeAngleWithValue(mInsideTrack);
        float b = mSweepAngle / 2f;
        mPaint.setShader(null);

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
        canvas.rotate(a - b, mCenterX, mCenterY);
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
                mStartAngle + calculateRelativeAngleWithValue(mOuterRing)
        );
        String value = "";
        if (checkType.equals(Type.BloodPressure)){
            //画外圈指示亮点
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(point[0], point[1], mSparkleWidth / 2f, mPaint);

            //画结果数值
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(sp2px(34));
            mPaint.setTextAlign(Paint.Align.CENTER);
            value = String.valueOf(mOuterRing + "/" + mInsideTrack);
            canvas.drawText(value, mCenterX , mCenterY + 40, mPaint);

            //画数值单位
            mPaint.setTextSize(sp2px(14));
            canvas.drawText("mmHg", mCenterX , mCenterY + 100, mPaint);

        }else if (checkType.equals(Type.BloodSurge)){
            //画结果数值
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(sp2px(34));
            mPaint.setTextAlign(Paint.Align.CENTER);
            value = String.valueOf(mInsideTrack);
            canvas.drawText(value, mCenterX, mCenterY + 40, mPaint);

            //画数值单位
            mPaint.setTextSize(sp2px(14));
            canvas.drawText("mmol/L", mCenterX, mCenterY + 100, mPaint);
        }




        /**
         * 画检测结果描述
         */
        mPaint.setTextSize(sp2px(24));
        canvas.drawText(calculateCreditDescription(), mCenterX, mCenterY - 100, mPaint);

    }


    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     * @param radius
     * @param angle
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
     * @return
     */
    private String calculateCreditDescription() {
        if (checkType.equals(Type.BloodPressure)){
            if (mOuterRing >= polarAltitude) {
                return "极高";
            } else if (mOuterRing > slightlyHigher) {
                return "偏高";
            } else if (mOuterRing > normal) {
                return "正常";
            } else if (mOuterRing > slightLow) {
                return "略低";
            } else if (mOuterRing > flat) {
                return "偏低";
            }
            return "未测量";
        }else if (checkType.equals(Type.BloodSurge)){
            if (mInsideTrack >= polarAltitude) {
                return "极高";
            } else if (mInsideTrack > slightlyHigher) {
                return "偏高";
            } else if (mInsideTrack > normal) {
                return "正常";
            } else if (mInsideTrack > slightLow) {
                return "略低";
            } else if (mInsideTrack > flat) {
                return "偏低";
            }
        }
        return "";

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
     * @param pressure
     */
    public void setSystolicPressureValue(int pressure) {
        if (mOuterRing == pressure || pressure < mMinValue || pressure > mMaxValue) {
            return;
        }

        mOuterRing = pressure;
        postInvalidate();
    }

    /**
     * 设置内圈检测值
     * @param pressure
     */
    public void setDiastolicPressureValue(int pressure) {
        if (mInsideTrack == pressure || pressure < mMinValue || pressure > mMaxValue) {
            return;
        }

        mInsideTrack = pressure;
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
