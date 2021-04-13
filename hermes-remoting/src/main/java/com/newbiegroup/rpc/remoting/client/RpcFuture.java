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
 * <p>ClassName: RpcFuture 异步结果对象 （待完善，补充更多机制）</p>
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

    /**
     * 回调任务执行线程池
     */
    private ThreadPoolExecutor callBackExecutor
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
            return getState() == done;
        }

        protected boolean tryRelease(int release) {
            if (getState() == pending) {
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    /**
     * @param response
     */
    public void done(RpcResponse response) {
        this.response = response;
        boolean success = sync.release(1);
        if (success) {
            invokeCallBacks();
        }
        long costTime = System.currentTimeMillis() - startTime;
        if (TIME_THRESHOLD < costTime) {
            log.warn("the rpc response time is too slow,request id = " + this.request.getRequestId() + "cost time:" + costTime);
        }
    }

    /**
     * 依次执行回调函数
     */
    private void invokeCallBacks() {
        lock.lock();
        try {
            for (final RpcCallBack callBack : pendingCallbacks) {
                runCallBack(callBack);
            }
        } finally {
            lock.unlock();
        }
    }

    private void runCallBack(RpcCallBack callBack) {
        final RpcResponse response = this.response;
        callBackExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (response.getThrowable() == null) {
                    callBack.success(response.getResult());
                } else {
                    callBack.failure(response.getThrowable());
                }
            }
        });
    }

    public RpcFuture addCallBack(RpcCallBack callBack) {
        lock.lock();
        try {
            if (isDone()) {
                runCallBack(callBack);
            } else {
                this.pendingCallbacks.add(callBack);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * 何时调用get方法
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        if (this.response != null) {
            return this.response.getResult();
        } else {
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (this.response != null) {
                return this.response.getResult();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("timeout exception requestId:" +
                    this.request.getRequestId()
                    + ",className:" + this.request.getClassName()
                    + ",methodName:" + this.request.getMethodName());
        }

    }
}
