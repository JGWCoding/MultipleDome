package ziweiyang.toppine.com.oschinadome.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 用户第一次进来,提示用户进行订阅频道
 * 这个为一开始进来显示 "+" (如果是用户第一次使用进行一个展示图片提示)
 */

public class RippleIntroView extends RelativeLayout implements Runnable {

    private int mMaxRadius = 70;
    private int mInterval = 20;
    private int count = 0;

    private Bitmap mCacheBitmap;
    private Paint mRipplePaint;
    private Paint mCirclePaint;
    private Path mArcPath;

    public RippleIntroView(Context context) {
        this(context, null);
    }

    public RippleIntroView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleIntroView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRipplePaint = new Paint();
        mRipplePaint.setAntiAlias(true);
        mRipplePaint.setStyle(Paint.Style.STROKE);
        mRipplePaint.setColor(Color.WHITE);
        mRipplePaint.setStrokeWidth(2.f);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);    //设置填充
        mCirclePaint.setColor(Color.WHITE);

        mArcPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mCacheBitmap != null) {
            mCacheBitmap.recycle();
            mCacheBitmap = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {  //直接在自己的canvas里面 画
        // 我知道直接getChildAt(int)很挫，但是我就是要这么简单粗暴！
        View mPlusChild = getChildAt(0);
        View mRefsChild = getChildAt(1);
        if (mPlusChild == null || mRefsChild == null) return;

        final int pw = mPlusChild.getWidth();
        final int ph = mPlusChild.getHeight();

        final int fw = mRefsChild.getWidth();
        final int fh = mRefsChild.getHeight();

        if (pw == 0 || ph == 0) return;

        final float px = mPlusChild.getX() + pw / 2;
        final float py = mPlusChild.getY() + ph / 2;
        final float fx = mRefsChild.getX();
        final float fy = mRefsChild.getY();
        final int rw = pw / 2;
        final int rh = ph / 2;

        if (mCacheBitmap == null) {
            mCacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(mCacheBitmap);//背景有点半透明黑
            super.onDraw(cv);

            mArcPath.reset();
            mArcPath.moveTo(px, py + rh + mInterval);   //移动到add图片下面
            mArcPath.quadTo(px, fy - mInterval, fx + fw * 0.618f, fy - mInterval);//贝塞尔曲线
            mRipplePaint.setAlpha(255);//设置不透明笔
            cv.drawPath(mArcPath, mRipplePaint);    //画贝塞尔曲线
            cv.drawCircle(px, py + rh + mInterval, 6, mCirclePaint);    //画曲线上的圆
        }
        canvas.drawBitmap(mCacheBitmap, 0, 0, mCirclePaint);

        int save = canvas.save();
        for (int step = count; step <= mMaxRadius; step += mInterval) {
            mRipplePaint.setAlpha(255 * (mMaxRadius - step) / mMaxRadius);
            canvas.drawCircle(px, py, (float) (rw + step), mRipplePaint);   //画圆 --> 形成波浪
        }
        canvas.restoreToCount(save);
        postDelayed(this, 80);  //80ms重画一次  run
    }

    @Override
    public void run() {
        removeCallbacks(this);
        count += 2;
        count %= mInterval;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCacheBitmap != null) {
            mCacheBitmap.recycle();
            mCacheBitmap = null;
        }
    }
}
