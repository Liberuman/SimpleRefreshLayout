package com.sxu.simplerefresh;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxu.refreshlayout.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Freeman
 * @date 2017/12/28
 */


public class RecycleFragment extends Fragment {

	private View mContentView;
	private List<String> data = new ArrayList<>();

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_recycle_layout, null);
		return mContentView;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ViewPager viewPager = mContentView.findViewById(R.id.viewPager);
		for (int i = 0; i < 50; i++) {
			data.add("item" + (i+1));
		}

		viewPager.setAdapter(new ViewPagerAdapter());
	}

	public class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
			return view.equals(object);
		}

		@NonNull
		@Override
		public Object instantiateItem(@NonNull ViewGroup container, int position) {
			View itemView = View.inflate(getContext(), R.layout.item_recycle_layout, null);
			final RefreshLayout refreshLayout = itemView.findViewById(R.id.refresh_layout);
			final RecyclerView recyclerView = itemView.findViewById(R.id.recycle);
			refreshLayout.setMode(RefreshLayout.Mode.MODE_BOTH);
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
			if (position == 0) {
				recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
				recyclerView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
					@Override
					public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
						return new MyViewHolder(View.inflate(getContext(),
								android.R.layout.simple_list_item_1, null));
					}

					@Override
					public void onBindViewHolder(MyViewHolder holder, int position) {
						holder.textView.setText(data.get(position));
					}

					@Override
					public int getItemCount() {
						return data.size();
					}
				});
			} else if (position == 1) {
				recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
				recyclerView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
					@Override
					public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
						return new MyViewHolder(View.inflate(getContext(),
								android.R.layout.simple_list_item_1, null));
					}

					@Override
					public void onBindViewHolder(MyViewHolder holder, int position) {
						holder.textView.setText(data.get(position));
					}

					@Override
					public int getItemCount() {
						return data.size();
					}
				});
			} else {
				recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
				recyclerView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
					@Override
					public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
						return new MyViewHolder(View.inflate(getContext(),
								android.R.layout.simple_list_item_1, null));
					}

					@Override
					public void onBindViewHolder(MyViewHolder holder, int position) {
						holder.textView.setText(data.get(position));
					}

					@Override
					public int getItemCount() {
						return data.size();
					}
				});
			}
			ViewGroup.LayoutParams params = container.getLayoutParams();
			//params.width = getActivity().getResources().getDisplayMetrics().widthPixels;
			//params.height = getActivity().getResources().getDisplayMetrics().heightPixels;
			container.addView(itemView);

			return itemView;
		}

		@Override
		public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
			container.removeView((View) object);
		}
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {

		public TextView textView;

		public MyViewHolder(View view) {
			super(view);
			textView = view.findViewById(android.R.id.text1);
		}
	}
}
