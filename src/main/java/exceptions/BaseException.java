package exceptions;

public class BaseException extends java.lang.RuntimeException {
    public BaseException() {
        super("Kusa: unknown exception");
    }

    public BaseException(String msg) {
        super("Kusa: " + msg);
    }
}
