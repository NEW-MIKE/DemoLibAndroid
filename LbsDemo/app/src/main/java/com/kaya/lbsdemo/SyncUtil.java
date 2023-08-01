package com.kaya.lbsdemo;

import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SyncUtil {
    private static final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private static ScheduledThreadPoolExecutor mExecutorService;
    private static ThreadPoolExecutor mThreadPool;

    public static synchronized ScheduledThreadPoolExecutor getExecutorService() {
        return getExecutorService(5);
    }

    public static synchronized ScheduledThreadPoolExecutor getExecutorService(int corePoolSize) {
        if (mExecutorService == null || mExecutorService.isShutdown()) {
            try {
                mExecutorService = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(corePoolSize);
                mExecutorService.setRemoveOnCancelPolicy(true);
            } catch (Throwable var2) {
            }

        } else {
            if (mExecutorService.getCorePoolSize() != corePoolSize) {
                mExecutorService.setCorePoolSize(corePoolSize);
            }
        }

        return mExecutorService;
    }

    public static synchronized ExecutorService getPool() {
        if (mThreadPool == null || mThreadPool.isShutdown()) {
            try {
                mThreadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
                mThreadPool.setCorePoolSize(5);
            } catch (Exception e) {
            }
        }

        return mThreadPool;
    }

    public static Future<?> submit(Runnable task) {
        return getPool().submit(task);
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return getPool().submit(task);
    }

    public static ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        try {
            return getExecutorService().schedule(command, delay, unit);
        } catch (Throwable var5) {
            return null;
        }
    }

    public static ScheduledFuture<?> schedule(Runnable command, long initialDelay, long period, TimeUnit unit) {
        try {
            return getExecutorService().scheduleAtFixedRate(command, initialDelay, period, unit);
        } catch (Throwable var7) {
            return null;
        }
    }

    public static void run(Runnable run) {
        run(run, (String) null);
    }

    public static void run(final Runnable run, String name) {
        if (null != mThreadPool) {
            poolRun(run);
        } else {
            try {
                Thread thread = new Thread(() -> {
                    try {
                        run.run();
                    } catch (Throwable var2) {
                    }
                });
                if (name != null) {
                    thread.setName(name);
                }
                thread.start();
            } catch (Throwable var3) {
            }
        }
    }

    public static void work(Runnable run) {
        work(run, (String) null);
    }

    public static void work(final Runnable run, String name) {
        if (null != mThreadPool) {
            poolRun(run);
        } else {
            try {
                Thread thread = new Thread(() -> {
                    try {
                        run.run();
                    } catch (Throwable var2) {
                    }

                });
                if (name != null) {
                    thread.setName(name);
                }

                thread.setDaemon(true);
                thread.start();
            } catch (Throwable var3) {
            }
        }

    }

    public static Thread commit(final Runnable run) {
        Thread thread = null;
        if (null != mThreadPool) {
            poolRun(run);
        } else {
            try {
                thread = new Thread(() -> {
                    try {
                        run.run();
                    } catch (Throwable var2) {
                    }

                });
                thread.setDaemon(true);
                thread.start();
            } catch (Throwable var3) {
            }
        }

        return thread;
    }

    private static void poolRun(final Runnable runnable) {
        try {
            getPool().execute(() -> {
                try {
                    runnable.run();
                } catch (Throwable var2) {
                }
            });
        } catch (Throwable var2) {
        }
    }

    public static void delay(long delay) {
        if (delay >= 0L) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException var3) {
            }
        }
    }

    public static void ui(final Runnable run) {
        if (mUiHandler != null) {
            mUiHandler.post(() -> {
                try {
                    run.run();
                } catch (Throwable var2) {
                }
            });
        } else {
        }
    }

    public static void delayUI(final Runnable run, long span) {
        if (mUiHandler != null) {
            mUiHandler.postDelayed(() -> {
                try {
                    run.run();
                } catch (Throwable var2) {
                }
            }, span);
        } else {
        }
    }

    public static void delay(final Runnable run, long delay) {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (run != null) {
                            run.run();
                        }
                    } catch (Throwable var2) {
                    }
                }
            }, delay);
        } catch (Throwable var4) {
        }

    }
}
