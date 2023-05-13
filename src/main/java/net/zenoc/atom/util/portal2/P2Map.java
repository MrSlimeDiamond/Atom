package net.zenoc.atom.util.portal2;

public class P2Map {
    String mapName;
    String formattedName;
    int chapter;
    Boolean coop;
    int steamMapID;
    public P2Map(String mapName, String formattedName, int chapter, Boolean coop, int steamMapID) {
        this.mapName = mapName;
        this.formattedName = formattedName;
        this.chapter = chapter;
        this.coop = coop;
        this.steamMapID = steamMapID;
    }

    public String getName() {
        return this.mapName;
    }

    public String getFormattedName() {
        return this.formattedName;
    }

    public int getChapter() {
        return this.chapter;
    }

    public Boolean isCoop() {
        return this.coop;
    }

    public Integer getSteamMapID() {
        return this.steamMapID;
    }
}
