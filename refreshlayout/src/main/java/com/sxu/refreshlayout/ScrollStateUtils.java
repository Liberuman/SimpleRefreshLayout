package com.sxu.refreshlayout;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

/**
 * @author Freeman
 * @date 2017/12/26
 */


public class ScrollStateUtils {

	public static boolean reachTop(View view) {
		if (view instanceof AbsListView) {
			return absListViewReachTop((AbsListView) view);
		} else if (view instanceof ScrollView) {
			return scrollViewReachTop((ScrollView) view);
		} else if (view instanceof RecyclerView) {
			return !view.canScrollVertically(-1);
		} else {
			return true;
		}
	}

	public static boolean reachBottom(View view) {
		if (view instanceof AbsListView) {
			return absListViewReachBottom((AbsListView) view);
		} else if (view instanceof RecyclerView) {
			return !view.canScrollVertically(1);
		} else {
			return false;
		}
	}

	public static boolean absListViewReachTop(AbsListView listView) {
		int firstItemTop = 0;
		if (listView.getChildCount() > 0) {
			firstItemTop = listView.getChildAt(0).getTop() - listView.getPaddingTop();
		}
		Log.i("out", "firstItemTop===" + firstItemTop);
		return listView.getFirstVisiblePosition() == 0 && firstItemTop == 0;
	}

	public static boolean scrollViewReachTop(ScrollView scrollView) {
		return scrollView.getScrollY() == 0;
	}

	public static boolean recycleViewReachTop(RecyclerView recyclerView) {
		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
		if (layoutManager == null || layoutManager.getItemCount() == 0) {
			return true;
		}

		View firstChild = recyclerView.getChildAt(0);
		int position = recyclerView.getChildAdapterPosition(firstChild);
		int firstItemTop = firstChild.getTop() - recyclerView.getPaddingTop();

		if (layoutManager instanceof LinearLayoutManager
				&& ((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL) {
			return position == 0 && firstItemTop == 0;
		} else if (layoutManager instanceof GridLayoutManager || layoutManager instanceof StaggeredGridLayoutManager) {
			return position < ((GridLayoutManager)layoutManager).getSpanCount() && firstItemTop == 0;
		} else {
			/**
			 * Nothing
			 */
		}

		return false;
	}

	public static boolean absListViewReachBottom(AbsListView listView) {
		if (listView.getChildCount() > 0) {
			int lastItemBottom = listView.getChildAt(listView.getChildCount() - 1).getBottom();
			int listHeight = listView.getBottom() - listView.getTop();
			if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1  && lastItemBottom <= listHeight) {
				return true;
			}
		}

		return false;
	}
}
