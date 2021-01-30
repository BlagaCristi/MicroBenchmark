import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

public class UserInterface extends JFrame {

    private static final String JAVA_BENCHMARK = "/home/cristi/Desktop/scs_project/java_benchmark";
    private static final String C_BENCHMARK = "/home/cristi/Desktop/scs_project/c_benchmark";
    private static final String C_SHARP_BENCHMARK = "/home/cristi/Desktop/scs_project/c#_benchmark";
    private static final String PYTHON_PLOT = "/home/cristi/Desktop/scs_project/python_plot";
    private static final int HEIGHT = 500;
    private static final int WIDTH = 600;

    private JPanel mainPanel;
    private JPanel javaBenchmarkPanel;
    private JPanel cBenchmarkPanel;
    private JPanel cSharpBenchmarkPanel;
    private JPanel plotPanel;
    private JTabbedPane jTabbedPane;

    private JLabel javaMemoryAllocationTriesLabel;
    private JLabel javaMemoryAllocationSizeLabel;
    private JLabel javaMemoryAccessTriesLabel;
    private JLabel javaThreadCreationTriesLabel;
    private JLabel javaThreadSwitchContextTriesLabel;
    private JLabel javaThreadMigrationTriesLabel;
    private JLabel cMemoryAllocationTriesLabel;
    private JLabel cMemoryAllocationSizeLabel;
    private JLabel cMemoryAccessTriesLabel;
    private JLabel cThreadCreationTriesLabel;
    private JLabel cThreadSwitchContextTriesLabel;
    private JLabel cThreadMigrationTriesLabel;
    private JLabel cSharpMemoryAllocationTriesLabel;
    private JLabel cSharpMemoryAllocationSizeLabel;
    private JLabel cSharpMemoryAccessTriesLabel;
    private JLabel cSharpThreadCreationTriesLabel;
    private JLabel cSharpThreadSwitchContextTriesLabel;
    private JLabel cSharpThreadMigrationTriesLabel;

    private JTextField javaMemoryAllocationTriesField;
    private JTextField javaMemoryAllocationSizeField;
    private JTextField javaMemoryAccessTriesField;
    private JTextField javaThreadCreationTriesField;
    private JTextField javaThreadSwitchContextTriesField;
    private JTextField javaThreadMigrationTriesField;
    private JTextField cMemoryAllocationTriesField;
    private JTextField cMemoryAllocationSizeField;
    private JTextField cMemoryAccessTriesField;
    private JTextField cThreadCreationTriesField;
    private JTextField cThreadSwitchContextTriesField;
    private JTextField cThreadMigrationTriesField;
    private JTextField cSharpMemoryAllocationTriesField;
    private JTextField cSharpMemoryAllocationSizeField;
    private JTextField cSharpMemoryAccessTriesField;
    private JTextField cSharpThreadCreationTriesField;
    private JTextField cSharpThreadSwitchContextTriesField;
    private JTextField cSharpThreadMigrationTriesField;

    private JButton javaBenchmarkStartButton;
    private JButton cBenchmarkStartButton;
    private JButton cSharpBenchmarkStartButton;
    private JButton plotGenerateButton;

    private JRadioButton javaPlotRadioButton;
    private JRadioButton cPlotRadioButton;
    private JRadioButton cSharpPlotRadioButton;
    private JRadioButton allPlotRadioButton;
    private JRadioButton memoryAllocationPlotRadioButton;
    private JRadioButton memoryDeallocationPlotRadioButton;
    private JRadioButton memoryAccessPlotRadioButton;
    private JRadioButton threadCreationPlotRadioButton;
    private JRadioButton threadSwitchContextPlotRadioButton;
    private JRadioButton threadMigrationPlotRadioButton;

    private static volatile String programCommand;
    private static volatile String language;
    private static volatile Boolean isEvent = false;

    public UserInterface() {

        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

        mainPanel = new JPanel();
        javaBenchmarkPanel = new JPanel();
        cBenchmarkPanel = new JPanel();
        cSharpBenchmarkPanel = new JPanel();
        plotPanel = new JPanel();
        jTabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        jTabbedPane.addTab("Java Benchmark", null, javaBenchmarkPanel, null);
        jTabbedPane.addTab("C Benchmark", null, cBenchmarkPanel, null);
        jTabbedPane.addTab("C# Benchmark", null, cSharpBenchmarkPanel, null);
        jTabbedPane.addTab("Plots", null, plotPanel, null);

        jTabbedPane.setVisible(true);

        mainPanel.add(jTabbedPane);
        mainPanel.setVisible(true);
        javaBenchmarkPanel.setVisible(true);
        cBenchmarkPanel.setVisible(true);
        cSharpBenchmarkPanel.setVisible(true);
        plotPanel.setVisible(true);

        this.add(mainPanel);

        mainPanel.setPreferredSize(new Dimension(400, 400));
        javaBenchmarkPanel.setPreferredSize(new Dimension(400, 400));
        cBenchmarkPanel.setPreferredSize(new Dimension(400, 400));
        cSharpBenchmarkPanel.setPreferredSize(new Dimension(400, 400));
        plotPanel.setPreferredSize(new Dimension(400, 400));
        this.setLocation(0, 0);

        initializeJavaBenchmarkPanel();
        initializeCBenchmarkPanel();
        initializeCSharpBenchmarkPanel();
        initializePlot();

        initializeJavaBenchmark();
        initializeCBenchmark();
        initializeCSharpBenchmark();
        initializePlotButton();
    }

    private void initializePlot() {
        plotPanel.setLayout(null);

        javaPlotRadioButton = new JRadioButton("Java");
        javaPlotRadioButton.setVisible(true);
        javaPlotRadioButton.setBounds(10, 10, 200, 40);
        plotPanel.add(javaPlotRadioButton);

        cPlotRadioButton = new JRadioButton("C");
        cPlotRadioButton.setVisible(true);
        cPlotRadioButton.setBounds(250, 10, 200, 40);
        plotPanel.add(cPlotRadioButton);

        cSharpPlotRadioButton = new JRadioButton("C#");
        cSharpPlotRadioButton.setVisible(true);
        cSharpPlotRadioButton.setBounds(10, 80, 200, 40);
        plotPanel.add(cSharpPlotRadioButton);

        allPlotRadioButton = new JRadioButton("All", true);
        allPlotRadioButton.setVisible(true);
        allPlotRadioButton.setBounds(250, 80, 200, 40);
        plotPanel.add(allPlotRadioButton);

        memoryAccessPlotRadioButton = new JRadioButton("Memory Access");
        memoryAccessPlotRadioButton.setVisible(true);
        memoryAccessPlotRadioButton.setBounds(10, 150, 200, 40);
        plotPanel.add(memoryAccessPlotRadioButton);

        memoryAllocationPlotRadioButton = new JRadioButton("Memory Allocation");
        memoryAllocationPlotRadioButton.setVisible(true);
        memoryAllocationPlotRadioButton.setBounds(250, 150, 200, 40);
        plotPanel.add(memoryAllocationPlotRadioButton);

        memoryDeallocationPlotRadioButton = new JRadioButton("Memory Deallocation");
        memoryDeallocationPlotRadioButton.setVisible(true);
        memoryDeallocationPlotRadioButton.setBounds(10, 220, 200, 40);
        plotPanel.add(memoryDeallocationPlotRadioButton);

        threadCreationPlotRadioButton = new JRadioButton("Thread Creation");
        threadCreationPlotRadioButton.setVisible(true);
        threadCreationPlotRadioButton.setBounds(250, 220, 200, 40);
        plotPanel.add(threadCreationPlotRadioButton);

        threadSwitchContextPlotRadioButton = new JRadioButton("Thread Switch Context");
        threadSwitchContextPlotRadioButton.setVisible(true);
        threadSwitchContextPlotRadioButton.setBounds(10, 290, 200, 40);
        plotPanel.add(threadSwitchContextPlotRadioButton);

        threadMigrationPlotRadioButton = new JRadioButton("Thread Migration");
        threadMigrationPlotRadioButton.setVisible(true);
        threadMigrationPlotRadioButton.setBounds(250, 290, 200, 40);
        plotPanel.add(threadMigrationPlotRadioButton);

        plotGenerateButton = new JButton("Generate");
        plotGenerateButton.setVisible(true);
        plotGenerateButton.setBounds(135, 350, 130, 30);
        plotPanel.add(plotGenerateButton);

        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(javaPlotRadioButton);
        bgroup.add(cPlotRadioButton);
        bgroup.add(cSharpPlotRadioButton);
        bgroup.add(allPlotRadioButton);
        bgroup.add(memoryAccessPlotRadioButton);
        bgroup.add(memoryAllocationPlotRadioButton);
        bgroup.add(memoryDeallocationPlotRadioButton);
        bgroup.add(threadCreationPlotRadioButton);
        bgroup.add(threadSwitchContextPlotRadioButton);
        bgroup.add(threadMigrationPlotRadioButton);

        repaint();
    }

    private void initializePlotButton() {
        plotGenerateButton.addActionListener(e -> {
            String arguments = "";
            if (javaPlotRadioButton.isSelected()) {
                arguments = "java";
            }
            if (cPlotRadioButton.isSelected()) {
                arguments = "c";
            }
            if (cSharpPlotRadioButton.isSelected()) {
                arguments = "c#";
            }
            if (allPlotRadioButton.isSelected()) {
                arguments = "all";
            }
            if (memoryAccessPlotRadioButton.isSelected()) {
                arguments = "mem_access MemoryAccessTime";
            }
            if (memoryAllocationPlotRadioButton.isSelected()) {
                arguments = "mem_allocation MemoryAllocationTime";
            }
            if (memoryDeallocationPlotRadioButton.isSelected()) {
                arguments = "mem_deallocation MemoryDeallocationTime";
            }
            if (threadCreationPlotRadioButton.isSelected()) {
                arguments = "thread_creation ThreadCreationTime";
            }
            if (threadSwitchContextPlotRadioButton.isSelected()) {
                arguments = "thread_switch_context ThreadSwitchContextTime";
            }
            if (threadMigrationPlotRadioButton.isSelected()) {
                arguments = "thread_migration ThreadMigrationTime";
            }
            String command = "./python_plot.py " + arguments;
            System.out.println(command);
            programCommand = command;
            language = "plot";
            isEvent = true;
        });
    }

    private void initializeJavaBenchmarkPanel() {
        javaBenchmarkPanel.setLayout(null);

        javaMemoryAllocationTriesLabel = new JLabel("Memory allocation tries:");
        javaMemoryAllocationTriesLabel.setVisible(true);
        javaMemoryAllocationTriesLabel.setBounds(10, 10, 200, 40);
        javaBenchmarkPanel.add(javaMemoryAllocationTriesLabel);

        javaMemoryAllocationTriesField = new JTextField("");
        javaMemoryAllocationTriesField.setVisible(true);
        javaMemoryAllocationTriesField.setBounds(220, 10, 150, 30);
        javaBenchmarkPanel.add(javaMemoryAllocationTriesField);

        javaMemoryAllocationSizeLabel = new JLabel("Memory allocation size: ");
        javaMemoryAllocationSizeLabel.setVisible(true);
        javaMemoryAllocationSizeLabel.setBounds(10, 60, 200, 40);
        javaBenchmarkPanel.add(javaMemoryAllocationSizeLabel);

        javaMemoryAllocationSizeField = new JTextField("");
        javaMemoryAllocationSizeField.setVisible(true);
        javaMemoryAllocationSizeField.setBounds(220, 60, 150, 30);
        javaBenchmarkPanel.add(javaMemoryAllocationSizeField);

        javaMemoryAccessTriesLabel = new JLabel("Memory access tries: ");
        javaMemoryAccessTriesLabel.setVisible(true);
        javaMemoryAccessTriesLabel.setBounds(10, 110, 200, 40);
        javaBenchmarkPanel.add(javaMemoryAccessTriesLabel);

        javaMemoryAccessTriesField = new JTextField("");
        javaMemoryAccessTriesField.setVisible(true);
        javaMemoryAccessTriesField.setBounds(220, 110, 150, 30);
        javaBenchmarkPanel.add(javaMemoryAccessTriesField);

        javaThreadCreationTriesLabel = new JLabel("Thread creation tries: ");
        javaThreadCreationTriesLabel.setVisible(true);
        javaThreadCreationTriesLabel.setBounds(10, 160, 200, 40);
        javaBenchmarkPanel.add(javaThreadCreationTriesLabel);

        javaThreadCreationTriesField = new JTextField("");
        javaThreadCreationTriesField.setVisible(true);
        javaThreadCreationTriesField.setBounds(220, 160, 150, 30);
        javaBenchmarkPanel.add(javaThreadCreationTriesField);

        javaThreadSwitchContextTriesLabel = new JLabel("Thread switch tries: ");
        javaThreadSwitchContextTriesLabel.setVisible(true);
        javaThreadSwitchContextTriesLabel.setBounds(10, 210, 200, 40);
        javaBenchmarkPanel.add(javaThreadSwitchContextTriesLabel);

        javaThreadSwitchContextTriesField = new JTextField("");
        javaThreadSwitchContextTriesField.setVisible(true);
        javaThreadSwitchContextTriesField.setBounds(220, 210, 150, 30);
        javaBenchmarkPanel.add(javaThreadSwitchContextTriesField);

        javaThreadMigrationTriesLabel = new JLabel("Thread migration tries: ");
        javaThreadMigrationTriesLabel.setVisible(true);
        javaThreadMigrationTriesLabel.setBounds(10, 260, 200, 40);
        javaBenchmarkPanel.add(javaThreadMigrationTriesLabel);

        javaThreadMigrationTriesField = new JTextField("");
        javaThreadMigrationTriesField.setVisible(true);
        javaThreadMigrationTriesField.setBounds(220, 260, 150, 30);
        javaBenchmarkPanel.add(javaThreadMigrationTriesField);

        javaBenchmarkStartButton = new JButton("Start");
        javaBenchmarkStartButton.setVisible(true);
        javaBenchmarkStartButton.setBounds(150, 310, 100, 30);
        javaBenchmarkPanel.add(javaBenchmarkStartButton);

        repaint();
    }

    private void initializeCBenchmarkPanel() {
        cBenchmarkPanel.setLayout(null);

        cMemoryAllocationTriesLabel = new JLabel("Memory allocation tries:");
        cMemoryAllocationTriesLabel.setVisible(true);
        cMemoryAllocationTriesLabel.setBounds(10, 10, 200, 40);
        cBenchmarkPanel.add(cMemoryAllocationTriesLabel);

        cMemoryAllocationTriesField = new JTextField("");
        cMemoryAllocationTriesField.setVisible(true);
        cMemoryAllocationTriesField.setBounds(220, 10, 150, 30);
        cBenchmarkPanel.add(cMemoryAllocationTriesField);

        cMemoryAllocationSizeLabel = new JLabel("Memory allocation size: ");
        cMemoryAllocationSizeLabel.setVisible(true);
        cMemoryAllocationSizeLabel.setBounds(10, 60, 200, 40);
        cBenchmarkPanel.add(cMemoryAllocationSizeLabel);

        cMemoryAllocationSizeField = new JTextField("");
        cMemoryAllocationSizeField.setVisible(true);
        cMemoryAllocationSizeField.setBounds(220, 60, 150, 30);
        cBenchmarkPanel.add(cMemoryAllocationSizeField);

        cMemoryAccessTriesLabel = new JLabel("Memory access tries: ");
        cMemoryAccessTriesLabel.setVisible(true);
        cMemoryAccessTriesLabel.setBounds(10, 110, 200, 40);
        cBenchmarkPanel.add(cMemoryAccessTriesLabel);

        cMemoryAccessTriesField = new JTextField("");
        cMemoryAccessTriesField.setVisible(true);
        cMemoryAccessTriesField.setBounds(220, 110, 150, 30);
        cBenchmarkPanel.add(cMemoryAccessTriesField);

        cThreadCreationTriesLabel = new JLabel("Thread creation tries: ");
        cThreadCreationTriesLabel.setVisible(true);
        cThreadCreationTriesLabel.setBounds(10, 160, 200, 40);
        cBenchmarkPanel.add(cThreadCreationTriesLabel);

        cThreadCreationTriesField = new JTextField("");
        cThreadCreationTriesField.setVisible(true);
        cThreadCreationTriesField.setBounds(220, 160, 150, 30);
        cBenchmarkPanel.add(cThreadCreationTriesField);

        cThreadSwitchContextTriesLabel = new JLabel("Thread switch tries: ");
        cThreadSwitchContextTriesLabel.setVisible(true);
        cThreadSwitchContextTriesLabel.setBounds(10, 210, 200, 40);
        cBenchmarkPanel.add(cThreadSwitchContextTriesLabel);

        cThreadSwitchContextTriesField = new JTextField("");
        cThreadSwitchContextTriesField.setVisible(true);
        cThreadSwitchContextTriesField.setBounds(220, 210, 150, 30);
        cBenchmarkPanel.add(cThreadSwitchContextTriesField);

        cThreadMigrationTriesLabel = new JLabel("Thread migration tries: ");
        cThreadMigrationTriesLabel.setVisible(true);
        cThreadMigrationTriesLabel.setBounds(10, 260, 200, 40);
        cBenchmarkPanel.add(cThreadMigrationTriesLabel);

        cThreadMigrationTriesField = new JTextField("");
        cThreadMigrationTriesField.setVisible(true);
        cThreadMigrationTriesField.setBounds(220, 260, 150, 30);
        cBenchmarkPanel.add(cThreadMigrationTriesField);

        cBenchmarkStartButton = new JButton("Start");
        cBenchmarkStartButton.setVisible(true);
        cBenchmarkStartButton.setBounds(150, 310, 100, 30);
        cBenchmarkPanel.add(cBenchmarkStartButton);

        repaint();
    }

    private void initializeCSharpBenchmarkPanel() {
        cSharpBenchmarkPanel.setLayout(null);

        cSharpMemoryAllocationTriesLabel = new JLabel("Memory allocation tries:");
        cSharpMemoryAllocationTriesLabel.setVisible(true);
        cSharpMemoryAllocationTriesLabel.setBounds(10, 10, 200, 40);
        cSharpBenchmarkPanel.add(cSharpMemoryAllocationTriesLabel);

        cSharpMemoryAllocationTriesField = new JTextField("");
        cSharpMemoryAllocationTriesField.setVisible(true);
        cSharpMemoryAllocationTriesField.setBounds(220, 10, 150, 30);
        cSharpBenchmarkPanel.add(cSharpMemoryAllocationTriesField);

        cSharpMemoryAllocationSizeLabel = new JLabel("Memory allocation size: ");
        cSharpMemoryAllocationSizeLabel.setVisible(true);
        cSharpMemoryAllocationSizeLabel.setBounds(10, 60, 200, 40);
        cSharpBenchmarkPanel.add(cSharpMemoryAllocationSizeLabel);

        cSharpMemoryAllocationSizeField = new JTextField("");
        cSharpMemoryAllocationSizeField.setVisible(true);
        cSharpMemoryAllocationSizeField.setBounds(220, 60, 150, 30);
        cSharpBenchmarkPanel.add(cSharpMemoryAllocationSizeField);

        cSharpMemoryAccessTriesLabel = new JLabel("Memory access tries: ");
        cSharpMemoryAccessTriesLabel.setVisible(true);
        cSharpMemoryAccessTriesLabel.setBounds(10, 110, 200, 40);
        cSharpBenchmarkPanel.add(cSharpMemoryAccessTriesLabel);

        cSharpMemoryAccessTriesField = new JTextField("");
        cSharpMemoryAccessTriesField.setVisible(true);
        cSharpMemoryAccessTriesField.setBounds(220, 110, 150, 30);
        cSharpBenchmarkPanel.add(cSharpMemoryAccessTriesField);

        cSharpThreadCreationTriesLabel = new JLabel("Thread creation tries: ");
        cSharpThreadCreationTriesLabel.setVisible(true);
        cSharpThreadCreationTriesLabel.setBounds(10, 160, 200, 40);
        cSharpBenchmarkPanel.add(cSharpThreadCreationTriesLabel);

        cSharpThreadCreationTriesField = new JTextField("");
        cSharpThreadCreationTriesField.setVisible(true);
        cSharpThreadCreationTriesField.setBounds(220, 160, 150, 30);
        cSharpBenchmarkPanel.add(cSharpThreadCreationTriesField);

        cSharpThreadSwitchContextTriesLabel = new JLabel("Thread switch tries: ");
        cSharpThreadSwitchContextTriesLabel.setVisible(true);
        cSharpThreadSwitchContextTriesLabel.setBounds(10, 210, 200, 40);
        cSharpBenchmarkPanel.add(cSharpThreadSwitchContextTriesLabel);

        cSharpThreadSwitchContextTriesField = new JTextField("");
        cSharpThreadSwitchContextTriesField.setVisible(true);
        cSharpThreadSwitchContextTriesField.setBounds(220, 210, 150, 30);
        cSharpBenchmarkPanel.add(cSharpThreadSwitchContextTriesField);

        cSharpThreadMigrationTriesLabel = new JLabel("Thread migration tries: ");
        cSharpThreadMigrationTriesLabel.setVisible(true);
        cSharpThreadMigrationTriesLabel.setBounds(10, 260, 200, 40);
        cSharpBenchmarkPanel.add(cSharpThreadMigrationTriesLabel);

        cSharpThreadMigrationTriesField = new JTextField("");
        cSharpThreadMigrationTriesField.setVisible(true);
        cSharpThreadMigrationTriesField.setBounds(220, 260, 150, 30);
        cSharpBenchmarkPanel.add(cSharpThreadMigrationTriesField);

        cSharpBenchmarkStartButton = new JButton("Start");
        cSharpBenchmarkStartButton.setVisible(true);
        cSharpBenchmarkStartButton.setBounds(150, 310, 100, 30);
        cSharpBenchmarkPanel.add(cSharpBenchmarkStartButton);

        repaint();
    }

    private void initializeJavaBenchmark() {
        javaBenchmarkStartButton.addActionListener(e -> {
            try {
                Long memAllocationTries = Long.parseLong(javaMemoryAllocationTriesField.getText());
                Long memAllocationSize = Long.parseLong(javaMemoryAllocationSizeField.getText());
                Long memAccessTries = Long.parseLong(javaMemoryAccessTriesField.getText());
                Long threadCreationTries = Long.parseLong(javaThreadCreationTriesField.getText());
                Long threadSwitchTries = Long.parseLong(javaThreadSwitchContextTriesField.getText());
                Long threadMigrationTries = Long.parseLong(javaThreadMigrationTriesField.getText());
                if (memAllocationTries < 1 ||
                        memAllocationTries < 1 ||
                        memAccessTries < 1 ||
                        threadCreationTries < 1 ||
                        threadSwitchTries < 1 ||
                        threadMigrationTries < 1) {
                    throw new InvalidParameterException();
                }
                System.out.println(memAllocationTries + " \n" +
                        memAllocationSize + " \n" +
                        memAccessTries + " \n" +
                        threadCreationTries + " \n" +
                        threadSwitchTries + " \n" +
                        threadMigrationTries);

                String command = "java -Djava.library.path=. JavaBenchmark " + memAllocationTries + " " +
                        memAllocationSize + " " +
                        memAccessTries + " " +
                        threadCreationTries + " " +
                        threadSwitchTries + " " +
                        threadMigrationTries;
                System.out.println(command);
                programCommand = command;
                language = "java";
                isEvent = true;

            } catch (NumberFormatException ex) {
                new MessageFrame("Each input should be a valid long value!");
            } catch (InvalidParameterException ex) {
                new MessageFrame("All values should be strictly greater than 0!");
            }
        });
    }

    private void initializeCBenchmark() {
        cBenchmarkStartButton.addActionListener(e -> {
            try {
                Long memAllocationTries = Long.parseLong(cMemoryAllocationTriesField.getText());
                Long memAllocationSize = Long.parseLong(cMemoryAllocationSizeField.getText());
                Long memAccessTries = Long.parseLong(cMemoryAccessTriesField.getText());
                Long threadCreationTries = Long.parseLong(cThreadCreationTriesField.getText());
                Long threadSwitchTries = Long.parseLong(cThreadSwitchContextTriesField.getText());
                Long threadMigrationTries = Long.parseLong(cThreadMigrationTriesField.getText());
                if (memAllocationTries < 1 ||
                        memAllocationTries < 1 ||
                        memAccessTries < 1 ||
                        threadCreationTries < 1 ||
                        threadSwitchTries < 1 ||
                        threadMigrationTries < 1) {
                    throw new InvalidParameterException();
                }
                System.out.println(memAllocationTries + " \n" +
                        memAllocationSize + " \n" +
                        memAccessTries + " \n" +
                        threadCreationTries + " \n" +
                        threadSwitchTries + " \n" +
                        threadMigrationTries);
                String command = "./c_benchmark " + memAllocationTries + " " +
                        memAllocationSize + " " +
                        memAccessTries + " " +
                        threadCreationTries + " " +
                        threadSwitchTries + " " +
                        threadMigrationTries;
                System.out.println(command);
                programCommand = command;
                language = "c";
                isEvent = true;
            } catch (NumberFormatException ex) {
                new MessageFrame("Each input should be a valid long value!");
            } catch (Exception ex) {
                new MessageFrame("All values should be strictly greater than 0!");
            }
        });
    }

    private void initializeCSharpBenchmark() {
        cSharpBenchmarkStartButton.addActionListener(e -> {
            try {
                Long memAllocationTries = Long.parseLong(cSharpMemoryAllocationTriesField.getText());
                Long memAllocationSize = Long.parseLong(cSharpMemoryAllocationSizeField.getText());
                Long memAccessTries = Long.parseLong(cSharpMemoryAccessTriesField.getText());
                Long threadCreationTries = Long.parseLong(cSharpThreadCreationTriesField.getText());
                Long threadSwitchTries = Long.parseLong(cSharpThreadSwitchContextTriesField.getText());
                Long threadMigrationTries = Long.parseLong(cSharpThreadMigrationTriesField.getText());
                if (memAllocationTries < 1 ||
                        memAllocationTries < 1 ||
                        memAccessTries < 1 ||
                        threadCreationTries < 1 ||
                        threadSwitchTries < 1 ||
                        threadMigrationTries < 1) {
                    throw new InvalidParameterException();
                }
                System.out.println(memAllocationTries + " \n" +
                        memAllocationSize + " \n" +
                        memAccessTries + " \n" +
                        threadCreationTries + " \n" +
                        threadSwitchTries + " \n" +
                        threadMigrationTries);
                String command = "./c#_benchmark " + memAllocationTries + " " +
                        memAllocationSize + " " +
                        memAccessTries + " " +
                        threadCreationTries + " " +
                        threadSwitchTries + " " +
                        threadMigrationTries;
                System.out.println(command);
                programCommand = command;
                language = "c#";
                isEvent = true;
            } catch (NumberFormatException ex) {
                new MessageFrame("Each input should be a valid long value!");
            } catch (Exception ex) {
                new MessageFrame("All values should be strictly greater than 0!");
            }
        });
    }

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            UserInterface userInterface = new UserInterface();
        });
        thread.start();
        try {
            while (true) {
                if (isEvent) {
                    isEvent = false;
                    if (language.equals("c")) {
                        Runtime.getRuntime().exec(programCommand, null, new File(C_BENCHMARK));
                    } else {
                        if (language.equals("java")) {
                            Runtime.getRuntime().exec(programCommand, null, new File(JAVA_BENCHMARK));
                        } else {
                            if (language.equals("c#")) {
                                Runtime.getRuntime().exec(programCommand, null, new File(C_SHARP_BENCHMARK));
                            } else {
                                Runtime.getRuntime().exec(programCommand, null, new File(PYTHON_PLOT));
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
