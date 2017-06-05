package ru.onotole.dumper.utils;

import com.google.gson.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.onotole.dumper.model.Config;
import ru.onotole.dumper.model.Tank;
import ru.onotole.dumper.model.TankPerUserStat;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by onotole on 25/05/2017.
 */
@Slf4j
public class APIProcessor {
    private Gson gson = new Gson();
    private JsonParser jsonParser = new JsonParser();
    private static final int BUNCH_SIZE_PER_REQ = 100;
    private static final Path file = Paths.get("accountIds.txt");

    @SneakyThrows
    public void getTankUserStatById(Integer id, BlockingQueue<TankPerUserStat> queue) {
        String url = String.format(Config.WOTB_TANKS_STATS_URL, id);
        Reader reader = APIProcessor.getByUrl(url);
        JsonObject jObj = jsonParser.parse(reader).getAsJsonObject();
        if (jObj.get("data").isJsonNull()) {
            log.error("Problem with json output: " + jObj.getAsString() + " by url: " + url);
            return;
        }
        JsonElement data = jObj.get("data").getAsJsonObject().get("" + id);
        if (data instanceof JsonNull) {
            log.error("Problem2 with json output: " + jObj.getAsString() + " by url: " + url);
            return;
        }
        JsonArray jsonArray = data.getAsJsonArray();
        for (JsonElement element : jsonArray ) {
            TankPerUserStat user = gson.fromJson(element, TankPerUserStat.class);
            user.setUser_id(id);
            queue.put(user);
        }
    }

    public void getValidAccountIdSaveToFile(int id) {
        List<String> list = getValidAccountId(id);
        saveListToFile(file, list);
    }

    @SneakyThrows
    public void getValidAccountIdsPutToQueue(int id, BlockingQueue<String> queue) {
        List<String> list = getValidAccountId(id);
        for (String s: list) {
            queue.put(s);
        }
    }

    @SneakyThrows
    public List<String> getValidAccountId(int startId) {
        StringBuilder ids = new StringBuilder(BUNCH_SIZE_PER_REQ * 2);
        for (int i = 0; i < BUNCH_SIZE_PER_REQ - 1; i++) {
            ids.append(startId + i).append(",");
        }
        ids.append(startId + BUNCH_SIZE_PER_REQ);

        String url = String.format(Config.WOTB_ACCOUNT_INFO_URL, ids.toString());
        Reader response = getByUrl(url);
        return extractUserIdsListFromJson(response);
    }

    @SneakyThrows
    public List<Tank> getTankData() {
        String url = Config.WOTB_ENCYCLOPEDIA_VEHICLES;
        List<Tank> resultList = new ArrayList<>();
        Reader response = getByUrl(url);
        JsonObject wholeObject = jsonParser.parse(response).getAsJsonObject();
        JsonObject tanksArray = wholeObject.get("data").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry: tanksArray.entrySet()) {
            resultList.add(gson.fromJson(entry.getValue(), Tank.class));
        }
        return resultList;
    }

    private List<String> extractUserIdsListFromJson(Reader response) {
        List<String> result = new ArrayList<>();
        JsonObject wholeObject = jsonParser.parse(response).getAsJsonObject();
        if (wholeObject.get("data").isJsonNull()) {
            log.error("bad json: " + wholeObject);
            return result;
        }
        JsonObject usersArray = wholeObject.get("data").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : usersArray.entrySet()) {
            if (! entry.getValue().isJsonNull()) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    @SneakyThrows
    private synchronized void saveListToFile(Path file, List<String> list) {
        Files.write(file, list);
    }

    private static Reader getByUrl(String URL) throws InterruptedException {
        Reader reader = null;
        while (reader == null) {
            try {
                java.net.URL url = new URL(URL);
                InputStream in = url.openStream();
                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            } catch (IOException e) {
                reader = null;
                log.info("{} Network problem with URL : {}, trace: {}", Thread.currentThread().getName(),
                        URL, e.toString());
                Thread.sleep(10_000);
            }
        }

        return reader;
    }
}
