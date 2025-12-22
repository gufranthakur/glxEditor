package glx;

import glx.GLXWriter;
import glx.Scene;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.io.File;

public class OutputPanel extends JPanel {
    private Scene scene;
    private JTextArea outputArea;
    private Timer updateTimer;
    private String lastOutput = "";

    public OutputPanel(Scene scene) {
        this.scene = scene;
        initializeUI();
        startAutoUpdate();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("GLX JSON Output");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("Stage 1 - Base Node Geometry");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 12));
        subtitle.setForeground(Color.GRAY);
        titlePanel.add(subtitle, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);

        // Output text area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setTabSize(4);
        outputArea.setBackground(new Color(43, 43, 43));
        outputArea.setForeground(new Color(169, 183, 198));
        outputArea.setCaretColor(Color.WHITE);
        outputArea.setLineWrap(false);
        outputArea.setWrapStyleWord(false);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(500, 600));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setToolTipText("Manually refresh the output");
        refreshButton.addActionListener(e -> updateOutput());

        JButton copyButton = new JButton("📋 Copy");
        copyButton.setToolTipText("Copy output to clipboard");
        copyButton.addActionListener(e -> copyToClipboard());

        JButton saveButton = new JButton("💾 Save");
        saveButton.setToolTipText("Save output to file");
        saveButton.addActionListener(e -> saveToFile());

        JButton clearButton = new JButton("🗑 Clear");
        clearButton.setToolTipText("Clear the output");
        clearButton.addActionListener(e -> outputArea.setText(""));

        JCheckBox autoUpdateCheckbox = new JCheckBox("Auto-update", true);
        autoUpdateCheckbox.setToolTipText("Automatically refresh output");
        autoUpdateCheckbox.addActionListener(e -> {
            if (autoUpdateCheckbox.isSelected()) {
                startAutoUpdate();
            } else {
                stopAutoUpdate();
            }
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(autoUpdateCheckbox);

        add(buttonPanel, BorderLayout.SOUTH);

        // Initial update
        updateOutput();
    }

    private void startAutoUpdate() {
        if (updateTimer != null && updateTimer.isRunning()) {
            return;
        }
        // Update every 500ms for responsive updates
        updateTimer = new Timer(500, e -> {
            if (isVisible()) {
                updateOutput();
            }
        });
        updateTimer.start();
    }

    public void updateOutput() {
        try {
            String json = GLXWriter.generateJSON(scene.getMeshes());

            // Only update if content has changed to reduce flicker
            if (!json.equals(lastOutput)) {
                lastOutput = json;

                if (json.isEmpty()) {
                    outputArea.setText("// No meshes created yet.\n// Add a Cube or Cylinder in Stage 1 to see output.");
                } else {
                    // Store caret position
                    int caretPos = outputArea.getCaretPosition();
                    outputArea.setText(json);

                    // Try to restore caret position if reasonable
                    try {
                        if (caretPos < outputArea.getDocument().getLength()) {
                            outputArea.setCaretPosition(caretPos);
                        } else {
                            outputArea.setCaretPosition(0);
                        }
                    } catch (IllegalArgumentException ex) {
                        outputArea.setCaretPosition(0);
                    }
                }
            }
        } catch (Exception ex) {
            // Silently ignore concurrent modification exceptions during update
            // They will be resolved on the next timer tick
            System.err.println("Error updating output (will retry): " + ex.getMessage());
        }
    }

    private void copyToClipboard() {
        String content = outputArea.getText();
        if (content.isEmpty() || content.startsWith("//")) {
            JOptionPane.showMessageDialog(this,
                    "Nothing to copy!",
                    "Empty Output",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringSelection selection = new StringSelection(content);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);

        JOptionPane.showMessageDialog(this,
                "Output copied to clipboard!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveToFile() {
        String content = outputArea.getText();
        if (content.isEmpty() || content.startsWith("//")) {
            JOptionPane.showMessageDialog(this,
                    "Nothing to save!",
                    "Empty Output",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save GLX Output");
        fileChooser.setSelectedFile(new File("glx_output.json"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files (*.json)", "json");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ensure .json extension
            if (!fileToSave.getName().endsWith(".json")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".json");
            }

            try (java.io.FileWriter writer = new java.io.FileWriter(fileToSave)) {
                writer.write(content);
                JOptionPane.showMessageDialog(this,
                        "File saved successfully!\n" + fileToSave.getAbsolutePath(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void stopAutoUpdate() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
}