package com.example.administrator.myview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.administrator.myview.bean.Point;

import java.util.Random;
import java.util.Set;

public class HexagramView extends View {
    public static final String TAG = "HexagramView";

    private Paint circlePaint;
    private Paint starPaint;
    ValueAnimator valueAnimator;
    ValueAnimator pointAnimator;

    Point currentPoint = new Point();

    float sweepAngle;

    public HexagramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public void init(Context context, AttributeSet attrs){
        circlePaint = new Paint();
        circlePaint.setColor(Color.GREEN);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5);

        starPaint = new Paint();
        starPaint.setColor(Color.RED);
        starPaint.setStyle(Paint.Style.STROKE);
        starPaint.setStrokeWidth(10);
        //设置动画属性
        valueAnimator = ValueAnimator.ofFloat(0,360);
        valueAnimator.setDuration(3000);
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                 sweepAngle = (float)valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.start();

        //绘制六芒星 数组长度必须为3
        setStarAnimator(300,new int[]{1000,0,2000});
        setStarAnimator(400,new int[]{3000,5000,4000});
    }

    RectF rectF = new RectF(200,250,800,850);
//    float radiu = 400;
    Path path1 = new Path();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG,rectF.centerX()+"     "+getY());

        canvas.drawPath(path1,starPaint);

        RectF rectF2 = new RectF(100,150,900,950);
        canvas.drawArc(rectF,0,sweepAngle,false,circlePaint);
        canvas.drawArc(rectF2,0,-sweepAngle,false,circlePaint);
    }

    //实现objectAnimator估值器
    // 实现TypeEvaluator接口
    public class PointEvaluator implements TypeEvaluator {

        // 复写evaluate（）
        // 在evaluate（）里写入对象动画过渡的逻辑
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {

            // 将动画初始值startValue 和 动画结束值endValue 强制类型转换成Point对象
            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;

            // 根据fraction来计算当前动画的x和y的值
            float x = startPoint.getX() + fraction * (endPoint.getX() - startPoint.getX());
            float y = startPoint.getY() + fraction * (endPoint.getY() - startPoint.getY());

            // 将计算后的坐标封装到一个新的Point对象中并返回
            Point point = new Point(x, y);
            return point;
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setStarAnimator(float radiu,int[] arr){
        final Point point1 = new Point(rectF.centerX(),rectF.centerY()-radiu);
        Point point2 = new Point((float) (rectF.centerX()-(radiu*Math.sin(Math.toRadians(60)))), (float) (rectF.centerY()+(radiu*Math.sin(Math.toRadians(30)))));
        Point point3 = new Point((float) (rectF.centerX()+(radiu*Math.sin(Math.toRadians(60)))), (float) (rectF.centerY()+(radiu*Math.sin(Math.toRadians(30)))));

        //制作随机数
        setLineAnimator(point1,point2,arr[0]);
        setLineAnimator(point2,point3,arr[1]);
        setLineAnimator(point3,point1,arr[2]);
        //绘制倒三角
        Point point4 = new Point(rectF.centerX(),rectF.centerY()+radiu);
        Point point5 = new Point((float) (rectF.centerX()-(radiu*Math.sin(Math.toRadians(60)))), (float) (rectF.centerY()-(radiu*Math.sin(Math.toRadians(30)))));
        Point point6 = new Point((float) (rectF.centerX()+(radiu*Math.sin(Math.toRadians(60)))), (float) (rectF.centerY()-(radiu*Math.sin(Math.toRadians(30)))));
        setLineAnimator(point4,point5,arr[2]);
        setLineAnimator(point5,point6,arr[0]);
        setLineAnimator(point6,point4,arr[1]);
    }

    public void setLineAnimator(final Point startPoint,final Point endPoint,int delayTime){
        pointAnimator =  ValueAnimator.ofObject(new PointEvaluator(),startPoint,endPoint);
        pointAnimator.setDuration(1000);
        pointAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        pointAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                path1.moveTo(startPoint.getX(),startPoint.getY());
                currentPoint = (Point) valueAnimator.getAnimatedValue();
                path1.lineTo(currentPoint.getX(),currentPoint.getY());
                invalidate();
            }
        });
        pointAnimator.setStartDelay(delayTime);
        pointAnimator.start();
    }

}
