import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (
//                ClassNotFoundException
//                | InstantiationException
//                | IllegalAccessException
//                | UnsupportedLookAndFeelException e
//        ) {
//            throw new RuntimeException(e);
//        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("gui");

                frame.setContentPane(new BasePanel());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(new Dimension(400, 400));
                frame.setVisible(true);
            }
        });
    }
}