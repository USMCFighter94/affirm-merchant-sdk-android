package com.affirm.android;

import android.os.AsyncTask;

public abstract class AffirmRequest<T> {

    interface RequestCreate {

        void create();

        void cancel();
    }

    abstract AsyncTask<Void, Void, T> createTask();

    void cancelTask() {
    }

    AsyncTask<Void, Void, T> task;

    private final RequestCreate requestCreate = new RequestCreate() {

        @Override
        public void create() {
            task = createTask();
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        public void cancel() {
            if (task != null && !task.isCancelled()) {
                task.cancel(true);
                task = null;
            }
            cancelTask();
        }
    };

    public void create() {
        requestCreate.create();
    }

    public void cancel() {
        requestCreate.cancel();
    }
}
