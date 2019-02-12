package com.hans.svandasek.fire.vyjezdy.models;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;

import com.hans.svandasek.fire.vyjezdy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kartik_ch on 12/13/2015.
 */
public class Categories {
    private Context mContext;

    public Categories(Context context) {
        this.mContext = context;
    }
//
//    public List<CategoryItem> getCategoryItems() {
//        List<CategoryItem> categoryItems = new ArrayList<>();
//
//        String[] categoryNames = mContext.getResources().getStringArray(R.array.category_names);
//        //TypedArray categoryImgs = mContext.getResources().obtainTypedArray(R.array.category_drawables);
//
//        for (int i = 0; i < categoryNames.length; i++) {
//            CategoryItem categoryItem = new CategoryItem();
//            categoryItem.setCategoryName(categoryNames[i]);
//            categoryItem.setCategoryImg(ContextCompat.getDrawable(mContext, categoryImgs.getResourceId(i, -1)));
//            categoryItems.add(categoryItem);
//        }
//
//        return categoryItems;
//    }

    public int getDrawableId(String category) {
        switch (category) {
            case "Požár":
                return R.drawable.ic_firee;
            case "Dopravní nehoda":
                return R.drawable.ic_side_crash;
            case "Technická pomoc":
                return R.drawable.ic_technical_support1;
            case "Záchrana osob/zvířat":
                return R.drawable.ic_lifesaver;
            case "Chemické látky":
                return R.drawable.ic_flask_outline;
            case "Ostatní":
                return R.drawable.ic_siren;
            default:
                return 0;
        }
    }
}