package exceptions;

public class ClassException extends BaseException {
    public ClassException(String name) {
        super(String.format("error class: `%s`", name));
    }
}
