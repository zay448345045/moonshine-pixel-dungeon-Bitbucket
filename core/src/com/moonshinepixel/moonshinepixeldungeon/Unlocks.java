package com.moonshinepixel.moonshinepixeldungeon;

import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;

public enum Unlocks {

	GOLDHUD(12),
	BUTTONBRONZE(3),
	BUTTONSILVER(6),
	BUTTONGOLDEN(9),
	BUTTONEMERALD(12),
	ITEMRENAMING(10),
	INVULNERABILITY(10),
	TOMSTART(10,8),
	CHALLENGES(12),
	DYNASTY(10,8){
		@Override
		public String dispName() {
			return Messages.get(this,this.codeName+".codeName", getDynSize()+1);
		}
	},
	DYNASTY2(0),
	DYNASTY3(0),
	DYNASTY4(0),
	DYNASTY5(0);

	private int size;
	private int price;
	private String codeName;

	Unlocks(int price){
		this(price,9,"");
	}

	Unlocks(int price, int size){
		this(price, size,"");
	}

	Unlocks(int price, int size, String basename){
		this.size=size;
		if (basename.equals("")){
			basename=toString().toLowerCase();
		}
		codeName =basename;
		this.price=price;
	}

	public int id(){
		return (int)Math.pow(2,ordinal());
	}

	public int textSize(){
		return size;
	}

	public String dispName(){
		return Messages.get(this, codeName +".codeName");
	}

	public static final int MAX_VALUE           = 8191;

	public static boolean[] getButtonTypes(int unlocks){
		return new boolean[]{
				true,
				isUnlocked(BUTTONBRONZE),
				isUnlocked(BUTTONSILVER),
				isUnlocked(BUTTONGOLDEN),
				isUnlocked(BUTTONEMERALD)
		};
	}

	public static int price(Unlocks id){
		return id.price;
	}

	public static Unlocks[] unlockables(){
		return new Unlocks[]{
				GOLDHUD,
				BUTTONBRONZE,
				BUTTONSILVER,
				BUTTONGOLDEN,
				BUTTONEMERALD,
				ITEMRENAMING,
				CHALLENGES,
				TOMSTART,
				INVULNERABILITY,
				DYNASTY
		};
	}

	public static int getDynSize(){
		return 1+(isUnlocked(DYNASTY)?1:0)+(isUnlocked(DYNASTY2)?1:0)+(isUnlocked(DYNASTY3)?1:0)+(isUnlocked(DYNASTY4)?1:0)+(isUnlocked(DYNASTY5)?1:0);
	}

	public boolean isUnlocked(){
		return (MoonshinePixelDungeon.unlocks()&id())!=0;
	}

	public static boolean isUnlocked(Unlocks id){
		return (MoonshinePixelDungeon.unlocks()&id.id())!=0;
	}
	public static void unlock(Unlocks id){
		if (id==DYNASTY){
			if (!isUnlocked(DYNASTY2))id=DYNASTY2;
			else if (!isUnlocked(DYNASTY3))id=DYNASTY3;
			else if (!isUnlocked(DYNASTY4))id=DYNASTY4;
			else if (!isUnlocked(DYNASTY5))id=DYNASTY5;
		}
		MoonshinePixelDungeon.unlocks(MoonshinePixelDungeon.unlocks()|id.id());
	}
	public static void lock(Unlocks id){
		if (isUnlocked(id)){
			MoonshinePixelDungeon.unlocks(MoonshinePixelDungeon.unlocks()^id.id());
		}
	}
}