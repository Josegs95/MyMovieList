package model;

public class MultimediaAtList {
    final private Multimedia multimedia;
    private MultimediaStatus status;
    private int current_episode;
    private String multimediaType;

    public MultimediaAtList(Multimedia multimedia, MultimediaStatus status, int current_episode) {
        this.multimedia = multimedia;
        this.status = status;
        this.current_episode = current_episode;

        multimediaType = multimedia instanceof Movie ? "MOVIE" : "TV_SHOW";
    }

    public MultimediaAtList(Multimedia multimedia, MultimediaStatus status) {
        this(multimedia, status, -1);
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }

    public MultimediaStatus getStatus() {
        return status;
    }

    public void setStatus(MultimediaStatus status) {
        this.status = status;
    }

    public int getCurrent_episode() {
        return current_episode;
    }

    public void setCurrent_episode(int current_episode) {
        this.current_episode = current_episode;
    }

    public String getMultimediaType() {
        return multimediaType;
    }
}
