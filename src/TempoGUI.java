import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class TempoGUI extends JFrame{
    private JSONObject weatherData;

    public TempoGUI(){
        //
        super("Previsão Tempo");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(450,650);

        setLocationRelativeTo(null);

        setLayout(null);

        setResizable(false);

       addGuiComponents();
    }

     private void addGuiComponents(){
        JTextField searchTextField = new JTextField();

        searchTextField.setBounds(15, 15,351, 45);

        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);



        JLabel weatherConditionImage = new JLabel(loadImage("src/Assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //CENTRALIZAÇÃO DO TEXTO
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //CONDIÇÃO DO TEMPO
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        JLabel humidityImage = new JLabel(loadImage("src/Assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%<html/>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        JLabel windspeedImage = new JLabel(loadImage("src/Assets/windspeed.png"));
        windspeedImage.setBounds(220,500,74,66);
        add(windspeedImage);

        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km</html>");
        windspeedText.setBounds(310,500,85,55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

         JButton searchButton = new JButton(loadImage("src/Assets/search.png"));

         searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         searchButton.setBounds(375, 13,47, 45);
         searchButton.addActionListener(new ActionListener(){
             @Override
             public void actionPerformed(ActionEvent e ){
                 //PEGAR LOCALIZAÇÃO DO USUARIO
                 String userInput = searchTextField.getText();

                 if(userInput.replaceAll("\\s", "").length() <= 0){
                     return;
                 }
                //recuperar dados meteorológicos
                 weatherData = TempoBack.getWeatherData(userInput);

                 //UPDATE GUI
                 //UPDATE DA IMAGEM
                 String weatherCondition = (String) weatherData.get("weather_condition");

                 switch(weatherCondition) {
                     case "Clear":
                         weatherConditionImage.setIcon(loadImage("src/Assets/clear.png"));
                         break;
                     case "Cloudy":
                         weatherConditionImage.setIcon(loadImage("src/Assets/cloudy.png"));
                         break;
                     case "Rain":
                         weatherConditionImage.setIcon(loadImage("src/Assets/rain.png"));
                         break;
                     case "Snow":
                         weatherConditionImage.setIcon(loadImage("src/Assets/snow.png"));
                         break;
                 }

                     double temperature = (double) weatherData.get("temperature");
                     temperatureText.setText(temperature + "C");


                     weatherConditionDesc.setText(weatherCondition);

                     long humidity = (long) weatherData.get("");

             }
         });
         add(searchButton);
    }

    private ImageIcon loadImage(String resourcePath){
        try {

            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Could not find resource");
        return null;
    }
};