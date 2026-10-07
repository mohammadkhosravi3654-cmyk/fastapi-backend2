package com.khosravi.assistant;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

public class AssistantAccessibilityService extends AccessibilityService {
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            // The service is intentionally passive in v2: it observes the active UI.
            // Action methods can be added here without bypassing Android permissions.
        }
    }
    @Override public void onInterrupt() {}
}
