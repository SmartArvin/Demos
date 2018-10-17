package com.example.daic.install;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.factory.vo.EnumAcOnPowerOnMode;
import com.smart.arvin.supertools.R;
import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity implements OnClickListener{
    private static final String TAG = "OTA";
    
    
    private File ota_files;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ota_files= new File("/data/update_signed.zip");
    }
    
    @Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.install_force :
			Intent intent0 = new Intent();
			intent0.setAction("android.intent.action.INFOCUS.RECOVERY");
			intent0.putExtra("mount_point","/cache/update.zip");
			intent0.putExtra("updateRightNow", true);
			sendBroadcast(intent0);
			AndroidConfirmUpdate();
			break;
		case R.id.install_nextboot :
			Intent intent1 = new Intent();
			intent1.setAction("android.intent.action.INFOCUS.RECOVERY");
			intent1.putExtra("mount_point","/cache/update.zip");
			intent1.putExtra("updateRightNow", false);
			sendBroadcast(intent1);
			AndroidConfirmUpdate();
			break;
		case R.id.install_ktc_local :
			try {
				TvManager.getInstance().getFactoryManager().setEnvironmentPowerMode(EnumAcOnPowerOnMode.values()[2]);
			} catch (TvCommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AndroidConfirmUpdate();
			break;

		default:
			break;
		}
	}

    private void AndroidConfirmUpdate(){
        try {
            RecoverySystem.verifyPackage(ota_files, new RecoverySystem.ProgressListener() {
                @Override
                public void onProgress(int progress) {
                    Log.i(TAG, "progress = " + progress);
                }
            } , null);
            Log.i(TAG, "------verifyPackage  Over----");
            try {
            	Log.i(TAG, "------installPackage  start----");
                RecoverySystem.installPackage(MainActivity.this, ota_files);
                Log.i(TAG, "------into  recovery----");
            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException: "+e.toString());
            }
        }catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        Log.i(TAG, "Exception: "+e.toString());
	    }
    }

}
