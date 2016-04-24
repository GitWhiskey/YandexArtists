package isaacnov.yandexartists.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import isaacnov.yandexartists.Utils.Artist;
import isaacnov.yandexartists.R;

public class ArtistInfoActivity extends AppCompatActivity {

    private LinearLayout mainLayout;
    private int bigCoverSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);
        Intent intent = getIntent();
        Artist artist = (Artist) intent.getSerializableExtra("artist");

        mainLayout = (LinearLayout) findViewById(R.id.artist_info_main);
        ImageView image = (ImageView) findViewById(R.id.artist_info_image);
        TextView description = (TextView) findViewById(R.id.artist_info_descrip);
        TextView genres = (TextView) findViewById(R.id.artist_info_genres);
        TextView albums = (TextView) findViewById(R.id.artist_info_albums);
        TextView tracks = (TextView) findViewById(R.id.artist_info_tracks);
        TextView link = (TextView) findViewById(R.id.artist_info_link);

        getSupportActionBar().setTitle(artist.getName());
        setBigCover(getResources().getConfiguration().orientation, image, artist.getCoverBig());
        if(artist.getDescription() != null) {
            description.setText(artist.getDescription());
        } else {
            description.setVisibility(View.GONE);
        }
        setGenres(genres, artist.getGenres());
        albums.append(String.valueOf(artist.getAlbums()));
        tracks.append(String.valueOf(artist.getTracks()));
        if(artist.getLink() != null) {
            link.setText(artist.getLink());
        } else {
            findViewById(R.id.artist_info_linkheader).setVisibility(View.GONE);
            link.setVisibility(View.GONE);
        }
    }

    private void setBigCover(final int orientation, final ImageView image, final String path) {
        image.post(new Runnable() {
            @Override
            public void run() {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    bigCoverSize = mainLayout.getMeasuredHeight()-20;
                } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
                    bigCoverSize = mainLayout.getMeasuredWidth();
                }
                LinearLayout.LayoutParams imageLayout = (LinearLayout.LayoutParams) image.getLayoutParams();
                Log.i("--IMAGE SIZE: ", ""+bigCoverSize);
                imageLayout.width = bigCoverSize;
                imageLayout.height = bigCoverSize;
                image.setLayoutParams(imageLayout);
                Picasso.with(ArtistInfoActivity.this).load(path).resize(bigCoverSize, bigCoverSize)
                        .placeholder(R.drawable.unknown).resize(bigCoverSize, bigCoverSize)
                        .into(image);
            }
        });
    }

    private void setGenres(TextView textView, String[] genres) {
        if(genres.length != 0) {
            StringBuilder sb = new StringBuilder();
            for (String genre : genres) {
                sb.append(genre + ", ");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            textView.append(sb.toString().trim());
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_out, R.anim.left_in);
    }
}
