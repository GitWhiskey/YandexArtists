package isaacnov.yandexartists.LoadingUtils;

import isaacnov.yandexartists.Utils.Artist;
import minimalJson.Json;
import minimalJson.JsonArray;
import minimalJson.JsonObject;
import minimalJson.JsonValue;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Парсер файла JSON. Каждый элемент массива исполнителей файла JSON записывается в класс Artist
 * и добавляется в ArrayList исполнителей. После парсинга список сортируется в алфавитнм порядке
 * по именам исполнителей. Парсинг производится при помощи minimal-json
 * (https://github.com/ralfstx/minimal-json).
 *
 * @author Max K.
 */
public class ArtistParser {

    private static ArrayList<Artist> artists = new ArrayList<>();

    public static void parse(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        parse(reader);
    }

    public static void parse(Reader reader) throws IOException {
        JsonArray array = Json.parse(reader).asArray();
        for (JsonValue artist : array) {
            JsonObject artistObj = artist.asObject();
            int id = artistObj.getInt("id", 0);
            String name = artistObj.getString("name", "");
            JsonArray genresJA = artistObj.get("genres").asArray();
            String[] genres = convertJAtoStringA(genresJA);
            int tracks = artistObj.getInt("tracks", 0);
            int albums = artistObj.getInt("albums", 0);
            String link = artistObj.getString("link", null);
                 String description = artistObj.getString("description", null);
            if(!description.isEmpty()) {
                description = description.substring(0,1).toUpperCase() + description.substring(1);
            }
            JsonObject coversObj = artistObj.get("cover").asObject();
            String coverSmall = coversObj.getString("small", null);
            String coverBig = coversObj.getString("big", null);
                 Artist a = new Artist(id, name);
            a.setGenres(genres);
            a.setTracks(tracks);
            a.setAlbums(albums);
            a.setLink(link);
            a.setDescription(description);
            a.setCoverSmall(coverSmall);
            a.setCoverBig(coverBig);
            artists.add(a);
        }
        reader.close();
        Collections.sort(artists);
    }

    public static ArrayList<Artist> getArtists() {
        return artists;
    }

    private static String[] convertJAtoStringA(JsonArray ja) {
        int size = ja.size();
        String[] res = new String[ja.size()];
        for (int i = 0; i < size; i++) {
            res[i] = ja.get(i).asString();
        }
        return res;
    }
}
