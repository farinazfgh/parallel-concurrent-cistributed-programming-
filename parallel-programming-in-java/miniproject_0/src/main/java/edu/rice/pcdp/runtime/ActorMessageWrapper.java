//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.rice.pcdp.runtime;

public final class ActorMessageWrapper {
    private final Object msg;
    private ActorMessageWrapper next;

    public ActorMessageWrapper(Object setMsg) {
        this.msg = setMsg;
        this.next = null;
    }

    public void setNext(ActorMessageWrapper setNext) {
        this.next = setNext;
    }

    public ActorMessageWrapper getNext() {
        return this.next;
    }

    public Object getMessage() {
        return this.msg;
    }
}
