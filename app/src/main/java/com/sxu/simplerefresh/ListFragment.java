package com.sxu.simplerefresh;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sxu.refreshlayout.RefreshLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Freeman
 * @date 2017/12/28
 */

public class ListFragment extends Fragment {

	private View mContentView;
	private RefreshLayout refreshLayout;

	private BaseAdapter adapter;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 100) {
				refreshLayout.refreshComplete();
			} else {
				adapter.notifyDataSetChanged();
				refreshLayout.refreshComplete();
			}
		}
	};

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_list_layout, null);
		return mContentView;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ListView listView = mContentView.findViewById(R.id.list);
		refreshLayout = mContentView.findViewById(R.id.refresh_layout);

		final TextView textView = new TextView(getActivity());
		textView.setText("textview");
		listView.addHeaderView(textView);

		final List<String> items = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			items.add("Item" + i);
		}
		adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				textView.setText("textview222");
			}
		});
		refreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh(View headerView) {
				Log.i("out", "onRefresh");
				handler.sendEmptyMessageDelayed(100, 2000);
			}

			@Override
			public void onLoad(View footerView) {
				for (int i = 0; i < 20; i++) {
					items.add("Item" + i);
				}
				handler.sendEmptyMessageDelayed(0, 2000);
			}
		});
	}
}
