package com.sxu.simplerefresh;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxu.refreshlayout.RefreshLayout;

/**
 * @author Freeman
 * @date 2017/12/28
 */


public class ViewPagerFragment extends Fragment {

	private View mContentView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_pager_layout, null);
		return mContentView;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ViewPager viewPager = mContentView.findViewById(R.id.pager);
		final RefreshLayout refreshLayout = mContentView.findViewById(R.id.refresh_layout);

		viewPager.setAdapter(new ViewPagerAdapter());

		refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(View headerView) {
				final TextView refreshText = headerView.findViewById(R.id.refresh_text);
				refreshText.setText("刷新中...");
				refreshLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						refreshText.setText("刷新完成");
						refreshLayout.postDelayed(new Runnable() {
							@Override
							public void run() {
								refreshLayout.refreshComplete();
							}
						}, 250);
					}
				}, 2000);
			}

			@Override
			public void onLoad(View footerView) {
				final TextView refreshText = footerView.findViewById(R.id.refresh_text);
				refreshText.setText("底部在刷新...");
				refreshLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						refreshText.setText("底部刷新完成");
						refreshLayout.postDelayed(new Runnable() {
							@Override
							public void run() {
								refreshLayout.refreshComplete();
							}
						}, 250);
					}
				}, 2000);
			}
		});
	}

	public class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
			return view.equals(object);
		}

		@NonNull
		@Override
		public Object instantiateItem(@NonNull ViewGroup container, int position) {
			TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setText("item" + (position + 1));
			ViewGroup.LayoutParams params = container.getLayoutParams();
			params.width = getActivity().getResources().getDisplayMetrics().widthPixels;
			params.height = getActivity().getResources().getDisplayMetrics().heightPixels;
			container.addView(textView, params);

			return textView;
		}

		@Override
		public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

		}
	}
}
