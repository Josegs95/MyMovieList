package model.entity;

public enum MultimediaStatus {
    PLAN_TO_WATCH,
    WATCHING,
    ON_HOLD,
    DROPPED,
    FINISHED;

    public static MultimediaStatus[] getMultimediaStatusValues(MultimediaType multimediaType) {
        if (multimediaType == MultimediaType.MOVIE) {
            return new MultimediaStatus[] { PLAN_TO_WATCH, FINISHED };
        }
        else if (multimediaType == MultimediaType.SERIES) {
            return MultimediaStatus.values();
        } else {
            throw new IllegalStateException("Invalid Multimedia type: " + multimediaType.name());
        }
    }
}
