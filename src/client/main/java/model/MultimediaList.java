package model;

import java.util.Map;

public class MultimediaList {
    final private String NAME;
    final private Map<Multimedia, MultimediaStatus> MULTIMEDIA_MAP;

    public MultimediaList(String name, Map<Multimedia, MultimediaStatus> multimediaMap){
        this.NAME = name;
        this.MULTIMEDIA_MAP = multimediaMap;
    }

    public String getNAME() {
        return NAME;
    }

    public Map<Multimedia, MultimediaStatus> getMULTIMEDIA_MAP() {
        return MULTIMEDIA_MAP;
    }

    @Override
    public String toString() {
        return NAME + " (" + MULTIMEDIA_MAP.size() + " items)";
    }
}
