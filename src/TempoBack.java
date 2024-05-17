import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class TempoBack {
    // fetch weather data for given location
    public static JSONObject getWeatherData(String locationName){
        // obter coordenadas de localização usando a API de geolocalização
        JSONArray locationData = getLocationData(locationName);

        // extrair dados de latitude e longitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // construir URL de solicitação de API com coordenadas de localização
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FLos_Angeles";

        try{
            // ligue para a API e obtenha resposta
            HttpURLConnection conn = fetchApiResponse(urlString);


            // 200 -SIGNIFICA QUE FOI CONECTADO COM SUCESSO
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // armazenar dados JSON resultantes
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                // read and store into the string builder
                resultJson.append(scanner.nextLine());
            }


            scanner.close();

            // FECHA CONEXÃO COM A URL
            conn.disconnect();

            // ANALISA OS DADOS
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // RECUPERAR DADOS POR HORA
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            // OBTER DADOS ATUAIS
            // OBTER O INDICE ATUAL
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // PEGRA TEMPERATURE
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // PEGAR O CODIGO CLIMATICO
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // PEGAR A UMIIDADE
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // PEGAR A VELOCIDADE DO VENTO
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            // CONTRUIR O OBJETO EM DADOS QUE FAZ ACESSO NO PARTE DO FRONTEND
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //recupera coordenadas geográficas para determinado nome de local
    public static JSONArray getLocationData(String locationName){
        // replace any whitespace in location name to + to adhere to API's request format
        locationName = locationName.replaceAll(" ", "+");

        // CONTRUIR URL DA API COM PARAMETROS DE LOCALIZÇÃO
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try{
            // CHAMAR A API E OBTER A REPOSTA
            HttpURLConnection conn = fetchApiResponse(urlString);

            // CEHCAR A RESPOTA DO STATUS
            // 200 SIGNIFICA QUE FOI CONECTADO COM SUCESSO
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                // ARMAZENAR OS DADOS DA API
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                // LER E ARMAZENAR OS DADOS JSON RESULTANTES D CONTRUTOR DA STRING
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }


                scanner.close();

                // FECHAR CONEXÃO COM URL
                conn.disconnect();

                // ANALISAR A STRING JSON EM UM OBJETO JSON
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // OBTER A LISTA DE DADOS DE LOCALIZAÇÃO QUE API A PARTIR DO NOME DA LOCALIZAÇÃO
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        // NÃO FOI POSSIVEL ACHAR A LOCALIZAÇÃO
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            // TENTAR CRIAR A CONEXÃO
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // DEFINIR METODO CONSEGUIR OBTE-LO
            conn.setRequestMethod("GET");

            // CONEXÃO COM A API
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }

        // NÃO FOI POSSIVEL FAZER A CONEXÃO
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        // PERCORRER A LISTA DE HORARIOS E VER QUAL DELES CORREPONDE AO NOSSO HORARIO ATUAL
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                // RETORNO DA INDEX
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        // OBTER DOADOS O HORA ATUAIS
        LocalDateTime currentDateTime = LocalDateTime.now();

        // FORMATO DE DATA PARA 2023-09-02T00:00(POIS ASSIM É LIDO NA API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // FORMATA E MOSTRA HORA E DATA ATUAIS
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    //CONVERTER O CODIGO METEOROLOGIOC PARA ALGO LEGIVEL
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            // LIMPO
            weatherCondition = "Clear";
        }else if(weathercode > 0L && weathercode <= 3L){
            // NUBALDO
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <= 67L)
                || (weathercode >= 80L && weathercode <= 99L)){
            // CHUVA
            weatherCondition = "Rain";
        }else if(weathercode >= 71L && weathercode <= 77L){
            // NEVE
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}






