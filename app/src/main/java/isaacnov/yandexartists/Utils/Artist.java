package isaacnov.yandexartists.Utils;

import java.io.Serializable;

/**
 * Класс, отображающий один элемент массива исполнителей из файла JSON.
 *
 * @author Max K.
 */
public class Artist implements Serializable, Comparable<Artist> {

    private int id;
    private String name;
    private String[] genres;
    private int tracks;
    private int albums;
    private String link;
    private String description;
    private String coverSmall;
    private String coverBig;

    public Artist(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public void setTracks(int tracks) {
        this.tracks = tracks;
    }

    public void setAlbums(int albums) {
        this.albums = albums;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoverSmall(String coverSmall) {
        this.coverSmall = coverSmall;
    }

    public void setCoverBig(String coverBig) {
        this.coverBig = coverBig;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getGenres() {
        return genres;
    }

    public int getTracks() {
        return tracks;
    }

    public int getAlbums() {
        return albums;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverSmall() {
        return coverSmall;
    }

    public String getCoverBig() {
        return coverBig;
    }

    @Override
    public int compareTo(Artist another) {
        return this.getName().compareTo(another.getName());
    }
}
