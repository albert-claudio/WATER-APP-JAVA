import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

//BACJ
public class TempoBack {
    public static JSONObject getWeatherData(String locationName){
        //pegar as coorddenadas e fazer a geolocalização
        JSONArray locationData = getLocationData(locationName);

        return null;
    }

    public static JSONArray  getLocationData(String locationName){
        locationName = locationName.replaceAll("", "+");

        String UrlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";

        try{
            HttpURLConnection conn = fetchApiResponse(UrlString);

            //RETORNA STATUS DE CONEXÃO COM A API
            if(conn.getResponseCode() != 200){
                System.out.println("Error: API não conectada");
                return null;
            }else{
                StringBuilder resultJson = new StringBuilder();
                Scanner scan = new Scanner(conn.getInputStream());
                while(scan.hasNext()){
                    resultJson.append(scan.nextLine());
                }

                scan.close();

                //FECHAR CONEXÃO COM A URL/API
                conn.disconnect();

                //PARSE JSON E INTRODUZINDO UMA STRING JSON OBJ
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                JSONArray locationData =(JSONArray) resultJsonObj.get("results");
                return locationData;
            }
        }catch(Exception e){
            e.printStackTrace();
        }


    private static HttpURLConnection fetchApiResponse(String urlString){
            
        }
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();
            return conn;
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

}
