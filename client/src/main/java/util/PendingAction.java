package util;

@FunctionalInterface
public interface PendingAction {

    void execute() throws Exception;
}
