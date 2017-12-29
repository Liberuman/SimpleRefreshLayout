package com.sxu.simplerefresh;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * @author Freeman
 * @date 2017/12/28
 */


public class RefreshMainFragment extends Fragment implements View.OnClickListener {

	private View mContentView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_refresh_main_layout, null);
		return mContentView;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Button listButton = mContentView.findViewById(R.id.list_button);
		Button gridButton = mContentView.findViewById(R.id.grid_button);
		Button recycleButton = mContentView.findViewById(R.id.recycle_button);
		Button scrollButton = mContentView.findViewById(R.id.scroll_button);
		Button webViewButton = mContentView.findViewById(R.id.webview_button);
		Button pagerButton = mContentView.findViewById(R.id.pager_button);
		Button textButton = mContentView.findViewById(R.id.text_button);
		Button customizeButton = mContentView.findViewById(R.id.customized_button);

		listButton.setOnClickListener(this);
		gridButton.setOnClickListener(this);
		recycleButton.setOnClickListener(this);
		scrollButton.setOnClickListener(this);
		webViewButton.setOnClickListener(this);
		pagerButton.setOnClickListener(this);
		textButton.setOnClickListener(this);
		customizeButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.list_button:
				addFragment(new ListFragment());
				break;
			case R.id.grid_button:
				addFragment(new GridFragment());
				break;
			case R.id.recycle_button:
				addFragment(new RecycleFragment());
				break;
			case R.id.scroll_button:
				addFragment(new ScrollViewFragment());
				break;
			case R.id.webview_button:
				addFragment(new WebFragment());
				break;
			case R.id.pager_button:
				addFragment(new ViewPagerFragment());
				break;
			case R.id.text_button:
				addFragment(new TextFragment());
				break;
			case R.id.customized_button:
				addFragment(new CustomizeRefreshFragment());
				break;
			default:
				break;
		}
	}

	private void addFragment(Fragment fragment) {
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.add(R.id.container_layout, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}
