package model.entity;

import jakarta.persistence.*;

@Entity
@IdClass(MultimediaListItemKey.class)
public class MultimediaListItem {

    @Id
    @ManyToOne
    @JoinColumn(name = "multimedia_id")
    private Multimedia multimedia;

    @Id
    @ManyToOne
    @JoinColumn(name = "list_id")
    private UserList list;

    @Enumerated(EnumType.STRING)
    private MultimediaStatus status;

    @Column(name = "current_episode")
    private Integer currentEpisode;

    public MultimediaListItem() {
    }

    public MultimediaListItem(Multimedia multimedia, UserList list, MultimediaStatus status, Integer currentEpisode) {
        this.multimedia = multimedia;
        this.list = list;
        this.status = status;
        this.currentEpisode = currentEpisode;
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(Multimedia multimedia) {
        this.multimedia = multimedia;
    }

    public MultimediaStatus getStatus() {
        return status;
    }

    public void setStatus(MultimediaStatus status) {
        this.status = status;
    }

    public Integer getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(Integer currentEpisode) {
        this.currentEpisode = currentEpisode;
    }

    public UserList getList() {
        return list;
    }

    public void setList(UserList list) {
        this.list = list;
    }
}
