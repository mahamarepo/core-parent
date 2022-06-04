package com.mahama.parent.factory;

import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimeTaskManage {
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
    private static final TimeTaskManage timeTaskManage = new TimeTaskManage();

    public static TimeTaskManage me() {
        return timeTaskManage;
    }

    public void execute(TimerTask task) {
        executor.schedule(task, 10, TimeUnit.MILLISECONDS);
    }
}
