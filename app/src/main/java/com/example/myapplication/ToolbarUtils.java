package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class ToolbarUtils {

    private static final int[] profileImages = {
            R.drawable.mercury, R.drawable.venus, R.drawable.earth,
            R.drawable.mars, R.drawable.jupiter, R.drawable.saturn,
            R.drawable.uranus, R.drawable.neptune
    };

    public static void applyProfileIconToToolbar(Toolbar toolbar, Context context) {
        if (toolbar == null || context == null) return;

        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid == null) return;

        boolean hasChanged = prefs.getBoolean(uid + "_hasChangedProfilePic", false);
        int index = prefs.getInt(uid + "_profilePicIndex", 0);

        if (hasChanged && index >= 0 && index < profileImages.length) {
            Drawable circularIcon = getCircularIcon(context, profileImages[index], 96); // 96px
            if (circularIcon != null) {
                toolbar.getMenu().findItem(R.id.action_profile).setIcon(circularIcon);
            }
        }
    }

    private static Drawable getCircularIcon(Context context, int resId, int targetSizePx) {
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), resId);

        // Step 1: Crop the original image to a centered square
        int width = original.getWidth();
        int height = original.getHeight();
        int newEdge = Math.min(width, height);

        int xOffset = (width - newEdge) / 2;
        int yOffset = (height - newEdge) / 2;

        Bitmap squareCropped = Bitmap.createBitmap(original, xOffset, yOffset, newEdge, newEdge);

        // Step 2: Scale the square to the target size (e.g., 96x96)
        Bitmap scaled = Bitmap.createScaledBitmap(squareCropped, targetSizePx, targetSizePx, true);

        // Step 3: Draw the scaled image as a circle
        Bitmap circleBitmap = Bitmap.createBitmap(targetSizePx, targetSizePx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(circleBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        Path path = new Path();
        path.addCircle(targetSizePx / 2f, targetSizePx / 2f, targetSizePx / 2f, Path.Direction.CCW);
        canvas.clipPath(path);
        canvas.drawBitmap(scaled, 0, 0, paint);

        return new BitmapDrawable(context.getResources(), circleBitmap);
    }
}