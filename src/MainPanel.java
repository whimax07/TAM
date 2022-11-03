import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class MainPanel extends JPanel {

    private JTextField offsetEntry;

    private JButton exchangeClipboardButton;

    private JTextArea originalTextBox;

    private JTextArea changedTextBox;

    private long offset = 0;



    public MainPanel() {
        configPanel();
        addOffsetEdit();
        addExchangeClipboardButton();
        addOrdinalTextBox();
        addChangedTextBox();
    }



    private void configPanel() {
        this.setBackground(Color.BLACK);
        this.setLayout(new GridBagLayout());
    }

    private void addOffsetEdit() {
        offsetEntry = new JTextField("Offset");
        offsetEntry.setBackground(Color.BLACK);
        offsetEntry.setForeground(Color.LIGHT_GRAY);
        offsetEntry.setCaretColor(new Color(171, 171, 255));
        offsetEntry.setFont(new Font("Courier New", Font.PLAIN, 20));

        offsetEntry.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                offsetChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                offsetChanged(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                offsetChanged(e);
            }
        });

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.weighty = 0.1;

        this.add(offsetEntry, constraints);
    }

    private void addExchangeClipboardButton() {
        exchangeClipboardButton = new JButton("Auto Exchange Clipboard");
        exchangeClipboardButton.setBackground(Color.BLACK);
        exchangeClipboardButton.setForeground(Color.lightGray);
        exchangeClipboardButton.setFocusable(false);

        final LineBorder normalBorder = new LineBorder(Color.white, 2);
        exchangeClipboardButton.setBorder(normalBorder);

        final LineBorder hoverBoarder = new LineBorder(new Color(124, 197, 246), 2);

        ButtonModel model = exchangeClipboardButton.getModel();
        model.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (model.isRollover()) {
                    exchangeClipboardButton.setBorder(hoverBoarder);
                } else {
                    exchangeClipboardButton.setBorder(normalBorder);
                }
            }
        });



        exchangeClipboardButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard systemClipboard = Toolkit
                        .getDefaultToolkit()
                        .getSystemClipboard();

                String clipboardText;
                try {
                    clipboardText = (String) systemClipboard.getData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException | IOException ex) {
                    changedTextBox.setText("{USER_CLIPBOARD_BAD}\n" + ex);
                    return;
                }

                originalTextBox.setText(clipboardText);
                StringSelection newText = new StringSelection(changedTextBox.getText());
                systemClipboard.setContents(newText, newText);
            }
        });

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 0.1;
        constraints.gridy = 1;
        constraints.insets = new Insets(0, 0, 10, 0);

        this.add(exchangeClipboardButton, constraints);
    }

    private void addOrdinalTextBox() {
        originalTextBox = new JTextArea();
        originalTextBox.setBackground(Color.BLACK);
        originalTextBox.setForeground(Color.LIGHT_GRAY);
        originalTextBox.setBorder(new LineBorder(Color.WHITE, 2 ));
        originalTextBox.setCaretColor(new Color(171, 171, 255));
        originalTextBox.setFont(new Font("Courier New", Font.PLAIN, 16));

        originalTextBox.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                originalTextChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                originalTextChanged(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                originalTextChanged(e);
            }
        });

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        constraints.gridy = 2;

        this.add(originalTextBox, constraints);
    }

    private void addChangedTextBox() {
        changedTextBox = new JTextArea();
        changedTextBox.setBackground(Color.BLACK);
        changedTextBox.setForeground(Color.LIGHT_GRAY);
        changedTextBox.setBorder(new LineBorder(Color.WHITE, 2 ));
        changedTextBox.setCaretColor(new Color(146, 255, 92));
        changedTextBox.setFont(new Font("Courier New", Font.PLAIN, 16));
        changedTextBox.setEditable(false);

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        constraints.gridy = 3;

        this.add(changedTextBox, constraints);
    }



    private void offsetChanged(DocumentEvent event) {
        Document document = event.getDocument();
        String text = "";

        try {
            text = document.getText(0, document.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        while (text.startsWith("0x") || text.startsWith("0X")) {
            text = text.substring(2);
        }

        long number;
        try {
            number = Long.parseLong(text, 16);
        } catch (NumberFormatException e) {
            return;
        }

        offset = number;
        changeText(originalTextBox.getText());
    }

    private void originalTextChanged(DocumentEvent event) {
        Document document = event.getDocument();
        String text = "";

        try {
            text = document.getText(0, document.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        changeText(text);
    }

    private void changeText(String text) {
        String[] words = text.split(" ");

        for (int i = 0; i < words.length; i++) {
            long number;

            String trimmedWord = words[i];

            if (trimmedWord.equals("a")) {
                continue;
            }

            while (trimmedWord.startsWith("0x") || trimmedWord.startsWith("0X")) {
                trimmedWord = trimmedWord.substring(2);
            }

            try {
                number = Long.parseLong(trimmedWord, 16);
            } catch (NumberFormatException e) {
                continue;
            }

            number += offset;
            String changed = "0x" + Long.toHexString(number);

            String original = words[i];

            if (changed.length() < original.length()) {
                StringBuilder stringBuilder = new StringBuilder(changed);
                do {
                    stringBuilder.insert(2, "0");
                } while (stringBuilder.length() < original.length());

                changed = stringBuilder.toString();
                System.out.println("In the format kepper: " + changed);
            }

            words[i] = changed;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String word : words) {
            stringBuilder.append(word).append(" ");
        }
        changedTextBox.setText(stringBuilder.toString().trim());
    }

}
