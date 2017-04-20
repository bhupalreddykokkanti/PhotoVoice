package self.photovoice.Adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import self.photovoice.R;
import self.photovoice.imagezoom.ImageViewTouch;
import self.photovoice.imagezoom.utils.CustomSimpleImageLoadingListener;


public class FullScreenImageAdapter extends PagerAdapter {

	private Activity _activity;
	private List<String> _imagePaths;
	private LayoutInflater inflater;
	private ImageLoader fullSizedImageLoader;
	private DisplayImageOptions displayImageOptions;

	private void initImageLoader(Context context)
	{
		DisplayImageOptions defaultOptions = getDisplayOptions(0);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .defaultDisplayImageOptions(defaultOptions)
        .build();
		fullSizedImageLoader = ImageLoader.getInstance();
		fullSizedImageLoader.init(config);
	}
	
	public DisplayImageOptions getDisplayOptions(int resId)
	{
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .considerExifParams(true)
        .showImageOnLoading(resId)
        .showImageOnFail(resId)
        .showImageForEmptyUri(resId)
        .build();
		return defaultOptions;
	}
	
	public FullScreenImageAdapter(Activity activity,
								  List<String> imagePaths) {
		this._activity = activity;
		this._imagePaths = imagePaths;
		initImageLoader(activity);
		displayImageOptions = getDisplayOptions(0);
		inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return this._imagePaths.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((FrameLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);
		ImageViewTouch imgDisplay = (ImageViewTouch) viewLayout.findViewById(R.id.imgDisplay);
		ProgressBar pBar = (ProgressBar) viewLayout.findViewById(R.id.pBar);
		if (_imagePaths.get(position).contains("http")) {
			fullSizedImageLoader.displayImage(String.valueOf(Uri.parse(_imagePaths.get(position))), imgDisplay, displayImageOptions, new CustomSimpleImageLoadingListener(pBar));
		}else{
			fullSizedImageLoader.displayImage("file:/"+_imagePaths.get(position), imgDisplay, displayImageOptions, new CustomSimpleImageLoadingListener(pBar));
		}
		((ViewPager)container).addView(viewLayout);
		return viewLayout;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((FrameLayout) object);
    }

	/*public void removeImageAt(int position){
		_imagePaths.remove(position);
		this.notifyDataSetChanged();
	}*/

	public void refreshAdapter(ArrayList<String> imgList){
		this._imagePaths = imgList;
		this.notifyDataSetChanged();
	}
}
