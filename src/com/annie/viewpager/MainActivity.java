package com.annie.viewpager;

import android.os.Bundle;
import android.os.Handler;
import android.R.integer;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int VIEWPAGER_PAGE_CHANGED = 0;

	private ViewPager viewPager;
	
	private int[] imageResIds = {
			R.drawable.a,
			R.drawable.b,
			R.drawable.c,
			R.drawable.d,
			R.drawable.e,
	};

	private String[] descs = {
			"巩俐不低俗，我就不能低俗",
			"扑树又回来啦！再唱经典老歌引万人大合唱",
			"揭秘北京电影如何升级",
			"乐视网TV版大派送",
			"热血屌丝的反杀",
	};

	//创建一个imageview类型的数组,用来保存要显示的图片
	private ImageView[] imageViews = new ImageView[imageResIds.length];
	//创建数组保存点
	private View[] point = new View[imageResIds.length];
	//用来保存当前白点的位置,为空表示黑点
	private View currentPoint;
	private int pagesize;

	private TextView viewpager_text;
	
	Handler hd = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case VIEWPAGER_PAGE_CHANGED:
				//自动切换界面
				switchChange();
				break;
			}
			
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
		
	}

	/**
	 * @Description:设置界面切换的方法
	 * @param:
	 */
	protected void switchChange() {
		//获取当前显示的界面.获取当前显示的条目(下标)
		int currentItem = viewPager.getCurrentItem();
		//获取下一个界面的条目
		if (currentItem == viewPager.getAdapter().getCount() - 1) {
			currentItem = 0;
		}else {
			currentItem++;
		}
		
		//设置当前显示的界面
		viewPager.setCurrentItem(currentItem);
		
		//给handl发送延时消息,自动切换下一个界面
		hd.sendEmptyMessageDelayed(VIEWPAGER_PAGE_CHANGED, 1600);
	}

	//当界面由不可见-->可见的时候,自动切换界面
	@Override
	protected void onStart() {
		super.onStart();
		//发送消息自动切换界面
		hd.sendEmptyMessageDelayed(VIEWPAGER_PAGE_CHANGED, 1600);
	}
	
	//当界面由可见-->不可见的时候,销毁消息,不再自动切换,避免浪费资源
	@Override
	protected void onStop() {
		super.onStop();
		//删除消息任务
		hd.removeMessages(VIEWPAGER_PAGE_CHANGED);
	}
	
	/**
	 * @Description:初始化控件
	 * @param:
	 */
	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewpager_text = (TextView) findViewById(R.id.viewpager_text);
		ll_point = (LinearLayout) findViewById(R.id.ll_dot);
		
		//将要展示的图片保存到imageview数组中的imageview对象中
		for (int i = 0; i < imageResIds.length; i++) {
			//创建imageview对象,保存到imageview数组中(或者可以在创建数组的时候直接往里面存imageview对象)
			imageViews[i] = new ImageView(this);
			imageViews[i].setImageResource(imageResIds[i]);
			
			//创建点,有多少张图片创建多少个点
			point[i] = new View(this);
			//设置点的宽高
			LinearLayout.LayoutParams params = new LayoutParams(5, 5);
			//设置点与点之间的间距
			params.rightMargin = 5;
			//设置点的背景(状态选择器)
			point[i].setBackgroundResource(R.drawable.selector_dot);
			//将之前设置params属性设置给点
			point[i].setLayoutParams(params);
			//将点放到布局文件中创建的容器中
			ll_point.addView(point[i]);
		}
		
		//设置viewpager的界面切换监听
		viewPager.setOnPageChangeListener(onPageChangeListener);
		//设置开始的时候显示第一张图片的文本信息
		change(0);
	 	
		pagesize = imageResIds.length * 100 * 100;
		
		// 根据listview相似
		viewPager.setAdapter(new MyAdapter());
				
		//解决第一个图片向左永久滑动的操作
		int currentPosition = pagesize / 2;
		//设置当前显示的条目位置是currentPosition
		//注: 设置viewpager当前显示的界面，参数：显示的界面的位置,必须显示数据之后，才能设置当前显示的位置
		viewPager.setCurrentItem(currentPosition);
		
	}
	
	//设置viewpager的界面切换监听
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		//当viewpager界面切换完成调用的方法
		@Override
		public void onPageSelected(int position) {
			//当viewpager界面切换完成的时候显示文本
			change(position);
		}
		//当viewpager切换的时候调用的方法
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// TODO Auto-generated method stub
			
		}
		//当viewpager切换状态改变的时候调用的方法
		@Override
		public void onPageScrollStateChanged(int state) {
			//SCROLL_STATE_IDLE : 空闲状态
			//SCROLL_STATE_DRAGGING : 拖动的状态
			//SCROLL_STATE_SETTLING: 滑动到最后的状态
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				//当处于空闲状态时,发送消息
				hd.sendEmptyMessageDelayed(VIEWPAGER_PAGE_CHANGED, 1600);
			}else {
				//删除消息任务,不能自动滑动
				hd.removeMessages(VIEWPAGER_PAGE_CHANGED);
			}
		}
	};

	private LinearLayout ll_point;

	//当viewpager界面切换完成的时候显示文本
	public void change(int position) {
		
		position = position % imageResIds.length;
		
		viewpager_text.setText(descs[position]);
		
		//判断当前点是黑点还是白点,当下一个界面是白点的时候，上一个界面会变成黑点
		if (currentPoint !=null) {
			//不为空说明为白点-->界面切换完成后,须变成黑点
			currentPoint.setSelected(false);
		}
		//界面切换完成后将,该界面位置对应的点设置为白色,设置点是否被选中,true;选中 false:没有被选中
		point[position].setSelected(true);
		//将白色点的信息存入,currentPoint,用作后续判断
		currentPoint = point[position];
	}
	
	//为ViewPager设置适配器
	private class MyAdapter extends PagerAdapter {

		//设置条目的个数
		@Override
		public int getCount() {
			return pagesize;
		}

		// 判断viewpager的界面的对象是否和instantiateItem条目的对象一致，一致表示可以执行，不一致：没有条目
		// View : viewpager的界面的对象; object:instantiateItem返回的object对象
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
		//因为viewPager最多同时加载三个界面,所以当显示新的条目时就需要通过这个方法来添加条目
		//container : viewpager; position : 条目的位置
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			position = position % imageResIds.length;
			//根据条目的位置获取相应的imageView
			ImageView imageView = imageViews[position];
			//将imageview添加到ViewPager中展示
			container.addView(imageView);
			//返回添加成功的view对象，添加了什么view对象，就返回什么样的view对象
			return imageView;
		}
	
		//删除viewpager条目的操作
		//container : ViewPager; position : 条目的位置; Object : instantiateItem返回的添加的对象
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			
			//super.destroyItem(container, position, object);抛出异常的,可直接删了
			//删除已添加的条目
			container.removeView((View) object);
		}
	}

}
