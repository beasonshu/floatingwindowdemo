package com.windinwork.floatingwindowdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

public class FloatingWindow extends FrameLayout {

    private ViewDragHelper mHelper;
    private Context mContext;
    private float mParentWidth;
    private float mParentHeight;
    private View mView;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mMoveMotionX;
    private float mMoveMotionY;
    public FloatingWindow(Context context) {
        super(context);
        init();
    }

    public FloatingWindow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingWindow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FloatingWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mContext = getContext();
        mView = new FloatingBall(mContext);
        LayoutParams lp = new LayoutParams(dp2px(mContext, 64), dp2px(mContext, 64));
        lp.topMargin = dp2px(mContext, 256);
        lp.gravity = Gravity.END;
        addView(mView, lp);
        mHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == mView;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            //手指释放的时候调用
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                if (releasedChild == mView) {
                    int y = mView.getTop();
                    int x = mView.getLeft();
                    if ((x + mView.getWidth() / 2) < mParentWidth / 2.0) {
                        x = 0;
                    } else if ((mParentHeight - mView.getHeight() - mView.getTop()) < 200 && (mView.getLeft() + mView.getWidth()) > mParentWidth) {
                        x = (int) (mParentWidth - mView.getWidth());
                    } else if ((mParentHeight - mView.getHeight() - mView.getTop()) < 200) {
                        x = mView.getLeft();
                    } else if (mView.getTop() < 200 && (mView.getLeft() + mView.getWidth()) > mParentWidth) {
                        x = (int) (mParentWidth - mView.getWidth());
                    } else if (mView.getTop() < 200) {
                        x = mView.getLeft();
                    } else {
                        x = (int) (mParentWidth - mView.getWidth());
                    }
                    if (y > (mParentHeight - mView.getHeight())) {
                        y = (int) (mParentHeight - mView.getHeight());
                    } else if ((mParentHeight - mView.getHeight() - mView.getTop()) < 200) {
                        y = (int) (mParentHeight - mView.getHeight());
                    } else if (mView.getTop() < 200) {
                        y = 0;
                    } else if (y < 0) {
                        y = 0;
                    }
                    mHelper.settleCapturedViewAt(x, y);
                    invalidate();
                }
            }

            // 在边界拖动的时候调用
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {

            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                int width = mView==child?child.getWidth():0;
                return width;
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                int height = mView==child?child.getHeight():0;
                return height;
            }


        });
        mHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    @Override
    public void computeScroll() {
        if (mHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mParentWidth = getWidth();
        mParentHeight = getHeight();

    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return mHelper.shouldInterceptTouchEvent(event);
    }
    //是否向下传递
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (( action != MotionEvent.ACTION_DOWN)) {
            mHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mHelper.cancel();
            return false;
        }

        final float x = ev.getX();
        final float y = ev.getY();
        boolean interceptTap = false;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = x;
                mInitialMotionY = y;
                interceptTap = mHelper.isViewUnder(mView, (int) x, (int) y);
//                Log.i("inter+down","----------");
                break;
            }

            case MotionEvent.ACTION_MOVE: {
//                Log.i("inter+move","----------");
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int slop = mHelper.getTouchSlop();
                /*useless*/
                if (ady > slop && adx > ady) {
                    mHelper.cancel();
                    return false;
                }
                break;
            }
        }
        return mHelper.shouldInterceptTouchEvent(ev) || interceptTap;
    }
    //是否向上传递
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mHelper.processTouchEvent(event);
        final float x = event.getX();
        final float y = event.getY();
        boolean isHeaderViewUnder = mHelper.isViewUnder(mView, (int) x, (int) y);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                Log.i("touch+down","----------");
                mInitialMotionX = x;
                mInitialMotionY = y;
                mMoveMotionX = x;
                mMoveMotionY = y;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                Log.i("touch+move","----------");
                mMoveMotionX = x;
                mMoveMotionY = y;
                break;
            }
            case MotionEvent.ACTION_UP: {
                Log.i("touch+up","----------");
                mMoveMotionX = x;
                mMoveMotionY = y;
                float dex = Math.abs(mInitialMotionX - mMoveMotionX);
                float dey = Math.abs(mInitialMotionY - mMoveMotionY);
                final int slop = mHelper.getTouchSlop();
                if(dex<= slop && dey<= slop){
                    if(isViewUnder(this,mView, (int) x, (int) y)){
                        //callBack.click(mView);
                        showPopWindow();
                    }
                }
                break;
            }
        }

        return isHeaderViewUnder;
    }


    private void showPopWindow(){
        Context mContext = getContext();
//        PopupWindow popupWindow = new PopupWindow(mContext);
//        WebView mwebview = new WebView(mContext);
//        mwebview.loadUrl("http://wwww.baidu.com");
//        popupWindow.setContentView(mwebview);
//        popupWindow.showAsDropDown(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
// Add the buttons
        builder.setPositiveButton("22", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });


// Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    public boolean isViewUnder(View parentView, View view, int x, int y) {
        if (view == null) {
            return false;
        }
        return x >= parentView.getLeft()+view.getLeft()
                && x < parentView.getLeft()+view.getRight()
                && y >= parentView.getTop()+view.getTop()
                && y < parentView.getTop()+view.getBottom();
    }

    private CallBack callBack;
    public void setCallBack(CallBack callBack){
        this.callBack = callBack;
    }


    public interface CallBack {
        void click(View view);
    }
}
