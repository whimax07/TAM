import javax.swing.*;

public class Main {

    public Main() {
        // Do the thing.
        //  UIManager.put("ScrollBarUI", "my.package.MyScrollBarUI");
        //  https://stackoverflow.com/a/8209911/13066845
        //  https://stackoverflow.com/a/60584865/13066845
        //  https://stackoverflow.com/a/53662678/13066845

        JFrame frame = new JFrame("Addition on Text");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.getContentPane().add(new MainPanel());

        frame.setSize(400, 400);
        frame.setVisible(true);
    }



    public static void main(String[] args) {
        //noinspection Convert2Lambda,Anonymous2MethodRef
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //noinspection InstantiationOfUtilityClass
                new Main();
            }
        });
    }

}
