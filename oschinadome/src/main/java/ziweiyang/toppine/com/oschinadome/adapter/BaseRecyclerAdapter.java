package ziweiyang.toppine.com.oschinadome.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ziweiyang.toppine.com.oschinadome.R;


/**
 *
 */
@SuppressWarnings("unused")
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {
    protected List<T> mItems;
    protected Context mContext;
    protected LayoutInflater mInflater;

    protected String mSystemTime;

    public static final int STATE_NO_MORE = 1;  //状态 ---> mState
    public static final int STATE_LOAD_MORE = 2;
    public static final int STATE_INVALID_NETWORK = 3;
    public static final int STATE_HIDE = 5;
    public static final int STATE_REFRESHING = 6;
    public static final int STATE_LOAD_ERROR = 7;
    public static final int STATE_LOADING = 8;

    public final int BEHAVIOR_MODE; //控制是否有footer和header view
    protected int mState;

    public static final int NEITHER = 0;    //控制是否有footer和header view --> BEHAVIOR_MODE
    public static final int ONLY_HEADER = 1;
    public static final int ONLY_FOOTER = 2;
    public static final int BOTH_HEADER_FOOTER = 3;

    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_HEADER = -1;
    public static final int VIEW_TYPE_FOOTER = -2;

    private OnItemClickListener onItemClickListener;    //item点击事件  setOnItemClickListener
    private OnItemLongClickListener onItemLongClickListener;    //item长击事件

    private OnClickListener onClickListener;    //不会外提供 驱动  onItemClickListener
    private OnLongClickListener onLongClickListener;//驱动 onItemLongClickListener


    protected View mHeaderView; //headerView一般自己定制

    private OnLoadingHeaderCallBack onLoadingHeaderCallBack;

    public BaseRecyclerAdapter(Context context, int mode) {
        mItems = new ArrayList<>();
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        BEHAVIOR_MODE = mode;
        mState = STATE_HIDE;    //一开始初始化页面为刷新页面
        //mFooterView = mInflater.inflate(R.layout.footer_view, null);
        initListener();
    }

    /**
     * 初始化listener
     */
    private void initListener() {
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(int position, long itemId) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position, itemId);
            }
        };

        onLongClickListener = new OnLongClickListener() {
            @Override
            public boolean onLongClick(int position, long itemId) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onLongClick(position, itemId);
                    return true;
                }
                return false;
            }
        };
    }

    public void setSystemTime(String systemTime) {
        this.mSystemTime = systemTime;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("=====","viewType:"+viewType);
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                if (onLoadingHeaderCallBack != null)
                    return onLoadingHeaderCallBack.onCreateHeaderHolder(parent);    //显示headerView(需要你写headerView)
                else
                    throw new IllegalArgumentException("you have to impl the interface when using this viewType");
            case VIEW_TYPE_FOOTER:  //显示footerView (footer视图已固定,不需要你写)
                return new FooterViewHolder(mInflater.inflate(R.layout.recycler_footer_view, parent, false));
            default:    //itemView
                final RecyclerView.ViewHolder holder = onCreateDefaultViewHolder(parent, viewType);//把viewHolder交给子类实现
                if (holder != null) {
                    holder.itemView.setTag(holder);
                    holder.itemView.setOnClickListener(onClickListener);
                    holder.itemView.setOnLongClickListener(onLongClickListener);
                }
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {     //返回itemViewType,给予不同视图类型
            case VIEW_TYPE_HEADER:
                if (onLoadingHeaderCallBack != null)
                    onLoadingHeaderCallBack.onBindHeaderHolder(holder, position);   //headerView需要自己实现
                break;
            case VIEW_TYPE_FOOTER:
                FooterViewHolder fvh = (FooterViewHolder) holder;
                fvh.itemView.setVisibility(View.VISIBLE);
                switch (mState) {   //根据不同状态对footerView进行不同展示
                    case STATE_INVALID_NETWORK:
                        fvh.tv_footer.setText(mContext.getResources().getString(R.string.state_network_error));
                        fvh.pb_footer.setVisibility(View.GONE);
                        break;
                    case STATE_LOAD_MORE:
                    case STATE_LOADING:
                        fvh.tv_footer.setText(mContext.getResources().getString(R.string.state_loading));
                        fvh.pb_footer.setVisibility(View.VISIBLE);
                        break;
                    case STATE_NO_MORE:
                        fvh.tv_footer.setText(mContext.getResources().getString(R.string.state_not_more));
                        fvh.pb_footer.setVisibility(View.GONE);
                        break;
                    case STATE_REFRESHING:
                        fvh.tv_footer.setText(mContext.getResources().getString(R.string.state_refreshing));
                        fvh.pb_footer.setVisibility(View.GONE);
                        break;
                    case STATE_LOAD_ERROR:
                        fvh.tv_footer.setText(mContext.getResources().getString(R.string.state_load_error));
                        fvh.pb_footer.setVisibility(View.GONE);
                        break;
                    case STATE_HIDE:
                        fvh.itemView.setVisibility(View.GONE);  //隐藏
                        break;
                }
                break;
            default:
                onBindDefaultViewHolder(holder, getItems().get(getIndex(position)), position);  //把itemView的展示交给子类实现
                break;
        }
            Log.e("=====","holder.getItemViewType()"+holder.getItemViewType()+"mState"+mState);
    }


    /**
     * 当添加到RecyclerView时获取GridLayoutManager布局管理器，修正header和footer显示整行
     *
     * @param recyclerView the mRecyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {     //是GridLayoutManager的时候,head和foot占据一行
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    //例如:gridManager =new GridLayoutManager(this, 3); 如果是footer和header占据一行,item占据 1/3
                    return getItemViewType(position) == VIEW_TYPE_HEADER || getItemViewType(position) == VIEW_TYPE_FOOTER
                            ? gridManager.getSpanCount() : 1;//Span 跨度
                }
            });
        }
    }

    /**
     * 当RecyclerView在windows活动时获取StaggeredGridLayoutManager布局管理器，修正header和footer显示整行
     * StaggeredGridLayoutManager 瀑布流
     * @param holder the RecyclerView.ViewHolder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            if (BEHAVIOR_MODE == ONLY_HEADER) { //如果是headerView 第0个item认定为headerView,设置占据一行
                p.setFullSpan(holder.getLayoutPosition() == 0);
            } else if (BEHAVIOR_MODE == ONLY_FOOTER) {  //设置最后一条占据一行
                p.setFullSpan(holder.getLayoutPosition() == mItems.size() + 1);
            } else if (BEHAVIOR_MODE == BOTH_HEADER_FOOTER) {   //设置headerview和footer占据一行
                if (holder.getLayoutPosition() == 0 || holder.getLayoutPosition() == mItems.size() + 1) {
                    p.setFullSpan(true);
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {  //第一条是否返回header 最后一条是否返回footer
        if (position == 0 && (BEHAVIOR_MODE == ONLY_HEADER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER))
            return VIEW_TYPE_HEADER;
        if (position + 1 == getItemCount() && (BEHAVIOR_MODE == ONLY_FOOTER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER))
            return VIEW_TYPE_FOOTER;
        else return VIEW_TYPE_NORMAL;
    }

    protected int getIndex(int position) {
        return BEHAVIOR_MODE == ONLY_HEADER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER ? position - 1 : position;
    }

    @Override
    public int getItemCount() {     //得到itemView的总数
        if (BEHAVIOR_MODE == ONLY_FOOTER || BEHAVIOR_MODE == ONLY_HEADER) {
            return mItems.size() + 1;
        } else if (BEHAVIOR_MODE == BOTH_HEADER_FOOTER) {
            return mItems.size() + 2;
        } else return mItems.size();
    }

    public int getCount() { //得到item数,并没有加上header或者footer
        return mItems.size();
    }

    protected abstract RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type);

    protected abstract void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, T item, int position);

    public final View getHeaderView() {
        return this.mHeaderView;
    }

    public final void setHeaderView(View view) {
        this.mHeaderView = view;
    }

    public final List<T> getItems() {
        return mItems;
    }


    public void addAll(List<T> items) {
        if (items != null) {
            this.mItems.addAll(items);
            notifyItemRangeInserted(this.mItems.size(), items.size()); //todo 这个可能有点问题,应该是过去的item的size
        }
    }

    public final void addItem(T item) {
        if (item != null) {
            this.mItems.add(item);
            notifyItemChanged(mItems.size()); //todo 这个可能有点问题,headerView没算上,mItems可能应该加一
        }
    }


    public void addItem(int position, T item) {
        if (item != null) {
            this.mItems.add(getIndex(position), item);
            notifyItemInserted(position);   //todo 这个可能有点问题,headerView没算上(不知道要不要算上headerView)
        }
    }

    public void replaceItem(int position, T item) {
        if (item != null) {
            this.mItems.set(getIndex(position), item);
            notifyItemChanged(position);
        }
    }

    public void updateItem(int position) {
        if (getItemCount() > position) {
            notifyItemChanged(position);
        }
    }


    public final void removeItem(T item) {
        if (this.mItems.contains(item)) {
            int position = mItems.indexOf(item);
            this.mItems.remove(item);
            notifyItemRemoved(position);
        }
    }

    public final void removeItem(int position) {    //删除items里的position,并不算上headerView的size
        if (this.getItemCount() > position) {
            this.mItems.remove(getIndex(position));
            notifyItemRemoved(position);
        }
    }

    public final T getItem(int position) {
        int p = getIndex(position);
        if (p < 0 || p >= mItems.size())
            return null;
        return mItems.get(getIndex(position));
    }

    public final void resetItem(List<T> items) {
        if (items != null) {
            clear();
            addAll(items);
        }
    }

    public final void clear() {
        this.mItems.clear();
        setState(STATE_HIDE, false);
        notifyDataSetChanged();
    }

    public void setState(int mState, boolean isUpdate) {    //一般用来隐藏footerView
        this.mState = mState;
        if (isUpdate)
            updateItem(getItemCount() - 1);
    }

    public int getState() {
        return mState;
    }

    /**
     * 添加项点击事件
     *
     * @param onItemClickListener the RecyclerView item click listener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 添加项点长击事件
     *
     * @param onItemLongClickListener the RecyclerView item long click listener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public final void setOnLoadingHeaderCallBack(OnLoadingHeaderCallBack listener) {
        onLoadingHeaderCallBack = listener;
    }


    /**
     * 可以共用同一个listener，相对高效
     */
    public static abstract class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            onClick(holder.getAdapterPosition(), holder.getItemId());
        }

        public abstract void onClick(int position, long itemId);
    }


    /**
     * 可以共用同一个listener，相对高效
     */
    public static abstract class OnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            return onLongClick(holder.getAdapterPosition(), holder.getItemId());
        }

        public abstract boolean onLongClick(int position, long itemId);
    }


    /**
     *
     */
    public interface OnItemClickListener {
        void onItemClick(int position, long itemId);
    }


    public interface OnItemLongClickListener {
        void onLongClick(int position, long itemId);
    }

    /**
     * for load header view
     */
    public interface OnLoadingHeaderCallBack {
        RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent); //给予视图模板Holder

        void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position);  //给模板填充不同数据
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pb_footer;
        public TextView tv_footer;

        public FooterViewHolder(View view) {
            super(view);
            pb_footer = (ProgressBar) view.findViewById(R.id.pb_footer);
            tv_footer = (TextView) view.findViewById(R.id.tv_footer);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
