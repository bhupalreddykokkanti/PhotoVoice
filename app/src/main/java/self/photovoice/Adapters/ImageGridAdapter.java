package self.photovoice.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darsh.multipleimageselect.models.Image;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import self.photovoice.FullScreenSlideActivity;
import self.photovoice.R;

/**
 * Created by shyamprasadg on 16/11/16.
 */

public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageGridAdapterViewHolder>
{
    private Context context;
    ArrayList<Image> listSelectedImages = new ArrayList<>();
    ArrayList<String> listSelectedImagesUrls = new ArrayList<>();

    public ImageGridAdapter(Context context, ArrayList<Image> listSelectedImages)
    {
        this.context    =   context;
        this.listSelectedImages = listSelectedImages;
        convertImageDOtoUrlList();
    }

    @Override
    public ImageGridAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_image_files, parent, false);
        return new ImageGridAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageGridAdapterViewHolder holder, int position) {
        holder.tvImageName.setText(listSelectedImages.get(position).name);

        File file = new File(listSelectedImages.get(position).path);
        Picasso.with(context).load(file).resize(150, 150).into(holder.ivImage);
    }

    @Override
    public int getItemCount()
    {
        if (listSelectedImages != null && listSelectedImages.size() > 0)
            return listSelectedImages.size();
        else
            return 10;
    }

    public class ImageGridAdapterViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tvImageName;
        private ImageView ivImage;
        private LinearLayout llContainer;

        public ImageGridAdapterViewHolder(View itemView)
        {
            super(itemView);
            tvImageName       =   (TextView)     itemView.findViewById(R.id.tvImageName);
            ivImage           =   (ImageView)    itemView.findViewById(R.id.ivImage);
            llContainer       =   (LinearLayout)    itemView.findViewById(R.id.llContainer);

            llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent SlideActivity = new Intent(context, FullScreenSlideActivity.class);
                    SlideActivity.putExtra("position", getAdapterPosition());
                    SlideActivity.putExtra("flag", "PresImgSumAdapter");
                    SlideActivity.putStringArrayListExtra("PRESCRIPTIONS_LIST",listSelectedImagesUrls);
                    context.startActivity(SlideActivity);
                }
            });
        }

    }
    public void refreshAdapters(ArrayList<Image> listSelectedImages) {
        this.listSelectedImages = listSelectedImages;
        notifyDataSetChanged();

    }

    public void setLayoutParams(View v, int drawableId)
    {
        int [] dDimens = getDrawableDimensions(v.getContext(), drawableId);
        v.getLayoutParams().width = dDimens[0];
        v.getLayoutParams().height = dDimens[1];
    }

    public int[] getDrawableDimensions(Context context, int id)
    {
        int[] dimensions = new int[2];
        if(context == null)
            return dimensions;
        Drawable d = context.getResources().getDrawable(id);
        if(d != null)
        {
            dimensions[0] = d.getIntrinsicWidth();
            dimensions[1] = d.getIntrinsicHeight();
        }
        return dimensions;
    }

    public void convertImageDOtoUrlList(){
        if (listSelectedImages != null && listSelectedImages.size() > 0){
            for (int i = 0; i < listSelectedImages.size(); i++) {
                listSelectedImagesUrls.add(listSelectedImages.get(i).path);
            }
        }
    }
}




