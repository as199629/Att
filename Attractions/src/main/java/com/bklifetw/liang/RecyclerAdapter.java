package com.bklifetw.liang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements View.OnClickListener, Filterable {
    private final Context mContext;
    private final ArrayList<Post> mData;
    ArrayList<Post> arrayListFilter;
    private final int TABLE_ID;
    //    -------------------------------------------------------------------
    private OnItemClickListener mOnItemClickListener = null;

    //--------------------------------------------
    public RecyclerAdapter(int TABLE_ID, Context context, ArrayList<Post> data) {
        this.TABLE_ID=TABLE_ID;
        this.mContext = context;
        this.mData = data;

        arrayListFilter = new ArrayList<>();
        /**這裡把初始陣列複製進去了*/

        arrayListFilter.addAll(data);

    }

    //    -------------------------------------------------------------------
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    //-------------------------------------------------------------------
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.cell_post, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.img = (ImageView) view.findViewById(R.id.img);
        holder.Name = (TextView) view.findViewById(R.id.Name);
        holder.Content = (TextView) view.findViewById(R.id.Content);
        holder.Add = (TextView) view.findViewById(R.id.Addr);
        holder.Zipcode = (TextView) view.findViewById(R.id.Zipcode);

        holder.Picdescribe1 = (TextView) view.findViewById(R.id.Picdescribe1);
        holder.tel = (TextView) view.findViewById(R.id.tel);
        holder.Ticketinfo = (TextView) view.findViewById(R.id.Ticketinfo);
        holder.Opentime = (TextView) view.findViewById(R.id.Opentime);


        holder.post_TB4=(TableRow)view.findViewById(R.id.post_TB4);
        holder.post_TB5=(TableRow)view.findViewById(R.id.post_TB5);



        //----------------------------------------------------
        //將創建的View註冊點擊事件
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = mData.get(position);
        //-----------------------------------
        holder.Name.setText(post.Name);
        holder.Add.setText(post.Add);
//        holder.Content.setText(post.Content);
        if (post.Zipcode.length() > 0) {
            holder.Zipcode.setText("[" + post.Zipcode + "]");
        } else {
            holder.Zipcode.setText("[000]");
        }
        holder.tel.setText(post.Tel);

        if(post.Picdescribe1.equals(""))
        {
            holder.Picdescribe1.setText("無");
        }
        else
        {
            holder.Picdescribe1.setText(post.Picdescribe1);
        }

        holder.Ticketinfo.setText(post.Ticketinfo);
        holder.Opentime.setText(post.Opentime);

        if(TABLE_ID==1)
        {
            holder.post_TB4.setVisibility(View.VISIBLE);
            holder.post_TB5.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.post_TB4.setVisibility(View.GONE);
            holder.post_TB5.setVisibility(View.GONE);
        }




//---------------------------------------
//        若圖片檔名是中文無法下載,可用此段檢查圖片網址且將中文解碼
        String ans_Url = post.posterThumbnailUrl;
        if (post.posterThumbnailUrl.getBytes().length == post.posterThumbnailUrl.length() ||
                post.posterThumbnailUrl.getBytes().length > 100) {
            ans_Url = post.posterThumbnailUrl;//不包含中文，不做處理
        } else {
//    ans_Url = utf8Togb2312(post.posterThumbnailUrl);
           ans_Url = utf8Togb2312(post.posterThumbnailUrl).replace("http://", "https://");
        }
//        Glide.with(mContext)
//                .load(ans_Url)
//                .into(holder.img);
//----------------------------------------
        RequestOptions options = new RequestOptions() .placeholder(R.drawable.ic_launcher007) .diskCacheStrategy(DiskCacheStrategy.NONE);



        Glide.with(mContext)
//                .load(post.posterThumbnailUrl)
                    .load(ans_Url)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(options)
                .override(250, 250)
                .transition(withCrossFade())
                .error(
                        Glide.with(mContext)
                                .load("https://bklifetw.com/img/nopic1.jpg"))
                .into(holder.img);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //將position保存在itemView的Tag中，以便點擊時進行獲取
        holder.itemView.setTag(position);
    }

////    //    -----------把中文字符轉換為帶百分號的瀏覽器編碼-----------
    public static String utf8Togb2312(String inputstr) {
        String r_data = "";
        try {
            for (int i = 0; i < inputstr.length(); i++) {
                char ch_word = inputstr.charAt(i);
//            下面這段代碼的意義是:只對中文進行轉碼
                if (ch_word + "".getBytes().length > 1 && ch_word != ':' && ch_word != '/') {
                    r_data = r_data + java.net.URLEncoder.encode(ch_word + "", "utf-8");
                } else {
                    r_data = r_data + ch_word;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
//            System.out.println(r_data);
        }
        return r_data;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意這裡使用getTag方法獲取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
    /**使用Filter濾除方法*/
    Filter mFilter = new Filter() {
        /**此處為正在濾除字串時所做的事*/
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            /**先將完整陣列複製過去*/
            ArrayList<Post> filteredList = new ArrayList<>();
            /**如果沒有輸入，則將原本的陣列帶入*/


            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrayListFilter);
            } else {
                /**如果有輸入，則照順序濾除相關字串
                 * 如果你有更好的搜尋演算法，就是寫在這邊*/
                for (Post movie: arrayListFilter) {

                    if (movie.Name.contains(constraint.toString().toLowerCase())
                    ||movie.Add.contains(constraint.toString().toLowerCase())){
                        filteredList.add(movie);
                    }
                }
            }
            /**回傳濾除結果*/
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }
        /**將濾除結果推給原先RecyclerView綁定的陣列，並通知RecyclerView刷新*/
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mData.clear();
            mData.addAll((Collection<? extends Post>) results.values);
            notifyDataSetChanged();
        }
    };
    //define interface
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    //======= sub class   ==================
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView img;
        public TextView Name;
        public TextView Add;
        public TextView Content;
        public TextView Zipcode;
        public TextView Picdescribe1;
        public TextView tel;
        public TextView Ticketinfo;
        public TextView Opentime;
        public TableRow post_TB4;
        public TableRow post_TB5;

//        public ViewHolder(View itemView) {
//            super(itemView);
//        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            tvItem = itemView.findViewById(android.R.id.text1);
            /**點擊事件*/
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
//-----------------------------------------------
}