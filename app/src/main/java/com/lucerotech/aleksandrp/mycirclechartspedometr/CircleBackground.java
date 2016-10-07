package com.lucerotech.aleksandrp.mycirclechartspedometr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by AleksandrP on 06.10.2016.
 */

public class CircleBackground extends View
//        implements SensorEventListener
{

    private static final String TAG = CircleBackground.class.getSimpleName();

//    private Handler handler;

    // drawing tools
    private RectF rimRect;
    private Paint rimPaint;
    private Paint rimCirclePaint;

    private RectF faceRect;
    private Bitmap faceTexture;
    private Paint facePaint;
    private Paint rimShadowPaint;

    // шкала
    private Path textPath;     // text decimal
    private Paint scaleText;
    private Paint scalePaint;
    private RectF scaleRect;        // круг

    // text title
    private Paint titlePaint;
    private Path titlePath;

    private Paint logoPaint;
    private Bitmap logo;
    private Matrix logoMatrix;
    private float logoScale;

    private Paint handPaint;
    private Path handPath;
    private Paint handScrewPaint;

    private Paint backgroundPaint;
    // end drawing tools

    private Bitmap background; // holds the cached static part

    // scale configuration
    private static final int countWicks = 4;        // количество штрихов в разделители
    private static final int totalNicks = 48;       // общее количество разделителей
    private static final float degreesPerNick = 360.0f / totalNicks;        // градусов на1 шьрих
    private static final int centerDegree = totalNicks / 2; // the one in the top center (12 o'clock)
    private static final int minDegrees = 50;
    private static final int maxDegrees = 130;
    private static final int showTextItem = 5;



    // цвет
    private static final int colorWicks = Color.parseColor("#ffffff");  // вся шкала
    private static final int colorTitle = Color.parseColor("#adadae");
    private static final int color = Color.parseColor("#4bf7f9");       // сентер

    // text размер
    private static final float textSize = 0.05f;       // сентер

    // hand dynamics -- all are angular expressed in F degrees
    private boolean handInitialized = false;
    private float handPosition = centerDegree;
    private float handTarget = centerDegree;
    private float handVelocity = 0.0f;
    private float handAcceleration = 0.0f;
    private long lastHandMoveTime = -1L;


    public CircleBackground(Context context) {
        super(context);
        init();
    }

    public CircleBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleBackground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        attachToSensor();
    }

    @Override
    protected void onDetachedFromWindow() {
//        detachFromSensor();
        super.onDetachedFromWindow();
    }

//    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//        Bundle bundle = (Bundle) state;
//        Parcelable superState = bundle.getParcelable("superState");
//        super.onRestoreInstanceState(superState);
//
//        handInitialized = bundle.getBoolean("handInitialized");
//        handPosition = bundle.getFloat("handPosition");
//        handTarget = bundle.getFloat("handTarget");
//        handVelocity = bundle.getFloat("handVelocity");
//        handAcceleration = bundle.getFloat("handAcceleration");
//        lastHandMoveTime = bundle.getLong("lastHandMoveTime");
//    }
//
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        Parcelable superState = super.onSaveInstanceState();
//
//        Bundle state = new Bundle();
//        state.putParcelable("superState", superState);
//        state.putBoolean("handInitialized", handInitialized);
//        state.putFloat("handPosition", handPosition);
//        state.putFloat("handTarget", handTarget);
//        state.putFloat("handVelocity", handVelocity);
//        state.putFloat("handAcceleration", handAcceleration);
//        state.putLong("lastHandMoveTime", lastHandMoveTime);
//        return state;
//    }

    private void init() {
//        handler = new Handler();

        initDrawingTools();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setHandTarget(45f);
            }
        }, 500);
    }

    private String getTitle() {
        return "AleksandrP";
    }

//    private SensorManager getSensorManager() {
//        return (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
//    }

//    private void attachToSensor() {
//        SensorManager sensorManager = getSensorManager();
//
//        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_TEMPERATURE);
//        if (sensors.size() > 0) {
//            Sensor sensor = sensors.get(0);
//            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST, handler);
//        } else {
//            Log.e(TAG, "No temperature sensor found");
//        }
//    }

//    private void detachFromSensor() {
//        SensorManager sensorManager = getSensorManager();
//        sensorManager.unregisterListener(this);
//    }

    private void initDrawingTools() {
        rimRect = new RectF(0.1f, 0.1f, 0.9f, 0.9f);

        // the linear gradient is a bit skewed for realism
        rimPaint = new Paint();
        rimPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        rimPaint.setShader(new LinearGradient(0.40f, 0.0f, 0.60f, 1.0f,
                Color.rgb(0xf0, 0xf5, 0xf0),
                Color.rgb(0x30, 0x31, 0x30),
                Shader.TileMode.CLAMP));

        rimCirclePaint = new Paint();
        rimCirclePaint.setAntiAlias(true);
        rimCirclePaint.setStyle(Paint.Style.STROKE);
        rimCirclePaint.setColor(Color.argb(0x4f, 0x33, 0x36, 0x33));
        rimCirclePaint.setStrokeWidth(0.005f);

        float rimSize = 0.005f;      // ширина обода
        faceRect = new RectF();
        faceRect.set(rimRect.left + rimSize, rimRect.top + rimSize,
                rimRect.right - rimSize, rimRect.bottom - rimSize);

        faceTexture = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.background_dark);
        BitmapShader paperShader = new BitmapShader(faceTexture,
                Shader.TileMode.MIRROR,
                Shader.TileMode.MIRROR);
        Matrix paperMatrix = new Matrix();
        facePaint = new Paint();
        facePaint.setFilterBitmap(true);
        paperMatrix.setScale(1.0f / faceTexture.getWidth(),
                1.0f / faceTexture.getHeight());
        paperShader.setLocalMatrix(paperMatrix);
        facePaint.setStyle(Paint.Style.FILL);
        facePaint.setShader(paperShader);

        rimShadowPaint = new Paint();
        rimShadowPaint.setShader(new RadialGradient(0.5f, 0.5f, faceRect.width() / 2.0f,
                new int[]{0x00000000, 0x00000500, 0x50000500},
                new float[]{0.96f, 0.96f, 0.99f},
                Shader.TileMode.MIRROR));
        rimShadowPaint.setStyle(Paint.Style.FILL);

        // шкала и текст
        scalePaint = new Paint();
        scalePaint.setStyle(Paint.Style.STROKE);
//        scalePaint.setColor(colorWicks);
        scalePaint.setShader(new LinearGradient(0.0f, 0.0f, 0.750f, 0.750f,
                Color.parseColor("#0b1348"),
                Color.parseColor("#4bf7f9"),
                Shader.TileMode.REPEAT));
        scalePaint.setStrokeWidth(0.005f);
        scalePaint.setAntiAlias(true);

        // текст
        // текст на шкале
        scaleText = new Paint();
        scaleText.setStyle(Paint.Style.FILL);
        scaleText.setColor(colorWicks);
        scaleText.setStrokeWidth(0.002f);
        scaleText.setAntiAlias(true);
        // текст на шкале
        scaleText.setTextSize(textSize);
        scaleText.setTypeface(Typeface.SANS_SERIF);
        scaleText.setTextScaleX(0.5f);
        scaleText.setTextAlign(Paint.Align.CENTER);

        // текста
        textPath = new Path();
        textPath.addArc(new RectF(0.24f, 0.24f, 0.76f, 0.76f), -200.0f, -180.0f);
//        textPath.addArc(new RectF(0.24f, 0.24f, 0.76f, 0.76f), -120.0f, -180.0f);


        // шкала
        // уменьшение квадрата внутри основного квадрата на размер scalePosition
        float scalePosition = 0.1250f;
        scaleRect = new RectF();
        scaleRect.set(faceRect.left + scalePosition, faceRect.top + scalePosition,
                faceRect.right - scalePosition, faceRect.bottom - scalePosition);

        titlePaint = new Paint();
        titlePaint.setColor(colorTitle);
        titlePaint.setAntiAlias(true);
        titlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTextSize(0.05f);
        titlePaint.setTextScaleX(0.8f);

        titlePath = new Path();
        titlePath.addArc(new RectF(0.24f, 0.24f, 0.76f, 0.76f), -180.0f, -180.0f);


        logoPaint = new Paint();
        logoPaint.setFilterBitmap(true);
        logo = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher);
        logoMatrix = new Matrix();
        logoScale = (1.0f / logo.getWidth()) * 0.3f;
        ;
        logoMatrix.setScale(logoScale, logoScale);

        handPaint = new Paint();
        handPaint.setAntiAlias(true);
        handPaint.setColor(color);
        handPaint.setShadowLayer(0.01f, -0.005f, -0.005f, 0x7f000000);
        handPaint.setStyle(Paint.Style.FILL);

        handPath = new Path();
        handPath.moveTo(0.5f, 0.5f + 0.2f);
        handPath.lineTo(0.5f - 0.010f, 0.5f + 0.2f - 0.007f);
        handPath.lineTo(0.5f - 0.002f, 0.5f - 0.32f);
        handPath.lineTo(0.5f + 0.002f, 0.5f - 0.32f);
        handPath.lineTo(0.5f + 0.010f, 0.5f + 0.2f - 0.007f);
        handPath.lineTo(0.5f, 0.5f + 0.2f);
        handPath.addCircle(0.5f, 0.5f, 0.025f, Path.Direction.CW);

        handScrewPaint = new Paint();
        handScrewPaint.setAntiAlias(true);
        handScrewPaint.setColor(color);
        handScrewPaint.setStyle(Paint.Style.FILL);

        backgroundPaint = new Paint();
        backgroundPaint.setFilterBitmap(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
        Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);

        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return size;
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            return getPreferredSize();
        }
    }

    // in case there is no size specified
    private int getPreferredSize() {
        return 300;
    }


    // рисует внешний круг
    private void drawRim(Canvas canvas) {
        // first, draw the metallic body
        canvas.drawOval(rimRect, rimPaint);
        // now the outer rim circle
        canvas.drawOval(rimRect, rimCirclePaint);
    }

    // рисует фон
    private void drawFace(Canvas canvas) {
        canvas.drawOval(faceRect, facePaint); // фон круга до границы
//         draw the inner rim circle
        canvas.drawOval(faceRect, rimCirclePaint); // фон круга до границы
        // draw the rim shadow inside the face
        canvas.drawOval(faceRect, rimShadowPaint);      // рисует внешнюю грань по кругу
    }

    // риссуем шкалу
    private void drawScale(Canvas canvas) {
        canvas.drawOval(scaleRect, scalePaint);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        for (int i = 0; i < totalNicks; ++i) {
            float y1 = scaleRect.top - 0.02f;       // установка растояния начала штриха от круга
            float y2 = y1 - 0.01f; // длина штриха

            canvas.drawLine(0.5f, y1, 0.5f, y2, scalePaint);        // 1 штрих

            // вычисляем значение
            if (i % countWicks == 0) {
                int value = nickToDegree(i);

                if (value >= minDegrees && value <= maxDegrees) {
                    String valueString = Integer.toString(value);
                    canvas.drawTextOnPath(valueString, textPath, 0.0f, 0.1f, scaleText);
                }
            }

            canvas.rotate(degreesPerNick, 0.5f, 0.5f);
        }
        canvas.restore();
    }

    private int nickToDegree(int nick) {
        int i = (maxDegrees - minDegrees) / (showTextItem * countWicks);

        int rawDegree = nick * i ;
//        int rawDegree = ((nick < totalNicks / 2) ? nick : (nick - totalNicks)) * 2;
        int shiftedDegree = rawDegree + centerDegree;
        return shiftedDegree;
    }

    private float degreeToAngle(float degree) {
        return (degree - centerDegree) / 2.0f * degreesPerNick;
    }

    private void drawTitle(Canvas canvas) {
        String title = getTitle();
        canvas.drawTextOnPath(title, titlePath, 0.0f, 0.0f, titlePaint);
    }

    private void drawLogo(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(0.5f - logo.getWidth() * logoScale / 2.0f,
                0.5f - logo.getHeight() * logoScale / 2.0f);

        int color = 0x00000000;
        float position = getRelativeTemperaturePosition();
        if (position < 0) {
            color |= (int) ((0xf0) * -position); // blue
        } else {
            color |= ((int) ((0xf0) * position)) << 16; // red
        }
        //Log.d(TAG, "*** " + Integer.toHexString(color));
        LightingColorFilter logoFilter = new LightingColorFilter(0xff338822, color);
        logoPaint.setColorFilter(logoFilter);

        canvas.drawBitmap(logo, logoMatrix, logoPaint);
        canvas.restore();
    }

    private void drawHand(Canvas canvas) {
        if (handInitialized) {
            float handAngle = degreeToAngle(handPosition);
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(handAngle, 0.5f, 0.5f);
            canvas.drawPath(handPath, handPaint);
            canvas.restore();

            canvas.drawCircle(0.5f, 0.5f, 0.01f, handScrewPaint);
        }
    }

    private void drawBackground(Canvas canvas) {
        if (background == null) {
            Log.w(TAG, "Background not created");
        } else {
            canvas.drawBitmap(background, 0, 0, backgroundPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);

        float scale = (float) getWidth();
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(scale, scale);
//
        drawLogo(canvas);
        drawHand(canvas);
//
        canvas.restore();
//
        if (handNeedsToMove()) {
            moveHand();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "Size changed to " + w + "x" + h);

        regenerateBackground();
    }

    private void regenerateBackground() {
        // free the old bitmap
        if (background != null) {
            background.recycle();
        }

        background = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas backgroundCanvas = new Canvas(background);
        float scale = (float) getWidth();
        backgroundCanvas.scale(scale, scale);

        drawRim(backgroundCanvas);      // внешний круг
        drawFace(backgroundCanvas); // фон до внешнего  круга
        drawScale(backgroundCanvas);
        drawTitle(backgroundCanvas);
    }

    private boolean handNeedsToMove() {
        return Math.abs(handPosition - handTarget) > 0.01f;
    }

    private void moveHand() {
        if (!handNeedsToMove()) {
            return;
        }

        if (lastHandMoveTime != -1L) {
            long currentTime = System.currentTimeMillis();
            float delta = (currentTime - lastHandMoveTime) / 1000.0f;

            float direction = Math.signum(handVelocity);
            if (Math.abs(handVelocity) < 90.0f) {
                handAcceleration = 5.0f * (handTarget - handPosition);
            } else {
                handAcceleration = 0.0f;
            }
            handPosition += handVelocity * delta;
            handVelocity += handAcceleration * delta;
            if ((handTarget - handPosition) * direction < 0.01f * direction) {
                handPosition = handTarget;
                handVelocity = 0.0f;
                handAcceleration = 0.0f;
                lastHandMoveTime = -1L;
            } else {
                lastHandMoveTime = System.currentTimeMillis();
            }
            invalidate();
        } else {
            lastHandMoveTime = System.currentTimeMillis();
            moveHand();
        }
    }

//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }

//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//        if (sensorEvent.values.length > 0) {
//            float temperatureC = sensorEvent.values[0];
//            //Log.i(TAG, "*** Temperature: " + temperatureC);
//
//            float temperatureF = (9.0f / 5.0f) * temperatureC + 32.0f;
//            setHandTarget(temperatureF);
//        } else {
//            Log.w(TAG, "Empty sensor event received");
//        }
//    }

    private float getRelativeTemperaturePosition() {
        if (handPosition < centerDegree) {
            return -(centerDegree - handPosition) / (float) (centerDegree - minDegrees);
        } else {
            return (handPosition - centerDegree) / (float) (maxDegrees - centerDegree);
        }
    }

    // // TODO: 06.10.2016 устанавливает температуру
    private void setHandTarget(float temperature) {
        if (temperature < minDegrees) {
            temperature = minDegrees;
        } else if (temperature > maxDegrees) {
            temperature = maxDegrees;
        }
        handTarget = temperature;
        handInitialized = true;
        invalidate();
    }
}
