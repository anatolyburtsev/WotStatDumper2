package ru.onotole.dumper;

import lombok.SneakyThrows;
import ru.onotole.dumper.model.Tank;
import ru.onotole.dumper.utils.APIProcessor;
import ru.onotole.dumper.utils.DB;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FillUpTankData {
    private final APIProcessor apiProcessor = new APIProcessor();

    @SneakyThrows
    public static void main(String[] args) {
        FillUpTankData fillUp = new FillUpTankData();
        DB db = new DB();
        List<Tank> tankDataList = fillUp.apiProcessor.getTankData();
        for (Tank tank :
                tankDataList) {
            db.putTankDataToDB(tank);
        }
        db.close();
//        Path file = Paths.get("tank_data.csv");
//        List<String> toFile = new ArrayList<>();
//        toFile.add(Tank.toCSVDefault());
//        tankDataList.forEach(p -> toFile.add(p.toCSV()));
//        Files.write(file, toFile);
    }
}
