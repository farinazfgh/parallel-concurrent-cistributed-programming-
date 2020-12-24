//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.rice.pcdp.runtime;

import edu.rice.pcdp.config.Configuration;
import edu.rice.pcdp.config.SystemProperty;
import edu.rice.pcdp.runtime.BaseTask.FinishTask;
import edu.rice.pcdp.runtime.BaseTask.FutureTask;
import java.util.Stack;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public final class Runtime {
    private static final ThreadLocal<Stack<BaseTask>> threadLocalTaskStack = new ThreadLocal<Stack<BaseTask>>() {
        protected Stack<BaseTask> initialValue() {
            return new Stack();
        }
    };
    private static ForkJoinPool taskPool;

    private Runtime() {
    }

    public static void resizeWorkerThreads(int numWorkers) throws InterruptedException {
        taskPool.shutdown();
        boolean terminated = taskPool.awaitTermination(10L, TimeUnit.SECONDS);

        assert terminated;

        SystemProperty.numWorkers.set(numWorkers);
        taskPool = new ForkJoinPool(numWorkers);
    }

    public static BaseTask currentTask() {
        Stack<BaseTask> taskStack = (Stack)threadLocalTaskStack.get();
        return taskStack.isEmpty() ? null : (BaseTask)taskStack.peek();
    }

    public static void pushTask(BaseTask task) {
        ((Stack)threadLocalTaskStack.get()).push(task);
    }

    public static void popTask() {
        ((Stack)threadLocalTaskStack.get()).pop();
    }

    public static void submitTask(BaseTask task) {
        taskPool.execute(task);
    }

    public static void showRuntimeStats() {
        System.out.println("Runtime Stats (" + Configuration.BUILD_INFO + "): ");
        System.out.println("   " + taskPool.toString());
        System.out.println("   # finishes = " + FinishTask.TASK_COUNTER.get());
        System.out.println("   # asyncs = " + FutureTask.TASK_COUNTER.get());
    }

    static {
        taskPool = new ForkJoinPool(Configuration.readIntProperty(SystemProperty.numWorkers));
    }
}
