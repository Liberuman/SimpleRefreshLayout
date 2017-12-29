package com.sxu.simplerefresh;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sxu.refreshlayout.RefreshLayout;

/**
 * @author Freeman
 * @date 2017/12/28
 */


public class CustomizeRefreshFragment extends Fragment {

	private View mContentView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_customize_refresh_layout, null);
		return mContentView;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final RefreshLayout refreshLayout = mContentView.findViewById(R.id.refresh_layout);

		refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(View headerView) {
				refreshLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						refreshLayout.refreshComplete();
					}
				}, 2000);
			}

			@Override
			public void onLoad(View footerView) {
				refreshLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						refreshLayout.refreshComplete();
					}
				}, 2000);
			}
		});
	}
}
