package com.android.jewelry.activity.gallery;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jewelry.R;
import com.android.jewelry.apphelper.SharedValue;
import com.android.jewelry.dbmodel.DesignModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ItemHolder> {

    private Context context = null;
    private List<DesignModel> adapterItems = new ArrayList<>();
    int counter = 0;
    private ArrayList<Uri> checkedImageUri = new ArrayList<Uri>();
    private ArrayList<String> checkedImageCost = new ArrayList<String>();
    private ArrayList<String> checkedImageName = new ArrayList<String>();
    private int loc;
    private IGalleryView iGalleryView;
    AlertDialog chooserDialog = null;

    public GalleryAdapter(Context context, List<DesignModel> adapterItems, IGalleryView iGalleryView) {
        setHasStableIds(true);
        this.context = context;
        this.adapterItems = adapterItems;
        this.iGalleryView = iGalleryView;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_single_image, null);
        ItemHolder viewHolder = new ItemHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int position) {
        final DesignModel product = adapterItems.get(position);
        File file = new File(product.getDesignUri());
        Uri uri = Uri.fromFile(file);

        if (adapterItems.get(position).getSelected() == 1) {
            itemHolder.imgLike.setChecked(true);
        } else if (adapterItems.get(position).getSelected() == 0) {
            itemHolder.imgLike.setChecked(false);
        }

        Picasso.with(context)
                .load(uri)
                .fit()
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)
                .into(itemHolder.imgProduct);

        itemHolder.imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPictureChooserPopUp(product);
            }
        });

        itemHolder.tvPrice.setText(""+product.getDesignCost());
        itemHolder.tvProductName.setText(""+product.getDesignName());

        itemHolder.imgLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (counter <= 30) {
                        String designname = adapterItems.get(position).getDesignUri();
                        checkedImageUri.add(Uri.parse(designname));
                        checkedImageCost.add(String.valueOf(adapterItems.get(position).getDesignCost()));
                        Log.e("Price ????",""+adapterItems.get(position).getDesignCost());
                        checkedImageName.add(adapterItems.get(position).getDesignName());
                        adapterItems.get(position).setSelected(1);
                        counter++;
                        //labelUpdate();
                        new SharedValue(context).setScrollPosition(position);
                    }
                } else if (!isChecked) {
                    String designname = adapterItems.get(position).getDesignUri();
                    checkedImageUri.remove(Uri.parse(designname));
                    checkedImageCost.remove(String.valueOf(adapterItems.get(position).getDesignCost()));
                    checkedImageName.remove(adapterItems.get(position).getDesignName());
                    adapterItems.get(position).setSelected(0);
                    counter--;
                    //labelUpdate();
                }
            }
        });

        itemHolder.linZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loc = position;
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.picture_frame, null);
                final ImageView img_pic = (ImageView) dialogView.findViewById(R.id.img_pic);
                LinearLayout lin_cancel = (LinearLayout) dialogView.findViewById(R.id.lin_cancel);
                File file = new File(product.getDesignUri());
                Uri uri = Uri.fromFile(file);
                Picasso.with(context)
                        .load(uri)
                        .error(R.drawable.ic_launcher_background)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(img_pic);
                /**
                 * Use Simple ImageView
                 */

                //usingSimpleImage(img_pic);

                dialogBuilder.setView(dialogView);
                final AlertDialog alertDialog = dialogBuilder.create();
                lin_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        /*itemHolder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter < 30) {
                    itemHolder.imgLike.setChecked(true);
                } else {
                    Toast.makeText(context, "Sorry! You can not select more than 30 images at a time.", Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }

    private void openPictureChooserPopUp(DesignModel product) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dlg_image_chooser, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        Button btnCamera = (Button) promptsView.findViewById(R.id.btnCamera);
        Button btnBrowse = (Button) promptsView.findViewById(R.id.btnBrowse);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iGalleryView.openCamera(product,chooserDialog);
            }
        });
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iGalleryView.openBrowser(product,chooserDialog);
            }
        });
        chooserDialog = alertDialogBuilder.create();
        chooserDialog.show();
    }

    @Override
    public int getItemCount() {
        return adapterItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_product)
        ImageView imgProduct;
        @BindView(R.id.tv_product_name)
        TextView tvProductName;
        @BindView(R.id.txtStock)
        TextView txtStock;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.img_like)
        CheckBox imgLike;
        @BindView(R.id.lin_fav)
        LinearLayout linFav;
        @BindView(R.id.lin_zoom)
        LinearLayout linZoom;
        @BindView(R.id.imgDownload)
        ImageView imgDownload;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public ArrayList<Uri> getCheckedImageUri(){
        return checkedImageUri;
    }

    public ArrayList<String> getCheckedImageCost(){
        return checkedImageCost;
    }

    public ArrayList<String> getCheckedImageName(){
        return checkedImageName;
    }
}
