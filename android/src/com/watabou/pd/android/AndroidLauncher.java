/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pd.android;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.moonshinepixel.moonshinepixeldungeon.MoonshinePixelDungeon;
import com.moonshinepixel.moonshinepixeldungeon.input.GameAction;
import com.watabou.utils.PDPlatformSupport;

public class AndroidLauncher extends AndroidApplication {
	public static AndroidLauncher instance;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		String version;
		int versionCode;
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().build();

		StrictMode.setThreadPolicy(policy);
		try {
			version = getPackageManager().getPackageInfo( getPackageName(), 0 ).versionName;
			versionCode = getPackageManager().getPackageInfo( getPackageName(), 0  ).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			version = "???";
			versionCode = 0;
		}
		instance=this;
		initialize(new MoonshinePixelDungeon(new PDPlatformSupport<GameAction>(version, versionCode, "android", new AndroidInputProcessor(), false)), config);
	}

	public static void rotate(boolean landscape){
		instance.setRequestedOrientation (landscape?ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public void shareImage(Pixmap pm){

	}
}
