package com.project.cs371.couponapp;

        import android.os.Handler;

        import java.util.LinkedList;
        import java.util.concurrent.locks.ReentrantLock;


public class RateLimit {
    public interface RateLimitCallback {
        void rateLimitReady();
    }

    protected Handler handler;
    protected Runnable rateLimitRequest;
    protected final int rateLimitMillis = 1000; // 2 sec
    protected ReentrantLock l = new ReentrantLock();
    protected boolean okToRun;
    protected LinkedList<RateLimitCallback> rateLimitCallbacks;

    private RateLimit() {
        handler = new Handler();
        l = new ReentrantLock();
        okToRun = false;
        rateLimitCallbacks = new LinkedList<>();
        rateLimitRequest = new Runnable() {
            @Override
            public void run() {
                l.lock();
                okToRun = true;
                l.unlock();
                runIfOk();
                handler.postDelayed(this, rateLimitMillis);
            }
        };
        handler.postDelayed(rateLimitRequest, rateLimitMillis);
    }

    protected void runIfOk() {
        l.lock();
        if (!rateLimitCallbacks.isEmpty() && okToRun) {
            okToRun = false;
            RateLimitCallback rlc = rateLimitCallbacks.pop();
            l.unlock();

            rlc.rateLimitReady();
            return;
        }
        l.unlock();
    }


    private static class RateLimitHolder {
        public static final RateLimit rateLimit = new RateLimit();
    }

    public static RateLimit getInstance() {
        return RateLimitHolder.rateLimit;
    }

    public void add(RateLimitCallback rlc) {
        l.lock();
        try {
            if (!rateLimitCallbacks.contains(rlc)) {
                rateLimitCallbacks.add(rlc);
            }
        } finally {
            l.unlock();
        }
        runIfOk();
    }


    public void addFront(RateLimitCallback rlc) {
        l.lock();
        try {
            if (!rateLimitCallbacks.contains(rlc)) {
                rateLimitCallbacks.push(rlc);
            }
        } finally {
            l.unlock();
        }
        runIfOk();
    }
}

