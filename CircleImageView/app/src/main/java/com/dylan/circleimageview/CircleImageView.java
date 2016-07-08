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
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * author   Dylan.zhuang
 * Date:    16/6/30-下午2:43
 */
public class CircleImageView extends ImageView {
    private static final String TAG = "CircleImageView";

    private static final int MAX = 5;

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
    /**
     * 存放多人头像的列表
     */
    private List<CircleImageViewBean> mCircleImageViewBeanList;
    /**
     * 逻辑层
     */
    private CircleImageViewPresenter mPresenter;
    /**
     * 记录头像的个数
     */
    private int mCount;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //#5AC3B2
        int defaultColor = getResources().getColor(R.color.colorGreen);
        //14sp
        int defaultTextSize = getResources().getDimensionPixelSize(R.dimen.dimen_default_text_size);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);
        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_stroke_width, 0);
        mStrokeColor = typedArray.getColor(R.styleable.CircleImageView_stroke_color, defaultColor);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CircleImageView_text_size, defaultTextSize);
        mBackground = typedArray.getColor(R.styleable.CircleImageView_random_backgroud, defaultColor);
        mText = typedArray.getString(R.styleable.CircleImageView_text);
        //一定要记得回收
        typedArray.recycle();

        mPresenter = new CircleImageViewPresenter();
    }

    /**
     * 设置边缘宽度
     *
     * @param width
     */
    public void setStrokeWidth(int width) {
        mStrokeWidth = width;
    }

    /**
     * 设置边缘颜色
     *
     * @param color
     */
    public void setStrokeColor(int color) {
        mStrokeColor = color;
    }

    /**
     * 设置文本大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    /**
     * 设置背景颜色
     *
     * @param background
     */
    public void setBackground(int background) {
        mBackground = background;
    }

    /**
     * 设置文本
     *
     * @param text
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * 绘制随机背景
     */
    public void drawRandomBackground() {
        readyInvalidate(mBitmap, mBackground, mText);
    }

    /**
     * 设置多人头像
     * @param circleImageViewBeanList
     */
    public void setImageBitmaps(List<CircleImageViewBean> circleImageViewBeanList) {
        mCircleImageViewBeanList = circleImageViewBeanList;
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
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        readyInvalidate(mBitmap, mBackground, mText);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        readyInvalidate(mBitmap, mBackground, mText);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        readyInvalidate(mBitmap, mBackground, mText);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //一定要注释掉,否则圆形图像没法生效
//        super.onDraw(canvas);
        if (mWidth > 0 && mHeight > 0) {
//            Bitmap bitmap = createCircleBitmapForSRC_IN(canvas);
//            if (bitmap != null) {
//                canvas.drawBitmap(bitmap, 0, 0, new Paint());
//            }
            if (mCircleImageViewBeanList != null && mCircleImageViewBeanList.size() > 0) {
                mCount = Math.min(mCircleImageViewBeanList.size(), MAX);
                //绘制多人头像
                createCircleBitmap(canvas);
            }
        }
    }

    /**
     * 为调用onDraw之前准备数据
     * @param bitmap
     * @param randomBg
     * @param text
     */
    private void readyInvalidate(Bitmap bitmap, int randomBg, String text) {
        if (mCircleImageViewBeanList == null) {
            mCircleImageViewBeanList = new ArrayList<CircleImageViewBean>();
        }
        mCircleImageViewBeanList.clear();
        CircleImageViewBean bean = new CircleImageViewBean(bitmap, randomBg, text);
        mCircleImageViewBeanList.add(bean);
        invalidate();
    }

    /**
     * 创建圆形位图
     * @param targetCanvas
     */
    private void createCircleBitmap(Canvas targetCanvas) {
        //初始化画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        //获取缩放比例
        float scale = mPresenter.getScale(mCount);
        int viewSize = Math.min(mWidth, mHeight);
        //获取与大圆最上面相切小圆的左上角位置，为了方便计算每个圆具体展示在哪个位置
        float[] topPosition = mPresenter.getTopPosition(viewSize / 2, scale);
        //获取旋转角度
        float[] rotation = mPresenter.getRotation(mCount);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        for (int i = 0; i < mCount; i++) {
            CircleImageViewBean bean = mCircleImageViewBeanList.get(i);
             //对canvas进行状态保存，防止被变换引起状态的改变
            targetCanvas.save();
            float[] radianPosition = mPresenter.getTranslatePosition(viewSize / 2, rotation[i], scale, topPosition[0], topPosition[1]);
            targetCanvas.translate(radianPosition[0], radianPosition[1]);

            Bitmap scaleBitmap = getScaleBitmap(bean, matrix, viewSize, scale);
            if (scaleBitmap == null) {
                return;
            }
            int newSize = Math.min(scaleBitmap.getWidth(), scaleBitmap.getHeight());
            int center = newSize / 2;
            //画边缘圈
            boolean isDrawBorder = drawCircleBorder(targetCanvas, center, paint);
            Bitmap circleBitmap = drawCircleBitmap(scaleBitmap, newSize, isDrawBorder);
            paint.setStyle(Paint.Style.FILL);
            targetCanvas.drawBitmap(circleBitmap, 0, 0, paint);
            targetCanvas.restore();
        }
    }

    /**
     * 获取缩放后的位图
     * @param bean
     * @param matrix
     * @param viewSize
     * @param scale
     * @return
     */
    private Bitmap getScaleBitmap(CircleImageViewBean bean, Matrix matrix, int viewSize, float scale) {
        if (bean == null) {
            return null;
        }
        Bitmap newBitmap = null;
        Bitmap bitmap = bean.getBitmap();
        String text = bean.getText();
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            if (width > 0 && height > 0) {
                //对位图进行缩放
                float scaleX = (float) mWidth / width;
                float scaleY = (float) mHeight / height;
                Matrix bitmapMatrix = new Matrix();
                bitmapMatrix.postScale(scaleX, scaleY);
                bitmapMatrix.postConcat(matrix);

                newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                        height, bitmapMatrix, true);
            }
        } else {
            int size = (int) (viewSize * scale);
            newBitmap = createRandomMaskBitmap(size, scale, text);
        }
        return newBitmap;
    }

    /**
     *
     * @param newBitmap
     * @param newSize
     * @param isDrawBorder
     * @return
     */
    private Bitmap drawCircleBitmap(Bitmap newBitmap, int newSize, boolean isDrawBorder) {
        Bitmap bitmap = Bitmap.createBitmap(newSize, newSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        int center = newSize / 2;
        if (isDrawBorder) {
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
     * 创建圆形图像
     *
     * @param targetCanvas
     * @return
     */
    private Bitmap createCircleBitmapForSRC_IN(Canvas targetCanvas) {
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
            newBitmap = createRandomMaskBitmap(size, 1.0f, mText);
        }

        if (newBitmap == null) {
            return null;
        }

        int center = size / 2;
        Paint paint = new Paint();
        boolean isDrawBorder = drawCircleBorder(targetCanvas, center, paint);

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
            canvas.drawCircle(size, size, size - mStrokeWidth, paint);
            return true;
        }
        return false;
    }

    /**
     * 创建随机背景
     *
     * @param size
     * @return
     */
    private Bitmap createRandomMaskBitmap(int size, float scale, String text) {
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
        setText(canvas, size, paint, scale, text);

        return output;
    }

    /**
     * 绘制文本
     *
     * @param canvas
     * @param size
     * @param paint
     */
    private void setText(Canvas canvas, int size, Paint paint, float scale, String text) {
        Rect targetRect = new Rect(0, 0, size, size);
        //设置绘制文本字体的颜色
        paint.setColor(Color.WHITE);
        //设置绘制文本的大小
        paint.setTextSize(mTextSize * scale);
        //获取文本展示的居中位置
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, targetRect.centerX(), baseline, paint);
    }
}
