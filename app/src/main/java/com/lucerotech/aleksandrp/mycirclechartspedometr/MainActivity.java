package com.lucerotech.aleksandrp.mycirclechartspedometr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // for check params value number picker weight
    public static final int PICKER_WATER = 1;
    public static final int PICKER_CALORIES = 2;
    public static final int PICKER_WEIGHT = 3;
    public static final int PICKER_FAT_LEVEL = 4;
    public static final int PICKER_MUSCLE_MASS = 5;
    public static final int PICKER_BONE_MASS = 6;
    public static final int PICKER_FAT = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircleBackground viewById = (CircleBackground) findViewById(R.id.view);
        viewById.setDataInChart(56.5f, 0, 0, PICKER_WEIGHT);
    }
}
