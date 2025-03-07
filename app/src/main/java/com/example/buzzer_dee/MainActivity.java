package com.example.buzzer_dee;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final String PWM_EXPORT = "/sys/class/pwm/pwmchip0/device/pwm/pwmchip0/export";
    private final String PWM_PERIOD = "/sys/class/pwm/pwmchip0/device/pwm/pwmchip0/pwm2/period";
    private final String PWM_DUTY_CYCLE = "/sys/class/pwm/pwmchip0/device/pwm/pwmchip0/pwm2/duty_cycle";
    private final String PWM_ENABLE = "/sys/class/pwm/pwmchip0/device/pwm/pwmchip0/pwm2/enable";

    private final Handler handler = new Handler(Looper.getMainLooper()); // FIXED: Declare the handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnInit = findViewById(R.id.btn_init);
        Button btnOn = findViewById(R.id.btn_on);
        Button btnOff = findViewById(R.id.btn_off);
        Button btnMode = findViewById(R.id.btn_mode);
        Button btnKeyBeep = findViewById(R.id.btn_key_beep);

        btnInit.setOnClickListener(v -> {
            executeShellCommand("echo 2 > " + PWM_EXPORT);
            executeShellCommand("echo 250000 > " + PWM_PERIOD);
            executeShellCommand("echo 125000 > " + PWM_DUTY_CYCLE);
            showToast("PWM Initialized");
        });

        btnKeyBeep.setOnClickListener(v -> {
            executeShellCommand("echo 1 > " + PWM_ENABLE);
            showToast("Key Beep");

            // FIXED: Use postDelayed after initializing handler
            handler.postDelayed(() -> {
                executeShellCommand("echo 0 > " + PWM_ENABLE);
            }, 100); // 100ms beep duration
        });

        btnOn.setOnClickListener(v -> {
            executeShellCommand("echo 1 > " + PWM_ENABLE);
            showToast("Buzzer On");
        });

        btnOff.setOnClickListener(v -> {
            executeShellCommand("echo 0 > " + PWM_ENABLE);
            showToast("Buzzer Off");
        });

        btnMode.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                executeShellCommand("echo 1 > " + PWM_ENABLE);
                showToast("Buzzer Beep Start");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                executeShellCommand("echo 0 > " + PWM_ENABLE);
                showToast("Buzzer Beep Stop");
            }
            return true;
        });
    }

    private void executeShellCommand(String command) {
        try {
            Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Error executing command");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
