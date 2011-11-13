package org.team1337.checkers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CheckersView extends View {
	final String TAG = CheckersView.class.getSimpleName();
	FieldValue[][] fields = new FieldValue[8][8];
	
	static final int BOARD_ROWS = 8;
	static final int BOARD_COLS = 8;
	static float MARGIN_X = (float) 1 / ( BOARD_COLS + 2 );
	static float CELL_X = MARGIN_X;
	static float MARGIN_Y = (float) 1 / ( BOARD_ROWS + 2 );
	static float CELL_Y = MARGIN_Y;
	
	FieldValue currentPlayer = FieldValue.O;
	boolean isGame = true;
	FieldValue winner = null;
	
	public CheckersView(Context context) {
		super(context);
	}
	
	public CheckersView(Context context, AttributeSet attrs) {
		super(context, attrs);
		for(FieldValue[] row: fields)
			for(int col=0; col<8; col++)
				row[col] = FieldValue.EMPTY;
		
		setField(3, 4, FieldValue.X);
		setField(4, 3, FieldValue.X);
		setField(4, 4, FieldValue.O);
		
	}

	static Paint paint;
	static{
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.rgb(255, 255, 255));
		paint.setTextSize( (float) 1 / (Math.max(BOARD_ROWS, BOARD_COLS) + 2) );
	}
	
	private FieldValue getField(int row, int col) {
		return fields[row][col];
	}
	private void setField(int row, int col, FieldValue value) {
		fields[row][col] = value;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG, String.format("onMeasure: %d %d", widthMeasureSpec, heightMeasureSpec));
		
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		Log.d(TAG, String.format("parentWidth: %d, parentHeight: %d", parentWidth, parentHeight));
		
		this.setMeasuredDimension(parentWidth, parentHeight);
		//this.setLayoutParams(new .LayoutParams(parentWidth,parentHeight));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		float scale = (float) Math.min( getWidth(), getHeight() ); //norm to [0.0, 1.0] coords
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.scale(scale, scale);

		drawField(canvas);
		drawResult(canvas);
		
		canvas.restore();
	}
	
	private void drawField(Canvas canvas) {
		for( int row = 0; row < BOARD_ROWS; row++ ) //draw board, leaving margins on the sides of the board
			for( int col = 0; col < BOARD_COLS; col++ ) {
				canvas.drawText(
						getField(row, col).getDisplayValue(), 
						MARGIN_X + col * CELL_X, 
						MARGIN_Y + row * CELL_Y + CELL_Y, //one more cellY since has to calculate the bottom corner 
						paint);
			}
	}
	
	private void drawResult(Canvas canvas) {
		if( !isGame ) {
			String msg;
			if( winner != null )
				msg = winner.getDisplayValue() + " won!";
			else 
				msg = "Tie!";
			float width = paint.measureText(msg);
			canvas.drawText(msg, 0.3f, 1.0f, paint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float scale = (float) Math.min( getWidth(), getHeight() );

		float x = event.getX()/scale; //norm to [0.0, 1.0] coords
		float y = event.getY()/scale;
		Log.d(TAG, String.format("touch event: x:%.2f, y: %.2f", x, y));
		
		int col = -1;
		int row = -1;
		if( x >= 0 + MARGIN_X &&
				x < 1.0f - MARGIN_X ) //if event is in board boundaries,
			col =  (int) ((x - MARGIN_X) / CELL_X ); //calculate column
		if( y >= 0 + MARGIN_Y &&
				y < 1.0f - MARGIN_Y )
			row = (int) ((y - MARGIN_Y) / CELL_Y );
		
		//TODO: only accept one change per touch - detect touch end
		if( col >= 0 && row >= 0 &&
				isGame &&
				FieldValue.EMPTY.equals( getField(row, col) ) ) { //in boundaries and cell was empty
			setField(row, col, currentPlayer);
		
			currentPlayer = nextPlayer(currentPlayer);
			checkGoals();
		
			this.invalidate(); //redraw all
		}
		return true;
	}
	
	private FieldValue nextPlayer( FieldValue current ) {
		if( FieldValue.O.equals(current))
			return FieldValue.X;
		return FieldValue.O;
	}
	
	private void checkGoals() {
		boolean isEmptyCell = false;
		for( int row = 0; row < BOARD_ROWS; row++ )
			for( int col = 0; col < BOARD_COLS; col++ ) {
				FieldValue player = getField(row, col);
				if( player.equals(FieldValue.EMPTY) )
					isEmptyCell = true;
				else if( isWinInDirection( player, col, row, 5, 1, 0 ) || //checked east
						isWinInDirection( player, col, row, 5, 0, 1 ) || //checked south
						isWinInDirection( player, col, row, 5, -1, 1 ) || //checked southwest
						isWinInDirection( player, col, row, 5, 1, 1 ) ) { //checked southeast
					Log.d(TAG, String.format("Win detected at x: %d, y: %d", col, row));
					isGame = false;
					winner = player;
				}
			}
		if( !isEmptyCell )
			isGame = false; //no more empty cells
	}
	
	private boolean fieldOutOfBounds( int x, int y ) {
		if( x < 0 || x >= BOARD_COLS ) return true;
		if( y < 0 || y >= BOARD_ROWS ) return true;
		return false;
	}
	
	private boolean isWinInDirection( FieldValue player, int x, int y, int count, int xStep, int yStep ) {
		if( count == 1 ) 
			return true;
		if( fieldOutOfBounds(x + xStep, y + yStep) || !player.equals(getField( y + yStep,x + xStep )) )
			return false;
		return isWinInDirection( player, x + xStep, y + yStep, count - 1, xStep, yStep);
	}
	
}
