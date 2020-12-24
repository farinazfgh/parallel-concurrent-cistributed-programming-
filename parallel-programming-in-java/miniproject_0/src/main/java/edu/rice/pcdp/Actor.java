//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.rice.pcdp;

import edu.rice.pcdp.runtime.ActorMessageWrapper;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Actor {
    private final ConcurrentLinkedQueue<ActorMessageWrapper> queue = new ConcurrentLinkedQueue();
    private final AtomicInteger queueSize = new AtomicInteger(0);

    public Actor() {
    }

    public abstract void process(Object var1);

    public final void send(Object msg) {
        ActorMessageWrapper wrapper = new ActorMessageWrapper(msg);
        int oldQueueSize = this.queueSize.getAndIncrement();
        this.queue.add(wrapper);
        if (oldQueueSize == 0) {
            PCDP.async(() -> {
                int newQueueSize;
                for(boolean done = false; !done; done = newQueueSize == 0) {
                    ActorMessageWrapper curr;
                    do {
                        curr = (ActorMessageWrapper)this.queue.poll();
                    } while(curr == null);

                    this.process(curr.getMessage());
                    newQueueSize = this.queueSize.decrementAndGet();
                }

            });
        }

    }
}
