package org.team1337.checkers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class CheckersView extends View {
	public CheckersView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	static Paint paint;
	static{
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.rgb(255, 255, 255));
		paint.setTextSize(0.1f);
	}
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		this.setMeasuredDimension(parentWidth, parentHeight);
		//this.setLayoutParams(new .LayoutParams(parentWidth,parentHeight));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		float scale = (float) getWidth();
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);
		canvas.drawText("XOXOXOX", 0.25f, 0.5f, paint);
		canvas.restore();
	}
	

}
