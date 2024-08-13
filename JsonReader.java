import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonReader {

    public static void main(String[] args) {
        try {
            String filePath = "strings.json";
            JSONArray stringsArray = readJsonFile(filePath);

            // StringBuilder to collect all strings
            StringBuilder result = new StringBuilder();

            // Iterate over the JSONArray and append each string to result
            for (Object stringObj : stringsArray) {
                String str = (String) stringObj;
                result.append(str).append(", ");

                // Sleep for 1 second between each addition
                Thread.sleep(1000);
            }

            // Remove the last comma and space
            if (result.length() > 0) {
                result.setLength(result.length() - 2);
            }

            // Print the final result
            System.out.println(result.toString());

        } catch (IOException | ParseException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray readJsonFile(String filePath) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();

        FileReader reader = new FileReader(filePath);
        Object obj = jsonParser.parse(reader);

        JSONObject jsonObject = (JSONObject) obj;

        // Return the JSONArray directly
        return (JSONArray) jsonObject.get("strings");
    }
}
