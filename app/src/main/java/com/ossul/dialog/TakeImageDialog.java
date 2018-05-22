package com.ossul.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.ossul.appconstant.AppConstants;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.interfaces.ImagePickerCallback;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * @author Rajan
 */
public class TakeImageDialog {
    private ImagePickerCallback imagePickerCallback;
    private Activity activity;
    private Uri mCapturedImageURI;
    private String imageName = "";

    public TakeImageDialog(Activity activity, ImagePickerCallback imagePickerCallback) {
        this.activity = activity;
        this.imagePickerCallback = imagePickerCallback;
    }

    private static Bitmap decodeSampledBitmapFromResource(File file, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static File saveOutput(Context context, String filePath) {
        File file = new File(filePath);
        int WIDTH = 180;
        int HEIGHT = 180;
        Bitmap croppedImage = adjustOrientation(filePath, decodeSampledBitmapFromResource(file, WIDTH, HEIGHT));
        OutputStream outputStream;
        try {
            File tempFile = new File(context.getCacheDir(), System.currentTimeMillis() + ".jpg");
            tempFile.createNewFile();
            outputStream = new FileOutputStream(tempFile);
            if (croppedImage != null && outputStream != null) {
                croppedImage.compress(Bitmap.CompressFormat.PNG, 10, outputStream);
                return tempFile;
            }

            if (croppedImage != null) {
                croppedImage.recycle();
                return file;
            }


        } catch (IOException ignored) {
            return null;
        }
        return null;
    }

    private static Bitmap adjustOrientation(String filepath, Bitmap imageToBeCropped) {
        Matrix matrix = new Matrix();
        matrix.postRotate(getFileRotation(filepath));
        imageToBeCropped = Bitmap.createBitmap(imageToBeCropped, 0, 0, imageToBeCropped.getWidth(), imageToBeCropped.getHeight(), matrix, true);
        return imageToBeCropped;
    }

    private static int getFileRotation(String filepath) {
        int rotate = 0;
        try {
            ExifInterface exif = new ExifInterface(filepath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    // get sd card path of image
    private String getPath(Intent intent) {
        Cursor cursor = null;
        String fileImagePath = "";
        try {
            Uri selectedImage;
            if (intent == null) {
                selectedImage = mCapturedImageURI;
            } else {
                if (intent.getData() == null) {
                    selectedImage = mCapturedImageURI;
                } else {
                    selectedImage = intent.getData();
                }
            }
            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            cursor = activity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                fileImagePath = cursor.getString(columnIndex);
                cursor.close();
            }
        } catch (Exception ignored) {

        } finally {
            if (cursor != null)
                cursor.close();
        }
        return fileImagePath;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("activity res", "requestCode=>" + requestCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.REQ_CODE_GALLERY_IMAGE_PICK || requestCode == AppConstants.REQ_CODE_CAMERA_IMAGE_PICK) {
                String filePath = getPath(data);
                String fileFormat = null;
                if (filePath != null) {
                    String encoded;
                    try {
                        encoded = URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
                    } catch (UnsupportedEncodingException e) {
                        encoded = filePath;
                    }
                    fileFormat = MimeTypeMap.getFileExtensionFromUrl(encoded);
                }
                if (filePath != null && fileFormat != null && !fileFormat.isEmpty()) {

                    File file = saveOutput(activity, filePath);
                    if (file != null) {
                        imagePickerCallback.onImageClicked(file, filePath, imageName);
                    }
                }
            }
        }
    }

    public AlertDialog.Builder getImagePickerDialog(final Activity activity, String imageName) {
        this.activity = activity;
        this.imageName = imageName;
        String album = "Album", camera = "Camera";
        if (!Validator.isEmptyString(AppPreferences.get().getLabels())) {
            try {
                JSONObject jsonObject = new JSONObject(AppPreferences.get().getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Camera")))
                        camera = (AppUtilsMethod.getValueFromKey(jsonObject, "Camera"));
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Album")))
                        album = (AppUtilsMethod.getValueFromKey(jsonObject, "Album"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        CharSequence options[] = new CharSequence[]{album, camera};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openGallery();
                        break;
                    case 1:
                        openCamera();
                }
            }
        });
        return builder;
    }

    public AlertDialog.Builder getImagePickerDialog(final Activity activity, String imageName, String title) {
        this.activity = activity;
        this.imageName = imageName;
        String album = "Album", camera = "Camera";
        if (!Validator.isEmptyString(AppPreferences.get().getLabels())) {
            try {
                JSONObject jsonObject = new JSONObject(AppPreferences.get().getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Camera")))
                        camera = (AppUtilsMethod.getValueFromKey(jsonObject, "Camera"));
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Album")))
                        album = (AppUtilsMethod.getValueFromKey(jsonObject, "Album"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        CharSequence options[] = new CharSequence[]{album, camera};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (!Validator.isEmptyString(title)) {
            builder.setTitle(title);
        }
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openGallery();
                        break;
                    case 1:
                        openCamera();
                }
            }
        });
        return builder;
    }

    private void openGallery() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            activity.startActivityForResult(intent, AppConstants.REQ_CODE_GALLERY_IMAGE_PICK);
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, activity.getPackageName());
        mCapturedImageURI = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        activity.startActivityForResult(cameraIntent, AppConstants.REQ_CODE_CAMERA_IMAGE_PICK);
    }

}