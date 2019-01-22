package view.edit.input.imagecompressor;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.github.chrisbanes.photoview.PhotoView;

public class PhotoViewActivity extends AppCompatActivity {

    private PhotoView photoView;
    private ImageButton closeButton;

    public static void startPhotoViewActivity(Context context, String imagePath) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(PhotoViewActivity.class.getSimpleName(), imagePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_layout_view);

        photoView = findViewById(R.id.image);
        closeButton = findViewById(R.id.ib_close);

        processImageView();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void processImageView() {
        Intent intent = getIntent();
        String imagePath = intent
                .getStringExtra(PhotoViewActivity.class.getSimpleName());
        photoView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        photoView.setZoomable(true);
    }

}
