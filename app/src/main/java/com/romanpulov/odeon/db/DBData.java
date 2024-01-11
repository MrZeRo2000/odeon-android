package com.romanpulov.odeon.db;

import java.util.ArrayList;
import java.util.List;

public class DBData {
    private List<Artist> artists = new ArrayList<>();
    private List<Artifact> artifacts = new ArrayList<>();
    private List<Composition> compositions = new ArrayList<>();

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public List<Composition> getCompositions() {
        return compositions;
    }

    public void setCompositions(List<Composition> compositions) {
        this.compositions = compositions;
    }

    public static DBData from(List<Artist> artists, List<Artifact> artifacts, List<Composition> compositions) {
        DBData instance = new DBData();
        instance.setArtists(artists);
        instance.setArtifacts(artifacts);
        instance.setCompositions(compositions);

        return instance;
    }

    private DBData() {}
}
