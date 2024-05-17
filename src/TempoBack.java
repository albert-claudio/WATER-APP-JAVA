import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//BACJ
public class TempoBack {
    public static JSONObject getWeatherData(String locationName){
        //pegar as coorddenadas e fazer a geolocalização
        JSONArray locationData = getLocationData(locationName);

        //EXTRAIR A DATA DA LONGITUDE E LATITUDE
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //CONSTRUINDO API PARA FAZER REQUEST DO URL COM COORDENADA DA LOCALIDADE
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude +"&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FLos_Angeles";
        try{
            //CHAMAR A API E CONSEGUIR A RESPOSTA
            HttpURLConnection conn = fetchApiResponse(urlString);

            //VERIFICAR O STATUS DA RESPOSTA
            //200 - SIGINIFICA QUE FOI CONECTADO COM SUCESSO
            if (conn.getResponseCode() != 200){
                System.out.println("Error: Não foi possivel conectar a API");
                return null;
            }

            //ARMAZENAR OS DADOS DO JSON
            StringBuilder resultJson = new StringBuilder();
            Scanner scan = new Scanner(conn.getInputStream());
            while (scan.hasNext()){
                //LER E ARMAZENAR ENTRADAS DO STRING BUILDER
                resultJson.append(scan.nextLine());
            }

            //LIMPAR SCANNER
            scan.close();

            //FECHAR A CONEXÃO DA URL
            conn.disconnect();

            //ANALISE DOS DADOS
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //RECUPERAR DADOS DO HORARIO
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //QUEREMOS OBTER OS DADOS DA HORA ATUAL
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            //PEGAR TEMPERATURA
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //PEGAR O CÓDIGO METEOROLOGICO
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            //PEGAR UMIDADE
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            //PEGAR VELOCIDADE DO VENTO
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            //CONSTUIR OS DADOS OBJETO EM JSON METEOROLOGICOPARA QUE TERMOS ACESSO AO FRONTEND
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;


        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replace(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);

            // RETORNA STATUS DE CONEXÃO COM A API
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: API não conectada");
                return null;
            } else {
                StringBuilder resultJson = new StringBuilder();
                Scanner scan = new Scanner(conn.getInputStream());
                while (scan.hasNext()) {
                    resultJson.append(scan.nextLine());
                }

                scan.close();

                // FECHAR CONEXÃO COM A URL/API
                conn.disconnect();

                // PARSE JSON E INTRODUZINDO UMA STRING JSON OBJ
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());

                JSONArray locationData = (JSONArray) resultJsonObj.get("results");
                return locationData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
            return null;  // Adicionado para garantir que haja um retorno
        }
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        //percorrer A LISTA DE TEMPO E VER QUAL CORRESPONDE E HORA ATUAL
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                //RETORNA A INDEX
                return i;
            }

        }

        return 0;
    }

    public static String getCurrentTime(){
        //PEAGR A DATA E HORARIO ATUAL
        LocalDateTime currrentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH':00'");

        String formattedDateTime = currrentDateTime.format(formatter);

        return formattedDateTime;
    }

    //CONVERTER O CODIGO METEOROLOGIOC PARA ALGO MAIS LEGIVEL
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            //LIMPO
            weatherCondition = "Clear";
        }else if(weathercode <= 3L && weathercode > 0L){
            //NUBLADO
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)){
            //CHUVA
            weatherCondition = "Rain";
        }else if(weathercode >= 71L && weathercode <= 77L){
            //NEVE
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}