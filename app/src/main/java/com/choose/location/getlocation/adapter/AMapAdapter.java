package com.choose.location.getlocation.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.choose.location.getlocation.R;
import com.choose.location.getlocation.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuzhendong on 2018/4/20.
 */

public class AMapAdapter extends BaseAdapter {
    protected Context mContext;
    protected List<AMapBean> mDatas = new ArrayList<AMapBean>();
    protected LayoutInflater mInflater;
    protected int mPosotion;
    public static int TYPE_POI = 1;
    public static int TYPE_INPUT = 2;
    private int type = 1;

    public AMapAdapter(Context context, List<AMapBean> mDatas, int type) {
        this.mContext = context;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(context);
        this.type = type;
    }

    public void setDatas(List<AMapBean> mDatas) {
        if (mDatas != null) {
            this.mDatas = mDatas;
        }
        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public AMapBean getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.mPosotion = position;
        SimpleViewHolder holder = SimpleViewHolder.get(mContext, convertView, parent, R.layout.item_map_layout, position);
        convert(holder, getItem(position));
        return holder.getConvertView();
    }

    protected void convert(SimpleViewHolder holder, AMapBean aMapBean){
        TextView address = (TextView) holder.getConvertView().findViewById(R.id.address);
        TextView addressDeatil = (TextView) holder.getConvertView().findViewById(R.id.address_detail);
        address.setText(aMapBean.getAddress());
        if(type == TYPE_INPUT){
            addressDeatil.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) address.getLayoutParams();
            params.setMargins(UiUtils.dip2px(mContext,10),UiUtils.dip2px(mContext,5),0,0);
            addressDeatil.setLayoutParams(params);
        }else {
            addressDeatil.setText(aMapBean.getAddressDetail());
        }
        ImageView choose = (ImageView) holder.getConvertView().findViewById(R.id.choose);
        if(aMapBean.isChoose()){
            choose.setVisibility(View.VISIBLE);
        }else {
            choose.setVisibility(View.GONE);
        }
    }
}

