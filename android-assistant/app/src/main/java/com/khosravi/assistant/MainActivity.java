package com.khosravi.assistant;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    private TextToSpeech tts;
    private static final int REQ_AUDIO = 21;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 40, 32, 32);

        TextView title = new TextView(this);
        title.setText("Khosravi Assistant\nFloating AI • Voice • UI Access");
        title.setTextSize(24);
        root.addView(title);

        Button overlay = new Button(this);
        overlay.setText("Enable Floating Assistant");
        root.addView(overlay);
        overlay.setOnClickListener(v -> enableOverlay());

        Button voice = new Button(this);
        voice.setText("Speak a command");
        root.addView(voice);
        voice.setOnClickListener(v -> startVoice());

        Button accessibility = new Button(this);
        accessibility.setText("Enable UI Automation Access");
        root.addView(accessibility);
        accessibility.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));

        Button chat = new Button(this);
        chat.setText("Open ChatGPT");
        root.addView(chat);
        chat.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://chatgpt.com/"))));

        TextView status = new TextView(this);
        status.setText("\nGrant overlay permission, then enable the floating assistant.\nAccessibility is optional and controlled by Android Settings.");
        status.setTextSize(16);
        root.addView(status);
        setContentView(root);

        tts = new TextToSpeech(this, s -> { if (s == TextToSpeech.SUCCESS) tts.setLanguage(new Locale("fa","IR")); });
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQ_AUDIO);
    }

    private void enableOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName())));
            return;
        }
        startService(new Intent(this, FloatingService.class));
    }

    private void startVoice() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR");
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "fa-IR");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "فرمان را بگویید");
        try { startActivityForResult(i, 33); } catch (Exception e) {
            Toast.makeText(this, "Speech recognition is unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 33 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> r = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (r != null && !r.isEmpty()) {
                String text = r.get(0);
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
                if (tts != null) tts.speak("دستور دریافت شد", TextToSpeech.QUEUE_FLUSH, null, "cmd");
            }
        }
    }

    @Override protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}
