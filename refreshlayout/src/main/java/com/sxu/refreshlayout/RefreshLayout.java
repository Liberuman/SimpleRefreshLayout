package com.sxu.refreshlayout;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * @author Freeman
 * @date 2017/12/25
 */


public class RefreshLayout extends LinearLayout {

	private int mHeaderViewResId;
	private int mFooterViewResId;
	private int mHeaderViewHeight;
	private int mFooterViewHeight;
	private int mMinVelocity;
	private int mRefreshStatus;
	private int mScreenHeight;
	private int mDefaultScrollDistance;
	private int mLoadMoreOffset; // 触底加载后页面的偏移量
	private float mScrollY;
	private float mStartX;
	private float mStartY;
	private float mDownY;
	private float mViscosity = 0.35f; // 粘性系统，控制下拉或上拉时的阻力，取值(0, 1]
	private boolean mSupportedTouchBottomLoad;

	private boolean cancelRefresh = false;
	private boolean cancelLoad = false;
	private int maxHeaderHeight = 600;
	private int scrollDistance = 0;
	protected View mHeaderView;
	protected View mFooterView;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private OnRefreshListener mListener;

	private final int REFRESH_STATUS_NONE = 0;
	private final int REFRESH_STATUS_REFRESH = 1;
	private final int REFRESH_STATUS_LOAD_MORE = 2;
	private final int REFRESH_STATUS_REFRESH_COMPLETE = 3;
	private final int REFRESH_STATUS_LOAD_COMPLETE = 4;

	private final int SPRING_BACK_DURATION = 300;
	private final int DEFAULT_SCROLL_DURATION = 600;
	private final int DELAY_RESUME_DURATION = 500;
	private final int DELAY_COMPLETE_DURATION = 250;
	private final int DEFAULT_OFFSET_DURATION = 500;

	private Mode mMode = Mode.MODE_BOTH;

	public enum Mode {
		/**
		 * 下拉刷新和上拉加载都不可用
		 */
		MODE_DISABLE(0),
		/**
		 * 只可下拉刷新
		 */
		MODE_REFRESH(1),
		/**
		 * 只可上拉加载
		 */
		MODE_LOAD_MORE(2),
		/**
		 * 同时支持下拉刷新和上拉加载
		 */
		MODE_BOTH(3);

		static Mode getMode(int modeValue) {
			for (Mode value : Mode.values()) {
				if (modeValue == value.getValue()) {
					return value;
				}
			}

			return MODE_REFRESH;
		}

		private int mValue;

		Mode(int value) {
			this.mValue = value;
		}

		public int getValue() {
			return mValue;
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				mRefreshStatus = REFRESH_STATUS_REFRESH_COMPLETE;
				mScroller.startScroll(0, 0, 0, mHeaderViewHeight, DEFAULT_SCROLL_DURATION);
			} else {
				mRefreshStatus = REFRESH_STATUS_LOAD_COMPLETE;
				mScroller.startScroll(0, mDefaultScrollDistance + mFooterViewHeight,
						0, -mFooterViewHeight, DEFAULT_SCROLL_DURATION);
			}
			invalidate();
		}
	};

	public RefreshLayout(Context context) {
		this(context, null, 0);
	}

	public RefreshLayout(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray arrays = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout);
		mHeaderViewResId = arrays.getResourceId(R.styleable.RefreshLayout_headerViewResId, R.layout.item_header_layout);
		mFooterViewResId = arrays.getResourceId(R.styleable.RefreshLayout_footerViewResId, R.layout.item_footer_layout);
		mMode = Mode.getMode(arrays.getInt(R.styleable.RefreshLayout_mode, 1));
		mSupportedTouchBottomLoad = arrays.getBoolean(R.styleable.RefreshLayout_supportTouchBottomLoad, true);
		mViscosity = arrays.getFloat(R.styleable.RefreshLayout_viscosity, 0.5f);
		mLoadMoreOffset = arrays.getDimensionPixelOffset(R.styleable.RefreshLayout_loadMoreOffset, 150);
		arrays.recycle();

		initView(context);
	}

	private void initView(Context context) {
		setOrientation(VERTICAL);
		mRefreshStatus = REFRESH_STATUS_NONE;
		mScroller = new Scroller(context);
		mScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
		mVelocityTracker = VelocityTracker.obtain();
		ViewConfiguration configuration = ViewConfiguration.get(context);
		mMinVelocity = configuration.getScaledMinimumFlingVelocity();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (getChildCount() > 1) {
			throw new IllegalStateException("including up to one child view");
		}

		addHeaderViewAndFooterView();
	}

	protected void addHeaderViewAndFooterView() {
		if ((mMode == Mode.MODE_REFRESH || mMode == Mode.MODE_BOTH) && mHeaderView == null) {
			mHeaderView = View.inflate(getContext(), mHeaderViewResId, null);
			addView(mHeaderView, 0);
		}

		if ((mMode == Mode.MODE_LOAD_MORE || mMode == Mode.MODE_BOTH) && mFooterView == null) {
			mFooterView = View.inflate(getContext(), mFooterViewResId, null);
			addView(mFooterView, getChildCount());
			if (mSupportedTouchBottomLoad) {
				setTouchBottomLoadEnabled(getChildCount() > 2 ? getChildAt(1) : getChildAt(0));
			}
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			scrollTo(0, mScroller.getCurrY());
			invalidate();
		} else {
			if (mRefreshStatus == REFRESH_STATUS_REFRESH_COMPLETE
					|| mRefreshStatus == REFRESH_STATUS_NONE) {
				resetRefreshLayout();
				mRefreshStatus = REFRESH_STATUS_NONE;
			} else if (mRefreshStatus == REFRESH_STATUS_LOAD_COMPLETE) {
				resetLoadMoreLayout();
				mRefreshStatus = REFRESH_STATUS_NONE;
				// 加载更多完成后，页面上移，为用户展示部分新数据
				if (mLoadMoreOffset != 0) {
					View childView = getChildAt(1);
					if (childView instanceof AbsListView) {
						((AbsListView) childView).smoothScrollBy(mLoadMoreOffset, DEFAULT_OFFSET_DURATION);
					} else if (childView instanceof RecyclerView) {
						((RecyclerView) childView).smoothScrollBy(0, mLoadMoreOffset);
					} else if (childView instanceof ScrollView) {
						((ScrollView) childView).smoothScrollBy(0, mLoadMoreOffset);
					}
				}
			} else if (mRefreshStatus == REFRESH_STATUS_NONE) {
				resetLoadMoreLayout();
			}
		}
	}

	@Override
	public void setOrientation(int orientation) {
		if (orientation == VERTICAL) {
			super.setOrientation(orientation);
		}
	}

	public void setMode(Mode mode) {
		this.mMode = mode;
		addHeaderViewAndFooterView();
	}

	public void setSupportedTouchBottomLoad(boolean supported) {
		this.mSupportedTouchBottomLoad = supported;
	}

	public void setTouchBottomLoadEnabled(View contentLayout) {
		if (contentLayout instanceof AbsListView) {
			((AbsListView) contentLayout).setOnScrollListener(new AbsListView.OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {

				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					if (firstVisibleItem != 0 && ScrollStateUtils.reachBottom(view)) {
						startLoadMore();
					}
				}
			});
		} else if (contentLayout instanceof RecyclerView) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				contentLayout.setOnScrollChangeListener(new OnScrollChangeListener() {
					@Override
					public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
						if (ScrollStateUtils.reachBottom(v)) {
							startLoadMore();
						}
					}
				});
			} else {
				((RecyclerView) contentLayout).setOnScrollListener(new RecyclerView.OnScrollListener() {
					@Override
					public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
						super.onScrollStateChanged(recyclerView, newState);
					}

					@Override
					public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
						super.onScrolled(recyclerView, dx, dy);
						if (ScrollStateUtils.reachBottom(recyclerView)) {
							startLoadMore();
						}
					}
				});
			}
		}
	}

	public void startLoadMore() {
		if (mListener != null) {
			mListener.onLoad(mFooterView);
		}
		showLoadingLayout();
		mRefreshStatus = REFRESH_STATUS_LOAD_MORE;
		mScroller.startScroll(0, getScrollY(), 0, mFooterViewHeight, DEFAULT_SCROLL_DURATION);
		invalidate();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				mStartX = ev.getX();
				mStartY = ev.getY();
				mDownY = mScrollY;
				break;
			case MotionEvent.ACTION_MOVE:
				// View在顶部且Mode为可刷新或View已滑到底部且可加载更多时
				if (Math.abs(ev.getY() - mStartY) > Math.abs(ev.getX() - mStartX)
						&& (mRefreshStatus != REFRESH_STATUS_NONE
							|| ((mMode == Mode.MODE_REFRESH || mMode == Mode.MODE_BOTH)
								&& ev.getY() >= mStartY && ScrollStateUtils.reachTop(getChildAt(1)))
							|| ((mMode == Mode.MODE_LOAD_MORE || mMode == Mode.MODE_BOTH) && ev.getY() <= mStartY
								&& ScrollStateUtils.reachBottom(getChildAt(1))))) {
					return true;
				}
				mDownY = ev.getY();
				break;
			case MotionEvent.ACTION_UP:
				break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mVelocityTracker.addMovement(event);
		float currentTouchY = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (mScrollY == 0) {
					mScrollY = mStartY;
				}
				//mViscosity = (maxHeaderHeight - Math.abs(getScrollY())) * 1.0f / maxHeaderHeight;
				float differentY = (currentTouchY - mScrollY) * mViscosity;
				mScrollY = currentTouchY;
				scrollDistance += differentY;
				Log.i("out", "ccc====" + differentY + " refreshStatus==" + mRefreshStatus);
				if (mRefreshStatus == REFRESH_STATUS_NONE
						|| ((mRefreshStatus == REFRESH_STATUS_REFRESH
							|| mRefreshStatus == REFRESH_STATUS_REFRESH_COMPLETE) && differentY > 0)
						|| ((mRefreshStatus == REFRESH_STATUS_LOAD_MORE
							|| mRefreshStatus == REFRESH_STATUS_LOAD_COMPLETE) && differentY < 0)) {
					scrollBy(0, (int) -differentY);
					Log.i("out", "scrollY=============" + scrollDistance + " getScrollY==" + getScrollY());
					if (getScrollY() < 0 && mRefreshStatus == REFRESH_STATUS_NONE) {
						showRefreshReleaseLayout();
					} else if (scrollDistance < -mFooterViewHeight) {
						//showLoadReleaseLayout();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if (currentTouchY > mStartY) { // 下拉刷新
					Log.i("out", "up Event=======");
					if (getScrollY() < 0) {
						if (mListener != null && mRefreshStatus == REFRESH_STATUS_NONE) {
							mListener.onRefresh(mHeaderView);
						}
						showRefreshingLayout();
						mRefreshStatus = REFRESH_STATUS_REFRESH;
						mScroller.startScroll(0, getScrollY(), 0, Math.abs(getScrollY()), SPRING_BACK_DURATION);
					} else if (getScrollY() > 0) {
						if (mRefreshStatus == REFRESH_STATUS_LOAD_MORE) {
							handler.removeCallbacksAndMessages(null);
							mRefreshStatus = REFRESH_STATUS_NONE;
						}
						mScroller.startScroll(0, getScrollY(), 0, mHeaderViewHeight - getScrollY(), SPRING_BACK_DURATION);
					}
				} else { // 上拉加载
					if (getScrollY() - mDefaultScrollDistance < mFooterViewHeight) {
						if (mRefreshStatus == REFRESH_STATUS_REFRESH) {
							handler.removeCallbacksAndMessages(null);
							mRefreshStatus = REFRESH_STATUS_NONE;
						}
						mScroller.startScroll(0, getScrollY(), 0,
								mDefaultScrollDistance - getScrollY(), SPRING_BACK_DURATION);
					} else {
						if (mListener != null && mRefreshStatus == REFRESH_STATUS_NONE) {
							mListener.onLoad(mFooterView);
						}
						showLoadingLayout();
						mRefreshStatus = REFRESH_STATUS_LOAD_MORE;
						mScroller.startScroll(0, getScrollY(), 0,
								mFooterViewHeight + mDefaultScrollDistance - getScrollY(), SPRING_BACK_DURATION);
					}
				}

				invalidate();
				mVelocityTracker.clear();
				mScrollY = 0;
				scrollDistance = 0;
				break;
			default:
				break;
		}

		return true;
	}

	/**
	 * 手动调用刷新数据
	 */
	public void startRefresh() {
		if ((mMode == Mode.MODE_BOTH || mMode == Mode.MODE_REFRESH)
				&& mRefreshStatus == REFRESH_STATUS_NONE) {
			if (mListener != null) {
				mListener.onRefresh(mHeaderView);
			}
			mRefreshStatus = REFRESH_STATUS_REFRESH;
			mScroller.startScroll(0, mHeaderViewHeight, 0, -mHeaderViewHeight, DEFAULT_SCROLL_DURATION);
			invalidate();
		}
	}

	public void refreshComplete() {

		if (mRefreshStatus == REFRESH_STATUS_REFRESH) {
			refreshingComplete();
			handler.sendEmptyMessageDelayed(1, DELAY_COMPLETE_DURATION);
		}

		if (mRefreshStatus == REFRESH_STATUS_LOAD_MORE) {
			if (getScrollY() > mFooterViewHeight) {
				loadingComplete();
				handler.sendEmptyMessageDelayed(0, DELAY_COMPLETE_DURATION);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = 0;
		for (int i = 0; i < getChildCount(); i++) {
			View childView = getChildAt(i);
			measureChild(childView, widthMeasureSpec, heightMeasureSpec);
			height += getChildAt(i).getMeasuredHeight();
			if (mRefreshStatus == REFRESH_STATUS_NONE && childView.getMeasuredHeight() != 0
					&& i == 1 && childView.getBottom() < mScreenHeight) {
				if (mMode == Mode.MODE_REFRESH || mMode == Mode.MODE_BOTH) {
					childView.setMinimumHeight(mScreenHeight - childView.getTop() + mHeaderViewHeight);
				} else {
					childView.setMinimumHeight(mScreenHeight - childView.getTop());
				}
			}
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		mHeaderViewHeight = getChildAt(0).getMeasuredHeight();
		mFooterViewHeight = getChildAt(getChildCount() - 1).getMeasuredHeight();
		mDefaultScrollDistance  = mHeaderViewHeight + getPaddingTop();
		if (mRefreshStatus == REFRESH_STATUS_NONE) {
			scrollTo(0, mDefaultScrollDistance);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mVelocityTracker != null) {
			mVelocityTracker.clear();
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
		super.onDetachedFromWindow();
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		this.mListener = listener;
	}

	public void setHeaderViewResId(@LayoutRes int headerViewResId) {
		this.mHeaderViewResId = headerViewResId;
	}

	public void setFooterViewResId(@LayoutRes int footerViewResId) {
		this.mFooterViewResId = footerViewResId;
	}

	/**
	 * 显示松手布局时（释放立即刷新），设置布局样式
	 */
	protected void showRefreshReleaseLayout() {

	}

	/**
	 * 显示刷新中的布局时，设置布局样式
	 */
	protected void showRefreshingLayout() {

	}

	/**
	 * 显示加载更多布局时，设置布局样式
	 */
	protected void showLoadingLayout() {

	}

	/**
	 * 显示刷新中的布局时，设置布局样式
	 */
	protected void refreshingComplete() {

	}

	/**
	 * 显示加载更多布局时，设置布局样式
	 */
	protected void loadingComplete() {

	}

	/**
	 * 显示松手布局时（释放立即加载），设置布局样式
	 */
	protected void showLoadReleaseLayout() {

	}

	/**
	 * 刷新完成后重置头部刷新布局的内容
	 */
	protected void resetRefreshLayout() {

	}

	/**
	 * 刷新完成后重置底部加载布局的内容
	 */
	protected void resetLoadMoreLayout() {

	}

	public interface OnRefreshListener {

		void onRefresh(View headerView);

		void onLoad(View footerView);
	}
}
