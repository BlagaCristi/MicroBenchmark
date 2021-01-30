import javax.swing.*;

public class MessageFrame extends JFrame {
    private String msg;
    private JTextField jTextField;
    private static final int HEIGHT = 100;
    private static final int WIDTH = 300;

    public MessageFrame(String msg) {
        this.msg = msg;
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(WIDTH, HEIGHT);
        jTextField = new JTextField(msg);
        jTextField.setEditable(false);
        this.add(jTextField);

        this.setResizable(false);
    }
}