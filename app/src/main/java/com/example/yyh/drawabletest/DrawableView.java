package com.example.yyh.drawabletest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * Created by yyh on 2016/3/31.
 */
public class DrawableView extends ImageView {
    //图片的类型，圆形或者圆角

    private int type;
    public static final int TYPE_CIRCLE =0;
    public static final int TYPE_ROUND =1;

    //圆角的默认大小

    private static final int BORDER_RADIUS_DEFAULT = 10;

    //圆角大小
    private int mBorderRadius;

    //画笔
    private Paint mBitmapPaint;

    //圆角的半径
    private int mRadius;

    //3*3矩阵，主要用于放大和缩小。

    private Matrix mMatrix;

    //渲染图像，使用图像为绘制图像着色
    private BitmapShader mBitmapShader;
    //View的宽度
    private int mWidth;

    private RectF mRoundRect;

    public DrawableView(Context context) {
        this(context, null);

    }

    public DrawableView(Context context,AttributeSet attrs) {
        super(context,attrs);
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.DrawableView);
        mBorderRadius = typedArray.getDimensionPixelSize(R.styleable.DrawableView_broderRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,BORDER_RADIUS_DEFAULT,getResources().getDisplayMetrics()));

        type = typedArray.getInt(R.styleable.DrawableView_type,TYPE_CIRCLE);//默认为Circle

        typedArray.recycle();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        if (type==TYPE_CIRCLE){
            mWidth = Math.min(getMeasuredWidth(),getMeasuredHeight());
            mRadius=mWidth/2;
            setMeasuredDimension(mWidth,mWidth);


        }






    }

    //初始化BitmapShoder

    /**
     * 1.将drawable转化为bitmap
     * 2.初始化mBitmapShader = new BitmapShaser(bmp,TileMode.CLAMP,TileMode.CLAMP);
     * 3.根据type以及bitmap和view的宽高，计算scale.
     *    type==TYPE_CIRCLE时， 取bitmap宽和高小的为基准，如果采用大值，缩放后就不能填满我们的圆形区域。
     *    type==TYPE_BOUND时，涉及到宽高比例，Math.max(getWidth()*1.0f/bmp.getWidth(),getHeight()*1.0f/bmp.getHeight());取大值，因为我们要让最终缩放完成的图片一定要大于我们的view区域。
     * 4.有了scale，设置给我们的mMatrix;
     * 5.使用mBitmapShader.setLocalMatrix(mMatrix);
     * 6.最后将mBitmapShader设置给paint.
     *
     */

    private void setUpShader(){

        Drawable drawable = getDrawable();
        if (drawable==null){
            return;

        }
        Bitmap bmp = drawableToBitmap(drawable);

        /**
         * 将bmp作为着色器，就是在指定的区域内绘制bmp
         */
        mBitmapShader = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        float scale = 1.0f;

        if (type==TYPE_CIRCLE){

            int bSize = Math.min(bmp.getWidth(),bmp.getHeight());

            scale = mWidth*1.0f/bSize;

        }else if (type==TYPE_ROUND){
            //如果图片的宽高和View的宽高不匹配，计算出需要缩放的比例，缩放后图片的宽高，一定要大于我们view的宽高，所以这里取大值
                scale = Math.max(getWidth()*1.0f/bmp.getWidth(),getHeight()*1.0f/bmp.getHeight());



        }
        //shader 的变换矩阵，这里主要用于放大和缩小。
        mMatrix.setScale(scale,scale);
        //设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        //设置shader.
        mBitmapPaint.setShader(mBitmapShader);

    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable){
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();


        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,w,h);
        drawable.draw(canvas);


        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (getDrawable()==null){
            return;

        }
        setUpShader();
        if (type==TYPE_CIRCLE){
            canvas.drawCircle(mRadius,mRadius,mRadius,mBitmapPaint);
        }else{
            canvas.drawRoundRect(mRoundRect,mBorderRadius,mBorderRadius,mBitmapPaint);

        }


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //设置圆角图片的范围
        if (type==TYPE_ROUND){
            mRoundRect = new RectF(0,0,getWidth(),getHeight());

        }
    }
    /**
     * 状态的存储与恢复
     *
     */

    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    private static final String STATE_BORDER_RADIUS = "state_border_radius";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE,super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE,type);
        bundle.putInt(STATE_BORDER_RADIUS,mBorderRadius);



        return bundle;



    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle rbundle = (Bundle) state;
            super.onRestoreInstanceState(((Bundle) state).getParcelable(STATE_INSTANCE));
            this.type = rbundle.getInt(STATE_TYPE);
            this.mBorderRadius = rbundle.getInt(STATE_BORDER_RADIUS);



        }else {


            super.onRestoreInstanceState(state);
        }

    }


    public void setType(int type){
        if (this.type!=type){
            this.type=type;
            if (this.type!=TYPE_CIRCLE&&this.type!=TYPE_ROUND){

                this.type=TYPE_CIRCLE;
            }
            requestLayout();


        }

    }
    public void setmBorderRadius(int borderRadius){
        int pxVal =dp2px(borderRadius);
        if (this.mBorderRadius!=pxVal){
            this.mBorderRadius=pxVal;
            invalidate();
        }

    }

    private int dp2px(int dpVal) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpVal,getResources().getDisplayMetrics());
    }

}
