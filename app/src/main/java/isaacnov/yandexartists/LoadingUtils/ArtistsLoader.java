package isaacnov.yandexartists.LoadingUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import isaacnov.yandexartists.Activities.ArtistInfoActivity;
import isaacnov.yandexartists.Activities.MainActivity;
import isaacnov.yandexartists.Utils.Artist;
import isaacnov.yandexartists.R;
import isaacnov.yandexartists.Utils.Utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Класс-загрузчик списка артистов. Загружает список в фоновом режиме.
 * @author Max K.
 */
public class ArtistsLoader extends AsyncTask<Boolean, Void, ArtistLoaderResult> {

    private Context context;
    private RelativeLayout errorLayout;
    private int smallCoverSize;

    ProgressBar bar;

    public ArtistsLoader(Context context, RelativeLayout errorLayout, int smallCoverSize) {
        super();
        this.context = context;
        this.errorLayout = errorLayout;
        this.smallCoverSize = smallCoverSize;
        bar = (ProgressBar) ((Activity) context).findViewById(R.id.progressBar);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bar.setVisibility(View.VISIBLE);
    }

    /**
     * Основной метод загрузки списка. В зависимости от передаваемых на вход параметров
     * либо загружает файл JSON из сети, либо считывает из памяти устройства, либо просто
     * передает готовый список.
     * @param params - 2 boolean параметра:
     *               1. loadJson - нужно ли загружать файл.
     *               2. loadFromNet - нужно ли загружать из сети.
     * @return - пара значений: сообщеиние о результате загрузки и ArtistArrayAdapter.
     *                             Если во время загрузки или парсинга произошла ошибка,
     *                             ArtistArrayAdapter будет равен null.
     */
    @Override
    protected ArtistLoaderResult doInBackground(Boolean... params) {
        boolean loadJson = params[0];
        boolean loadFromNet = params[1];
        String message;

        if(loadJson) {
            if(loadFromNet) {
                try {
                    URL url = new URL(Utils.FILE_URL);
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    int fileSize = connection.getContentLength();
                    boolean mk = new File(Utils.DEST_DIR).mkdirs();
                    boolean storageAvailable =isStorageAvailable(fileSize);
                    if(!(mk) || !storageAvailable) {
                        //Если внешнее хранилище недоступно, или в нем недостаточно места для хранения
                        //файла - скачиваем, но не записываем файл.
                        return downloadWithoutWriting(url);
                    }
                    downloadFile(url);
                } catch (IOException e) {
                    Log.e("DOWNLOAD", "Error", e);
                    message = context.getString(R.string.error_download);
                    return new ArtistLoaderResult(message, null);
                }
            }
            try {
                ArtistParser.parse(Utils.DEST_FILE);
            } catch (IOException e) {
                Log.e("PARSE", "Error", e);
                message = context.getString(R.string.error_parse);
                return new ArtistLoaderResult(message, null);
            }
        }
        ((MainActivity) context).setFileLoadNeeded(false);
        List<Artist> artists = ArtistParser.getArtists();
        ArtistsArrayAdapter adapter = new ArtistsArrayAdapter(context, artists, smallCoverSize);
        return new ArtistLoaderResult("Success", adapter);
    }

    public boolean isStorageAvailable(int fileSize) {
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            int blockSize = stat.getBlockSize();
            int availableBlocks = stat.getAvailableBlocks();
            return (fileSize < blockSize*availableBlocks);
        }
        return false;
    }

    private void downloadFile(URL url) throws IOException {
        InputStream input = new BufferedInputStream(url.openStream());
        OutputStream output = new FileOutputStream(new File(Utils.DEST_FILE));
        int n;
        byte data[] = new byte[1024];
        while ((n = input.read(data)) != -1) {
            output.write(data, 0, n);
        }
        output.close();
        input.close();
    }

    private ArtistLoaderResult downloadWithoutWriting(URL url) {
        try {
            Reader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            ArtistParser.parse(reader);
            reader.close();
        } catch (IOException e) {
            String message = context.getString(R.string.error_parse);
            return new ArtistLoaderResult(message, null);
        }
        ((MainActivity) context).setFileLoadNeeded(false);
        List<Artist> artists = ArtistParser.getArtists();
        ArtistsArrayAdapter adapter = new ArtistsArrayAdapter(context, artists, smallCoverSize);
        return new ArtistLoaderResult("Success", adapter);
    }

    @Override
    protected void onPostExecute(ArtistLoaderResult result) {
        super.onPostExecute(result);
        bar.setVisibility(View.GONE);
        if(!result.getMessage().equals("Success")) {
            TextView errorInfo = (TextView)((Activity) context).findViewById(R.id.error_info);
            errorInfo.setText(result.getMessage());
            errorLayout.setVisibility(View.VISIBLE);
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.textshake);
            shake.setRepeatCount(2);
            errorInfo.startAnimation(shake);
        } else {
            final ListView list = (ListView) ((Activity) context).findViewById(R.id.list_artists);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Artist artist = (Artist) parent.getItemAtPosition(position);
                    Intent intent = new Intent(context, ArtistInfoActivity.class);
                    intent.putExtra("artist", artist);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.left_out, R.anim.right_in);
                }
            });
            list.setAdapter(result.getAdapter());
        }
    }
}
