package com.example.birds;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;


public class GameView extends View{
    private boolean afterEnd;
    private Sprite playerBird;
    private Sprite enemyBird;
    private Sprite boi, pause;
    private int add = 0;
    private int viewWidth;
    private int viewHeight;
    private int points = 2;
    private int lvl = 0;
    private final int timerInterval = 30;
    Timer t = new Timer();


    public GameView(Context context) throws InterruptedException {
        super(context);
        newGame();
        t.start();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(250, 127, 199, 255);
        playerBird.draw(canvas);
        enemyBird.draw(canvas);
        boi.draw(canvas);
        pause.draw(canvas);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(55.0f);
        p.setColor(Color.WHITE);
        canvas.drawText("points: " + points + "", viewWidth - 300, 70, p);
        canvas.drawText("lvl: " + lvl + "", 20, 70, p);

    }

    protected void update() throws InterruptedException {
        playerBird.update(timerInterval);
        enemyBird.update(timerInterval+add);
        boi.update(10);

        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVy(-playerBird.getVy());
            points--;
        }
        else if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVy(-playerBird.getVy());
            points--;
        }

        if (enemyBird.getX() < - enemyBird.getFrameWidth()) {
            teleportEnemy();
            points +=10;
        }

        if (enemyBird.intersect(playerBird)) {
            teleportEnemy ();
            points -= 40;
        }
        //баба вышла за рамки
        if (boi.getX() < - boi.getFrameWidth()) {
            teleportGirl();
        }
        //когда баба стукнется в игрока
        if (boi.intersect(playerBird)) {
            teleportGirl();
            points += 30;
        }
        //условие для повышения лвла
        if (points>=150){
            newLvl();
        }

        if (lvl==3){
            setPL(0,0);
            t.cancel();
            showMessage(getContext(), "Ты победил! Нажми ок чтоб начать по новой");
        }
        //тут типа окно для окончания игры
        if (points<=-50){
            setPL(0,0);
            t.cancel();
            showMessage(getContext(), "Ты проиграл! Нажми ок чтоб начать по новой");
        }
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN)  {
            if (event.getX() >= pause.getMovingBoxRect().left-20 &&
                    event.getX() <= pause.getBoundingBoxRect().right+20 &&
                    event.getY() <= pause.getBoundingBoxRect().bottom+20 &&
                    event.getY() >= pause.getBoundingBoxRect().top-20){
                t.cancel();
                showPause(getContext(),"Пауза. Ок шоб возобновить");
            }
            else if (event.getX() >= boi.getMovingBoxRect().left &&
                    event.getX() <= boi.getBoundingBoxRect().right &&
                    event.getY() <= boi.getBoundingBoxRect().bottom &&
                    event.getY() >= boi.getBoundingBoxRect().top){
                points+=50;
                teleportGirl();
            }
            else if (event.getY() < playerBird.getBoundingBoxRect().top) {
                playerBird.setVy(-100);
                points--;
            }
            else if (event.getY() > (playerBird.getBoundingBoxRect().bottom)) {
                playerBird.setVy(100);
                points--;
            }

        }

        return true;
    }


    private void teleportEnemy () {
        enemyBird.setX(viewWidth + Math.random() * 500);
        enemyBird.setY(Math.random() * (viewHeight - enemyBird.getFrameHeight()));
    }
    private void teleportGirl () {
        boi.setX(viewWidth + Math.random() * 500);
        boi.setY(Math.random() * (viewHeight - boi.getFrameHeight()));
    }
    private void newLvl(){
        lvl++;
        points=0;
        add+=10;
    }
    public void newGame() throws InterruptedException {
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        int w = b.getWidth()/5;
        int h = b.getHeight()/3;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i ==0 && j == 0) {
                    continue;
                }
                if (i ==2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        w = b.getWidth()/5;
        h = b.getHeight()/3;
        firstFrame = new Rect(4*w, 0, 5*w, h);

        enemyBird = new Sprite(2000, 250, -300, 0, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {

                if (i ==0 && j == 4) {
                    continue;
                }

                if (i ==2 && j == 0) {
                    continue;
                }

                enemyBird.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
            }
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.girl);
        w = b.getWidth()/6;
        h = b.getHeight();
        firstFrame = new Rect(5*w, 0, 6*w, h);

        boi = new Sprite(2000, 550, -800, 0, firstFrame, b);

        for (int i = 0; i < 6; i++) {
            boi.addFrame(new Rect((i+1)*w, 0, (i+1)*w+w, h));
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        w = b.getWidth();
        h = b.getHeight();
        firstFrame = new Rect(0, 0, w, h);

        pause = new Sprite(500, 20, 0, 0, firstFrame, b);
    }

    public void setPL(int p, int l){
        points = p;
        lvl = l;
    }

    public void showMessage(Context context, String message)
    {

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Конец игры");
        //dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("OkAy",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            newGame();
                            t.start();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
        dlgAlert.create().show();
    }
    public void showPause(Context context, String message)
    {

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Конец игры");
        //dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("OkAy",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        t.start();
                    }
                });
        dlgAlert.create().show();
    }


    class Timer extends CountDownTimer {

        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            try {
                update ();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {

        }
    }
}
