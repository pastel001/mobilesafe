package com.dwl.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dwl.mobilesafe.db.dao.BlackNumberDao;
import com.dwl.mobilesafe.dto.BlackNumber;

public class CallSmsSafeActivity extends Activity {
	private ListView lv_call_sms_safe;
	private List<BlackNumber> list;
	private BlackNumberDao dao;
	private LinearLayout ll_loading;
	private CallSmsSafeAdapter adapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			isloading = false;
			ll_loading.setVisibility(View.INVISIBLE);
			if (adapter == null) {
				adapter = new CallSmsSafeAdapter();
				lv_call_sms_safe.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		};
	};
	// ��ҳ��ѯ�ȹ�
	// ������
	private int counts;
	// ÿ�β�ѯ��������Ĭ��Ϊ10
	private int count = 10;
	// ÿ�β�ѯ��ƫ����
	private int offset = 0;
	// �Ƿ����ڼ��أ��Ӵ�����ֹ�ظ�����
	private boolean isloading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_laoding);
		dao = new BlackNumberDao(this);
		// ��ȡһ���ж�������
		counts = dao.findCount();
		fillData();
		lv_call_sms_safe.setOnScrollListener(new OnScrollListener() {
			// ����״̬�仯���õķ���
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case SCROLL_STATE_IDLE:
					if (list.size() - view.getLastVisiblePosition() <= 3) {// getLastVisiblePosition,listView�Ŀ�ʼλ��Ϊ0
						if (isloading) {
							return;
						}
						fillData();
						if (offset >= counts
								&& (list.size() == view
										.getLastVisiblePosition() + 1)) {//ֻ�е�����Ҫ�ټ������ݣ������������һ��λ��ʱ��ʾû�и�������
							Toast.makeText(getApplicationContext(), "û�и���������",
									0).show();
						}
					}
					break;

				case SCROLL_STATE_FLING:

					break;
				case SCROLL_STATE_TOUCH_SCROLL:

					break;
				}

			}

			// ������ɺ���õķ���
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void fillData() {
		// ��ʱ�Ĳ���д�����߳�
		if (offset >= counts) {
			return;
		}
		ll_loading.setVisibility(View.VISIBLE);
		isloading = true;
		new Thread() {
			public void run() {
				if (list == null) {
					list = dao.findPart(count, offset);
				} else {
					list.addAll(dao.findPart(count, offset));
				}
				offset += count;
				// ���̲߳��ܸ���UI
				// lv_call_sms_safe.setAdapter(new CallSmsSafeAdapter());
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private class CallSmsSafeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView != null) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_call_sms_safe_item, null);
				holder = new ViewHolder();
				holder.tv_number = (TextView) view.findViewById(R.id.number);
				holder.tv_mode = (TextView) view.findViewById(R.id.mode);
				view.setTag(holder);
			}
			BlackNumber blackNumber = list.get(position);
			holder.tv_number.setText(blackNumber.getNumber());
			String mode = blackNumber.getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("�绰����");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("��������");
			} else if ("3".equals(mode)) {
				holder.tv_mode.setText("ȫ������");
			}
			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	class ViewHolder {
		TextView tv_number;
		TextView tv_mode;
	}
}