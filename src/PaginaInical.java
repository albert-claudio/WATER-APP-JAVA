import javax.swing.*;


public class PaginaInical {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TempoGUI().setVisible(true);



                System.out.println(TempoBack.getCurrentTime());
            }
        });
    }
}
