### 组件介绍
一个极简的组件，核心代码只是一个自定义View，代码不超过500行，是一个学习事件分发和View滚动机制的不错选择。虽然代码比较少，但却提供了完整功能。

### 特点

- 同时支持下拉刷新和上拉加载；
- 支持触底自动加载；
- 支持常用View；
- 支持自定义头部和底部布局；


### 描述

1. 该组件对所有View提供了下拉和上拉加载功能，具体使用哪种模式可通过设置mode属性来实现：


    // 下拉刷新和上拉加载都不可用
	MODE_DISABLE(0),
	
	// 只可下拉刷新
	MODE_REFRESH(1),
	
	// 只可上拉加载
	MODE_LOAD_MORE(2),
	
	// 同时支持下拉刷新和上拉加载
	MODE_BOTH(3);
	
2. 对AbsListView(如ListView, GridView)和RecycleView提供了触底加载机制，通过设置mSupportedTouchBottomLoad来实现开启，默认为开启状态；
3. 通过示例代码对ListView, GridView, ScrollView, WebView, RecycleView, ViewPager, TextView进行了测试，都可正常工作；
4. 该组件提供了自定义头部和底部布局的功能，既可通过设置监听来实现，也可通过重写RefreshLayout来实现统一风格的刷新组件。


    refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
    	@Override
    	public void onRefresh(View headerView) {
    		// 设置刷新中的布局样式
    	}
    
    	@Override
    	public void onLoad(View footerView) {
    		// 设置加载更多时的布局样式
    	}
    });
   
通过重写RefreshLayout中的几个方法实现统一样式的刷新组件：

    public class MyRefreshLayout extends RefreshLayout {
    
    	public MyRefreshLayout(Context context) {
    		super(context);
    	}
    
    	public MyRefreshLayout(Context context, @Nullable AttributeSet attrs) {
    		super(context, attrs);
    	}
    
    	public MyRefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    		super(context, attrs, defStyleAttr);
    	}
    
    	@Override
    	protected void addHeaderViewAndFooterView() {
    		mHeaderView = View.inflate(getContext(), R.layout.item_my_header_layout, null);
    		mFooterView = View.inflate(getContext(), R.layout.item_my_footer_layout, null);
    		addView(mHeaderView, 0);
    		addView(mFooterView, getChildCount());
    	}
    
    	@Override
    	protected void showRefreshingLayout() {
    		
    	}
    
    	@Override
    	protected void showLoadingLayout() {
    		
    	}
    
    	@Override
    	protected void refreshingComplete() {
    		
    	}
    
    	@Override
    	protected void loadingComplete() {
    		
    	}
    
    	@Override
    	protected void resetRefreshLayout() {
    		
    	}
    
    	@Override
    	protected void resetLoadMoreLayout() {
    		
    	}
    }

### 源码地址：
[SimpleRefresh](https://github.com/JuHonggang/SimpleRefreshLayout)

