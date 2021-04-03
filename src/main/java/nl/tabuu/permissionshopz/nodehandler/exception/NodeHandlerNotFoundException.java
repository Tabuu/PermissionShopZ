package nl.tabuu.permissionshopz.nodehandler.exception;

public class NodeHandlerNotFoundException extends RuntimeException {
    public NodeHandlerNotFoundException() {
        super();
    }

    public NodeHandlerNotFoundException(String message) {
        super(message);
    }

    public NodeHandlerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NodeHandlerNotFoundException(Throwable cause) {
        super(cause);
    }
}