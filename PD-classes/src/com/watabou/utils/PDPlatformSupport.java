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

package com.watabou.utils;

import com.watabou.input.NoosaInputProcessor;

public class PDPlatformSupport<GameActionType> {
	private final String version;
	private final boolean previewmode;
	private final int versionCode;
	private final String basePath;
	private final NoosaInputProcessor<GameActionType> inputProcessor;

	public PDPlatformSupport(String version, int versionCode, String basePath, NoosaInputProcessor<GameActionType> inputProcessor, boolean previewmode) {
		this.version = version;
		this.versionCode = versionCode;
		this.basePath = basePath;
		this.inputProcessor = inputProcessor;
		this.previewmode = previewmode;
	}

	public String getVersion() {
		return version;
	}

	public int getVersionCode() { return versionCode; }

	public String getBasePath() {
		return basePath;
	}

	public NoosaInputProcessor<GameActionType> getInputProcessor() {
		return inputProcessor;
	}

	public boolean isFullscreenEnabled() {
		return false;
	}

	public boolean isPreviewmode(){
		return previewmode;
	}
}
