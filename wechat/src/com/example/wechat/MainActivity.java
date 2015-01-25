package com.example.wechat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;

public class MainActivity extends ActionBarActivity implements OnClickListener,
		OnPageChangeListener {
	private ViewPager mViewPager;
	private List<Fragment> mTabs = new ArrayList<Fragment>();
	private FragmentPagerAdapter adapter;
	private String[] mTitles = new String[] { "微信", "通讯录", "发现", "我" };
	private List<BottomItemChangeColor> mTabIndicators = new ArrayList<BottomItemChangeColor>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setOverflowButtonAlways();
		getActionBar().setDisplayShowHomeEnabled(false);
		initView();
		initData();
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(this);
	}

	private void initData() {
		for (String title : mTitles) {
			TabFragment tabFragment = new TabFragment();
			Bundle bundle = new Bundle();
			bundle.putString("title", title);
			tabFragment.setArguments(bundle);
			mTabs.add(tabFragment);
		}
		adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return mTabs.size();
			}

			@Override
			public Fragment getItem(int position) {
				return mTabs.get(position);
			}
		};
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		BottomItemChangeColor tab_weChat = (BottomItemChangeColor) findViewById(R.id.id_wechat);
		mTabIndicators.add(tab_weChat);
		BottomItemChangeColor tab_contacts = (BottomItemChangeColor) findViewById(R.id.id_contacts);
		mTabIndicators.add(tab_contacts);
		BottomItemChangeColor tab_found = (BottomItemChangeColor) findViewById(R.id.id_found);
		mTabIndicators.add(tab_found);
		BottomItemChangeColor tab_me = (BottomItemChangeColor) findViewById(R.id.id_me);
		mTabIndicators.add(tab_me);
		tab_weChat.setOnClickListener(this);
		tab_contacts.setOnClickListener(this);
		tab_found.setOnClickListener(this);
		tab_me.setOnClickListener(this);
		tab_weChat.setIconAlpha(1.0f);
	}

	/*
	 * 通过反射获取overflow 的图标
	 */
	private void setOverflowButtonAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKey = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKey.setAccessible(true);
			menuKey.setBoolean(config, false);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 设置显示icon
	 * 
	 * @see android.app.Activity#onMenuOpened(int, android.view.Menu)
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method method = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					method.setAccessible(true);
					method.invoke(menu, true);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		resetAllTabs();
		switch (v.getId()) {
		case R.id.id_wechat:
			mTabIndicators.get(0).setIconAlpha(1.0f);
			;
			mViewPager.setCurrentItem(0, false);
			break;
		case R.id.id_contacts:
			mTabIndicators.get(1).setIconAlpha(1.0f);
			;
			mViewPager.setCurrentItem(1, false);
			break;
		case R.id.id_found:
			mTabIndicators.get(2).setIconAlpha(1.0f);
			;
			mViewPager.setCurrentItem(2, false);
			break;
		case R.id.id_me:
			mTabIndicators.get(3).setIconAlpha(1.0f);
			;
			mViewPager.setCurrentItem(3, false);
			break;
		default:
			break;
		}
	}

	private void resetAllTabs() {
		for (int i = 0; i < mTabIndicators.size(); i++) {
			mTabIndicators.get(i).setIconAlpha(0);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		if (positionOffset > 0) {
			BottomItemChangeColor left = mTabIndicators.get(position);
			BottomItemChangeColor right = mTabIndicators.get(position + 1);
			left.setIconAlpha(1 - positionOffset);
			right.setIconAlpha(positionOffset);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}

}
