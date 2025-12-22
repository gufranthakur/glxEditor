package glx.stage;

import glx.Scene;
import glx.mesh.Mesh;
import glx.shape.PlaneShape;
import glx.shape.SquareShape;
import glx.shape.CircleShape;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class Stage2Panel extends JPanel {
    private Scene scene;
    private DefaultListModel<Mesh> meshListModel;
    private JList<Mesh> meshList;
    private DefaultListModel<PlaneShape> shapeListModel;
    private JList<PlaneShape> shapeList;

    private JComboBox<String> planeCombo;
    private JSlider xSlider, ySlider, widthSlider, heightSlider, radiusSlider, rotationSlider, depthSlider;
    private JLabel xLabel, yLabel, widthLabel, heightLabel, radiusLabel, rotationLabel, depthLabel, shapeInfoLabel;
    private JPanel dimensionPanel;
    private JButton intrudeBtn;

    private int shapeCounter = 1;

    public Stage2Panel(Scene scene) {
        this.scene = scene;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left panel: Meshes and Shapes side by side
        JPanel leftPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        leftPanel.add(createMeshListPanel());
        leftPanel.add(createShapePanel());

        add(leftPanel, BorderLayout.WEST);
        add(createControlsPanel(), BorderLayout.CENTER);
    }

    private JPanel createMeshListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(200, 600));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Meshes",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));

        meshListModel = new DefaultListModel<>();
        refreshMeshList();

        meshList = new JList<>(meshListModel);
        meshList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        meshList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateShapeList();
                updateControlsForShape(); // Reset controls when mesh changes
            }
        });

        JScrollPane scrollPane = new JScrollPane(meshList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setToolTipText("Refresh the mesh list from the scene");
        refreshBtn.addActionListener(e -> {
            refreshMeshList();
            updateShapeList();
        });
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshMeshList() {
        meshListModel.clear();
        for (Mesh mesh : scene.getMeshes()) {
            meshListModel.addElement(mesh);
        }
        if (meshListModel.getSize() > 0 && meshList.getSelectedIndex() == -1) {
            meshList.setSelectedIndex(0);
        }
    }

    private JPanel createShapePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(200, 600));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Plane Shapes",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));

        shapeListModel = new DefaultListModel<>();
        shapeList = new JList<>(shapeListModel);
        shapeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shapeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateControlsForShape();
            }
        });

        JScrollPane scrollPane = new JScrollPane(shapeList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Shape actions panel
        JPanel actionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JButton addSquareBtn = new JButton("+ Add Square");
        JButton addCircleBtn = new JButton("+ Add Circle");
        JButton removeBtn = new JButton("- Remove Shape");
        intrudeBtn = new JButton("Intrude Shape");

        addSquareBtn.setToolTipText("Add a square shape to the selected mesh");
        addCircleBtn.setToolTipText("Add a circle shape to the selected mesh");
        removeBtn.setToolTipText("Remove the selected shape");
        intrudeBtn.setToolTipText("Intrude the selected shape (requires depth > 0)");

        addSquareBtn.addActionListener(e -> addShape("Square"));
        addCircleBtn.addActionListener(e -> addShape("Circle"));
        removeBtn.addActionListener(e -> removeShape());
        intrudeBtn.addActionListener(e -> intrudeShape());

        actionsPanel.add(addSquareBtn);
        actionsPanel.add(addCircleBtn);
        actionsPanel.add(removeBtn);
        actionsPanel.add(intrudeBtn);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Top info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        shapeInfoLabel = new JLabel("No shape selected", SwingConstants.CENTER);
        shapeInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(shapeInfoLabel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.NORTH);

        // Main controls in scrollpane
        JPanel controlsContainer = new JPanel();
        controlsContainer.setLayout(new BoxLayout(controlsContainer, BoxLayout.Y_AXIS));

        controlsContainer.add(createPlaneSelectionPanel());
        controlsContainer.add(Box.createVerticalStrut(10));
        controlsContainer.add(createPositionPanel());
        controlsContainer.add(Box.createVerticalStrut(10));
        controlsContainer.add(createDimensionsPanel());
        controlsContainer.add(Box.createVerticalStrut(10));
        controlsContainer.add(createRotationPanel());
        controlsContainer.add(Box.createVerticalStrut(10));
        controlsContainer.add(createDepthPanel());
        controlsContainer.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(controlsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPlaneSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Plane Selection",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        innerPanel.add(new JLabel("Plane:"));
        planeCombo = new JComboBox<>(new String[]{"Front", "Back", "Top", "Bottom", "Right", "Left"});
        planeCombo.setPreferredSize(new Dimension(150, 25));
        planeCombo.addActionListener(e -> updateShapePlane());
        innerPanel.add(planeCombo);

        panel.add(innerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPositionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Position",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        xLabel = new JLabel("0.00");
        xSlider = new JSlider(-100, 100, 0);
        panel.add(createSliderRow("X:", xSlider, xLabel, v -> updateShapeProperty("x", v), false));

        yLabel = new JLabel("0.00");
        ySlider = new JSlider(-100, 100, 0);
        panel.add(createSliderRow("Y:", ySlider, yLabel, v -> updateShapeProperty("y", v), false));

        return panel;
    }

    private JPanel createDimensionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Dimensions",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        dimensionPanel = new JPanel();
        dimensionPanel.setLayout(new BoxLayout(dimensionPanel, BoxLayout.Y_AXIS));

        widthLabel = new JLabel("0.30");
        widthSlider = new JSlider(5, 150, 30);

        heightLabel = new JLabel("0.30");
        heightSlider = new JSlider(5, 150, 30);

        radiusLabel = new JLabel("0.15");
        radiusSlider = new JSlider(5, 150, 15);

        panel.add(dimensionPanel);
        return panel;
    }

    private JPanel createRotationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Rotation",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        rotationLabel = new JLabel("0.00");
        rotationSlider = new JSlider(-180, 180, 0);
        panel.add(createSliderRow("Angle:", rotationSlider, rotationLabel, v -> updateShapeProperty("rotation", v), true));

        return panel;
    }

    private JPanel createDepthPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Depth (for Intrusion)",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        depthLabel = new JLabel("0.00");
        depthSlider = new JSlider(0, 150, 0);
        panel.add(createSliderRow("Depth:", depthSlider, depthLabel, v -> updateShapeProperty("depth", v), false));

        return panel;
    }

    private JPanel createSliderRow(String label, JSlider slider, JLabel valueLabel, SliderCallback callback, boolean isRotation) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 5));
        rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setPreferredSize(new Dimension(60, 25));
        rowPanel.add(titleLabel, BorderLayout.WEST);

        slider.setMajorTickSpacing(isRotation ? 45 : 25);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(false);
        rowPanel.add(slider, BorderLayout.CENTER);

        valueLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        valueLabel.setPreferredSize(new Dimension(50, 25));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rowPanel.add(valueLabel, BorderLayout.EAST);

        slider.addChangeListener(e -> {
            float value = isRotation ? slider.getValue() : slider.getValue() / 100.0f;
            callback.onValueChanged(value);
            valueLabel.setText(String.format("%.2f", value));
        });

        return rowPanel;
    }

    private void updateDimensionControls(PlaneShape shape) {
        dimensionPanel.removeAll();

        if (shape instanceof SquareShape) {
            dimensionPanel.add(createSliderRow("Width:", widthSlider, widthLabel, v -> updateShapeProperty("width", v), false));
            dimensionPanel.add(createSliderRow("Height:", heightSlider, heightLabel, v -> updateShapeProperty("height", v), false));
        } else if (shape instanceof CircleShape) {
            dimensionPanel.add(createSliderRow("Radius:", radiusSlider, radiusLabel, v -> updateShapeProperty("radius", v), false));
        }

        dimensionPanel.revalidate();
        dimensionPanel.repaint();
    }

    private void addShape(String type) {
        Mesh mesh = meshList.getSelectedValue();
        if (mesh == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a mesh first",
                    "No Mesh Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String plane = (String) planeCombo.getSelectedItem();
        PlaneShape shape;
        String name = type + " " + shapeCounter++;

        if (type.equals("Square")) {
            shape = new SquareShape(name, plane);
        } else {
            shape = new CircleShape(name, plane);
        }

        mesh.addShape(shape);
        updateShapeList();
        shapeList.setSelectedValue(shape, true);
    }

    private void removeShape() {
        PlaneShape shape = shapeList.getSelectedValue();
        Mesh mesh = meshList.getSelectedValue();

        if (shape == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a shape to remove",
                    "No Shape Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (mesh != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove '" + shape.name + "'?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                mesh.removeShape(shape);
                updateShapeList();
            }
        }
    }

    private void updateShapeList() {
        shapeListModel.clear();
        Mesh mesh = meshList.getSelectedValue();
        if (mesh != null) {
            for (PlaneShape shape : mesh.getShapes()) {
                shapeListModel.addElement(shape);
            }
        }
        updateControlsForShape();
    }

    private void updateControlsForShape() {
        PlaneShape shape = shapeList.getSelectedValue();
        if (shape == null) {
            shapeInfoLabel.setText("No shape selected");
            dimensionPanel.removeAll();
            dimensionPanel.revalidate();
            dimensionPanel.repaint();
            intrudeBtn.setEnabled(false);
            setControlsEnabled(false);
            return;
        }

        shapeInfoLabel.setText("Editing: " + shape.name);
        setControlsEnabled(true);
        intrudeBtn.setEnabled(true);

        removeListeners();

        planeCombo.setSelectedItem(shape.plane);
        xSlider.setValue((int)(shape.x * 100));
        ySlider.setValue((int)(shape.y * 100));
        rotationSlider.setValue((int)shape.rotation);
        depthSlider.setValue((int)(shape.depth * 100));

        xLabel.setText(String.format("%.2f", shape.x));
        yLabel.setText(String.format("%.2f", shape.y));
        rotationLabel.setText(String.format("%.2f", shape.rotation));
        depthLabel.setText(String.format("%.2f", shape.depth));

        if (shape instanceof SquareShape) {
            widthSlider.setValue((int)(shape.width * 100));
            heightSlider.setValue((int)(shape.height * 100));
            widthLabel.setText(String.format("%.2f", shape.width));
            heightLabel.setText(String.format("%.2f", shape.height));
        } else if (shape instanceof CircleShape) {
            radiusSlider.setValue((int)(shape.radius * 100));
            radiusLabel.setText(String.format("%.2f", shape.radius));
        }

        updateDimensionControls(shape);
        addListeners();
    }

    private void setControlsEnabled(boolean enabled) {
        planeCombo.setEnabled(enabled);
        xSlider.setEnabled(enabled);
        ySlider.setEnabled(enabled);
        rotationSlider.setEnabled(enabled);
        depthSlider.setEnabled(enabled);
        widthSlider.setEnabled(enabled);
        heightSlider.setEnabled(enabled);
        radiusSlider.setEnabled(enabled);
    }

    private void removeListeners() {
        for (var l : xSlider.getChangeListeners()) xSlider.removeChangeListener(l);
        for (var l : ySlider.getChangeListeners()) ySlider.removeChangeListener(l);
        for (var l : rotationSlider.getChangeListeners()) rotationSlider.removeChangeListener(l);
        for (var l : depthSlider.getChangeListeners()) depthSlider.removeChangeListener(l);
    }

    private void addListeners() {
        xSlider.addChangeListener(e -> {
            float v = xSlider.getValue() / 100.0f;
            updateShapeProperty("x", v);
            xLabel.setText(String.format("%.2f", v));
        });
        ySlider.addChangeListener(e -> {
            float v = ySlider.getValue() / 100.0f;
            updateShapeProperty("y", v);
            yLabel.setText(String.format("%.2f", v));
        });
        rotationSlider.addChangeListener(e -> {
            float v = rotationSlider.getValue();
            updateShapeProperty("rotation", v);
            rotationLabel.setText(String.format("%.2f", v));
        });
        depthSlider.addChangeListener(e -> {
            float v = depthSlider.getValue() / 100.0f;
            updateShapeProperty("depth", v);
            depthLabel.setText(String.format("%.2f", v));
        });
    }

    private void updateShapeProperty(String prop, float value) {
        PlaneShape shape = shapeList.getSelectedValue();
        if (shape != null) {
            switch (prop) {
                case "x": shape.x = value; break;
                case "y": shape.y = value; break;
                case "width": shape.width = value; break;
                case "height": shape.height = value; break;
                case "radius": shape.radius = value; break;
                case "rotation": shape.rotation = value; break;
                case "depth": shape.depth = value; break;
            }
        }
    }

    private void updateShapePlane() {
        PlaneShape shape = shapeList.getSelectedValue();
        if (shape != null) {
            shape.plane = (String) planeCombo.getSelectedItem();
        }
    }

    private void intrudeShape() {
        PlaneShape shape = shapeList.getSelectedValue();
        if (shape == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a shape first",
                    "No Shape Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (shape.depth <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Please set depth > 0 before intruding.\nCurrent depth: " + String.format("%.2f", shape.depth),
                    "Invalid Depth",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        shape.intruded = true;
        JOptionPane.showMessageDialog(this,
                "Shape '" + shape.name + "' has been intruded with depth " + String.format("%.2f", shape.depth),
                "Intrusion Complete",
                JOptionPane.INFORMATION_MESSAGE);
        shapeList.repaint();
    }

    @FunctionalInterface
    interface SliderCallback {
        void onValueChanged(float value);
    }
}