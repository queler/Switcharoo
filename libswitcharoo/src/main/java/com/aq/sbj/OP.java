package com.aq.sbj;

import java.util.EnumSet;

/**
 * Created by amq102 on 6/13/2015.
 */
public enum OP {
    swap,
    dd,
    hit,
    stand,
    split;
    public static final EnumSet<OP> NEW_HAND (){return  EnumSet.of(OP.hit);}
    /**
     * swap, dd, H/S
     */
    public static final EnumSet<OP> AFTER_DEAL(){return EnumSet.of(OP.swap, OP.dd, OP.hit, OP.stand);}
    /**
     * hit, stand,dd
     */
    public static final EnumSet<OP> PRE_HIT(){return EnumSet.of(OP.dd,OP.hit,OP.stand);}
    /**
     * Normal GamePlay, hit and stand
     */
    public static final EnumSet<OP> PLAY(){return EnumSet.of(OP.hit,OP.stand);}
    public static final EnumSet<OP> NONE_OF(){return  EnumSet.noneOf(OP.class);}
}
