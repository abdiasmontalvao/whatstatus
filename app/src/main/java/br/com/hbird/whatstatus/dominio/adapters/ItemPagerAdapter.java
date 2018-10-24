package br.com.hbird.whatstatus.dominio.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import br.com.hbird.whatstatus.R;

public class ItemPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<File> itens;
    private LayoutInflater inflater;

    public ItemPagerAdapter(Context context, ArrayList<File> itens) {
        this.context = context;
        this.itens = itens;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itens.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_slide, container, false);

        ImageView imgItem = layout.findViewById(R.id.img_item);
        ImageView imgPlay = layout.findViewById(R.id.img_play);

        if (itens.get(position).getName().contains(".mp4")) {
            imgPlay.setVisibility(View.VISIBLE);

            imgPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(itens.get(position).getAbsolutePath()));
                    intent.setDataAndType(Uri.parse(itens.get(position).getAbsolutePath()), "video/mp4");
                    context.startActivity(intent);
                }
            });
        }

        Glide.with(imgItem.getContext())
                .load(itens.get(position))
                .into(imgItem);

        container.addView(layout);

        return layout;
    }

    @Override
    public int getItemPosition(Object object) {
        if (itens.contains(object)) {
            return itens.indexOf(object);
        } else {
            return POSITION_NONE;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    public void removeItem(int position) {
        itens.remove(position);
        notifyDataSetChanged();
    }
}