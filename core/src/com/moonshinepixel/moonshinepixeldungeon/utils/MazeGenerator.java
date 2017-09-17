package com.moonshinepixel.moonshinepixeldungeon.utils;

import java.util.Arrays;
import java.util.Collections;

/*
 * recursive backtracking algorithm
 * shamelessly borrowed from the ruby at
 * http://weblog.jamisbuck.org/2010/12/27/maze-generation-recursive-backtracking
 */


public class MazeGenerator {
    private final int x;
    private final int sx;
    private final int rx;
    private final int y;
    private final int dx;
    private final int dy;
    private final int[][] maze;
    private final int[] out;
    private int exX=0;
    private int exY=0;

    public MazeGenerator(int sx, int sy, int dx, int dy) {
        this.dx=dx;
        this.dy=dy;
        this.x = (int)Math.floor((sx-1)/2);
        this.y = (int)Math.floor((sy-1)/2);
        this.rx = sx;
        this.sx = ((int)Math.floor((sy-1)/2))*2+1;
        maze = new int[this.x][this.y];
        out = new int[(sx+dx)*(sy+dy)];
        System.out.println("MazeGen-"+out.length);
        generateMaze(0, 0);
    }
    public MazeGenerator(int sx, int sy){
        this(sx,sy, 0, 0);
    }

    public int[] map() {
        String outp="";
        for (int i = 0; i < y; i++) {
            // draw the north edge
            for (int j = 0; j < x; j++) {
                //System.out.print((maze[j][i] & 1) == 0 ? "XX" : "XO");
                outp+=((maze[j][i] & 1) == 0 ? "XX" : "XO");
            }
            //System.out.println("X");
            outp+=("X");
            outp+=sx<rx?"X":"";
            for (int j =0; j<dx; j++) outp+="X";
            // draw the west edge
            for (int j = 0; j < x; j++) {
                //System.out.print((maze[j][i] & 8) == 0 ? "XO" : "OO");
                outp+=((maze[j][i] & 8) == 0 ? "XO" : "OO");
            }
            //System.out.println("X");
            outp+=("X");
            outp+=sx<rx?"X":"";
            for (int j =0; j<dx; j++) outp+="X";
        }
        // draw the bottom line
        for (int j = 0; j < x; j++) {
            //System.out.print("XX");
            outp+=("XX");
        }
        //System.out.println("X");
        outp+=("X");

        String[] outs = outp.split("");
        for(int i=0; i<outs.length;i++){
            switch (outs[i]){
                case "X":
                    out[i]=4;
                    break;
                case "O":
                    out[i]=1;
                    break;
            }
        }
        //System.out.println(out);
        for(int i=0; i<out.length;i++){
            out[i]=out[i]==0?4:out[i];
            System.out.print((i%(rx+dx)==0?"\n":"")+out[i]);
        }
        System.out.println("\n\n\n"+sx+"|"+rx);
        return out;
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
    }
}