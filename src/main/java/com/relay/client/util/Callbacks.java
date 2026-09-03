package com.relay.client.util;

import java.util.function.Consumer;

public final class Callbacks {
    private Callbacks() {}
    
    /**
     * Notify a Consumer 
     */
    public static <T> boolean notify(Consumer<T> handler, T arg, String handlerName) {
        if (handler != null) {
            handler.accept(arg);
            return true;
        } else {
            System.err.println(handlerName + " not specified.");
            return false;
        }
    }

    /**
     * Notify a Runnable
     */
    public static boolean notify(Runnable handler, String handlerName) {
        if (handler != null) {
            handler.run();
            return true;
        } else {
            System.err.println(handlerName + " not specified.");
            return false;
        }
    }
}
