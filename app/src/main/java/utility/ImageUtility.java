package utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by Otto on 4/14/2016.
 * This class is used for image handling
 */
public class ImageUtility {

    // Constructor
    public ImageUtility() {
    }

    /**
     * The calculateInSampleSize method is a helper that calculates the BitmapFactory.Option.inSampleSize
     * required to sample and resize a large image file into a specific size. The returned
     * size is then used by other methods.
     *
     * @param options options for resize
     * @param reqWidth width desired
     * @param reqHeight height desired
     * @return inSampleSize
     * @since 2016-04-14
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * The decodeSampledBitmapFromResource method takes in a resource is of an image and
     * resizes that image to the required width and hieght. This is used to sample and resize
     * a large image into a icon or a limited space. Also allows for less memory use.
     *
     * @param res resources for application
     * @param resId specific resource
     * @param reqWidth required width
     * @param reqHeight required height
     * @return Bitmap
     *
     * Usage:
     * //Creating Bitmap only
     * Bitmap bitmap = decodeSampledBitmapFromResource(context.getResources(),R.drawable.id,width,height);
     * //Set an ImageView
     * imageView.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), R.???.??id, width, height));
     *
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * The decodeSampledBitmapFromStream is used to sample and resize an image from
     * an InputStream
     *
     * @param fileIn
     * @param reqWidth
     * @param reqHeight
     * @return Bitmap
     * @author Otto L Lecuona
     * @version 1.0
     * @since 2016-04-14
     *
     * Usage:
     * //Get the input file
     * File inputFile = new File(filePath,fileName);
     * //Create the InputStream
     * InputStream fileInputStream = null;
     * fileInputStream = new FileInputStream(inputFile);
     *
     * //Call method to generate a sampled Bitmap
     * Bitmap newBitmap = decodeSampledBitmapFromStream(fileInputStream,width,height);
     * //Or to directly set an image view
     * imageView.setImageBitmap(decodeSampledBitmapFromStream(fileInputStream,width,height));
     */
    public static Bitmap decodeSampledBitmapFromStream(InputStream fileIn,
                                                       int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(fileIn, null, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(fileIn, null, options);
    }

    /**
     * The decodeSampledBitmapFromFile method samples and resizes a Bitmap from
     * a file
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return Bitmap
     *
     * @author Otto L Lecuona
     * @version 1.0
     * @since 2016-04-14
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * Process the bitmap that cames from a call to camera intent
     * @param TAG
     * @param photo
     * @param folder
     * @param file
     * @return Bitmap from camera
     */
    public Bitmap processCameraRequest(String TAG, Bitmap photo, String folder, String file){

        OutputStream fos = null;
        Bitmap image = null;

        // Process photo
        try {

            // Create file and outputstream
            File outFile = new File(folder, file);
            fos = new FileOutputStream(outFile);

            // Create a png file
            photo.compress(Bitmap.CompressFormat.PNG, 0, fos);

            // Show picture in imageview
            ImageUtility patientIcon = new ImageUtility();
            String iconFile = folder + file;
            image = ImageUtility.decodeSampledBitmapFromFile(iconFile, 96, 96);

        } catch (NullPointerException npe) {
                Log.e(TAG, "onActivityResult() ioe", npe);

        } catch (FileNotFoundException fnf) {

                Log.e(TAG, "onActivityResult()", fnf);

        } finally {
            // Close out the file
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }

            } catch (IOException ioe) {
                Log.e(TAG, "onActivityResult() on close", ioe);

            }
        }

        return image;

    }

    /**
     * Load a picture from the resource.
     * @param resources
     * @param resourceId
     * @return
     */
    public static Bitmap loadBitmapFromResource(Resources resources, int resourceId) {
        // Not scale the bitmap. This will turn the bitmap in raw pixel unit.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        // Load the bitmap object.
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId, options);
        // Set the density to NONE. This is needed for the ImageView to not scale.
        bitmap.setDensity(Bitmap.DENSITY_NONE);

        return bitmap;
    }

    /**
     * Load a picture from assets.
     * <p>
     * This method will throw RuntimeException if the image file does not exist.
     * @param context
     * @param assetPath The image file path in the assets folder.
     *   For example, if the image file is <code>assets/images/abc.png</code>,
     *   then assetPath is <code>images/abc.png</code>
     * @return
     */
    public static Bitmap loadBitmapFromAssetNoThrow(Context context, String assetPath) {
        try {
            return loadBitmapFromAsset(context, assetPath);
        } catch (IOException e) {
            throw ErrorUtil.runtimeException(e);
        }
    }

    /**
     * Load a picture from assets.
     *
     * @param context
     * @param assetPath The image file path in the assets folder.
     *   For example, if the image file is <code>assets/images/abc.png</code>,
     *   then assetPath is <code>images/abc.png</code>
     * @return
     * @throws IOException
     */
    public static Bitmap loadBitmapFromAsset(Context context, String assetPath) throws IOException {
        return BitmapFactory.decodeStream(context.getAssets().open(assetPath));
    }

    /**
     * Load Bitmap from the internet.
     * @param imageUrl
     * @return
     * @throws IOException
     */
    public static Bitmap loadBitmapFromInternet(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        return BitmapFactory.decodeStream(url.openConnection().getInputStream());
    }

    /**
     * Load Bitmap from the internet.
     * @param imageUrl
     * @param defaultValue Value to be returned if loading fails.
     * @return Bitmap object or null.
     */
    public static Bitmap loadBitmapFromInternet(String imageUrl, Bitmap defaultValue) {
        Bitmap bitmap = defaultValue;
        try {
            bitmap = loadBitmapFromInternet(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Calculate ratio to scale an object to fit the max size (width or height).
     * <br/>
     * Usage example:
     * <br/>
     * <pre>
     *   float ratio = sizeFitRatio(width, height, maxWidth, maxHeight);
     *   // Calculate size to fit maximum width or height.
     *   int newWidth = ratio * width;
     *   int newHeight = ratio * height;
     * </pre>
     * @param objectWidth
     * @param objectHeight
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static float sizeFitRatio(int objectWidth, int objectHeight, int maxWidth, int maxHeight) {
        float ratio = 1f; // Default ratio to 1.
        if (objectWidth != 0 && objectHeight != 0) {
            float ratioWidth = maxWidth / (float)objectWidth;
            float ratioHeight = maxHeight / (float)objectHeight;
            float minRatio = (ratioWidth < ratioHeight) ? ratioWidth : ratioHeight;
            if (minRatio > 0) {
                ratio = minRatio;
            }
        }
        return ratio;
    }

    /**
     * Scale an bitmap to fit frame size.
     * @param bitmap
     * @param frameWidth
     * @param frameHeight
     * @return Scaled bitmap, or the bitmap itself if scaling is unnecessary.
     */
    public static Bitmap scaleToFitFrame(Bitmap bitmap, int frameWidth, int frameHeight) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        float ratio = sizeFitRatio(bitmapWidth, bitmapHeight, frameWidth, frameHeight);
        return scaleImage(bitmap, ratio);
    }

    /**
     * Scale a bitmap.
     * @param bitmap
     * @param ratio
     * @return
     */
    public static Bitmap scaleImage(Bitmap bitmap, float ratio) {
        Bitmap result = bitmap;
        if (ratio != 1f) {
            int newWidth = (int) (bitmap.getWidth() * ratio);
            int newHeight = (int) (bitmap.getHeight() * ratio);
            result = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        }
        return result;
    }

    /**
     * Create rounded corner bitmap from original bitmap.
     * <p>
     * Reference http://stackoverflow.com/questions/2459916/how-to-make-an-imageview-to-have-rounded-corners
     * @param input Original bitmap.
     * @param cornerRadius Corner radius in pixel.
     * @param w
     * @param h
     * @param squareTL
     * @param squareTR
     * @param squareBL
     * @param squareBR
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap input,
                                                float cornerRadius, int w, int h, boolean squareTL, boolean squareTR,
                                                boolean squareBL, boolean squareBR) {

        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        // make sure that our rounded corner is scaled appropriately
        final float roundPx = cornerRadius;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        // draw rectangles over the corners we want to be square
        if (squareTL) {
            canvas.drawRect(0, 0, w / 2, h / 2, paint);
        }
        if (squareTR) {
            canvas.drawRect(w / 2, 0, w, h / 2, paint);
        }
        if (squareBL) {
            canvas.drawRect(0, h / 2, w / 2, h, paint);
        }
        if (squareBR) {
            canvas.drawRect(w / 2, h / 2, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(input, 0, 0, paint);

        return output;
    }


}
