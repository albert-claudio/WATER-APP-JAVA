import netscape.javascript.JSObject;
import org.json.simple.JSONArray;

//BACJ
public class TempoBack {
    public static JSObject getWeatherData(String locationName){
        //pegar as coorddenadas e fazer a geolocalização
        JSONArray locationData = getLocationData(locationName);
    }

    private static JSONArray  getLocationData(String locationName){
        locationName = locationName.replaceAll("", "+");

        String UrlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";

        try{

        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
