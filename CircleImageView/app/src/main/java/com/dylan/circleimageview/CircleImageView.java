package com.dylan.circleimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Description
 * author   Dylan.zhuang
 * Date:    16/6/30-下午2:43
 */
public class CircleImageView extends ImageView {
    private static final String TAG = "CircleImageView";

    private static final float DEFAULT_SCALE = 0.9f;
    /**
     * 绘制图片的位图
     */
    private Bitmap mBitmap;
    /**
     * 圆形图像边框宽度
     */
    private int mStrokeWidth;
    /**
     * 圆形图像边框颜色
     */
    private int mStrokeColor;
    /**
     * 随机背景文本大小
     */
    private int mTextSize;
    /**
     * 随机背景颜色
     */
    private int mBackground;
    /**
     * 随机背景要展示的文本
     */
    private String mText;
    /**
     * 视图宽度
     */
    private int mWidth;
    /**
     * 视图高度
     */
    private int mHeight;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int defaultColor = getResources().getColor(R.color.colorGreen);
        int defaultTextSize = getResources().getDimensionPixelSize(R.dimen.dimen_default_text_size);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);
        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_stroke_width, 0);
        mStrokeColor = typedArray.getColor(R.styleable.CircleImageView_stroke_color, defaultColor);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_text_size, defaultTextSize);
        mBackground = typedArray.getColor(R.styleable.CircleImageView_random_backgroud, defaultColor);
        mText = typedArray.getString(R.styleable.CircleImageView_text);
        typedArray.recycle();
    }

    /**
     * 设置边缘宽度
     * @param width
     */
    public void setStrokeWidth(int width) {
        mStrokeWidth = width;
    }

    /**
     * 设置边缘颜色
     * @param color
     */
    public void setStrokeColor(int color) {
        mStrokeColor = color;
    }

    /**
     * 设置文本大小
     * @param textSize
     */
    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    /**
     * 设置背景颜色
     * @param background
     */
    public void setBackground(int background) {
        mBackground = background;
    }

    /**
     * 设置文本
     * @param text
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * 绘制随机背景
     */
    public void drawRandomBackground() {
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    public void setImageResource(int resId) {
        Log.d(TAG, "setImageResource");
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        invalidate();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        Log.d(TAG, "setImageDrawable");
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        invalidate();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        Log.d(TAG, "setImageBitmap");
        super.setImageBitmap(bm);
        mBitmap = bm;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas); 一定要注释掉,否则圆形图像没法生效
        if (mWidth > 0 && mHeight > 0) {
            Bitmap bitmap = createCircleBitmapForSRC_IN(canvas);
            if (bitmap != null) {
                Log.d(TAG, "onDraw bitmap is not null");
                canvas.drawBitmap(bitmap, 0, 0, new Paint());
            } else {
                Log.d(TAG, "onDraw bitmap is null");
            }
        }
    }

    /**
     * 创建圆形图像
     * @param targetCanvs
     * @return
     */
    private Bitmap createCircleBitmapForSRC_IN(Canvas targetCanvs) {
        //创建一个和图片大小差不多的正方形矩阵
        int size = Math.min(mWidth, mHeight);

        Bitmap newBitmap = null;
        if (mBitmap != null) {
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            // 对bitmap进行缩放,缩放到指定view的大小
            Matrix matrix = new Matrix();
            matrix.postScale((float) mWidth / width, (float) mHeight / height);
            newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width,
                    height, matrix, true);
        } else {
            newBitmap = createRandomMaskBitmap(size);
        }

        if (newBitmap == null) {
            return null;
        }

        int center = size / 2;
        Paint paint = new Paint();
        boolean isDrawBorder = drawCircleBorder(targetCanvs, center, paint);

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (isDrawBorder) {
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            canvas.scale(DEFAULT_SCALE, DEFAULT_SCALE, center, center);
        }

        //在矩阵中心画圆，与矩阵的四边相切
        canvas.drawCircle(center, center, center, paint);
        //设置Xfermode为SRC_IN
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //绘制图片
        canvas.drawBitmap(newBitmap, 0, 0, paint);
        return bitmap;
    }

    /**
     * 获取bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * 绘制边界圆
     *
     * @param canvas
     * @param size
     * @param paint
     * @return
     */
    private boolean drawCircleBorder(Canvas canvas, int size, Paint paint) {
        if (mStrokeWidth > 0) {
            paint.setAntiAlias(true);
            paint.setColor(mStrokeColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(mStrokeWidth);
            canvas.drawCircle(size, size, size, paint);
            return true;
        }
        return false;
    }

    /**
     * 创建随机背景
     * @param size
     * @return
     */
    private Bitmap createRandomMaskBitmap(int size) {
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);// 抗锯齿
        paint.setFilterBitmap(true);
        paint.setColor(mBackground);

        int center = size / 2;//获取画布的中心位置

        //创建canvas对象，绘制随机背景
        Canvas canvas = new Canvas(output);
        canvas.drawCircle(center, center, center, paint);

        //绘制随机背景上的文字
        setText(canvas, size, paint);

        return output;
    }

    /**
     * 绘制文本
     * @param canvas
     * @param size
     * @param paint
     */
    private void setText(Canvas canvas, int size, Paint paint) {
        Rect targetRect = new Rect(0, 0, size, size);
        //设置绘制文本字体的颜色
        paint.setColor(Color.WHITE);
        //设置绘制文本的大小
        paint.setTextSize(mTextSize);
        //获取文本展示的居中位置
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(mText, targetRect.centerX(), baseline, paint);
    }
}
