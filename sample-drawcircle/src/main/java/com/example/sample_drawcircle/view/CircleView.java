package com.example.sample_drawcircle.view;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.example.sample_drawcircle.R;


/**
 * Created by liyo on 2017/10/21.
 */

public class CircleView extends View {


    private Paint mPaint;   //画笔
    private Paint mPaintText;   //文字画笔
    private Path mPath; //路径
    private int mRadius;    //画布边缘半径（去除padding后的半径）
    private int mStartAngle = 130;  //起始角度
    private int mSweepAngle = 280;  //绘制角度
    private int mMin = 0; //最小值
    private int mMax = 950; //最大值
    private int mSystolicPressure = 78; //收缩压
    private int mDiastolicPressure = 56; //舒张压
    private int mSparkleWidth;  //亮点宽度
    private int mProgressWidth; // 进度圆弧宽度
    private float mLength1; // 刻度顶部相对边缘的长度
    private float mLength2; // 压力值指示器顶部相对边缘的长度

    private int mPadding;
    private float mCenterX, mCenterY; // 圆心坐标
    private RectF mRectMax; //外圈实线圆
    private RectF mRectMin; //内圈虚线圆

    private float c_x,c_y;    //文字中心x，y轴

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

    private void initPaint() {
        mSparkleWidth = 36;
        mProgressWidth = 10;

        mPaintText = new Paint();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setColor(getResources().getColor(R.color.yyy_color));
        mPaint.setStyle(Paint.Style.STROKE);
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        mCenterX = (width-height/2)/2;
        mCenterY = height/4;
        mPadding = 60;
        mRectMax = new RectF(mCenterX,mCenterY,width-mCenterX,height-mCenterY);
        mRectMin = new RectF(mCenterX+mPadding,mCenterY+mPadding,width-mCenterX-mPadding,height-mCenterY-mPadding);
        c_x = mRectMax.centerX();
        c_y = mRectMax.centerY();
        mPath = new Path();
        mLength1 = mPadding + mSparkleWidth / 2f + dp2px(8);
        mLength2 = mLength1 + mProgressWidth + dp2px(4);
        mRadius = (width - mPadding * 2) / 2;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        //画外圈实线圆
        canvas.drawArc(mRectMax,mStartAngle,mSweepAngle,false,mPaint);

        float degree = mSweepAngle / ((mMax - mMin) / 10);
        //画外圈进度指示圆点
        float[] point = getCoordinatePoint(
                mRadius - mSparkleWidth / 2f,
                mStartAngle + calculateRelativeAngleWithValue(mSystolicPressure)
        );
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(point[0], point[1], mSparkleWidth / 2f, mPaint);

        canvas.save();

        //画内圈虚线圆
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setColor(getResources().getColor(R.color.color_green));
        mPaint.setStyle(Paint.Style.STROKE);
        int cnt = mStartAngle+mSweepAngle;
        for (int i = mStartAngle; i < cnt; i+=3) {
            canvas.drawArc(mRectMin,i,1,false,mPaint);
        }

        //画内圈进度指示圆点
//        float[] point2 = getCoordinatePoint(
//                mRadius - mSparkleWidth / 2f,
//                mStartAngle + calculateRelativeAngleWithValue(mDiastolicPressure)
//        );
//        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawCircle(point2[0]+mPadding, point2[1]+mPadding, mSparkleWidth / 2f, mPaint);

//        canvas.save();

        canvas.restore();
        /**
         * 画实时度数值
         */
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setTextSize(sp2px(40));
        mPaintText.setTextAlign(Paint.Align.CENTER);
        String value = String.valueOf(mSystolicPressure+"/"+mDiastolicPressure);
        canvas.drawText(value, c_x-80, c_y+20, mPaintText);

        /**
         * 画数值单位
         */
        mPaintText.setTextSize(sp2px(18));
        canvas.drawText("mmHg", c_x+200, c_y+20, mPaintText);

        /**
         * 画压力描述
         */
        mPaintText.setTextSize(sp2px(24));
        canvas.drawText(calculateCreditDescription(), c_x, c_y-180, mPaintText);

    }

    /**
     * 画外圈实线圆的压力指示点的位置
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
     * 相对起始角度计算信用分所对应的角度大小
     */
    private float calculateRelativeAngleWithValue(int value) {
        return mSweepAngle * value * 1f / mMax;
    }

    /**
     * 压力描述
     * @return
     */
    private String calculateCreditDescription() {
        if (mSystolicPressure >= 950) {
            return "极高";
        } else if (mSystolicPressure > 900) {
            return "偏高";
        } else if (mSystolicPressure > 600) {
            return "正常";
        } else if (mSystolicPressure > 550) {
            return "略低";
        }
        return "偏低";
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

