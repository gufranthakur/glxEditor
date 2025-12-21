package glx;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import glx.stage.Stage1Panel;
import glx.stage.Stage2Panel;

import javax.swing.*;

public class ControlPanel {
    private JFrame frame;
    private Functions functions;
    private Scene scene;

    public ControlPanel(Functions functions, Scene scene) {
        this.functions = functions;
        this.scene = scene;
    }

    public void create() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }

            frame = new JFrame("Mesh Controls");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JTabbedPane tabbedPane = new JTabbedPane();

            Stage1Panel stage1Panel = new Stage1Panel(functions, scene);
            tabbedPane.addTab("Stage 1", stage1Panel);

            Stage2Panel stage2Panel = new Stage2Panel(scene);
            tabbedPane.addTab("Stage 2", stage2Panel);

            frame.add(tabbedPane);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public void dispose() {
        if (frame != null) {
            SwingUtilities.invokeLater(() -> frame.dispose());
        }
    }
}