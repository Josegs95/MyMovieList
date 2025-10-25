package model;

public enum MultimediaStatus {
    PLAN_TO_WATCH,
    WATCHING,
    ON_HOLD,
    DROPPED,
    FINISHED;

    public static MultimediaStatus[] getMultimediaStatusValues(MultimediaType multimediaType) {
        if (multimediaType == MultimediaType.MOVIE) {
            return new MultimediaStatus[] { PLAN_TO_WATCH, FINISHED };
        } else {
            return MultimediaStatus.values();
        }
    }
}
