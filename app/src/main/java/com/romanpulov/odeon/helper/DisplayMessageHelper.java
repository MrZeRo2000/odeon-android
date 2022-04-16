package com.romanpulov.odeon.helper;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.romanpulov.odeon.R;

import java.util.HashMap;
import java.util.Map;

public class DisplayMessageHelper {

    private static enum MessageType {
        MT_INFO,
        MT_ERROR
    }

    private static final Map<MessageType, Integer> MESSAGE_COLORS = new HashMap<>();

    static {
        MESSAGE_COLORS.put(MessageType.MT_INFO, R.color.white);
        MESSAGE_COLORS.put(MessageType.MT_ERROR, R.color.red);
    }

    private static void displayMessage(
            @Nullable Activity activity,
            @Nullable View view,
            MessageType messageType,
            @NonNull CharSequence text
    ) {
        View snackBarView = activity == null ? view : activity.findViewById(android.R.id.content);

        if (snackBarView != null) {
            Integer colorResource = MESSAGE_COLORS.get(messageType);
            if (colorResource != null) {
                int textColor = snackBarView.getContext().getResources().getColor(colorResource);
                Snackbar.make(snackBarView, text, BaseTransientBottomBar.LENGTH_SHORT)
                        .setTextColor(textColor)
                        .show();
            }
        }
    }

    public static void displayInfoMessage(@NonNull View view, @NonNull CharSequence text) {
        displayMessage(null, view, MessageType.MT_INFO, text);

    }

    public static void displayInfoMessage(@NonNull Activity activity, @NonNull CharSequence text) {
        displayMessage(activity, null, MessageType.MT_INFO, text);
    }

    public static void displayErrorMessage(@NonNull View view, @NonNull CharSequence text) {
        displayMessage(null, view, MessageType.MT_ERROR, text);

    }

    public static void displayErrorMessage(@NonNull Activity activity, @NonNull CharSequence text) {
        displayMessage(activity, null, MessageType.MT_ERROR, text);
    }
}