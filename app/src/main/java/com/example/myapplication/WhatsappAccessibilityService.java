package com.example.myapplication;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import java.util.List;

public class WhatsappAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent (AccessibilityEvent event) {
        if (getRootInActiveWindow () == null) {
            return;
        }

        AccessibilityNodeInfoCompat rootInActiveWindow = AccessibilityNodeInfoCompat.wrap (getRootInActiveWindow ());
        List<AccessibilityNodeInfoCompat> messageNodeList = rootInActiveWindow.findAccessibilityNodeInfosByViewId ("com.whatsapp:id/entry");

        if (messageNodeList == null || messageNodeList.isEmpty ()) {
            return;
        }

        AccessibilityNodeInfoCompat messageField = messageNodeList.get (0);

        if (messageField.getText () == null || messageField.getText ().length () == 0 || !messageField.getText ().toString ().endsWith ("Sent by MY_APP")) {
            return;
        }

        List<AccessibilityNodeInfoCompat> sendMessageNodeInfoList = rootInActiveWindow.findAccessibilityNodeInfosByViewId ("com.whatsapp:id/send");

        if (sendMessageNodeInfoList == null || sendMessageNodeInfoList.isEmpty ()) {
            return;
        }

        AccessibilityNodeInfoCompat sendMessageButton = sendMessageNodeInfoList.get (0);

        if (!sendMessageButton.isVisibleToUser ()) {
            return;
        }

        sendMessageButton.performAction (AccessibilityNodeInfo.ACTION_CLICK);

        try {
            Thread.sleep (500);
            performGlobalAction (GLOBAL_ACTION_BACK);
            Thread.sleep (500);
            performGlobalAction (GLOBAL_ACTION_BACK);
        } catch (InterruptedException ignored) {

        }
        performGlobalAction (GLOBAL_ACTION_BACK);
        performGlobalAction (GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {
    }
}
