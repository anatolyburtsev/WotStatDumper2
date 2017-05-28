package ru.onotole.dumper.parallel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.onotole.dumper.model.TankPerUserStat;
import ru.onotole.dumper.utils.DB;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * Created by onotole on 28/05/2017.
 */
@Slf4j
@RequiredArgsConstructor
public class SaverToDB extends TimerTask{
    private final BlockingQueue<TankPerUserStat> queue;
    private final DB db = new DB();

    private static final int BUNCH_SIZE = 100;

    @Override
    @SneakyThrows
    public void run() {
        while (queue != null && queue.size() > BUNCH_SIZE) {
            List<TankPerUserStat> bunchToSave = new ArrayList<>();
            for (int i = 0; i < BUNCH_SIZE; i++) {
                bunchToSave.add(queue.take());
            }
            log.info(String.format("saving to db ids from %s to %s, queue size: %s", bunchToSave.get(0),
                    bunchToSave.get(bunchToSave.size()-1), queue.size()));

            for (TankPerUserStat aBunchToSave : bunchToSave) {
                db.putDataToDB(aBunchToSave);
            }
            log.info("data saved to db");
        }
    }
}
