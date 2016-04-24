package isaacnov.yandexartists.Activities;

import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import isaacnov.yandexartists.LoadingUtils.ArtistsLoader;
import isaacnov.yandexartists.R;
import isaacnov.yandexartists.Utils.Utils;

import java.io.*;

/**
 * Основная активити для отображения списка исполнителей, или вывода информации об ошибке, если список
 * загрузить не удалось.
 *
 * @author Max K.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /** Контейнер для вывода ошибок и кнопок для продолжения */
    private RelativeLayout errorLayout;
    /** Размер изображения обложки в списке */
    private int imgSize = 100;
    /** Указывает, нужо ли загрузить файл JSON */
    private boolean fileLoadNeeded = true;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
        list = (ListView)findViewById(R.id.list_artists);
        setSmallCoversSize(getResources().getConfiguration().orientation);

        Parcelable state = null;
        if(savedInstanceState != null) {
            //Если есть сохраненные данные, проверяем, нужно ли повторно закачивать и парсить файл
            fileLoadNeeded = savedInstanceState.getBoolean("FILE_LOAD_NEEDED");
            state = savedInstanceState.getParcelable("LIST_STATE");
        }

        loadArtistList();
        if(state != null) list.onRestoreInstanceState(state);
    }

    /**
     * Метод, реализующий загрузку списка исполнителей. В зависимости от ситуации метод
     * выбирает, скачать файл из сети, загрузить файл из памяти устройства, или просто получить
     * готовый загруженый список (например, если активити была пересоздана при повороте экрана).
     */
    private void loadArtistList() {
        String message;
        if(!fileLoadNeeded) {
            //Если загружать и парсить файл не нужно, просто выводим список исполнителей
            getList();
        } else {
            if(!Utils.isOnline(this)) {
                TextView errorInfo = (TextView) findViewById(R.id.error_info);
                if(new File(Utils.DEST_FILE).exists()) {
                    message = getString(R.string.error_no_inet_loadold);
                    errorInfo.setText(message);
                } else {
                    message = getString(R.string.error_no_inet);
                    errorInfo.setText(message);
                    hideLoadOldBtn();
                }
                errorLayout.setVisibility(View.VISIBLE);
                Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.textshake);
                shake.setRepeatCount(2);
                errorInfo.startAnimation(shake);
            } else {
                //Если при составлении списка сбоев не произошло, запоминаем, что его больше качать не нужно
                loadListFromNet();
            }
        }
    }

    /**
     * Метод, позволяющий получить уже готовый список артистов, хранящийся
     * в классе ArtistParser.
     */
    private void getList() {
        new ArtistsLoader(this, errorLayout, imgSize).execute(false, false);
    }

    /**
     * Метод, инициируюший процесс полученния файла из сети.
     */
    private void loadListFromNet() {
        ArtistsLoader loader = new ArtistsLoader(this, errorLayout, imgSize);
        loader.execute(true, true);
    }

    /**
     * Метод, инициируюший процесс считывания файла из памяти телефона.
     */
    private void loadListLocal() {
        ArtistsLoader loader = new ArtistsLoader(this, errorLayout, imgSize);
        loader.execute(true, false);
    }

    /**
     * Метод устанавливает размер ширину и высоту изображения обложки в списке
     * в зависимости от положения экрана..
     * @param orientation - положение экрана устройства.
     */
    private void setSmallCoversSize(final int orientation) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imgSize = displayMetrics.heightPixels/3;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
            imgSize = displayMetrics.heightPixels/5;
        }
    }

    private void hideLoadOldBtn() {
        findViewById(R.id.btn_load_old).setVisibility(View.GONE);
        Button tryAgainBtn = (Button)findViewById(R.id.btn_try_again);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.BELOW, R.id.error_info);
        tryAgainBtn.setLayoutParams(params);
    }

    public void setFileLoadNeeded(boolean needed) {
        fileLoadNeeded = needed;
    }

    @Override
    public void onClick(View v) {
        findViewById(R.id.error_layout).setVisibility(View.GONE);
        if(v.getId() == R.id.btn_try_again) {
            loadArtistList();
        }
        if(v.getId() == R.id.btn_load_old) {
            loadListLocal();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putBoolean("FILE_LOAD_NEEDED", fileLoadNeeded);
        if(list != null) {
            Parcelable state = list.onSaveInstanceState();
            b.putParcelable("LIST_STATE", state);
        } else {
            b.putParcelable("LIST_STATE", null);
        }
    }
}

