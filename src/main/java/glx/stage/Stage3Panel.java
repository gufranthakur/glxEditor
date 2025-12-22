package glx.stage;

import glx.Scene;
import glx.mesh.Mesh;
import glx.shape.PlaneShape;
import glx.shape.SquareShape;
import glx.shape.CircleShape;

import javax.swing.*;
import java.awt.*;

public class Stage3Panel extends JPanel {
    private Scene scene;
    private DefaultListModel<Mesh> meshListModel;
    private JList<Mesh> meshList;
    private DefaultListModel<PlaneShape> shapeListModel;
    private JList<PlaneShape> shapeList;

    private JComboBox<String> planeCombo;
    private JSlider xSlider, ySlider, widthSlider, heightSlider, radiusSlider, rotationSlider, depthSlider;
    private JLabel xLabel, yLabel, widthLabel, heightLabel, radiusLabel, rotationLabel, depthLabel, shapeInfoLabel;
    private JPanel dimensionPanel;

    private int shapeCounter = 1;

    public Stage3Panel(Scene scene) {
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

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        JButton addSquareBtn = new JButton("Add Square");
        JButton addCircleBtn = new JButton("Add Circle");
        JButton removeBtn = new JButton("Remove");
        JButton extrudeBtn = new JButton("Extrude");

        addSquareBtn.addActionListener(e -> addShape("Square"));
        addCircleBtn.addActionListener(e -> addShape("Circle"));
        removeBtn.addActionListener(e -> removeShape());
        extrudeBtn.addActionListener(e -> extrudeShape());

        buttonPanel.add(addSquareBtn);
        buttonPanel.add(addCircleBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(extrudeBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

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
        JLabel posTitle = new JLabel("Position");
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

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel dimTitle = new JLabel("Dimensions");
        dimTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(dimTitle, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        dimensionPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        panel.add(dimensionPanel, gbc);
        gbc.gridwidth = 1;

        widthLabel = new JLabel("0.30");
        widthSlider = new JSlider(5, 150, 30);

        heightLabel = new JLabel("0.30");
        heightSlider = new JSlider(5, 150, 30);

        radiusLabel = new JLabel("0.15");
        radiusSlider = new JSlider(5, 150, 15);

        rotationLabel = new JLabel("0.00");
        rotationSlider = new JSlider(-180, 180, 0);
        row = addSliderRow(panel, row, "Rotation:", rotationSlider, rotationLabel, v -> updateShapeProperty("rotation", v));

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
            float value;
            if (label.equals("Rotation:")) {
                value = slider.getValue();
            } else {
                value = slider.getValue() / 100.0f;
            }
            callback.onValueChanged(value);
            valueLabel.setText(String.format("%.2f", value));
        });

        return row + 1;
    }

    private void updateDimensionControls(PlaneShape shape) {
        dimensionPanel.removeAll();

        int row = 0;

        if (shape instanceof SquareShape) {
            row = addSliderToDimensionPanel(dimensionPanel, row, "Width:", widthSlider, widthLabel, v -> updateShapeProperty("width", v));
            row = addSliderToDimensionPanel(dimensionPanel, row, "Height:", heightSlider, heightLabel, v -> updateShapeProperty("height", v));
        } else if (shape instanceof CircleShape) {
            row = addSliderToDimensionPanel(dimensionPanel, row, "Radius:", radiusSlider, radiusLabel, v -> updateShapeProperty("radius", v));
        }

        dimensionPanel.revalidate();
        dimensionPanel.repaint();
    }

    private int addSliderToDimensionPanel(JPanel panel, int row, String label, JSlider slider, JLabel valueLabel, SliderCallback callback) {
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

        for (var l : slider.getChangeListeners()) slider.removeChangeListener(l);
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
            dimensionPanel.removeAll();
            dimensionPanel.revalidate();
            dimensionPanel.repaint();
            return;
        }

        shapeInfoLabel.setText(shape.name);

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

    private void extrudeShape() {
        PlaneShape shape = shapeList.getSelectedValue();
        if (shape == null) {
            JOptionPane.showMessageDialog(this, "Please select a shape first");
            return;
        }
        if (shape.depth <= 0) {
            JOptionPane.showMessageDialog(this, "Please set depth > 0 before extruding");
            return;
        }
        shape.extruded = true;
        shape.intruded = false;
        shapeList.repaint();
    }

    @FunctionalInterface
    interface SliderCallback {
        void onValueChanged(float value);
    }
}