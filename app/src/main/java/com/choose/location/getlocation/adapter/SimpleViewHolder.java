package com.choose.location.getlocation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by xuzhendong on 2018/4/20.
 */

public class SimpleViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public SimpleViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static SimpleViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new SimpleViewHolder(context, parent, layoutId, position);
        } else {
            SimpleViewHolder holder = (SimpleViewHolder) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }

    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public SimpleViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public SimpleViewHolder setText(int viewId, SpannableStringBuilder text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public SimpleViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public SimpleViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public SimpleViewHolder setImageURL(int viewId, String url) {
        ImageView view = getView(viewId);
        return this;
    }

    public SimpleViewHolder setInvisible(int viewId, int isVisible) {
        View view = getView(viewId);
        view.setVisibility(isVisible);
        return this;
    }

    public SimpleViewHolder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }

    public SimpleViewHolder setTextBackgroundDrawable(int viewId, Drawable drawable) {
        TextView view = getView(viewId);
        view.setBackgroundDrawable(drawable);
        return this;
    }

    public LinearLayout getLinearLayout(int viewId) {
        LinearLayout view = getView(viewId);
        return view;
    }

    public void setButtonOnClick(int viewId, View.OnClickListener l) {
        Button btn = getView(viewId);
        btn.setOnClickListener(l);
    }

    public void setTextOnClick(int viewId, View.OnClickListener l) {
        TextView tv = getView(viewId);
        tv.setOnClickListener(l);
    }

    public void setLayerType(int viewId, int viewType) {
        View view = getView(viewId);
        view.setLayerType(viewType, null);
    }

    public void setImageBitmapLeft(int viewId, Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        TextView view = getView(viewId);
        view.setCompoundDrawables(drawable, null, null, null);
    }

}

