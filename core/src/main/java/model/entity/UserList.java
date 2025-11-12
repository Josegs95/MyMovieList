package model.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "app_list")
public class UserList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "list", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MultimediaListItem> multimediaList = new ArrayList<>();

    public UserList() {
    }

    public UserList(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<MultimediaListItem> getMultimediaList() {
        return multimediaList;
    }

    public void setMultimediaList(List<MultimediaListItem> multimediaList) {
        this.multimediaList = multimediaList;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserList userList)) return false;
        return Objects.equals(id, userList.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
