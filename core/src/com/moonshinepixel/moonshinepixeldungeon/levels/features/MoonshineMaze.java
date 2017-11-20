/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2017 Evan Debenham
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

package com.moonshinepixel.moonshinepixeldungeon.levels.features;

import com.moonshinepixel.moonshinepixeldungeon.utils.BArray;
import com.moonshinepixel.moonshinepixeldungeon.utils.MazeGenerator;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MoonshineMaze {
	private final int x;
	private final int y;
	private final int[][] maze;
	private Rect rect;

	public MoonshineMaze(int x, int y) {
		this.x = x;
		this.y = y;
		maze = new int[this.x][this.y];
		rect = new Rect(0,0,x,y);
		generateMaze(0, 0);
	}

	public MoonshineMaze(Rect rect){
//		x = rect.width();
//		y = rect.height();
		x = (int)Math.floor((rect.width()-1)/2);
		y = (int)Math.floor((rect.height()-1)/2);
		maze = new int[x][y];
		this.rect=rect;
		generateMaze(0,0);
	}

	public void display() {
		for (int i = 0; i < y; i++) {
			// draw the north edge
			for (int j = 0; j < x; j++) {
				//System.out.print((maze[j][i] & 1) == 0 ? "+---" : "+   ");
			}
			//System.out.println("+");
			// draw the west edge
			for (int j = 0; j < x; j++) {
				//System.out.print((maze[j][i] & 8) == 0 ? "|   " : "    ");
			}
			//System.out.println("|");
		}
		// draw the bottom line
		for (int j = 0; j < x; j++) {
			//System.out.print("+---");
		}
		//System.out.println("+");
	}

	public boolean[][] array(){
		boolean[][] map = new boolean[rect.width()][rect.height()];
		for (int i = 0; i<map.length;i++){
			BArray.setFalse(map[i]);
		}
		String out = "";
		for (int i = 0; i<y;i++){
			for (int j = 0; j < x; j++) {
				out+=(maze[j][i] & 1) == 0 ? "XX" : "XO";
			}
			out+="X\n";
			// draw the west edge
			for (int j = 0; j < x; j++) {
				out+=(maze[j][i] & 8) == 0 ? "XO" : "OO";
			}
			out+="X\n";
		}
		for (int j = 0; j < x; j++) {
			out+=("XX");
		}
		out+="X";

//		//System.out.println(out);
		String[] chArr = out.split("");
		int dY = 0;
		int dX = 0;
		for (int i = 0; i<chArr.length;i++){
			switch (chArr[i]){
				case "X": map[dX][dY] = false; dX++; break;
				case "O": map[dX][dY] = true; dX++; break;
				case "\n": dX=0; dY++; break;
			}
		}
		return map;
	}

	private void generateMaze(int cx, int cy) {
		DIR[] dirs = DIR.values();
		Collections.shuffle(Arrays.asList(dirs));
		for (DIR dir : dirs) {
			int nx = cx + dir.dx;
			int ny = cy + dir.dy;
			if (between(nx, x) && between(ny, y)
					&& (maze[nx][ny] == 0)) {
				maze[cx][cy] |= dir.bit;
				maze[nx][ny] |= dir.opposite.bit;
				generateMaze(nx, ny);
			}
		}
	}

	private static boolean between(int v, int upper) {
		return (v >= 0) && (v < upper);
	}

	private enum DIR {
		N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
		private final int bit;
		private final int dx;
		private final int dy;
		private DIR opposite;

		// use the static initializer to resolve forward references
		static {
			N.opposite = S;
			S.opposite = N;
			E.opposite = W;
			W.opposite = E;
		}

		private DIR(int bit, int dx, int dy) {
			this.bit = bit;
			this.dx = dx;
			this.dy = dy;
		}
	};

	public static void main(String[] args) {
		int x = args.length >= 1 ? (Integer.parseInt(args[0])) : 8;
		int y = args.length == 2 ? (Integer.parseInt(args[1])) : 8;
		MoonshineMaze maze = new MoonshineMaze(new Rect(0,0,14,10));
//		MoonshineMaze maze = new MoonshineMaze(8,8);
//		maze.display();
		boolean[][] arr = maze.array();
		//System.out.print("\n\n\n");
		for (int i = 0; i<arr.length;i++){
			for (int j = 0; j<arr[0].length;j++){
				//System.out.print(arr[i][j]+"|");
			}
			//System.out.print("\n");
		}
	}
}
