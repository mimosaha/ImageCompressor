package view.edit.input.imagecompressor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

import id.zelory.compressor.Compressor;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView actualImageView;
    private ImageView compressedImageView;
    private TextView actualSizeTextView;
    private TextView compressedSizeTextView;

    private File actualImage;
    private File compressedImage;
    private int height, width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actualImageView = findViewById(R.id.actual_image);
        compressedImageView = findViewById(R.id.compressed_image);
        actualSizeTextView = findViewById(R.id.actual_size);
        compressedSizeTextView = findViewById(R.id.compressed_size);

        actualImageView.setBackgroundColor(getRandomColor());
        clearImage();

        setDeviceDimension();
    }

    private void setDeviceDimension() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        Log.v("MIMO_SAHA::", "height: " + height + " Width: " + width);
    }

    public void openActualImage(View view) {
        PhotoViewActivity.startPhotoViewActivity(this,
                actualImage.getAbsolutePath());
    }

    public void openCompressedImage(View view) {
        PhotoViewActivity.startPhotoViewActivity(this,
                compressedImage.getAbsolutePath());
    }

    private void clearImage() {
        actualImageView.setBackgroundColor(getRandomColor());
        compressedImageView.setImageDrawable(null);
        compressedImageView.setBackgroundColor(getRandomColor());
        compressedSizeTextView.setText("Size : -");
    }

    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void compressImage(View view) {
        if (actualImage == null) {
            showError("Please choose an image!");
        } else {
            try {
                compressedImage = new Compressor(this)
                        .compressToFile(actualImage);
                setCompressedImage();

                compressedImageView.setImageBitmap(new Compressor(this)
                        .compressToBitmap(actualImage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    https://github.com/zetbaitsu/Compressor

    public void customCompressImage(View view) {
        if (actualImage == null) {
            showError("Please choose an image!");
        } else {
            try {
                compressedImage = new Compressor(this)
                        .setMaxWidth(width)
                        .setMaxHeight(height)
                        .setQuality(100)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(actualImage);

                setCompressedImage();
            } catch (IOException e) {
                e.printStackTrace();
                showError(e.getMessage());
            }
        }
    }

    private void setCompressedImage() {
        compressedImageView.setImageBitmap(BitmapFactory
                .decodeFile(compressedImage.getAbsolutePath()));
        compressedSizeTextView.setText(String.format("Size : %s",
                getReadableFileSize(compressedImage.length())));

        showError("Compressed image save in " + compressedImage.getPath());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                actualImage = FileUtil.from(this, data.getData());
                actualImageView.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                actualSizeTextView.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));
                clearImage();
            } catch (IOException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }

    public void showError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private int getRandomColor() {
        Random rand = new Random();
        return Color.argb(100, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
