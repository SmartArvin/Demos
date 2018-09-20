package com.example.apkinstalldemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.content.Context;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class APKUtils {

	private static final String TAG = "APKUtils";
	private static APKUtils mAPKUtils;
	private static Context mContext = null;
	private final int INSTALL_REPLACE_EXISTING = 2;

	public static APKUtils getInstance(Context context) {
		mContext = context;
		if (mAPKUtils == null) {
			mAPKUtils = new APKUtils();
		}
		return mAPKUtils;
	}

	public APKUtils() {
		super();
	}

	/**
	 * @TODO 获取ROOT权限实现静默安装apk
	 * @param apkPath
	 * @return
	 */
	public boolean installApkBySilentPm(String apkPath) {
		boolean result = false;
		try {
			String command = "pm install "+ apkPath;
			Runtime runtime = Runtime.getRuntime();

			Process proc = runtime.exec(command);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return result;
	}
	
	public void installApkBySilentIPm(String apkPath) {
		PackageInstallObserver installObserver = new PackageInstallObserver();
		try {
			Log.d(TAG, "apkPath = " + apkPath);
			Class<?> ServiceManager = Class
					.forName("android.os.ServiceManager");
			Method getService = ServiceManager.getDeclaredMethod("getService",
					String.class);
			getService.setAccessible(true);
			IBinder packAgeBinder = (IBinder) getService
					.invoke(null, "package");
			IPackageManager iPm = IPackageManager.Stub
					.asInterface(packAgeBinder);
			iPm.installPackage(Uri.fromFile(new File(apkPath)),
					installObserver, INSTALL_REPLACE_EXISTING,
					new File(apkPath).getPath());

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "安装失败1");
			try {
				installObserver.packageInstalled(null, -1);
				Log.d(TAG, "安装失败2");
			} catch (RemoteException ignore) {
				Log.d(TAG, "安装失败3");
			}
		}
	}
	
	public class PackageInstallObserver extends IPackageInstallObserver.Stub {

		@Override
		public void packageInstalled(String packageName, int returnCode)
				throws RemoteException {
			if (returnCode == 1) // 返回1表示安装成功，否则安装失败
			{
				Log.i(TAG, "安装成功！");
			} else {
				Log.i(TAG, "安装失败！");
			}
		}
	}


	public void installApkBySilentInvoke(String installPath) {
		Class<?> pmService;
		Class<?> activityTherad;
		Method method;
		try {
			activityTherad = Class.forName("android.app.ActivityThread");
			Class<?> paramTypes[] = getParamTypes(activityTherad,
					"getPackageManager");
			method = activityTherad.getMethod("getPackageManager", paramTypes);
			Object PackageManagerService = method.invoke(activityTherad);
			pmService = PackageManagerService.getClass();
			Class<?> paramTypes1[] = getParamTypes(pmService, "installPackage");
			method = pmService.getMethod("installPackage", paramTypes1);
			method.invoke(PackageManagerService, Uri.parse(installPath), null,
					0, null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static Class<?>[] getParamTypes(Class<?> cls, String mName) {
		Class<?> cs[] = null;
		Method[] mtd = cls.getMethods();
		for (int i = 0; i < mtd.length; i++) {
			if (!mtd[i].getName().equals(mName)) {
				continue;
			}
			cs = mtd[i].getParameterTypes();
		}
		return cs;
	}

	
	/**
	 * @TODO 静默卸载
	 * @param pgName
	 * @return
	 */
	public boolean uninstallApkBySlientPm(String pgName) {
		boolean result = false;
		try {
			String command = "pm uninstall "+ pgName;
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec(command);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return result;
	}

	/**
	 * @TODO 将assets目录指定文件复制至file目录
	 * @param null
	 * 
	 */
	public void copyAssetsToFile() {
		try {
			AssetManager am = mContext.getAssets();
			String[] filesName = am.list("files");
			Log.i(TAG, "filesName:  " + filesName.length);
			for (String fileName : filesName) {
				int byteread = 0;
				File file = new File(mContext.getFilesDir().getPath()
						+ File.separator + fileName);
				Log.i(TAG, mContext.getFilesDir().getPath() + File.separator
						+ fileName);
				if (!file.exists() || file.length() == 0) {
					InputStream is = am.open("files/" + fileName);
					FileOutputStream fs = mContext.openFileOutput(fileName,
							Context.MODE_PRIVATE);
					byte[] buffer = new byte[1444];

					while ((byteread = is.read(buffer)) != -1) {
						fs.write(buffer, 0, byteread);
					}
					is.close();
					fs.close();
				}

				chmodFile(mContext.getFilesDir().getPath() + File.separator
						+ fileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * @TODO 修改文件权限
	 * @param filePath
	 */
	private void chmodFile(String filePath) {
		try {
			String command = "chmod 777 " + filePath;
			Runtime runtime = Runtime.getRuntime();

			Process proc = runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
