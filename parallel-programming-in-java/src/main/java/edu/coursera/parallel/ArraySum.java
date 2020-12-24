package edu.coursera.parallel;

import static edu.rice.pcdp.PCDP.async;
import static edu.rice.pcdp.PCDP.finish;

public class ArraySum {
    static int sum1, sum2;

    static double sequentialArraySum(double[] X) {
        long startTime = System.nanoTime();
        sum1 = 0;
        sum2 = 0;
        for (int i = 0; i < (X.length / 2); i++) {
            sum1 += 1 / X[i];
        }
        for (int i = (X.length / 2); i < X.length; i++) {
            sum2 += 1 / X[i];

        }
        double sum = sum1 + sum2;
        long timeInNanoSec = System.nanoTime() - startTime;
        System.out.println("sum: " + sum + ", timeInNanoSec:" + timeInNanoSec);
        return sum;
    }

    static double parallelArraySum(double[] X) {
        long startTime = System.nanoTime();
        sum1 = 0;
        sum2 = 0;
        finish(() -> {

            async(() -> {
                for (int i = 0; i < (X.length / 2); i++) {
                    sum1 += 1 / X[i];
                }
            });
            for (int i = (X.length / 2); i < X.length; i++) {
                sum2 += 1 / X[i];

            }
        });
        double sum = sum1 + sum2;
        long timeInNanoSec = System.nanoTime() - startTime;
        System.out.println("sum: " + sum + ", timeInNanoSec:" + timeInNanoSec);
        return sum;
    }
}
