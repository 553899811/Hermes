package com.newbiegroup.rpc.remoting.client;

import com.newbiegroup.rpc.remoting.codec.RpcRequest;
import com.newbiegroup.rpc.remoting.codec.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>ClassName:  </p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author zhangyong
 * @version 1.0.0
 * @date 2021/4/4 22:39
 */
@Slf4j
public class RpcFuture implements Future<Object> {

    private RpcRequest request;

    private RpcResponse response;

    private static final long TIME_THRESHOLD = 5000;

    private long startTime;

    private ReentrantLock lock = new ReentrantLock();

    private List<RpcCallBack> pendingCallbacks = new ArrayList<>();

    private ThreadPoolExecutor threadPoolExecutor
            = new ThreadPoolExecutor(16, 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1 << 16));

    /**
     * 同步队列
     */
    private Sync sync;

    public RpcFuture(RpcRequest request) {
        this.request = request;
        this.startTime = System.currentTimeMillis();
        this.sync = new Sync();
    }

    static class Sync extends AbstractQueuedSynchronizer {

        private final int done = 1;

        private final int pending = 0;

        protected boolean tryAcquire(int acquire) {
            return getState() == done ? true : false;
        }

        protected boolean tryRelease(int release) {
            if (getState() == release) {
                if (compareAndSetState(pending, done)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone() {
            return getState() == done;
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
