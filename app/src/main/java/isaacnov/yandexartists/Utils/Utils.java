package isaacnov.yandexartists.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * Вспомогательный класс.
 *
 * @author Max K.
 */
public class Utils {

    /** Ссылка на файл JSON с данными об исполнителях */
    public static final String FILE_URL = "http://download.cdn.yandex.net/mobilization-2016/artists1.json";
    /** Директория в памяти устройства, где будет храниться скаченный файл */
    public static final String DEST_DIR = Environment.getExternalStorageDirectory().toString() + "/YandexArtists";
    /** Путь к скаченному файлу */
    public static final String DEST_FILE = DEST_DIR + "/artists.json";

    /**
     * Проверяет наличие подключения к сети.
     * @param context активити, вызывающая метод.
     * @return true, если подключение есть, false, если подключения нет.
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
