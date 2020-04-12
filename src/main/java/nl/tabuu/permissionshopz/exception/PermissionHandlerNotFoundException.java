package nl.tabuu.permissionshopz.exception;

public class PermissionHandlerNotFoundException extends RuntimeException {
    public PermissionHandlerNotFoundException() {
        super();
    }

    public PermissionHandlerNotFoundException(String message) {
        super(message);
    }

    public PermissionHandlerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionHandlerNotFoundException(Throwable cause) {
        super(cause);
    }
}
