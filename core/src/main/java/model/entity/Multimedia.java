package model.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "app_multimedia")
public class Multimedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "api_id", nullable = false)
    private String apiId;

    @Column(name = "total_episodes")
    private Integer totalEpisodes;

    @Column(name = "multimedia_type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private MultimediaType multimediaType;

    public Multimedia() {
    }

    public Multimedia(String title, String apiId, Integer totalEpisodes, MultimediaType multimediaType) {
        this.title = title;
        this.apiId = apiId;
        this.totalEpisodes = totalEpisodes;
        this.multimediaType = multimediaType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public Integer getTotalEpisodes() {
        return totalEpisodes;
    }

    public void setTotalEpisodes(Integer totalEpisodes) {
        this.totalEpisodes = totalEpisodes;
    }

    public MultimediaType getMultimediaType() {
        return multimediaType;
    }

    public void setMultimediaType(MultimediaType multimediaType) {
        this.multimediaType = multimediaType;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Multimedia that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
