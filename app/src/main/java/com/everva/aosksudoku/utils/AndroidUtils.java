package com.everva.aosksudoku.utils;

import java.util.List;

import org.everva.aosksudoku.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

public class AndroidUtils {
	/**
	 * Indicates whether the specified action can be used as an intent. This
	 * method queries the package manager for installed packages that can
	 * respond to an intent with the specified action. If no suitable package is
	 * found, this method returns false.
	 *
	 * @param context The application's environment.
	 * @param action  The Intent action to check for availability.
	 * @return True if an Intent with the specified action can be sent and
	 *         responded to, false otherwise.
	 */
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public static void setThemeFromPreferences(Context context) {
		SharedPreferences gameSettings = PreferenceManager.getDefaultSharedPreferences(context);
		String theme = gameSettings.getString("theme", "default");
		if (theme.equals("default")) {
			context.setTheme(R.style.Theme_Default);
		} else if (theme.equals("paper")) {
			context.setTheme(R.style.Theme_Paper);
		} else if (theme.equals("graphpaper")) {
			context.setTheme(R.style.Theme_GraphPaper);
		} else if (theme.equals("light")) {
			context.setTheme(R.style.Theme_Light);
        } else if (theme.equals("paperlight")) {
            context.setTheme(R.style.Theme_PaperLight);
        } else if (theme.equals("graphpaperlight")) {
            context.setTheme(R.style.Theme_GraphPaperLight);
        } else if (theme.equals("highcontrast")) {
            context.setTheme(R.style.Theme_HighContrast);
        } else if (theme.equals("invertedhighcontrast")) {
            context.setTheme(R.style.Theme_InvertedHighContrast);
		} else {
			context.setTheme(R.style.Theme_Default);
		}
	}

	/**
	 * Returns version code of AoskSudoku.
	 *
	 * @return
	 */
	public static int getAppVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns version name of AoskSudoku.
	 *
	 * @return
	 */
	public static String getAppVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
