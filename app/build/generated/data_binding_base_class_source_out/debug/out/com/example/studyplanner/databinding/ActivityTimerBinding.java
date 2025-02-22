// Generated by view binder compiler. Do not edit!
package com.example.studyplanner.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.studyplanner.R;
import com.google.android.material.button.MaterialButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityTimerBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final NumberPicker hoursPicker;

  @NonNull
  public final NumberPicker minutesPicker;

  @NonNull
  public final MaterialButton startButton;

  @NonNull
  public final MaterialButton stopButton;

  @NonNull
  public final TextView timerTextView;

  private ActivityTimerBinding(@NonNull LinearLayout rootView, @NonNull NumberPicker hoursPicker,
      @NonNull NumberPicker minutesPicker, @NonNull MaterialButton startButton,
      @NonNull MaterialButton stopButton, @NonNull TextView timerTextView) {
    this.rootView = rootView;
    this.hoursPicker = hoursPicker;
    this.minutesPicker = minutesPicker;
    this.startButton = startButton;
    this.stopButton = stopButton;
    this.timerTextView = timerTextView;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityTimerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityTimerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_timer, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityTimerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.hoursPicker;
      NumberPicker hoursPicker = ViewBindings.findChildViewById(rootView, id);
      if (hoursPicker == null) {
        break missingId;
      }

      id = R.id.minutesPicker;
      NumberPicker minutesPicker = ViewBindings.findChildViewById(rootView, id);
      if (minutesPicker == null) {
        break missingId;
      }

      id = R.id.startButton;
      MaterialButton startButton = ViewBindings.findChildViewById(rootView, id);
      if (startButton == null) {
        break missingId;
      }

      id = R.id.stopButton;
      MaterialButton stopButton = ViewBindings.findChildViewById(rootView, id);
      if (stopButton == null) {
        break missingId;
      }

      id = R.id.timerTextView;
      TextView timerTextView = ViewBindings.findChildViewById(rootView, id);
      if (timerTextView == null) {
        break missingId;
      }

      return new ActivityTimerBinding((LinearLayout) rootView, hoursPicker, minutesPicker,
          startButton, stopButton, timerTextView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
