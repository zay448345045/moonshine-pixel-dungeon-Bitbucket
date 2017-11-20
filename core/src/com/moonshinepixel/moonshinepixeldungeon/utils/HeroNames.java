package com.moonshinepixel.moonshinepixeldungeon.utils;

import com.moonshinepixel.moonshinepixeldungeon.messages.Messages;
import com.watabou.utils.GameArrays;
import com.watabou.utils.Random;

import java.util.Arrays;
import java.util.Locale;

public enum HeroNames {
    MALE,
    FEMALE;

    private static String[] male;
    private static String[] female;
    private static String[] titles;
    private static String[] specNames;
    private static String[] specTitles;

    public static String randomName(HeroNames hn){
        if (male!=null&&female!=null&&titles!=null&&specNames!=null){
            try {
                switch (hn) {
                    case MALE:
                        return Random.element(male);
                    case FEMALE:
                        return Random.element(female);
                    default:
                        return "!!!NO TEXT FOUND!!!";
                }
            } catch (Exception e){
                return "!!!NO TEXT FOUND!!!";
            }
        } else {
            init();
            return randomName(hn);
        }
    }

    public static String randomTitle(){
        try {
            return Random.element(titles);
        } catch (NullPointerException e){
            init();
            try {
                return Random.element(titles);
            } catch (NullPointerException ee){
                return "%s the !!!NO TEXT FOUND!!!";
            }
        }
    }

    public static String getName(HeroNames hn, float titleChance){
        return Random.Float()<titleChance?titledName(hn):randomName(hn);
    }

    public static String titledName(HeroNames hn){
        return titledName(randomName(hn));
    }
    public static String titledName(String name){
        if (hasTitle(name)){
            return changeTitle(name,randomTitle());
        }
        if (GameArrays.contain(specNames,name.toLowerCase(Locale.ENGLISH))){
            return titledName(name,Random.element(Messages.get(HeroNames.class,name).split("_")));
        } else {
            return titledName(name,randomTitle());
        }
    }

    public static String titledName(String name, String title){
        return String.format(title,name);
    }

    public static boolean hasTitle(String name){
        String[] alltitles = GameArrays.concat(titles,specTitles,String.class);
        //System.out.println(name);
        for (String title:alltitles){
            //System.out.println(title);
            if (name.contains(title.replaceAll("%s",""))){
                return true;
            }
            //System.out.println(false+"\n");
        }
        return false;
    }
    public static String getTitle(String name){
        String[] alltitles = GameArrays.concat(titles,specTitles,String.class);
        //System.out.println(name);
        for (String title:alltitles){
            //System.out.println(title);
            if (name.contains(title.replaceAll("%s",""))){
                return title;
            }
            //System.out.println(false+"\n");
        }
        return null;
    }
    public static String changeTitle(String name, String newtitle){
        name = name.replaceAll(getTitle(name).replaceAll("%s",""),"");
        return titledName(name);
    }

    public static void init(){
        male=Messages.get(HeroNames.class,"male").split("_");
        female=Messages.get(HeroNames.class,"female").split("_");
        titles=Messages.get(HeroNames.class,"titles").split("_");
        specNames=Messages.get(HeroNames.class,"specnames").split("_");
        specTitles = new String[0];
        for (String key : specNames){
            specTitles=GameArrays.concat(specTitles,Messages.get(HeroNames.class,key).split("_"),String.class);
        }
        //System.out.println(Messages.get(HeroNames.class,"titles"));
        //System.out.println(specNames[0]);
    }
}
