package com.xiaoshan.mymobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoshan.mymobilesafe.R;

public class SettingSelectView extends RelativeLayout {
	
	private TextView settingTitle;
	private TextView settingDesc;

	public SettingSelectView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingSelectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.xiaoshan.mymobilesafe", "title");
		String desc = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.xiaoshan.mymobilesafe", "desc");
		settingTitle.setText(title);
		settingDesc.setText(desc);
	}

	public SettingSelectView(Context context) {
		super(context);
		initView(context);
	}
	
	private void initView(Context context) {
		View view = View.inflate(context, R.layout.setting_select_view, this);
		settingTitle = (TextView) view.findViewById(R.id.tv_title);
		settingDesc = (TextView) view.findViewById(R.id.tv_desc);
	}
	
	
	
	public void setDesc(String desc) {
		settingDesc.setText(desc);
	}
	
}
