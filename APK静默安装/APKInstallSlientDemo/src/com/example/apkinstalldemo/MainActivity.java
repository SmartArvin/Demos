package com.example.apkinstalldemo;

import java.io.File;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btn_install, btn_uninstall;
	private APKUtils mAPKUtils ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mAPKUtils = APKUtils.getInstance(this);
		
		btn_install = (Button) findViewById(R.id.btn_install);
		btn_uninstall = (Button) findViewById(R.id.btn_uninstall);

		btn_install.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new installApk("MyTest.apk").execute();
			}
		});

		btn_uninstall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAPKUtils.uninstallApkBySlientPm("com.example.mytest");
			}
		});
		
	}
	
	class installApk extends AsyncTask<Void, Void, Void> {
		String apkName;

		public installApk(String apkName) {
			this.apkName = apkName;
		}

		@Override
		protected Void doInBackground(Void... arg0) {// copyAssets
			mAPKUtils.copyAssetsToFile();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mAPKUtils.installApkBySilentPm(getFilesDir().getPath() + File.separator + apkName);//OK
			//mAPKUtils.installApkBySilentInvoke(getFilesDir().getPath() + File.separator + apkName);//OK
			//mAPKUtils.installApkBySilentIPm(getFilesDir().getPath() + File.separator + apkName);//OK
		}
	}
	
}
