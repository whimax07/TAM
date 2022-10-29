import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class BasePanel extends JPanel {

    private final JTextField valueEntry = new JTextField();

    private final JButton applyButton = new JButton("Apply");

    private final JTextArea inputTextArea = new JTextArea();

    private final JTextArea outputTextArea = new JTextArea();



    public BasePanel() {
        formatBasePanel();
        formatValueEntry();
        formatApplyButton();
        formatInputTextArea();
        formatOutputTextArea();
        addItems();
    }

    private void formatBasePanel() {
        this.setBackground(Color.black);
    }

    private void formatValueEntry() {
        valueEntry.setBackground(Color.black);
        valueEntry.setForeground(Color.white);
        valueEntry.setCaretColor(Color.white);
        valueEntry.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
    }

    private void formatApplyButton() {
        applyButton.setBackground(Color.black);
        applyButton.setForeground(Color.white);
        applyButton.setFont(applyButton.getFont().deriveFont(Font.BOLD, 18));
        applyButton.setBorder(new LineBorder(Color.white, 1));

        applyButton.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        applyButton.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
    }

    private void formatInputTextArea() {
        inputTextArea.setForeground(Color.white);
        inputTextArea.setBackground(Color.black);
        inputTextArea.setCaretColor(Color.white);
        inputTextArea.setBorder(new LineBorder(Color.white, 1));

        inputTextArea.setFont(inputTextArea.getFont().deriveFont(Font.PLAIN, 14f));
    }

    private void formatOutputTextArea() {
        outputTextArea.setForeground(Color.white);
        outputTextArea.setBackground(Color.black);
        outputTextArea.setCaretColor(Color.white);
        outputTextArea.setBorder(new LineBorder(Color.white, 1));

        outputTextArea.setEditable(false);
        outputTextArea.setFont(outputTextArea.getFont().deriveFont(Font.PLAIN, 14f));
    }

    private void addItems() {
        setLayout(new GridBagLayout());
        add(valueEntry, makeConstraints(0, 0.1f));
        add(applyButton, makeConstraints(1, 0.1f));
        add(inputTextArea, makeConstraints(2, 0.6f));
        add(outputTextArea, makeConstraints(3, 0.6f));
    }

    private static GridBagConstraints makeConstraints(int gridy, float weighty) {
        return new GridBagConstraints(
                0,
                gridy,
                1,
                1,
                (float) 1.0,
                weighty,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0),
                0,
                0
                );
    }

}
