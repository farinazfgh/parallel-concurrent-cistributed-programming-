//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.rice.pcdp;

import edu.rice.pcdp.config.SystemProperty;
import edu.rice.pcdp.runtime.BaseTask;
import edu.rice.pcdp.runtime.IsolatedManager;
import edu.rice.pcdp.runtime.Runtime;
import edu.rice.pcdp.runtime.BaseTask.FinishTask;
import edu.rice.pcdp.runtime.BaseTask.FutureTask;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public final class PCDP {
    protected static final String missingFinishMsg = "A new task cannot be created at the top-level of your program. It must be created from within a finish or another async. Please ensure that all asyncs, actor sends, parallel loops, and other types of parallelism-creating constructs are not called at the top-level of your program.";
    private static final IsolatedManager isolatedManager = new IsolatedManager();

    private PCDP() {
    }

    public static void finish(Runnable runnable) {
        BaseTask currentTask = Runtime.currentTask();
        FinishTask newTask = new FinishTask(runnable);
        if (currentTask == null) {
            Runtime.submitTask(newTask);
        } else {
            newTask.compute();
        }

        newTask.awaitCompletion();
    }

    public static void async(Runnable runnable) {
        FutureTask<Void> newTask = createFutureTask(runnable);
        newTask.fork();
    }

    public static void forasync(int startInc, int endInc, ProcedureInt1D body) {
        assert startInc <= endInc;

        for(int i = startInc; i <= endInc; ++i) {
            int finalI = i;
            Runnable loopRunnable = () -> {
                body.apply(finalI);
            };
            async(loopRunnable);
        }

    }

    public static void forasync2d(int startInc0, int endInc0, int startInc1, int endInc1, ProcedureInt2D body) {
        assert startInc0 <= endInc0;

        assert startInc1 <= endInc1;

        for(int i = startInc0; i <= endInc0; ++i) {
            int iCopy = i;

            for(int j = startInc1; j <= endInc1; ++j) {
                int finalJ = j;
                Runnable loopRunnable = () -> {
                    body.apply(iCopy, finalJ);
                };
                async(loopRunnable);
            }
        }

    }

    public static void forall(int startInc, int endInc, ProcedureInt1D body) {
        finish(() -> {
            forasync(startInc, endInc, body);
        });
    }

    public static void forall2d(int startInc0, int endInc0, int startInc1, int endInc1, ProcedureInt2D body) {
        finish(() -> {
            forasync2d(startInc0, endInc0, startInc1, endInc1, body);
        });
    }

    public static void forasyncChunked(int start, int endInclusive, int chunkSize, ProcedureInt1D body) {
        assert start <= endInclusive;

        for(int i = start; i <= endInclusive; i += chunkSize) {
            int finalI = i;
            async(() -> {
                int end = finalI + chunkSize - 1;
                if (end > endInclusive) {
                    end = endInclusive;
                }

                for(int innerI = finalI; innerI <= end; ++innerI) {
                    body.apply(innerI);
                }

            });
        }

    }

    public static void forasyncChunked(int start, int endInclusive, ProcedureInt1D body) {
        forasyncChunked(start, endInclusive, getChunkSize(endInclusive - start + 1, numThreads() * 2), body);
    }

    public static void forasync2dChunked(int start0, int endInclusive0, int start1, int endInclusive1, int chunkSize, ProcedureInt2D body) {
        assert start0 <= endInclusive0;

        assert start1 <= endInclusive1;

        int outerNIters = endInclusive0 - start0 + 1;
        int innerNIters = endInclusive1 - start1 + 1;
        int numIters = outerNIters * innerNIters;
        forasyncChunked(0, numIters - 1, chunkSize, (i) -> {
            int outer = i / innerNIters;
            int inner = i % innerNIters;
            body.apply(start0 + outer, start1 + inner);
        });
    }

    public static void forasync2dChunked(int start0, int endInclusive0, int start1, int endInclusive1, ProcedureInt2D body) {
        int numIters = (endInclusive0 - start0 + 1) * (endInclusive1 - start1 + 1);
        forasync2dChunked(start0, endInclusive0, start1, endInclusive1, getChunkSize(numIters, numThreads() * 2), body);
    }

    public static void forallChunked(int start, int endInclusive, int chunkSize, ProcedureInt1D body) {
        finish(() -> {
            forasyncChunked(start, endInclusive, chunkSize, body);
        });
    }

    public static void forallChunked(int start, int endInclusive, ProcedureInt1D body) {
        forallChunked(start, endInclusive, getChunkSize(endInclusive - start + 1, numThreads() * 2), body);
    }

    public static void forall2dChunked(int start0, int endInclusive0, int start1, int endInclusive1, int chunkSize, ProcedureInt2D body) {
        finish(() -> {
            forasync2dChunked(start0, endInclusive0, start1, endInclusive1, chunkSize, body);
        });
    }

    public static void forall2dChunked(int start0, int endInclusive0, int start1, int endInclusive1, ProcedureInt2D body) {
        int numIters = (endInclusive0 - start0 + 1) * (endInclusive1 - start1 + 1);
        forall2dChunked(start0, endInclusive0, start1, endInclusive1, getChunkSize(numIters, numThreads() * 2), body);
    }

    public static <R> Future<R> future(Callable<R> body) {
        FutureTask<R> newTask = createFutureTask(body, false);
        newTask.fork();
        return newTask.future();
    }

    public static void asyncAwait(Runnable runnable, Future<? extends Object>... futures) {
        FutureTask<Void> newTask = createFutureTask(runnable);
        CompletableFuture.allOf(wrapToCompletableFutures(futures)).whenComplete((a, b) -> {
            newTask.fork();
        });
    }

    public static <R> Future<R> futureAwait(Callable<R> runnable, Future<? extends Object>... futures) {
        FutureTask<R> newTask = createFutureTask(runnable, false);
        CompletableFuture.allOf(wrapToCompletableFutures(futures)).whenComplete((a, b) -> {
            newTask.fork();
        });
        return newTask.future();
    }

    private static FutureTask<Void> createFutureTask(Runnable runnable) {
        BaseTask currentTask = Runtime.currentTask();
        if (currentTask == null) {
            throw new IllegalStateException("A new task cannot be created at the top-level of your program. It must be created from within a finish or another async. Please ensure that all asyncs, actor sends, parallel loops, and other types of parallelism-creating constructs are not called at the top-level of your program.");
        } else {
            return createFutureTask(() -> {
                runnable.run();
                return null;
            }, true);
        }
    }

    private static <R> FutureTask<R> createFutureTask(Callable<R> body, boolean rethrowException) {
        BaseTask currentTask = Runtime.currentTask();
        if (currentTask == null) {
            throw new IllegalStateException("A new task cannot be created at the top-level of your program. It must be created from within a finish or another async. Please ensure that all asyncs, actor sends, parallel loops, and other types of parallelism-creating constructs are not called at the top-level of your program.");
        } else {
            return new FutureTask(body, currentTask.ief(), rethrowException);
        }
    }

    private static CompletableFuture<?>[] wrapToCompletableFutures(Future<? extends Object>... futures) {
        CompletableFuture<?>[] result = new CompletableFuture[futures.length];

        for(int i = 0; i < futures.length; ++i) {
            Future<? extends Object> future = futures[i];
            if (!(future instanceof CompletableFuture)) {
                throw new IllegalArgumentException("Future at index " + i + " is not an instance of CompletableFuture!");
            }

            result[i] = (CompletableFuture)future;
        }

        return result;
    }

    public static void forseq(int start, int endInclusive, ProcedureInt1D body) {
        assert start <= endInclusive;

        for(int i = start; i <= endInclusive; ++i) {
            body.apply(i);
        }

    }

    public static void forseq2d(int start0, int endInclusive0, int start1, int endInclusive1, ProcedureInt2D body) {
        assert start0 <= endInclusive0;

        assert start1 <= endInclusive1;

        for(int i = start0; i <= endInclusive0; ++i) {
            for(int j = start1; j <= endInclusive1; ++j) {
                body.apply(i, j);
            }
        }

    }

    public static int numThreads() {
        return Integer.parseInt(SystemProperty.numWorkers.getPropertyValue());
    }

    private static int getChunkSize(int nElements, int nChunks) {
        return (nElements + nChunks - 1) / nChunks;
    }

    public static void isolated(Runnable runnable) {
        isolatedManager.acquireAllLocks();

        try {
            runnable.run();
        } finally {
            isolatedManager.releaseAllLocks();
        }

    }

    public static void isolated(Object obj, Runnable runnable) {
        Object[] objArr = new Object[]{obj};
        isolatedManager.acquireLocksFor(objArr);

        try {
            runnable.run();
        } finally {
            isolatedManager.releaseLocksFor(objArr);
        }

    }

    public static void isolated(Object obj1, Object obj2, Runnable runnable) {
        Object[] objArr = new Object[]{obj1, obj2};
        isolatedManager.acquireLocksFor(objArr);

        try {
            runnable.run();
        } finally {
            isolatedManager.releaseLocksFor(objArr);
        }

    }
}
