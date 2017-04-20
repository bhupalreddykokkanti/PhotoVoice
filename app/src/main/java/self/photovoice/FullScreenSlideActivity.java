package self.photovoice;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import self.photovoice.Adapters.FullScreenImageAdapter;


public class FullScreenSlideActivity extends AppCompatActivity {

    private int counter = 0;
    private ArrayList<String> imgList;
    private TextView tvTitleBar;
    private FrameLayout flMain;
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private File[] listFile;
    private String clinicID;
    public Preference preference;
    private ImageView ivClose, ivShare;
    private int imageTypeId;
    private String title = "Images";
    private int position;
    private boolean isFromMedicineDetails = false;
    private Boolean delete = false;
    private ArrayList<File> files = new ArrayList<File>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        preference = new Preference(getApplicationContext());
        viewPager = (ViewPager) findViewById(R.id.pager);

//        llTop.setBackgroundResource(R.color.black_bg);
//        llLeftMenu.setVisibility(View.GONE);
//        llClose.setVisibility(View.VISIBLE);
//        ivMenu.setBackgroundResource(R.drawable.cloase_white);
//        flBottomBar.setVisibility(View.GONE);
//        llRight.setVisibility(View.GONE);
//        viewPager.setBackgroundResource(R.drawable.hcue_borderwhiteinput);

        Intent i = getIntent();
        if (i.hasExtra("PRESCRIPTIONS_LIST")) {
            imgList = i.getStringArrayListExtra("PRESCRIPTIONS_LIST");
        }
        if (i.hasExtra("position")) {
            position = i.getIntExtra("position", 0);
        }
        if (i.hasExtra("SalonId")) {
            clinicID = i.getStringExtra("SalonId");
        }
//		imgList = getImages();
        if (i.hasExtra("imageData")) {
            imgList = i.getStringArrayListExtra("imageData");
        }
        if (i.hasExtra("ImageTypeId")) {
            imageTypeId = getIntent().getIntExtra("ImageTypeId", 7);

//            if (imageTypeId == ApplicationConstants.IMG_MENU_TYPE)
//                title = "Menu Images";
        } else
            title = "Images";

        setTitleView(position);
        adapter = new FullScreenImageAdapter(FullScreenSlideActivity.this, imgList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                setTitleView(arg0);
                position = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });

        /*llClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFromMedicineDetails) {
                    imgList.add("");
                    setResult(RESULT_OK, new Intent().putStringArrayListExtra("PRESCRIPTIONS_LIST", imgList));
                    finish();
                } else
                    finish();
            }
        });
        llRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    llRight.setBackgroundColor(Color.parseColor("#33000000"));

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    llRight.setBackgroundColor(Color.parseColor("#00000000"));
                    showLoader("");
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            shareImg(imgList.get(viewPager.getCurrentItem()));
                        }
                    });
                    t.start();
                }
                return true;
            }
        });
        llFilter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgList.size() > 0){

                    new AlertDialog.Builder(FullScreenSlideActivity.this)
                            .setTitle("")
                            .setMessage("Are you sure you want to Delete?")
                            .setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    callDeletePatientPrescriptions(ApplicationConstants.getOrderRequest().getOrderImageDocuments().get(position));
                                }
                            })
                            .setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


                }else{
                    Toast.makeText(FullScreenSlideActivity.this, "At least one Prescription image should be present.", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }


    private void setTitleView(int arg0) {
        int totalCount = imgList != null ? imgList.size() : 0;
        String message = title;
        if (totalCount > 0)
            message = (arg0 + 1) + " of " + totalCount;
    }

    public void shareImg(String arrUrl) {

        delete = true;
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
//		for(String path : arrUrl /* List of the files you want to send */)
//        {
//            showLoader("");
        String uri = convertURLtoBitmapShare(arrUrl);
        File file = new File(uri);
        files.add(file);
        Uri uri2 = Uri.fromFile(file);
        imageUris.add(uri2);
//            hideLoader();
//		}
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        shareIntent.setType("image/*");
//        shareIntent.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(shareIntent, "Share images to.."));
    }

    public String convertURLtoBitmapShare(String src) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {

            URL url = new URL(src);

            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();

            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + System.currentTimeMillis() + ".jpg");
            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return f.toString();

        } catch (IOException e) {

            e.printStackTrace();
            return null;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (delete) {
            if (files != null && files.size() != 0) {
                for (int i = 0; i < files.size(); i++) {
                    File file = new File(files.get(i).toString());
                    file.delete();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (delete) {
            if (files != null && files.size() != 0) {
                for (int i = 0; i < files.size(); i++) {
                    File file = new File(files.get(i).toString());
                    file.delete();
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
//        llClose.performClick();
        super.onBackPressed();
        finish();
    }

    public void deleteImgAndRefresh() {
        imgList.remove(position);
        //adapter.removeImageAt(position);
        if(position==0){
            if(imgList.size()!=0) {
                position = 0;
            }
        }else{
            position = position - 1;
        }

        if (imgList != null && imgList.size() != 0) {
            adapter = new FullScreenImageAdapter(FullScreenSlideActivity.this, imgList);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(position);
            setTitleView(position);
        }
    }
}

