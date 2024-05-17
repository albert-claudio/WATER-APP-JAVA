import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.util.Objects;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class TempoGUI extends JFrame {
    private JSONObject weatherData;

    public static ImageIcon logo = new ImageIcon(Objects.requireNonNull(TempoGUI.class.getClassLoader().getResource("Assets/Sun.png")));

    public TempoGUI(){


        super("App Climatico");

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(logo.getImage());

        // configurar o gui para encerrar o processo do programa assim que ele for fechado
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        setSize(450, 650);

    //FICAR NO CENTRO
        setLocationRelativeTo(null);


        setLayout(null);

        // evitar qualquer redimensionamento na interface gráfica
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents(){
        // CAMPO DE PESQUISA
        JTextField searchTextField = new JTextField();

        // definir a localização e o tamanho do nosso componente
        searchTextField.setBounds(15, 15, 351, 45);

        //DEFINIR A LOCALIZAÇÃO E O TAMHO DO COMPONENTE
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        // IMAGEM DO TEMPO
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // TEXTO DA TEMPERATURA
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // CENTRALIZAR O TEXTO
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // DESCRIÇÃO DA CONDIÇÃO CLIMATICA
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // IMAGEM DA UMIDADE
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // TEXO DA UMIDADE
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // IMAGEM DA VELOCIDADE DO VENTO
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        // TEXTO DA VELOCIDADE DO VENTO
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        // BOTÃO DE PESQUISA
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        // MUDAR O CURSO QUANDO PASSAR PELO BOTÃO
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // OBTER LOCALIZAÇÃO DO USUARIO
                String userInput = searchTextField.getText();

                // VALIDAR INPUT - REMOVER ESPAÇOS EM BRANCO PARA GARANTIR QUE NÃO ESTJA VAZIO
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                // RECUPERAR DADOS METEOROLÓGICOS
                weatherData = TempoBack.getWeatherData(userInput);

                // ATUALIZAÇÃO DA GUI

                // ATULAIZAÇÃO DA CONDIÇAO
                String weatherCondition = (String) weatherData.get("weather_condition");

                // dependendo da condição, atualizaremos a imagem meteorológica que corresponde à condição
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.pngImage"));
                        break;
                }

                // atualizar a temperatura
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                // atualizar a condição do clima
                weatherConditionDesc.setText(weatherCondition);

                // atualizar texto de umidade
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Umidade</b> " + humidity + "%</html>");

                // atualizar texto de velocidade do vento
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Vento</b> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);
    }

    // usado para criar imagens em nossos componentes gui
    private ImageIcon loadImage(String resourcePath){
        try{
            // read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            //retorna um ícone de imagem para que nosso componente possa renderizá-lo
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}








