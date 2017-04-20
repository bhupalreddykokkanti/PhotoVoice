package self.photovoice.imagezoom.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class CustomSimpleImageLoadingListener extends SimpleImageLoadingListener
{
	private ProgressBar pBar;
	public CustomSimpleImageLoadingListener(ProgressBar pBar)
	{
		this.pBar = pBar;
	}
	
	@Override
    public void onLoadingStarted(String imageUri, View view) {
		pBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
    	pBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        pBar.setVisibility(View.GONE);
    }
}