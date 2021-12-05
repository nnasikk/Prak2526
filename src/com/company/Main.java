package prak_25_26;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws Exception
    {
        int col_lines = 17;
        String adress = "https://www.moscowmap.ru/metro.html#lines";
        Document doc = Jsoup.connect(adress).maxBodySize(0).get();
        Elements name_of_station = doc.getElementsByClass("js-metro-stations t-metrostation-list-table");
        JsonObject jsonObject = new JsonObject();
        JsonArray json_first_array = new JsonArray();
        JsonArray json_second_array = new JsonArray();

        for(int i = 0; i < col_lines; i++)
        {
            String str = Integer.toString(i+1);
            if(i+1 == 12)
            {
                str = "11A";
            }
            if(i+1 == 13)
            {
                str = "12";
            }
            if(i+1 == 16)
            {
                str = "D1";
            }

            if(i+1 == 17)
            {
                str = "D2";
            }
            Elements name_of_line = doc.getElementsByClass("js-metro-line t-metrostation-list-header t-icon-metroln ln-"+str);
            Elements names = name_of_station.get(i).getElementsByClass("name");
            String[] names_stat = new String[names.size()];
            JsonArray array = new JsonArray();
            JsonObject object = new JsonObject();
            JsonObject object1 = new JsonObject();

            for(int j = 0; j < names.size(); j++)
            {
                String[] n = names.get(j).toString().split(">");
                names_stat[j] = n[n.length-1].replace("</span", "");
                array.add(names_stat[j]);
            }
            String[] lines = name_of_line.toString().split(">");
            String line_final = lines[lines.length-1].replace("</span", "");
            object.add(str, array);
            json_first_array.add(object);
            object1.addProperty("number", str);
            object1.addProperty("name", line_final);
            json_second_array.add(object1);

        }

        jsonObject.add("stations", json_first_array);
        jsonObject.add("lines", json_second_array);

        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("/home/pasta/java_projects/first.json")) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser parser = new JsonParser();
        try{
            Object obj = parser.parse(new FileReader("/home/pasta/java_projects/first.json"));
            JsonObject object = (JsonObject) obj;

            JsonArray array1 = object.getAsJsonArray("lines");
            JsonArray array2 = object.getAsJsonArray("stations");
            String[] lines = new String[array1.size()];
            String[] numbers = new String[array1.size()];
            int[] stations = new int[array1.size()];
            for (int i = 0; i < array1.size(); i++)
            {
                JsonObject line = (JsonObject) array1.get(i);
                JsonObject station = (JsonObject) array2.get(i);
                lines[i] = line.get("name").toString();
                numbers[i] = line.get("number").toString();
                String str = numbers[i].replace("\"", "");
                JsonArray arr = station.getAsJsonArray(str);
                stations[i] = arr.size();
                System.out.println(lines[i] + " " + stations[i]);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
