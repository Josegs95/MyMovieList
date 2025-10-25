package model;

public enum MultimediaType {
    MOVIE("Movie"),
    TV_SHOW("Tv show");

    private final String name;

    MultimediaType(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
