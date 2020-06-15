package exceptions;

public class ParseException extends BaseException {
    public ParseException(String val) {
        super(String.format("parse exception, near `%s`", val));
    }
}
