import javax.swing.*;

public class Main {

    public Main() {
        JFrame frame = new JFrame("Maths on Text");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.getContentPane().add(new MainPanel());

        frame.setSize(400, 400);
        frame.setVisible(true);
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }

}
