package com.example.administrator.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.example.administrator.myview.utils.DpOrPxUtils;

public class CircleBarView extends View {

    private Paint rPaint;   //绘制矩形的画笔
    private Paint progressPaint;    //绘制圆弧的画笔
    private Paint bgPaint;  //绘制背景画笔

    private float progressNum;
    private float paogressMax;
    private float progressSweepAngle;
    private float startAngle;
    private float sweepAngle;//圆弧经过的角度
    Animation anim;

    AnimatorProgressListener listener;

    RectF rectF;

    private float barWidth;   //圆弧进度条宽度
    private int defaultSize;    //自定义view的宽高

    public CircleBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    int progressColor;
    int bgColor;
    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CircleBarView);

        progressColor = typedArray.getColor(R.styleable.CircleBarView_progress_color,Color.GREEN);//默认为绿色
        bgColor = typedArray.getColor(R.styleable.CircleBarView_bg_color,Color.GRAY);//默认为灰色
        startAngle = typedArray.getFloat(R.styleable.CircleBarView_start_angle,0);//默认为0
        sweepAngle = typedArray.getFloat(R.styleable.CircleBarView_sweep_angle,360);//默认为360
        barWidth = typedArray.getDimension(R.styleable.CircleBarView_bar_width,DpOrPxUtils.dip2px(context,10));//默认为10dp
        typedArray.recycle();//typedArray用完之后需要回收，防止内存泄漏


        anim = new CircleBarAnimation();

        rectF = new RectF();

        defaultSize = DpOrPxUtils.dip2px(context,110);
        barWidth = DpOrPxUtils.dip2px(context,10);

        rPaint = new Paint();
        rPaint.setStyle(Paint.Style.STROKE);    //只描边,不填充
        rPaint.setColor(Color.RED);
        rPaint.setStrokeWidth(barWidth/2);

        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeWidth(barWidth);
        progressPaint.setAntiAlias(true);   //抗锯齿

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(barWidth);

        progressNum = 0;
        paogressMax = 100;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = measureSize(defaultSize, heightMeasureSpec);
        int width = measureSize(defaultSize, widthMeasureSpec);
        int min = Math.min(width, height);// 获取View最短边的长度
        setMeasuredDimension(min, min);// 强制改View为以最短边为长度的正方形

        if(min >= barWidth*2){//这里简单限制了圆弧的最大宽度
            rectF.set(barWidth/2,barWidth/2,min-barWidth/2,min-barWidth/2);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



         rectF = new RectF(0,0,300,300); //建立300*300的正方形

        canvas.drawArc(rectF,startAngle,sweepAngle,false,bgPaint);
        canvas.drawArc(rectF,startAngle,progressSweepAngle,false,progressPaint);
//        canvas.drawRect(rectF,rPaint);
    }


    public class CircleBarAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            progressSweepAngle = interpolatedTime * sweepAngle * progressNum / paogressMax;//这里计算进度条的比例
            listener.changeTextListener(interpolatedTime * progressNum / paogressMax * 100);
            postInvalidate();
        }
    }

    //写个方法给外部调用，用来设置动画时间
    public void setProgressNum(float progressNum,int time) {
        this.progressNum = progressNum;
        anim.setDuration(time);
        this.startAnimation(anim);
        anim.setRepeatCount(0);
    }

    private int measureSize(int defaultSize,int measureSpec) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    public void setProgressText(AnimatorProgressListener listener){
        this.listener = listener;
    }
}
