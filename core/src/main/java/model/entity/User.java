package model.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    @Column(name = "salt", nullable = false)
    private Integer sessionToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "owner")
    private List<UserList> multimediaLists = new ArrayList<>();

    public User() {
    }

    public User(String username, Integer sessionToken) {
        this.username = username;
        this.sessionToken = sessionToken;
    }

    public User(String username, String password, String email, Integer sessionToken) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.sessionToken = sessionToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(Integer sessionToken) {
        this.sessionToken = sessionToken;
    }

    public List<UserList> getMultimediaLists() {
        return multimediaLists;
    }

    public void setMultimediaLists(List<UserList> multimediaLists) {
        this.multimediaLists = multimediaLists;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User {" +
                "id = " + id +
                ", username = \"" + username + '\"' +
                ", password = \"" + password + '\"' +
                ", email = \"" + email + '\"' +
                ", sessionToken = " + sessionToken +
                ", multimediaLists = " + multimediaLists +
                '}';
    }
}
