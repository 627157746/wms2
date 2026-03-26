package com.zhb.wms2.common.lock;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LocalMethodLockManager
 *
 * @author zhb
 * @since 2026/3/26
 */
@Component
public class LocalMethodLockManager {

    /**
     * 按锁键维护的本地锁容器。
     */
    private final ConcurrentHashMap<String, LockHolder> lockHolderMap = new ConcurrentHashMap<>();

    /**
     * 尝试获取指定 key 的本地方法锁。
     */
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

    /**
     * 释放已持有的本地方法锁。
     */
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

    /**
     * 释放锁引用计数，并在无占用时清理缓存。
     */
    private void releaseReference(String lockKey, LockHolder holder) {
        int refCount = holder.decrement();
        if (refCount == 0 && !holder.isLocked() && !holder.hasQueuedThreads()) {
            lockHolderMap.remove(lockKey, holder);
        }
    }

    /**
     * 已获取锁后的句柄对象。
     */
    static final class LockHandle {

        /**
         * 当前锁对应的业务键。
         */
        private final String lockKey;

        /**
         * 当前锁句柄持有的锁对象。
         */
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

    /**
     * 单个锁键对应的锁持有者。
     */
    static final class LockHolder {

        /**
         * 实际执行加锁的可重入锁。
         */
        private final ReentrantLock lock = new ReentrantLock();

        /**
         * 当前锁对象的引用计数。
         */
        private final AtomicInteger refCount = new AtomicInteger();

        /**
         * 增加锁引用计数。
         */
        private void increment() {
            refCount.incrementAndGet();
        }

        /**
         * 减少锁引用计数。
         */
        private int decrement() {
            return refCount.decrementAndGet();
        }

        /**
         * 以可中断方式获取锁。
         */
        private void lockInterruptibly() throws InterruptedException {
            lock.lockInterruptibly();
        }

        /**
         * 在指定等待时间内尝试获取锁。
         */
        private boolean tryLock(long waitTime, TimeUnit timeUnit) throws InterruptedException {
            return lock.tryLock(waitTime, timeUnit);
        }

        /**
         * 释放锁。
         */
        private void unlock() {
            lock.unlock();
        }

        /**
         * 判断锁是否仍被任意线程持有。
         */
        private boolean isLocked() {
            return lock.isLocked();
        }

        /**
         * 判断是否还有线程在等待该锁。
         */
        private boolean hasQueuedThreads() {
            return lock.hasQueuedThreads();
        }

        /**
         * 判断当前线程是否持有该锁。
         */
        private boolean isHeldByCurrentThread() {
            return lock.isHeldByCurrentThread();
        }
    }
}
