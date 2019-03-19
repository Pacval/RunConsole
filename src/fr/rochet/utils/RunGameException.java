package fr.rochet.utils;

import java.util.function.Supplier;

public class RunGameException extends Exception {

    public RunGameException() {
    }

    public RunGameException(String s) {
        super(s);
    }

    public RunGameException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RunGameException(Throwable throwable) {
        super(throwable);
    }

    public RunGameException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
