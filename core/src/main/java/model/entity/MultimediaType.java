package model.entity;

import com.fasterxml.jackson.annotation.JsonValue;

@SuppressWarnings("unused")
public enum MultimediaType {
    MOVIE("Movie"),
    SERIES("Series"),
    PERSON("Person");

    private final String name;

    MultimediaType(String name){
        this.name = name;
    }

    @JsonValue
    public String getTMDBValue() {
        return switch (this) {
            case MOVIE -> "movie";
            case SERIES -> "tv";
            case PERSON -> "person";
        };
    }

    @Override
    public String toString() {
        return name;
    }
}
