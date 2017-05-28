package ru.onotole.dumper.model;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class Tank {
    private String name;
    private int tier;
    private int tank_id;
    private String nation;
    private String type;
    private IntParam default_profile;

    @ToString
    @Getter
    public class IntParam {
        private int maneuverability;
        private int protection;
        private int shot_efficiency;
        private int firepower;
    }

    public static String toCSVDefault() {
        return "name;tier;tank_id;nation;type;maneuverability;protection;shot_efficiency;firepower";
    }

    public String toCSV() {
        List<String> list = new ArrayList<>();
        list.add(name);
        list.add("" + tier);
        list.add("" + tank_id);
        list.add(nation);
        list.add(type);
        list.add("" + default_profile.maneuverability);
        list.add("" + default_profile.protection);
        list.add("" + default_profile.shot_efficiency);
        list.add("" + default_profile.firepower);
        return String.join(";", list);
    }
}
