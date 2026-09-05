package com.relay.client.util;

import java.util.function.Consumer;

public final class Callbacks {
    private Callbacks() {}
    
    /**
     * Notify a Consumer 
     */
    public static <T> boolean notify(Consumer<T> handler, T arg) {
        if (handler != null) {
            handler.accept(arg);
            return true;
        }
        return false;
    }

    /**
     * Notify a Runnable
     */
    public static boolean notify(Runnable handler) {
        if (handler != null) {
            handler.run();
            return true;
        }
        return false;
    }
}
