package com.dyhl.dusky.huangchuanfp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dyhl.dusky.huangchuanfp.Base.UserState;
import com.dyhl.dusky.huangchuanfp.Design.imagePicker.SelectDialog;
import com.dyhl.dusky.huangchuanfp.Module.entity.Annuncement;
import com.dyhl.dusky.huangchuanfp.Module.entity.ApiMsg;
import com.dyhl.dusky.huangchuanfp.Module.entity.Exp;
import com.dyhl.dusky.huangchuanfp.Module.entity.Problem;
import com.dyhl.dusky.huangchuanfp.Net.RetrofitHelper;
import com.dyhl.dusky.huangchuanfp.R;
import com.dyhl.dusky.huangchuanfp.Utils.PreferenceUtil;
import com.dyhl.dusky.huangchuanfp.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @AUTHOR: dsy
 * @TIME: 2018/4/17
 * @DESCRIPTION:
 */
public class ProblemAdapter extends RecyclerView.Adapter<ProblemAdapter.ViewHolder>{

    private List<Problem> home;
    private Context context;
    private String code;

    public ProblemAdapter(List<Problem> home, Context context) {
        this.home = home;
        this.context=context;
        code= PreferenceUtil.getStringPRIVATE("permissions", UserState.NA);
    }

    public void updateData(List<Problem> home) {
        this.home.addAll(home);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.povertyproblem_item, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // 绑定数据
        if(home.get(position)==null){
            return;
        }
        holder.name.setText(home.get(position).getName());
        holder.mTvtime.setText(home.get(position).getTime());
        holder.hf.setText("回复 "+home.get(position).getReplycount());
        if(!TextUtils.isEmpty(home.get(position).getPublics())){
        holder.publics.setText(" "+home.get(position).getPublics());
            /*if(code.length()<=6){
                Drawable drawable = context.getResources().getDrawable(R.drawable.ic_action_down);
                holder.publics.setCompoundDrawablesWithIntrinsicBounds(null,null,drawable,null);
                holder.publics.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setPublic(home.get(position),position);
                    }
                });
            }else{*/
                if("公开".equals(home.get(position).getPublics())){
                    Drawable drawable = context.getResources().getDrawable(R.drawable.pub);
                    holder.publics.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                }else{
                    Drawable drawable = context.getResources().getDrawable(R.drawable.pri);
                    holder.publics.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
                }
           // }
        }

        //单独对应类型的设置事件
        if( onItemClickListener!= null){
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(position);
                }
            });
            holder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return home == null ? 0 : home.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,hf;
        TextView mTvtime,publics;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvtime = (TextView) itemView.findViewById(R.id.time);
            name = (TextView) itemView.findViewById(R.id.name);
            hf = (TextView) itemView.findViewById(R.id.hf);
            publics = (TextView) itemView.findViewById(R.id.publics);
        }
    }

    OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onClick(int position);
        void onLongClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this.onItemClickListener=onItemClickListener;
    }


    public void setPublic(Problem exp, int p){
        List<String> names = new ArrayList<>();
        names.add("公开");
        names.add("屏蔽");
        showDialog(new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        RetrofitHelper.getCommonServiceAPI()
                                .setupPublics(1,exp.getId(),"公开")
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(bean -> {
                                    String a=bean.string();
                                    ApiMsg apiMsg = JSON.parseObject(a,ApiMsg.class);
                                    String state = apiMsg.getState();
                                    switch (state){
                                        case "0":
                                            exp.setPublics("公开");
                                            notifyItemChanged(p);
                                            ToastUtil.ShortToast(apiMsg.getMessage());
                                            break;
                                        case "-1":
                                        case "-2":
                                            ToastUtil.ShortToast(apiMsg.getMessage());
                                            break;
                                    }
                                }, throwable -> {
                                    ToastUtil.ShortToast("返回错误，请确认网络正常或服务器正常");
                                });

                        break;
                    case 1:
                        RetrofitHelper.getCommonServiceAPI()
                                .setupPublics(1,exp.getId(),"屏蔽")
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(bean -> {
                                    String a=bean.string();
                                    ApiMsg apiMsg = JSON.parseObject(a,ApiMsg.class);
                                    String state = apiMsg.getState();
                                    switch (state){
                                        case "0":
                                            exp.setPublics("屏蔽");
                                            notifyItemChanged(p);
                                            ToastUtil.ShortToast(apiMsg.getMessage());
                                            break;
                                        case "-1":
                                        case "-2":
                                            ToastUtil.ShortToast(apiMsg.getMessage());
                                            break;
                                    }
                                }, throwable -> {
                                    ToastUtil.ShortToast("返回错误，请确认网络正常或服务器正常");
                                });
                        break;
                    default:
                        break;
                }

            }
        }, names);
    }

    Activity activity;
    SelectDialog dialog;
    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        try{
            activity=(Activity) context;
        }catch (Exception e){

        }
        if(activity!=null){
            dialog = new SelectDialog((Activity) context, R.style
                    .transparentFrameWindowStyle,
                    listener, names);

            if (!activity.isFinishing()) {
                dialog.show();
            }
        }

        return dialog;
    }
}

