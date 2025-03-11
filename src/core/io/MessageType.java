package io;

public enum MessageType {
    TEST("Test"),
    KNOCK("Knock"),
    LOGIN("Login"),
    REGISTER("Register");

    final private String NAME;

    MessageType(String name){
        this.NAME = name;
    }

    @Override
    public String toString() {
        return NAME;
    }
}
