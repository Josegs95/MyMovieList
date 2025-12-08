package context;

import model.dto.UserDTO;

public class SessionContext {

    private static SessionContext instance;
    private UserDTO currentUser;

    private SessionContext() {}

    public static synchronized SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }

        return instance;
    }

    public UserDTO getUser() {
        if (currentUser == null) {
            throw new IllegalStateException("No user logged");
        }
        return currentUser;
    }

    public void setUser(UserDTO user){
        this.currentUser = user;
    }
}
