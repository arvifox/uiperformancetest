package com.arvifox.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.ViewTreeObserver;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private Handler mHandler;
    private int i = 0;
    private volatile StringBuffer mStringBuffer = new StringBuffer();
    private int letter_count;
    private int line_count;

    /**
     * количество символов, помещающихся на экране.
     * чтобы не потреблять лишнюю память.
     */
    private int symbols_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.pitext);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > symbols_count * 2) {
                    editable.delete(0, symbols_count);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler = new Handler(getMainLooper());
        int letterwidth = (int) mTextView.getPaint().measureText("0");
        int screenwidth = getResources().getDisplayMetrics().widthPixels;
        letter_count = screenwidth / letterwidth;
        line_count = getResources().getDisplayMetrics().heightPixels / mTextView.getLineHeight();
        symbols_count = line_count * letter_count;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    mStringBuffer.append(Utils.getDigit(++i));
                }
            }
        }).start();
        mHandler.postDelayed(task, 300);
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            // из большого количества сгенерированных символов
            // оставляем только те, что будут видны на экране
            if (mStringBuffer.length() > symbols_count) {
                mStringBuffer.delete(0, mStringBuffer.length() - symbols_count);
            }
            // добавляем к вьюхе
            mTextView.append(mStringBuffer.toString());
            // буффер очищаем
            mStringBuffer.setLength(0);
            // откладываем заполнение еще на полсекунды
            mHandler.postDelayed(this, 500);
        }
    };

    private void calcline() {
        mTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int h = mTextView.getHeight();
                int y = mTextView.getScrollY();
                Layout layout = mTextView.getLayout();
                line_count = layout.getLineForVertical(h + y) - layout.getLineForVertical(y);
                symbols_count = line_count * letter_count;
            }
        });
    }
}
