package com.github.b3er.rxfirebase.common;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import io.reactivex.CompletableEmitter;
import io.reactivex.SingleEmitter;

public final class GmsTaskListeners {

  private GmsTaskListeners() {
    throw new AssertionError("No instances");
  }

  public static <T> OnCompleteListener<T> listener(@NonNull final SingleEmitter<T> emitter) {
    return new OnCompleteListener<T>() {
      @Override public void onComplete(@NonNull Task<T> task) {
        if (!task.isSuccessful()) {
          if (!emitter.isDisposed()) {
            emitter.onError(task.getException());
          }
          return;
        }

        if (!emitter.isDisposed()) {
          emitter.onSuccess(task.getResult());
        }
      }
    };
  }

  public static OnCompleteListener<Void> listener(@NonNull final CompletableEmitter emitter) {
    return new OnCompleteListener<Void>() {
      @Override public void onComplete(@NonNull Task<Void> task) {
        if (!task.isSuccessful()) {
          if (!emitter.isDisposed()) {
            emitter.onError(task.getException());
          }
          return;
        }

        if (!emitter.isDisposed()) {
          emitter.onComplete();
        }
      }
    };
  }
}