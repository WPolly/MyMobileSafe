package com.xiaoshan.mymobilesafe.ui;

import com.xiaoshan.mymobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {
	
	private TextView settingTitle;
	private TextView settingDesc;
	private CheckBox settingStatus;
	private String descOn;
	private String descOff;

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		descOn = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.xiaoshan.mymobilesafe", "desc_on");
		descOff = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.xiaoshan.mymobilesafe", "desc_off");
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.xiaoshan.mymobilesafe", "title");
		settingTitle.setText(title);
	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}
	
	private void initView(Context context) {
		View view = View.inflate(context, R.layout.setting_item_view, this);
		settingTitle = (TextView) view.findViewById(R.id.tv_title);
		settingDesc = (TextView) view.findViewById(R.id.tv_desc);
		settingStatus = (CheckBox) view.findViewById(R.id.cb_check_status);
	}
	
	public boolean isChecked() {
		return settingStatus.isChecked();
	}
	
	public void setChecked(boolean checked) {
		if (checked) {
			setDesc(descOn);
		}else {
			setDesc(descOff);
		}
		settingStatus.setChecked(checked);
	}
	
	public void setDesc(String desc) {
		settingDesc.setText(desc);
	}
	
}
