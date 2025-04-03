package io;

public enum MessageType {
    TEST("Test"),
    KNOCK("Knock"),
    LOGIN("Login"),
    REGISTER("Register"),
    GET_USER_LISTS("Get user lists"),
    CREATE_USER_LIST("Create user list");

    final private String name;

    MessageType(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
