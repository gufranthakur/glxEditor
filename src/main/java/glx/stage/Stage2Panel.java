package glx.stage;

import glx.Scene;
import glx.mesh.Mesh;
import glx.shape.PlaneShape;
import glx.shape.SquareShape;
import glx.shape.CircleShape;

import javax.swing.*;
import java.awt.*;

public class Stage2Panel extends JPanel {
    private Scene scene;
    private DefaultListModel<Mesh> meshListModel;
    private JList<Mesh> meshList;
    private DefaultListModel<PlaneShape> shapeListModel;
    private JList<PlaneShape> shapeList;

    private JComboBox<String> planeCombo;
    private JSlider xSlider, ySlider, sizeSlider, depthSlider;
    private JLabel xLabel, yLabel, sizeLabel, depthLabel, shapeInfoLabel;

    private int shapeCounter = 1;

    public Stage2Panel(Scene scene) {
        this.scene = scene;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createMeshListPanel(), BorderLayout.WEST);
        add(createShapePanel(), BorderLayout.CENTER);
        add(createControlsPanel(), BorderLayout.EAST);
    }

    private JPanel createMeshListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(150, 600));

        JLabel title = new JLabel("Meshes");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);

        meshListModel = new DefaultListModel<>();
        refreshMeshList();

        meshList = new JList<>(meshListModel);
        meshList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        meshList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateShapeList();
            }
        });

        panel.add(new JScrollPane(meshList), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
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
    }

    private JPanel createShapePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(200, 600));

        JLabel title = new JLabel("Plane Shapes");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);

        shapeListModel = new DefaultListModel<>();
        shapeList = new JList<>(shapeListModel);
        shapeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shapeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateControlsForShape();
            }
        });

        panel.add(new JScrollPane(shapeList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JButton addSquareBtn = new JButton("Add Square");
        JButton addCircleBtn = new JButton("Add Circle");
        JButton removeBtn = new JButton("Remove");

        addSquareBtn.addActionListener(e -> addShape("Square"));
        addCircleBtn.addActionListener(e -> addShape("Circle"));
        removeBtn.addActionListener(e -> removeShape());

        buttonPanel.add(addSquareBtn);
        buttonPanel.add(addCircleBtn);
        buttonPanel.add(removeBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        JButton intrudeBtn = new JButton("Intrude");
        intrudeBtn.addActionListener(e -> intrudeShape());
        panel.add(intrudeBtn, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(350, 600));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        shapeInfoLabel = new JLabel("No shape selected");
        shapeInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(shapeInfoLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        JLabel planeTitle = new JLabel("Plane Selection");
        planeTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(planeTitle, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Plane:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        planeCombo = new JComboBox<>(new String[]{"Front", "Back", "Top", "Bottom", "Right", "Left"});
        planeCombo.addActionListener(e -> updateShapePlane());
        panel.add(planeCombo, gbc);
        gbc.gridwidth = 1;
        row++;

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel posTitle = new JLabel("Position & Size");
        posTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(posTitle, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        xLabel = new JLabel("0.00");
        xSlider = new JSlider(-100, 100, 0);
        row = addSliderRow(panel, row, "X:", xSlider, xLabel, v -> updateShapeProperty("x", v));

        yLabel = new JLabel("0.00");
        ySlider = new JSlider(-100, 100, 0);
        row = addSliderRow(panel, row, "Y:", ySlider, yLabel, v -> updateShapeProperty("y", v));

        sizeLabel = new JLabel("0.30");
        sizeSlider = new JSlider(5, 150, 30);
        row = addSliderRow(panel, row, "Size:", sizeSlider, sizeLabel, v -> updateShapeProperty("size", v));

        depthLabel = new JLabel("0.00");
        depthSlider = new JSlider(0, 150, 0);
        row = addSliderRow(panel, row, "Depth:", depthSlider, depthLabel, v -> updateShapeProperty("depth", v));

        return panel;
    }

    private int addSliderRow(JPanel panel, int row, String label, JSlider slider, JLabel valueLabel, SliderCallback callback) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = row;

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        slider.setPreferredSize(new Dimension(200, 40));

        JPanel rowPanel = new JPanel(new GridBagLayout());
        GridBagConstraints rowGbc = new GridBagConstraints();
        rowGbc.fill = GridBagConstraints.HORIZONTAL;
        rowGbc.gridy = 0;

        rowGbc.gridx = 0;
        rowGbc.weightx = 0.0;
        rowPanel.add(titleLabel, rowGbc);

        rowGbc.gridx = 1;
        rowGbc.weightx = 1.0;
        rowGbc.insets = new Insets(0, 10, 0, 10);
        rowPanel.add(slider, rowGbc);

        rowGbc.gridx = 2;
        rowGbc.weightx = 0.0;
        rowGbc.insets = new Insets(0, 0, 0, 0);
        valueLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
        valueLabel.setPreferredSize(new Dimension(50, 20));
        rowPanel.add(valueLabel, rowGbc);

        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        panel.add(rowPanel, gbc);

        slider.addChangeListener(e -> {
            float value = slider.getValue() / 100.0f;
            callback.onValueChanged(value);
            valueLabel.setText(String.format("%.2f", value));
        });

        return row + 1;
    }

    private void addShape(String type) {
        Mesh mesh = meshList.getSelectedValue();
        if (mesh == null) {
            JOptionPane.showMessageDialog(this, "Please select a mesh first");
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
        if (shape != null && mesh != null) {
            mesh.removeShape(shape);
            updateShapeList();
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
    }

    private void updateControlsForShape() {
        PlaneShape shape = shapeList.getSelectedValue();
        if (shape == null) {
            shapeInfoLabel.setText("No shape selected");
            return;
        }

        shapeInfoLabel.setText(shape.getName());

        removeListeners();

        planeCombo.setSelectedItem(shape.getPlane());
        xSlider.setValue((int)(shape.getX() * 100));
        ySlider.setValue((int)(shape.getY() * 100));
        sizeSlider.setValue((int)(shape.getSize() * 100));
        depthSlider.setValue((int)(shape.getDepth() * 100));

        xLabel.setText(String.format("%.2f", shape.getX()));
        yLabel.setText(String.format("%.2f", shape.getY()));
        sizeLabel.setText(String.format("%.2f", shape.getSize()));
        depthLabel.setText(String.format("%.2f", shape.getDepth()));

        addListeners();
    }

    private void removeListeners() {
        for (var l : xSlider.getChangeListeners()) xSlider.removeChangeListener(l);
        for (var l : ySlider.getChangeListeners()) ySlider.removeChangeListener(l);
        for (var l : sizeSlider.getChangeListeners()) sizeSlider.removeChangeListener(l);
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
        sizeSlider.addChangeListener(e -> {
            float v = sizeSlider.getValue() / 100.0f;
            updateShapeProperty("size", v);
            sizeLabel.setText(String.format("%.2f", v));
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
                case "x": shape.setX(value); break;
                case "y": shape.setY(value); break;
                case "size": shape.setSize(value); break;
                case "depth": shape.setDepth(value); break;
            }
        }
    }

    private void updateShapePlane() {
        PlaneShape shape = shapeList.getSelectedValue();
        if (shape != null) {
            shape.setPlane((String) planeCombo.getSelectedItem());
        }
    }

    private void intrudeShape() {
        PlaneShape shape = shapeList.getSelectedValue();
        if (shape == null) {
            JOptionPane.showMessageDialog(this, "Please select a shape first");
            return;
        }
        if (shape.getDepth() <= 0) {
            JOptionPane.showMessageDialog(this, "Please set depth > 0 before intruding");
            return;
        }
        shape.intruded = true;
        shapeList.repaint();
    }

    @FunctionalInterface
    interface SliderCallback {
        void onValueChanged(float value);
    }
}