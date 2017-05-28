package ru.onotole.dumper.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by onotole on 25/05/2017.
 */
@ToString
@Getter
public class TankPerUserStat {
    @Setter
    private int user_id;
    private all all;
    private int tank_id;

    @ToString
    @Getter
    public class all {
        private int wins;
        private int battles;
    }


}
