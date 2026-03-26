package com.zhb.wms2.common.lock;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class LocalMethodLockManager {

    private final ConcurrentHashMap<String, LockHolder> lockHolderMap = new ConcurrentHashMap<>();

    public LockHandle acquire(String lockKey, long waitTime, TimeUnit timeUnit) throws InterruptedException {
        LockHolder holder = lockHolderMap.compute(lockKey, (key, current) -> {
            LockHolder target = current == null ? new LockHolder() : current;
            target.increment();
            return target;
        });

        boolean locked = false;
        try {
            if (waitTime < 0) {
                holder.lockInterruptibly();
                locked = true;
            } else {
                locked = holder.tryLock(waitTime, timeUnit);
            }
            if (!locked) {
                releaseReference(lockKey, holder);
                return null;
            }
            return new LockHandle(lockKey, holder);
        } catch (InterruptedException e) {
            releaseReference(lockKey, holder);
            throw e;
        } catch (RuntimeException e) {
            releaseReference(lockKey, holder);
            throw e;
        }
    }

    public void release(LockHandle lockHandle) {
        if (lockHandle == null) {
            return;
        }
        LockHolder holder = lockHandle.lockHolder();
        try {
            if (holder.isHeldByCurrentThread()) {
                holder.unlock();
            }
        } finally {
            releaseReference(lockHandle.lockKey(), holder);
        }
    }

    private void releaseReference(String lockKey, LockHolder holder) {
        int refCount = holder.decrement();
        if (refCount == 0 && !holder.isLocked() && !holder.hasQueuedThreads()) {
            lockHolderMap.remove(lockKey, holder);
        }
    }

    static final class LockHandle {

        private final String lockKey;
        private final LockHolder lockHolder;

        LockHandle(String lockKey, LockHolder lockHolder) {
            this.lockKey = lockKey;
            this.lockHolder = lockHolder;
        }

        String lockKey() {
            return lockKey;
        }

        LockHolder lockHolder() {
            return lockHolder;
        }
    }

    static final class LockHolder {

        private final ReentrantLock lock = new ReentrantLock();
        private final AtomicInteger refCount = new AtomicInteger();

        private void increment() {
            refCount.incrementAndGet();
        }

        private int decrement() {
            return refCount.decrementAndGet();
        }

        private void lockInterruptibly() throws InterruptedException {
            lock.lockInterruptibly();
        }

        private boolean tryLock(long waitTime, TimeUnit timeUnit) throws InterruptedException {
            return lock.tryLock(waitTime, timeUnit);
        }

        private void unlock() {
            lock.unlock();
        }

        private boolean isLocked() {
            return lock.isLocked();
        }

        private boolean hasQueuedThreads() {
            return lock.hasQueuedThreads();
        }

        private boolean isHeldByCurrentThread() {
            return lock.isHeldByCurrentThread();
        }
    }
}
