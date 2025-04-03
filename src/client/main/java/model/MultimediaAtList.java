package model;

public class MultimediaAtList {
    private final Multimedia multimedia;
    private final MultimediaType multimediaType;

    private MultimediaStatus status;
    private int currentEpisode;

    public MultimediaAtList(Multimedia multimedia, MultimediaStatus status, int currentEpisode,
                            MultimediaType multimediaType) {
        this.multimedia = multimedia;
        this.status = status;
        this.currentEpisode = currentEpisode;

        this.multimediaType = multimediaType;
    }

    public MultimediaAtList(Multimedia multimedia, MultimediaStatus status,
                            MultimediaType multimediaType) {
        this(multimedia, status, -1, multimediaType);
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

    public MultimediaType getMultimediaType() {
        return multimediaType;
    }
}
