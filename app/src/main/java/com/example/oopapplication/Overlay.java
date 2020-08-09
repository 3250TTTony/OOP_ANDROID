package com.example.oopapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.inline.InlineContentView;

import androidx.annotation.NonNull;

public class Overlay extends SurfaceView implements SurfaceHolder.Callback, Runnable  {

    // SurfaceHolder
    private SurfaceHolder mHolder;
    // 用于绘图的Canvas
    private Canvas mCanvas;
    // 子线程标志位
    private boolean mIsDrawing;

    public Overlay(Context context) {
        super(context);
        initView();
    }

    public Overlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Overlay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
        System.out.println ("OVERLAY CREATEEEEEE");
        //mHolder.setFormat(PixelFormat.OPAQUE);
    }



    @Override
    public void run() {
        System.out.println (mIsDrawing);
        while(mIsDrawing){

            draw();
        }
    }

    public void draw(){
        try{
            mCanvas = mHolder.lockCanvas();
            mCanvas.drawARGB(255,150,150,10);

        } catch (Exception e){
        } finally {
            if(mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);
            System.out.println ("ADDING FROM USER");
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        mIsDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mIsDrawing = true;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        mIsDrawing = false;
    }
}
