package exceptions;

public class FieldException extends BaseException {
    public FieldException(String clsName, String name) {
        super(String.format("error field: `%s`.`%s`", clsName, name));
    }
}
