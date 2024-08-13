import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonProcessor {
    private static final BlockingQueue<String> buffer = new LinkedBlockingQueue<>();
    private static final int LIMIT = 20;

    public static void main(String[] args) {
        String filePath = "strings.json";

        Thread producer = new Thread(() -> {
            try {
                readJsonFileAndProduce(filePath);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                consumeAndPrint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void readJsonFileAndProduce(String filePath) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filePath)) {
            Object obj = jsonParser.parse(reader);

            JSONObject jsonObject = (JSONObject) obj;
            JSONArray stringsArray = (JSONArray) jsonObject.get("strings");

            if (stringsArray != null) {
                for (Object stringObj : stringsArray) {
                    if (stringObj instanceof String) {
                        try {
                            buffer.put((String) stringObj); 
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        }
    }

    public static void consumeAndPrint() throws InterruptedException {
        while (true) {
            List<String> stringsToPrint = new ArrayList<>();
            buffer.drainTo(stringsToPrint, LIMIT);

            if (!stringsToPrint.isEmpty()) {
                for (String str : stringsToPrint) {
                    System.out.println(str);
                }
            }

            if (buffer.isEmpty() && Thread.currentThread().getState() == Thread.State.TERMINATED) {
                break;
            }

            Thread.sleep(2000);
        }
    }
}
