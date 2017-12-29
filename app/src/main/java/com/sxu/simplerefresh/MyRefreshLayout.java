package com.sxu.simplerefresh;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.sxu.refreshlayout.RefreshLayout;

/**
 * @author Freeman
 * @date 2017/12/28
 */


public class MyRefreshLayout extends RefreshLayout {

	private TextView refreshText;
	private TextView loadText;

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

		refreshText = findViewById(R.id.header_text);
		loadText = findViewById(R.id.footer_text);
	}

	@Override
	protected void showRefreshingLayout() {
		refreshText.setText("自定义刷新中");
	}

	@Override
	protected void showLoadingLayout() {
		loadText.setText("自定义加载中");
	}

	@Override
	protected void refreshingComplete() {
		refreshText.setText("自定义刷新完成");
	}

	@Override
	protected void loadingComplete() {
		loadText.setText("自定义加载完成");
	}

	@Override
	protected void resetRefreshLayout() {
		refreshText.setText("自定义头部");
	}

	@Override
	protected void resetLoadMoreLayout() {
		loadText.setText("自定义底部");
	}
}
