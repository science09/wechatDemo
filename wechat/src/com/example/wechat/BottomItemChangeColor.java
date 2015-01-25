package com.example.wechat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class BottomItemChangeColor extends View {
	private int mColor = 0xFF45C01A;
	private Bitmap mIconBitmap;
	private String mText = "微信";
	private int mTextSize = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
	private Canvas mCanvas;
	private Bitmap mBitmap;
	private Paint mPaint;

	private float mAlpha;
	private Rect mIconRect;
	private Rect mTextBound;
	private Paint mTextPaint;

	public BottomItemChangeColor(Context context) {
		this(context, null);
	}

	public BottomItemChangeColor(Context context, AttributeSet attr) {
		this(context, attr, 0);
	}

	/**
	 * 获取自定义属性的值
	 */
	public BottomItemChangeColor(Context context, AttributeSet attr,
			int defStyleAttr) {
		super(context, attr, defStyleAttr);
		TypedArray typeArray = context.obtainStyledAttributes(attr,
				R.styleable.BottomItemChangeColor);
		int n = typeArray.getIndexCount();
		for (int i = 0; i < n; i++) {
			int type = typeArray.getIndex(i);
			switch (type) {
			case R.styleable.BottomItemChangeColor_icons:
				BitmapDrawable drawable = (BitmapDrawable) typeArray.getDrawable(type);
				mIconBitmap = drawable.getBitmap();
				break;
			case R.styleable.BottomItemChangeColor_color:
				mColor = typeArray.getColor(type, 0xFF45C01A);
				break;
			case R.styleable.BottomItemChangeColor_text:
				mText = typeArray.getString(type);
				break;
			case R.styleable.BottomItemChangeColor_text_size:
				mTextSize = (int) typeArray.getDimension(type, TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
								getResources().getDisplayMetrics()));
				break;
			}
		}
		typeArray.recycle();

		mTextBound = new Rect();
		mTextPaint = new Paint();
		mTextPaint.setTextSize(mTextSize);
		mTextPaint.setColor(0xff555555);
		mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int iconWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
				- getPaddingRight(), getMeasuredHeight() - getPaddingTop()
				- getPaddingBottom() - mTextBound.height());

		int left = getMeasuredWidth() / 2 - iconWidth / 2;
		int top = getMeasuredHeight() / 2 - (mTextBound.height() + iconWidth)
				/ 2;
		mIconRect = new Rect(left, top, left + iconWidth, top + iconWidth);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
		int alpha = (int) Math.ceil(255 * mAlpha);
		// 内存准备mBitmap, setAlpha, 纯色，xfermode, 图标
		setupTargetBitmap(alpha);
		// 1.绘制源文本,2. 绘制变色文本
		drawSourceText(canvas, alpha);
		drawTargetText(canvas, alpha);
		canvas.drawBitmap(mBitmap, 0, 0, null);

	}

	private void drawTargetText(Canvas canvas, int alpha) {
		mTextPaint.setColor(mColor);
		mTextPaint.setAlpha(alpha);
		int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
		int y = mIconRect.bottom + mTextBound.height();
		canvas.drawText(mText, x, y, mTextPaint);
	}

	private void drawSourceText(Canvas canvas, int alpha) {
		mTextPaint.setColor(0xff333333);
		mTextPaint.setAlpha(255 - alpha);
		int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
		int y = mIconRect.bottom + mTextBound.height();
		canvas.drawText(mText, x, y, mTextPaint);
	}

	private void setupTargetBitmap(int alpha) {
		mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
				Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPaint = new Paint();
		mPaint.setColor(mColor);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setAlpha(alpha);
		mCanvas.drawRect(mIconRect, mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mPaint.setAlpha(255);
		mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
	}

	public void setIconAlpha(float alpha) {
		this.mAlpha = alpha;
		invalidateView();
	}

	/**
	 * 重绘
	 */
	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}

	private static final String INSTANCE_STATUS = "instance_status";
	private static final String STATUS_ALPHA = "status_alpha";

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
		bundle.putFloat(STATUS_ALPHA, mAlpha);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			mAlpha = bundle.getFloat(STATUS_ALPHA);
			super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
			return;
		}
		super.onRestoreInstanceState(state);
	}
}
