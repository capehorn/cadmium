package capehorn.cadmium;

public class CadmiumRuntimeException extends RuntimeException {
    public CadmiumRuntimeException(String message) {
        super(message);
    }

    public CadmiumRuntimeException(Throwable cause) {
        super(cause);
    }
}
