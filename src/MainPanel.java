import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class MainPanel extends JPanel {

    @SuppressWarnings("FieldCanBeLocal")
    private JTextField offsetEntry;

    private JButton exchangeClipboardButton;

    private JScrollPane originalTextWrapper;

    private JTextArea originalTextBox;

    private JScrollPane changedTextWrapper;

    private JTextArea changedTextBox;

    private long offset = 0;

    private final E_Literal noPrefixLiteral = E_Literal.HEX;

    private final ArrayList<String> notNumbers = new ArrayList<>();



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

        //noinspection Convert2Lambda
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
        originalTextBox.setBorder(null);
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

        originalTextWrapper = new JScrollPane(originalTextBox);
        originalTextWrapper.setBorder(new LineBorder(Color.WHITE, 2));
        originalTextWrapper.getHorizontalScrollBar().setBackground(Color.darkGray);
        var uis = originalTextWrapper.getHorizontalScrollBar().getUI();

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        constraints.gridy = 2;

        this.add(originalTextWrapper, constraints);
    }

    private void addChangedTextBox() {
        changedTextBox = new JTextArea();
        changedTextBox.setBackground(Color.BLACK);
        changedTextBox.setForeground(Color.LIGHT_GRAY);
        changedTextBox.setBorder(null);
        changedTextBox.setCaretColor(new Color(146, 255, 92));
        changedTextBox.setFont(new Font("Courier New", Font.PLAIN, 16));
        changedTextBox.setEditable(false);

        changedTextWrapper = new JScrollPane(changedTextBox);
        changedTextWrapper.setBorder(new LineBorder(Color.WHITE, 2));

        var constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        constraints.gridy = 3;

        this.add(changedTextWrapper, constraints);
    }



    private void offsetChanged(DocumentEvent event) {
        Document document = event.getDocument();
        String text = "";

        try {
            text = document.getText(0, document.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        var opNumber = getNumberFromWord(text);
        if (opNumber.isEmpty()) return;

        offset = opNumber.get();
        changeText(originalTextBox.getText(), " ");
    }

    private void originalTextChanged(DocumentEvent event) {
        Document document = event.getDocument();
        String text = "";

        try {
            text = document.getText(0, document.getLength());
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        notNumbers.clear();

        var newText = changeText(text, " ");
        changedTextBox.setText(newText);

        System.out.println("Words that failed the number parse: " + notNumbers);
    }

    private String changeText(String text, String splitToken) {
        final Pattern hexLetterWord = Pattern.compile("^[aAbBcCdDeEfF]+$");
        String[] words = text.split(splitToken);

        for (int i = 0; i < words.length; i++) {
            String trimmedWord = words[i].strip();

            if (trimmedWord.contains("\n")) {
                words[i] = changeText(trimmedWord, "\n");
                continue;
            }

            if (trimmedWord.isBlank()) {
                continue;
            }

            if (hexLetterWord.matcher(trimmedWord).matches()) {
                notNumbers.add(trimmedWord);
                continue;
            }

            var opNumber = getNumberFromWord(trimmedWord);
            if (opNumber.isEmpty()) continue;

            long number = opNumber.get() + offset;
            words[i] = rewriteWord(words[i], number);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String word : words) {
            stringBuilder.append(word).append(splitToken);
        }
        return stringBuilder.toString().trim();
    }

    private String rewriteWord(String original, long number) {
        E_Literal literal;

        if (original.startsWith("0x") || original.startsWith("0X")) {
            literal = E_Literal.HEX;
        } else if (original.startsWith("0b") || original.startsWith("0B")) {
            literal = E_Literal.BIN;
        } else if (original.startsWith("0d") || original.startsWith("0D")) {
            literal = E_Literal.DEC;
        } else {
            literal = noPrefixLiteral;
        }

        String changed = switch (literal) {
            case HEX -> "0x" + Long.toHexString(number);
            case BIN -> "0b" + Long.toBinaryString(number);
            case DEC -> Long.toString(number);
        };

        if (literal != E_Literal.DEC && changed.length() < original.length()) {
            StringBuilder stringBuilder = new StringBuilder(changed);
            do {
                stringBuilder.insert(2, "0");
            } while (stringBuilder.length() < original.length());

            changed = stringBuilder.toString();
            System.out.println("In the word rewriter: " + changed);
        }

        return changed;
    }

    private Optional<Long> getNumberFromWord(String trimmedWord) {
        MakeNumber makeNumber = (hasPrefix, base, input) -> {
            while (hasPrefix.apply(input)) {
                input = input.substring(2);
            }

            try {
                return Optional.of(Long.parseLong(input, base));
            } catch (NumberFormatException e) {
                notNumbers.add(input);
                return Optional.empty();
            }
        };

        Function<String, Boolean> checkHex = in -> in.startsWith("0x") || in.startsWith("0X");
        Function<String, Boolean> checkBin = in -> in.startsWith("0b") || in.startsWith("0B");
        Function<String, Boolean> checkDec = in -> in.startsWith("0d") || in.startsWith("0D");

        // If `trimmedWord` has a literal prefix parse it as such.
        if (checkHex.apply(trimmedWord)) {
            return makeNumber.apply(checkHex, 16, trimmedWord);
        }

        if (checkBin.apply(trimmedWord)) {
            return makeNumber.apply(checkBin, 2, trimmedWord);
        }

        if (checkDec.apply(trimmedWord)) {
            return makeNumber.apply(checkDec, 10, trimmedWord);
        }

        // `trimmedWord` does not have a prefix so use the default.
        return switch (noPrefixLiteral) {
            case HEX -> makeNumber.apply(checkHex, 16, trimmedWord);
            case BIN -> makeNumber.apply(checkBin, 2, trimmedWord);
            case DEC -> makeNumber.apply(checkDec, 10, trimmedWord);
        };
    }



    private enum E_Literal {
        HEX,
        DEC,
        BIN
    }

    private interface MakeNumber {
        Optional<Long> apply(Function<String, Boolean> checkPrefix, int base, String input);
    }



    private static class ModernScrollBarUI extends BasicScrollBarUI {
        private final ScrollPane scrollPane;

        public ModernScrollBarUI(ScrollPane scrollPane) {
            this.scrollPane = scrollPane;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return new InvisibleScrollBarButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return new InvisibleScrollBarButton();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            int alpha = isThumbRollover() ? SCROLL_BAR_ALPHA_ROLLOVER : SCROLL_BAR_ALPHA;
            int orientation = scrollbar.getOrientation();
            int x = thumbBounds.x;
            int y = thumbBounds.y;

            int width = orientation == JScrollBar.VERTICAL ? THUMB_SIZE : thumbBounds.width;
            width = Math.max(width, THUMB_SIZE);

            int height = orientation == JScrollBar.VERTICAL ? thumbBounds.height : THUMB_SIZE;
            height = Math.max(height, THUMB_SIZE);

            Graphics2D graphics2D = (Graphics2D) g.create();
            graphics2D.setColor(new Color(THUMB_COLOR.getRed(), THUMB_COLOR.getGreen(), THUMB_COLOR.getBlue(), alpha));
            graphics2D.fillRect(x, y, width, height);
            graphics2D.dispose();
        }

        @Override
        protected void setThumbBounds(int x, int y, int width, int height) {
            super.setThumbBounds(x, y, width, height);
            scrollPane.repaint();
        }

        /**
         * Invisible Buttons, to hide scroll bar buttons.
         */
        private static class InvisibleScrollBarButton extends JButton {

            private static final long serialVersionUID = 1552427919226628689L;

            private InvisibleScrollBarButton() {
                setOpaque(false);
                setFocusable(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setBorder(BorderFactory.createEmptyBorder());
            }
        }
    }

}
