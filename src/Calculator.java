import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Calculator {
    private final ArrayList<String> inputBuffer;
    private final DecimalFormat df;
    private JFrame frame;
    private JPanel panel;
    private JTextField textField;
    private JButton[] numButtons;
    private JButton[] funcButtons;

    public Calculator() {
        df = new DecimalFormat("#.######");
        inputBuffer = new ArrayList<>();
        createFrame();
        createPanel();
        createTextField();
        createNumButtons();
        createFuncButtons();
        createCalc();
    }

    private void createFrame(){
        frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new FlowLayout());
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void createPanel() {
        panel = new JPanel();
        panel.setPreferredSize(new Dimension(220,280));
        panel.setBackground(Color.lightGray);
    }

    private void createTextField(){
        textField = new JTextField("");
        textField.setPreferredSize(new Dimension(200,50));
        textField.setFont(new Font("Arial",Font.PLAIN,30));
        textField.setBackground(Color.black);
        textField.setForeground(Color.green);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char key = e.getKeyChar();
                int num = Character.getNumericValue(key);
                if(num >= 0 && num <= 9 || e.getKeyCode() == 8 || key == '.'){
                    textField.setEditable(true);
                } else {
                    if(key == '+' || key == '-' || key == '*' || key == '/'){
                        inputBuffer.add(textField.getText());
                        inputBuffer.add(Character.toString(key));
                        textField.setText("");
                    } else if(key == 'c' || key == 'C'){
                        inputBuffer.clear();
                        textField.setText("");
                    }
                    textField.setEditable(false);
                }
            }
        });
    }

    private void createNumButtons(){
        numButtons = new JButton[10];
        for(int i=0; i < 10; i++){
            numButtons[i] = new JButton(Integer.toString(i));
            numButtons[i].setPreferredSize(new Dimension(50,50));
            int finalI = i;
            numButtons[i].addActionListener(e -> textField.setText(textField.getText() + numButtons[finalI].getText()));
        }
    }

    private void createFuncButtons() {
        funcButtons = new JButton[6];
        char func = '\0';
        for(Functions i: Functions.values()){
            switch(i){
                case ADD: func = '+';break;
                case SUBTRACT: func = '-';break;
                case MULTIPLY: func = '*';break;
                case DIVIDE: func = '/';break;
                case EQUALS: func = '=';break;
                case CLEAR: func = 'C'; break;
            }
            funcButtons[i.ordinal()] = new JButton(String.valueOf(func));
            funcButtons[i.ordinal()].setPreferredSize(new Dimension(50,50));
            if( i == Functions.EQUALS) {
                funcButtons[i.ordinal()].addActionListener(e -> {
                    inputBuffer.add(textField.getText());
                    textField.setText(getResult());
                    inputBuffer.clear();
                });
                // Make the equals button the default button(i.e. trigger when Enter is pressed on keyboard)
                frame.getRootPane().setDefaultButton(funcButtons[i.ordinal()]);
            }else if(i == Functions.CLEAR){
                funcButtons[i.ordinal()].addActionListener(e -> {
                    inputBuffer.clear();
                    textField.setText("");
                });
            } else {
                funcButtons[i.ordinal()].addActionListener(e -> {
                    inputBuffer.add(textField.getText());
                    inputBuffer.add(funcButtons[i.ordinal()].getText());
                    textField.setText("");
                });
            }
        }
    }

    private void createCalc() {
        frame.add(panel);
        panel.add(textField);
        // Row 1
        panel.add(numButtons[1]);
        panel.add(numButtons[2]);
        panel.add(numButtons[3]);
        panel.add(funcButtons[Functions.ADD.ordinal()]);
        // Row 2
        panel.add(numButtons[4]);
        panel.add(numButtons[5]);
        panel.add(numButtons[6]);
        panel.add(funcButtons[Functions.SUBTRACT.ordinal()]);
        // Row 3
        panel.add(numButtons[7]);
        panel.add(numButtons[8]);
        panel.add(numButtons[9]);
        panel.add(funcButtons[Functions.MULTIPLY.ordinal()]);
        // Row 4
        panel.add(funcButtons[Functions.CLEAR.ordinal()]);
        panel.add(numButtons[0]);
        panel.add(funcButtons[Functions.EQUALS.ordinal()]);
        panel.add(funcButtons[Functions.DIVIDE.ordinal()]);
        frame.pack();
        textField.requestFocusInWindow();
    }

    /*
    Recursive method to handle multiplication and division.
    Looks for "*" or "/" in inputBuffer. If found: grabs values at indexes
    before and after, performs relevant operation, and updates inputBuffer with result.
     */
    private void multipleOrDivide(){
        double result = 0;
        String function;

        for(int i=0; i<inputBuffer.size();i++){
            function = inputBuffer.get(i);
            if(function.equals("*") || function.equals("/")){
                try {
                    double x = Double.parseDouble(inputBuffer.get(i-1));
                    double y = Double.parseDouble(inputBuffer.get(i+1));
                    switch(function) {
                        case "*": result = x * y; break;
                        case "/": result = x / y; break;
                    }
                    inputBuffer.remove(i+1);
                    inputBuffer.remove(i);
                    inputBuffer.remove(i-1);
                    inputBuffer.add(i-1, String.valueOf(result));
                    break;
                } catch (Exception ignored){
                }
            }
        }
        for (String i : inputBuffer) {
            if (i.equals("*") || i.equals("/")) {
                multipleOrDivide();
                break;
            }
        }
    }

    private double addOrSubtract() {
        double result = 0;
        String function = null;
        for(String i: inputBuffer){
            try{
                if(function != null){
                    switch(function){
                        case "+":  result += Double.parseDouble(i); break;
                        case "-":  result -= Double.parseDouble(i); break;
                    }
                } else {
                    result = Double.parseDouble(i);
                }
            } catch (Exception e){
                function = i;
            }
        }
        return result;
    }

    private String getResult(){
        multipleOrDivide();
        return df.format(addOrSubtract());
    }

    public static void main(String[] args) {
        new Calculator();
    }
}
