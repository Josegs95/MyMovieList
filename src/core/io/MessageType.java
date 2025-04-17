package io;

public enum MessageType {
    TEST("Test"),
    KNOCK("Knock"),
    LOGIN("Login"),
    REGISTER("Register"),
    GET_USER_LISTS("Get user lists"),
    CREATE_USER_LIST("Create user list"),
    ADD_MULTIMEDIA("Add multimedia to list"),
    MODIFY_MULTIMEDIA("Modify multimedia data in a list"),
    REMOVE_MULTIMEDIA("Remove multimedia from a list");

    final private String name;

    MessageType(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
