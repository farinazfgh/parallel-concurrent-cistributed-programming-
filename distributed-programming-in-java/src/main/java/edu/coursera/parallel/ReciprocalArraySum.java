package edu.coursera.parallel;

import java.util.concurrent.RecursiveAction;

/**
 * Class wrapping methods for implementing reciprocal array sum in parallel.
 */
public final class ReciprocalArraySum {

    /**
     * Default constructor.
     */
    private ReciprocalArraySum() {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "8");
    }

    /**
     * Sequentially compute the sum of the reciprocal values for a given array.
     */
    protected static double seqArraySum(final double[] input) {
        double sum = 0;

        // Compute sum of reciprocals of array elements
        for (double v : input) {
            sum += 1 / v;
        }

        return sum;
    }

    /**
     * Computes the size of each chunk, given the number of chunks to create across
     * a given number of elements.
     */
    private static int getChunkSize(final int nChunks, final int nElements) {
        // Integer ceil
        return (nElements + nChunks - 1) / nChunks;
    }

    /**
     * Computes the inclusive element index that the provided chunk starts at, given
     * there are a certain number of chunks.
     */
    private static int getChunkStartInclusive(final int chunk, final int nChunks, final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        return chunk * chunkSize;
    }

    /**
     * Computes the exclusive element index that the provided chunk ends at, given
     * there are a certain number of chunks.
     */
    private static int getChunkEndExclusive(final int chunk, final int nChunks, final int nElements) {
        final int chunkSize = getChunkSize(nChunks, nElements);
        final int end = (chunk + 1) * chunkSize;
        return Math.min(end, nElements);
    }

    /**
     * This class stub can be filled in to implement the body of each task created
     * to perform reciprocal array sum in parallel.
     */
    private static class ReciprocalArraySumTask extends RecursiveAction {

        private final int startIndexInclusive;
        private final int endIndexExclusive;
        private final double[] input;
        private double value;

        ReciprocalArraySumTask(final int setStartIndexInclusive, final int setEndIndexExclusive,
                               final double[] setInput) {
            this.startIndexInclusive = setStartIndexInclusive;
            this.endIndexExclusive = setEndIndexExclusive;
            this.input = setInput;
        }


        public double getValue() {
            return value;
        }

        @Override
        protected void compute() {
            for (int i = this.startIndexInclusive; i < this.endIndexExclusive; i++) {
                this.value += 1 / input[i];
            }
        }
    }

    /**
     * TODO: Modify this method to compute the same reciprocal sum as seqArraySum,
     * but use two tasks running in parallel under the Java Fork Join framework. You
     * may assume that the length of the input array is evenly divisible by 2.
     *
     * @param input Input array
     * @return The sum of the reciprocals of the array input
     */
    protected static double parArraySum(final double[] input) {
        assert input.length % 2 == 0;
        double sum;
        int SEQ_FACTOR = 5;
        ReciprocalArraySumTask left = new ReciprocalArraySumTask(0, input.length / 2,
                input);
        ReciprocalArraySumTask right = new ReciprocalArraySumTask(input.length / 2,
                input.length, input);

        int range = input.length;
        if (range <= (input.length / SEQ_FACTOR)) {
            left.compute();
            right.compute();
            sum = left.getValue() + right.getValue();
        } else {
            left.fork();
            right.compute();
            left.join();
            sum = left.getValue() + right.getValue();
        }
        return sum;
    }

    /**
     * tasks to compute the reciprocal array sum. You may find the above utilities
     * getChunkStartInclusive and getChunkEndExclusive helpful in computing the
     * range of element indices that belong to each chunk.
     */
    protected static double parManyTaskArraySum(final double[] input, final int numTasks) {
        double sum = 0;
        int taskNum = numTasks;
        if (taskNum > input.length) {
            taskNum = input.length;
        }
        ReciprocalArraySumTask[] tasks = new ReciprocalArraySumTask[taskNum];

        int i;
        for (i = 0; i < taskNum - 1; i++) {
            tasks[i] = new ReciprocalArraySumTask(getChunkStartInclusive(i, taskNum, input.length), getChunkEndExclusive(i, taskNum, input.length), input);
            tasks[i].fork();
        }
        tasks[i] = new ReciprocalArraySumTask(getChunkStartInclusive(i, taskNum, input.length), getChunkEndExclusive(i, taskNum, input.length), input);
        tasks[i].compute();

        for (int j = 0; j < taskNum - 1; j++) {
            tasks[j].join();
        }

        for (int j = 0; j < taskNum; j++) {
            sum += tasks[j].getValue();
        }
        return sum;
    }
}