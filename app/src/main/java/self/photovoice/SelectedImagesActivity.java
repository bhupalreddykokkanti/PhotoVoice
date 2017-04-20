package self.photovoice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.vansuita.pickimage.EPickTypes;
import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;
import com.vansuita.pickimage.listeners.IPickClick;

import java.util.ArrayList;
import java.util.Collection;

import self.photovoice.Adapters.ImageGridAdapter;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SelectedImagesActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            rvSelectedImages.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private RecyclerView rvSelectedImages;
    private ImageGridAdapter gridAdapter;
    private TextView tvPickPhotos;
    private ArrayList<Image> listSelectedImages = new ArrayList<>();
    private ArrayList<Image> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_selected_images);

        listSelectedImages = (ArrayList<Image>) getIntent().getSerializableExtra("listSelectedImages");

        mControlsView       =               findViewById(R.id.fullscreen_content_controls);
        rvSelectedImages    = (RecyclerView)findViewById(R.id.rvSelectedImages);
        tvPickPhotos        = (TextView)    findViewById(R.id.tvPickPhotos);

        // Set up the user interaction to manually show or hide the system UI.
        /*rvSelectedImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });*/

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.tvPickPhotos).setOnClickListener(this);

        gridAdapter    =   new ImageGridAdapter(SelectedImagesActivity.this, listSelectedImages);
        GridLayoutManager layoutManager= new GridLayoutManager(SelectedImagesActivity.this, 3);
        rvSelectedImages.setHasFixedSize(true);
        rvSelectedImages.setLayoutManager(layoutManager);
        rvSelectedImages.setAdapter(gridAdapter);
        tvPickPhotos.setOnClickListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
//        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvPickPhotos:
                showImagePickingOptions();
                break;
        }
    }

    private void showImagePickingOptions() {
        PickSetup setup = new PickSetup(BuildConfig.APPLICATION_ID);
        setup.setPickTypes( EPickTypes.GALLERY, EPickTypes.CAMERA)
                .setButtonOrientation(LinearLayoutCompat.HORIZONTAL);
        PickImageDialog.on(getSupportFragmentManager(), setup).setOnClick(new IPickClick() {
            @Override
            public void onGalleryClick() {
                Toast.makeText(SelectedImagesActivity.this, "Gallery Click!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SelectedImagesActivity.this, AlbumSelectActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 10);
                startActivityForResult(intent, Constants.REQUEST_CODE);
            }

            @Override
            public void onCameraClick() {
                Toast.makeText(SelectedImagesActivity.this, "Camera Click!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            listSelectedImages.addAll(images);
            gridAdapter.refreshAdapters(listSelectedImages);
        }
    }

    public void setLayoutParams(View v, int drawableId)
    {
        int [] dDimens = getDrawableDimensions(v.getContext(), drawableId);
//        v.getLayoutParams().width = dDimens[0];
        v.getLayoutParams().height = dDimens[1];
    }

    public int[] getDrawableDimensions(Context context, int id)
    {
        int[] dimensions = new int[2];
        if(context == null)
            return dimensions;
        Drawable d = getResources().getDrawable(id);
        if(d != null)
        {
            dimensions[0] = d.getIntrinsicWidth();
            dimensions[1] = d.getIntrinsicHeight();
        }
        return dimensions;
    }
}
