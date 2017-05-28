package ru.onotole.dumper.model;

import lombok.Getter;

/**
 * Created by onotole on 25/05/2017.
 */
public class Config {
    private static final String APPLICATION_ID = "77636487a299e95a72583de66dba7a6";
    public static final String WOTB_ACCOUNT_INFO_URL = "https://api.wotblitz.ru/wotb/account/info/?application_id=" + APPLICATION_ID + "&account_id=%s&fields=account_id";
    public static final String WOTB_TANKS_STATS_URL = "https://api.wotblitz.ru/wotb/tanks/stats/?application_id=" + APPLICATION_ID + "&account_id=%s&fields=all.battles,all.wins,tank_id";
    public static final String WOTB_ENCYCLOPEDIA_VEHICLES = "https://api.wotblitz.ru/wotb/encyclopedia/vehicles/?application_id=" + APPLICATION_ID + "&fields=tank_id,name,tier,type,nation,default_profile.maneuverability,default_profile.protection,default_profile.shot_efficiency,default_profile.firepower";
}
