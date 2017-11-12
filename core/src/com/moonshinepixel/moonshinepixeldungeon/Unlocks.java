package com.moonshinepixel.moonshinepixeldungeon;

import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;

public enum Unlocks {

	GOLDHUD,
	BUTTONBRONZE,
	BUTTONSILVER,
	BUTTONGOLDEN,
	BUTTONEMERALD,
	ITEMRENAMING;

	public int id(){
		return (int)Math.pow(2,ordinal());
	}

	public String dispName(){
		return Messages.get(this,toString().toLowerCase()+".name");
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
		switch (id){
			case GOLDHUD:
				return 12;
			case BUTTONBRONZE:
				return 3;
			case BUTTONSILVER:
				return 6;
			case BUTTONGOLDEN:
				return 9;
			case BUTTONEMERALD:
				return 12;
			case ITEMRENAMING:
				return 10;
			default:
				return 0;
		}
	}

	public static Unlocks[] unlockables(){
		return new Unlocks[]{
				BUTTONBRONZE,
				BUTTONSILVER,
				BUTTONGOLDEN,
				BUTTONEMERALD,
				ITEMRENAMING
		};
	}

	public boolean isUnlocked(){
		return (MoonshinePixelDungeon.unlocks()&id())!=0;
	}

	public static boolean isUnlocked(Unlocks id){
		return (MoonshinePixelDungeon.unlocks()&id.id())!=0;
	}
	public static void unlock(Unlocks id){
		MoonshinePixelDungeon.unlocks(MoonshinePixelDungeon.unlocks()|id.id());
	}
	public static void lock(Unlocks id){
		if (isUnlocked(id)){
			MoonshinePixelDungeon.unlocks(MoonshinePixelDungeon.unlocks()^id.id());
		}
	}
}