//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.rice.pcdp.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class BaseTask extends CountedCompleter<Void> {
    public BaseTask() {
    }

    public abstract BaseTask.FinishTask ief();

    public static final class FutureTask<R> extends BaseTask {
        protected static final AtomicLong TASK_COUNTER = new AtomicLong();
        private final Runnable runnable;
        private final BaseTask.FinishTask immediatelyEnclosingFinish;
        private final AtomicBoolean cancellationFlag = new AtomicBoolean(false);
        private final CompletableFuture<R> completableFuture = new CompletableFuture<R>() {
            public boolean cancel(boolean mayInterruptIfRunning) {
                return FutureTask.this.cancellationFlag.compareAndSet(false, true) && super.cancel(mayInterruptIfRunning);
            }
        };

        public FutureTask(Callable<R> setRunnable, BaseTask.FinishTask setImmediatelyEnclosingFinish, boolean rethrowException) {
            if (setImmediatelyEnclosingFinish == null) {
                throw new IllegalStateException("Async is not executing inside a finish!");
            } else {
                this.runnable = () -> {
                    try {
                        R result = setRunnable.call();
                        this.completableFuture.complete(result);
                    } catch (Exception var4) {
                        this.completableFuture.completeExceptionally(var4);
                        if (rethrowException) {
                            if (var4 instanceof RuntimeException) {
                                throw (RuntimeException)var4;
                            }

                            throw new RuntimeException("Error in executing callable", var4);
                        }
                    }

                };
                this.immediatelyEnclosingFinish = setImmediatelyEnclosingFinish;
                this.immediatelyEnclosingFinish.addToPendingCount(1);
                TASK_COUNTER.incrementAndGet();
            }
        }

        public void compute() {
            Runtime.pushTask(this);

            try {
                if (!this.cancellationFlag.get()) {
                    this.runnable.run();
                }
            } catch (Throwable var5) {
                this.immediatelyEnclosingFinish.pushException(var5);
            } finally {
                this.immediatelyEnclosingFinish.tryComplete();
                Runtime.popTask();
            }

        }

        public BaseTask.FinishTask ief() {
            return this.immediatelyEnclosingFinish;
        }

        public CompletableFuture<R> future() {
            return this.completableFuture;
        }
    }

    public static final class FinishTask extends BaseTask {
        protected static final AtomicLong TASK_COUNTER = new AtomicLong();
        private final Runnable runnable;
        private final CountDownLatch countDownLatch;
        private List<Throwable> exceptionList;

        public FinishTask(Runnable setRunnable) {
            this.runnable = setRunnable;
            this.countDownLatch = new CountDownLatch(1);
            this.exceptionList = null;
            TASK_COUNTER.incrementAndGet();
        }

        public void compute() {
            Runtime.pushTask(this);

            try {
                this.runnable.run();
            } catch (Throwable var5) {
                this.pushException(var5);
            } finally {
                this.tryComplete();
                Runtime.popTask();
                this.awaitCompletion();
            }

        }

        public void onCompletion(CountedCompleter<?> caller) {
            this.countDownLatch.countDown();
        }

        public void awaitCompletion() {
            try {
                this.countDownLatch.await();
            } catch (InterruptedException var3) {
                this.pushException(var3);
            }

            List<Throwable> finalExceptionList = this.exceptions();
            if (!finalExceptionList.isEmpty()) {
                if (finalExceptionList.size() == 1) {
                    Throwable t = (Throwable)finalExceptionList.get(0);
                    if (t instanceof Error) {
                        throw (Error)t;
                    }

                    if (t instanceof RuntimeException) {
                        throw (RuntimeException)t;
                    }
                }

                throw new MultiException(finalExceptionList);
            }
        }

        public BaseTask.FinishTask ief() {
            return this;
        }

        private List<Throwable> exceptions() {
            if (this.exceptionList == null) {
                this.exceptionList = new ArrayList();
            }

            return this.exceptionList;
        }

        public void pushException(Throwable throwable) {
            synchronized(this) {
                this.exceptions().add(throwable);
            }
        }
    }
}
