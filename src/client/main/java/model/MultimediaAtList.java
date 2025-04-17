package model;

public class MultimediaAtList {
    private final Multimedia multimedia;

    private MultimediaStatus status;
    private int currentEpisode;

    public MultimediaAtList(Multimedia multimedia, MultimediaStatus status, int currentEpisode) {
        this.multimedia = multimedia;
        this.status = status;
        this.currentEpisode = currentEpisode;
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

    public int getCurrentEpisode() {
        return currentEpisode;
    }

    public void setCurrentEpisode(int currentEpisode) {
        this.currentEpisode = currentEpisode;
    }
}
