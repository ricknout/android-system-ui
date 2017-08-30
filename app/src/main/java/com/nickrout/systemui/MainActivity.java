package com.nickrout.systemui;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.support.annotation.NonNull;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String DYNAMIC_SHORTCUT_ID = "dynamic_shortcut";

    private ImageView statusImageView;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusImageView = findViewById(R.id.iv_status);
        statusTextView = findViewById(R.id.tv_status);
        initialiseStatus();
    }

    private void initialiseStatus() {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        // First check pinned shortcuts
        for (ShortcutInfo shortcutInfo : shortcutManager.getPinnedShortcuts()) {
            if (DYNAMIC_SHORTCUT_ID.equals(shortcutInfo.getId())) {
                if (shortcutInfo.isEnabled()) {
                    setStatus(DynamicShortcutStatus.ENABLED);
                } else {
                    setStatus(DynamicShortcutStatus.DISABLED);
                }
                return;
            }
        }
        // Then check dynamic shortcuts
        for (ShortcutInfo shortcutInfo : shortcutManager.getDynamicShortcuts()) {
            if (DYNAMIC_SHORTCUT_ID.equals(shortcutInfo.getId())) {
                if (shortcutInfo.isEnabled()) {
                    setStatus(DynamicShortcutStatus.ENABLED);
                } else {
                    setStatus(DynamicShortcutStatus.DISABLED);
                }
                return;
            }
        }
        setStatus(DynamicShortcutStatus.NONE);
    }

    enum DynamicShortcutStatus {
        NONE,
        DISABLED,
        ENABLED
    }

    private void setStatus(@NonNull DynamicShortcutStatus status) {
        statusTextView.setText(status.name());
        switch (status) {
            case NONE:
                statusImageView.setImageResource(R.drawable.ic_none_black_48dp);
                break;
            case DISABLED:
                statusImageView.setImageResource(R.drawable.ic_disabled_black_48dp);
                break;
            case ENABLED:
                statusImageView.setImageResource(R.drawable.ic_enabled_black_48dp);
                break;
        }
    }

    public void addDynamicShortcut(View view) {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        Intent dynamicShortcutIntent = new Intent(this, ShortcutActivity.class);
        dynamicShortcutIntent.setAction(Intent.ACTION_VIEW);
        ShortcutInfo dynamicShortcut = new ShortcutInfo.Builder(this, DYNAMIC_SHORTCUT_ID)
                .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_dynamic))
                .setShortLabel(getString(R.string.shortcut_label_short_dynamic))
                .setLongLabel(getString(R.string.shortcut_label_long_dynamic))
                .setRank(1)
                .setIntent(dynamicShortcutIntent)
                .build();
        shortcutManager.setDynamicShortcuts(Arrays.asList(dynamicShortcut));
        Toast.makeText(this, R.string.toast_message_added, Toast.LENGTH_SHORT).show();
        setStatus(DynamicShortcutStatus.ENABLED);
    }

    public void removeDynamicShortcut(View view) {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        shortcutManager.removeDynamicShortcuts(Arrays.asList(DYNAMIC_SHORTCUT_ID));
        Toast.makeText(this, R.string.toast_message_removed, Toast.LENGTH_SHORT).show();
        setStatus(DynamicShortcutStatus.NONE);
    }

    public void disableDynamicShortcut(View view) {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        shortcutManager.disableShortcuts(Arrays.asList(DYNAMIC_SHORTCUT_ID),
                getString(R.string.shortcut_message_disabled));
        Toast.makeText(this, R.string.toast_message_disabled, Toast.LENGTH_SHORT).show();
        setStatus(DynamicShortcutStatus.DISABLED);
    }

    public void pinDynamicShortcut(View view) {
        // Use compat versions to support pinning back to API 25
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(this)) {
            Intent dynamicShortcutIntent = new Intent(this, ShortcutActivity.class);
            dynamicShortcutIntent.setAction(Intent.ACTION_VIEW);
            ShortcutInfoCompat dynamicShortcut =
                    new ShortcutInfoCompat.Builder(this, DYNAMIC_SHORTCUT_ID)
                            .setShortLabel(getString(R.string.shortcut_label_short_dynamic))
                            .setLongLabel(getString(R.string.shortcut_label_long_dynamic))
                            .setIntent(dynamicShortcutIntent)
                            .build();
            ShortcutManagerCompat.requestPinShortcut(this, dynamicShortcut, null);
        } else {
            Toast.makeText(this, R.string.toast_error_pinning, Toast.LENGTH_SHORT).show();
        }
    }
}
