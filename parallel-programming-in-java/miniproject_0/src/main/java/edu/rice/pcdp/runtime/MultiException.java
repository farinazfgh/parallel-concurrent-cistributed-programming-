//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.rice.pcdp.runtime;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class MultiException extends RuntimeException {
    private final List<Throwable> exceptions;

    public MultiException(List<Throwable> inputThrowableList) {
        List<Throwable> throwableList = new ArrayList();
        Iterator var3 = inputThrowableList.iterator();

        while(var3.hasNext()) {
            Throwable th = (Throwable)var3.next();
            if (th instanceof MultiException) {
                MultiException me = (MultiException)th;
                throwableList.addAll(me.exceptions);
            } else {
                throwableList.add(th);
            }
        }

        this.exceptions = throwableList;
    }

    public void printStackTrace() {
        this.printStackTrace(System.out);
    }

    public void printStackTrace(PrintStream printStream) {
        super.printStackTrace(printStream);
        int numExceptions = this.exceptions.size();
        printStream.println("  Number of exceptions: " + numExceptions);
        int numExceptionsToDisplay = Math.min(5, numExceptions);
        printStream.println("  Printing " + numExceptionsToDisplay + " stack traces...");

        for(int i = 0; i < numExceptionsToDisplay; ++i) {
            Throwable exception = (Throwable)this.exceptions.get(i);
            exception.printStackTrace(printStream);
        }

    }

    public String toString() {
        return this.exceptions.toString();
    }

    public List<Throwable> getExceptions() {
        return this.exceptions;
    }
}
