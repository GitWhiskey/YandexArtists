package isaacnov.yandexartists.LoadingUtils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import isaacnov.yandexartists.Utils.Artist;
import isaacnov.yandexartists.R;
import isaacnov.yandexartists.Utils.Utils;

import java.util.List;


/**
 * Адаптер для загрузки каждого эелемента списка исполнителей.
 *
 * @author Max K.
 */
public class ArtistsArrayAdapter extends ArrayAdapter<Artist> {

    private LayoutInflater inflater;
    private Context context;
    private String TAG = "ADAPTER";
    /** Размер обложки иполнителя в списке */
    private int coverSize;
    /** Используется для того, чтобы выводить сообщение о потере подключения к сети
     *  или восстонавлении подключения лишь один раз. */
    private boolean userKnowsInternetIsDown;

    public ArtistsArrayAdapter(Context context, List<Artist> objects, int coverSize) {
        super(context, R.layout.artist_list_item, objects);
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.coverSize = coverSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.artist_list_item, parent, false);
        }

        Artist artist = getItem(position);
        Log.i(TAG, "--LOADING ARTIST: "+ artist.getName());
        Log.i(TAG, "--LOADING FILE FROM "+artist.getCoverSmall());
        ImageView imageView = (ImageView) view.findViewById(R.id.artist_list_image);
        TextView name = (TextView) view.findViewById(R.id.artist_list_name);
        TextView genres = (TextView) view.findViewById(R.id.artist_list_genres);
        TextView albums = (TextView) view.findViewById(R.id.artist_list_albums);
        TextView tracks = (TextView) view.findViewById(R.id.artist_list_tracks);

        RelativeLayout.LayoutParams imageLayout = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        imageLayout.width = coverSize;
        imageLayout.height = coverSize;
        imageView.setLayoutParams(imageLayout);
        Picasso picasso = Picasso.with(context);
        picasso.load(artist.getCoverSmall()).resize(coverSize, coverSize)
                .placeholder(R.drawable.unknown).resize(coverSize, coverSize)
                .into(imageView, new ToastCallback());
        name.setText(artist.getName());
        setGenres(view, genres, artist.getGenres());
        String albumsText = view.getResources().getString(R.string.keyword_albums) + artist.getAlbums();
        albums.setText(albumsText);
        String tracksText = view.getResources().getString(R.string.keyword_tracks) + artist.getTracks();
        tracks.setText(tracksText);
        return view;
    }

    private void setGenres(View view, TextView textView, String[] genres) {
        if (genres.length != 0) {
            StringBuilder sb = new StringBuilder(view.getResources().getString(R.string.keyword_genres));
            for (String genre : genres) {
                sb.append(genre + ", ");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            textView.setText(sb.toString().trim());
        }
    }

    private class ToastCallback implements Callback {

        @Override
        public void onSuccess() {
            if(Utils.isOnline(context) && userKnowsInternetIsDown) {
                String message = context.getString(R.string.info_inet_isback_toast);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                userKnowsInternetIsDown = false;
            }
        }

        @Override
        public void onError() {
            if(!Utils.isOnline(context) && !userKnowsInternetIsDown) {
                String message = context.getString(R.string.error_no_inet_toast);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                userKnowsInternetIsDown = true;
            }
        }
    }
}
