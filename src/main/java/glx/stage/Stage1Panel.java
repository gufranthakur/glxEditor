package glx.stage;

import glx.*;
import glx.mesh.CubeMesh;
import glx.mesh.CylinderMesh;
import glx.mesh.DonutMesh;
import glx.mesh.TriangleMesh;
import glx.mesh.Mesh;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Stage1Panel extends JPanel {
    private Functions functions;
    private Scene scene;

    private DefaultListModel<Mesh> meshListModel;
    private JList<Mesh> meshList;

    private JSlider widthSlider;
    private JSlider lengthSlider;
    private JSlider heightSlider;
    private JSlider innerRadiusSlider;
    private JSlider outerRadiusSlider;
    private JSlider slopeFactorSlider;
    private JSlider xSlider;
    private JSlider ySlider;
    private JSlider zSlider;
    private JSlider rxSlider;
    private JSlider rySlider;
    private JSlider rzSlider;

    private JLabel widthValueLabel;
    private JLabel lengthValueLabel;
    private JLabel heightValueLabel;
    private JLabel innerRadiusValueLabel;
    private JLabel outerRadiusValueLabel;
    private JLabel slopeFactorValueLabel;
    private JLabel xValueLabel;
    private JLabel yValueLabel;
    private JLabel zValueLabel;
    private JLabel rxValueLabel;
    private JLabel ryValueLabel;
    private JLabel rzValueLabel;
    private JLabel volumeLabel;
    private JLabel surfaceAreaLabel;
    private JLabel meshTypeLabel;

    private JPanel dimensionPanel;

    private int meshCounter = 1;

    public Stage1Panel(Functions functions, Scene scene) {
        this.functions = functions;
        this.scene = scene;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = createMeshListPanel();
        JPanel rightPanel = createControlsPanel();

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createMeshListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(200, 600));

        JLabel title = new JLabel("Meshes");
        title.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 5, 5));

        JButton addCubeButton = new JButton("Add Cube");
        addCubeButton.addActionListener(e -> addMesh("Cube"));

        JButton addCylinderButton = new JButton("Add Cylinder");
        addCylinderButton.addActionListener(e -> addMesh("Cylinder"));

        JButton addDonutButton = new JButton("Add Donut");
        addDonutButton.addActionListener(e -> addMesh("Donut"));

        JButton addTriangleButton = new JButton("Add Triangle");
        addTriangleButton.addActionListener(e -> addMesh("Triangle"));

        JButton duplicateButton = new JButton("Duplicate");
        duplicateButton.addActionListener(e -> duplicateMesh());

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> removeMesh());

        JButton loadButton = new JButton("📂 Load JSON");
        loadButton.setToolTipText("Load meshes from GLX JSON file");
        loadButton.addActionListener(e -> loadFromJSON());

        buttonPanel.add(addCubeButton);
        buttonPanel.add(addCylinderButton);
        buttonPanel.add(addDonutButton);
        buttonPanel.add(addTriangleButton);
        buttonPanel.add(duplicateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(loadButton);

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);

        meshListModel = new DefaultListModel<>();
        meshList = new JList<>(meshListModel);
        meshList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        meshList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Mesh selected = meshList.getSelectedValue();
                if (selected != null) {
                    scene.setSelectedMesh(selected);
                    updateControlsForSelectedMesh(selected);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(meshList);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        meshTypeLabel = new JLabel("No mesh selected");
        meshTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(meshTypeLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        JLabel dimensionsTitle = new JLabel("Dimensions");
        dimensionsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(dimensionsTitle, gbc);
        gbc.gridwidth = 1;

        dimensionPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        panel.add(dimensionPanel, gbc);
        gbc.gridwidth = 1;

        widthValueLabel = createValueLabel();
        widthSlider = new JSlider(10, 300, 100);

        lengthValueLabel = createValueLabel();
        lengthSlider = new JSlider(10, 300, 100);

        heightValueLabel = createValueLabel();
        heightSlider = new JSlider(10, 300, 100);

        innerRadiusValueLabel = createValueLabel();
        innerRadiusSlider = new JSlider(10, 150, 30);

        outerRadiusValueLabel = createValueLabel();
        outerRadiusSlider = new JSlider(10, 300, 70);

        slopeFactorValueLabel = createValueLabel();
        slopeFactorSlider = new JSlider(0, 100, 50);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel positionTitle = new JLabel("Position");
        positionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(positionTitle, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        xValueLabel = createValueLabel();
        xSlider = new JSlider(-300, 300, 0);
        row = addSliderRow(panel, row, "X:", xSlider, xValueLabel,
                value -> functions.updatePositionX(value));

        yValueLabel = createValueLabel();
        ySlider = new JSlider(-300, 300, 0);
        row = addSliderRow(panel, row, "Y:", ySlider, yValueLabel,
                value -> functions.updatePositionY(value));

        zValueLabel = createValueLabel();
        zSlider = new JSlider(-300, 300, 0);
        row = addSliderRow(panel, row, "Z:", zSlider, zValueLabel,
                value -> functions.updatePositionZ(value));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel rotationTitle = new JLabel("Rotation");
        rotationTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(rotationTitle, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        rxValueLabel = createValueLabel();
        rxSlider = new JSlider(-180, 180, 0);
        row = addSliderRow(panel, row, "RX:", rxSlider, rxValueLabel,
                value -> functions.updateRotationX(value));

        ryValueLabel = createValueLabel();
        rySlider = new JSlider(-180, 180, 0);
        row = addSliderRow(panel, row, "RY:", rySlider, ryValueLabel,
                value -> functions.updateRotationY(value));

        rzValueLabel = createValueLabel();
        rzSlider = new JSlider(-180, 180, 0);
        row = addSliderRow(panel, row, "RZ:", rzSlider, rzValueLabel,
                value -> functions.updateRotationZ(value));

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton resetButton = new JButton("Reset Selected");
        resetButton.addActionListener(e -> resetSelected());
        panel.add(resetButton, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(10, 10, 10, 10);
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Mesh Information"));

        volumeLabel = new JLabel("Volume: -");
        volumeLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));

        surfaceAreaLabel = new JLabel("Surface Area: -");
        surfaceAreaLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));

        infoPanel.add(volumeLabel);
        infoPanel.add(surfaceAreaLabel);
        panel.add(infoPanel, gbc);

        return panel;
    }

    private int addSliderRow(JPanel panel, int row, String label, JSlider slider,
                             JLabel valueLabel, SliderCallback callback) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = row;

        gbc.gridx = 0;
        gbc.weightx = 0.0;
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        slider.setPreferredSize(new Dimension(300, 40));

        float initialValue = slider.getValue() / 100.0f;
        if (label.startsWith("R")) {
            initialValue = slider.getValue();
        }
        valueLabel.setText(String.format("%.2f", initialValue));

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
        rowPanel.add(valueLabel, rowGbc);

        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        panel.add(rowPanel, gbc);

        slider.addChangeListener(e -> {
            float value;
            if (label.startsWith("R")) {
                value = slider.getValue();
            } else {
                value = slider.getValue() / 100.0f;
            }
            callback.onValueChanged(value);
            valueLabel.setText(String.format("%.2f", value));
            updateInfo();
        });

        return row + 1;
    }

    private void updateDimensionControls(Mesh mesh) {
        dimensionPanel.removeAll();

        int row = 0;

        if (mesh instanceof CubeMesh) {
            row = addSliderToDimensionPanel(row, "Width:", widthSlider, widthValueLabel,
                    value -> functions.updateWidth(value));
            row = addSliderToDimensionPanel(row, "Length:", lengthSlider, lengthValueLabel,
                    value -> functions.updateLength(value));
            row = addSliderToDimensionPanel(row, "Height:", heightSlider, heightValueLabel,
                    value -> functions.updateHeight(value));
        } else if (mesh instanceof CylinderMesh) {
            row = addSliderToDimensionPanel(row, "Width:", widthSlider, widthValueLabel,
                    value -> functions.updateWidth(value));
            row = addSliderToDimensionPanel(row, "Length:", lengthSlider, lengthValueLabel,
                    value -> functions.updateLength(value));
            row = addSliderToDimensionPanel(row, "Height:", heightSlider, heightValueLabel,
                    value -> functions.updateHeight(value));
        } else if (mesh instanceof DonutMesh) {
            row = addSliderToDimensionPanel(row, "Inner R:", innerRadiusSlider, innerRadiusValueLabel,
                    value -> ((DonutMesh)mesh).setInnerRadius(value));
            row = addSliderToDimensionPanel(row, "Outer R:", outerRadiusSlider, outerRadiusValueLabel,
                    value -> ((DonutMesh)mesh).setOuterRadius(value));
        } else if (mesh instanceof TriangleMesh) {
            row = addSliderToDimensionPanel(row, "Width:", widthSlider, widthValueLabel,
                    value -> functions.updateWidth(value));
            row = addSliderToDimensionPanel(row, "Length:", lengthSlider, lengthValueLabel,
                    value -> functions.updateLength(value));
            row = addSliderToDimensionPanel(row, "Height:", heightSlider, heightValueLabel,
                    value -> functions.updateHeight(value));
            row = addSliderToDimensionPanelSpecial(row, "Slope:", slopeFactorSlider, slopeFactorValueLabel,
                    value -> ((TriangleMesh)mesh).setSlopeFactor(value));
        }

        dimensionPanel.revalidate();
        dimensionPanel.repaint();
    }

    private int addSliderToDimensionPanel(int row, String label, JSlider slider, JLabel valueLabel, SliderCallback callback) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = row;

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        slider.setPreferredSize(new Dimension(300, 40));

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
        rowPanel.add(valueLabel, rowGbc);

        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        dimensionPanel.add(rowPanel, gbc);

        for (var listener : slider.getChangeListeners()) slider.removeChangeListener(listener);
        slider.addChangeListener(e -> {
            float value = slider.getValue() / 100.0f;
            callback.onValueChanged(value);
            valueLabel.setText(String.format("%.2f", value));
            updateInfo();
        });

        return row + 1;
    }

    private int addSliderToDimensionPanelSpecial(int row, String label, JSlider slider, JLabel valueLabel, SliderCallback callback) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = row;

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        slider.setPreferredSize(new Dimension(300, 40));

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
        rowPanel.add(valueLabel, rowGbc);

        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        dimensionPanel.add(rowPanel, gbc);

        for (var listener : slider.getChangeListeners()) slider.removeChangeListener(listener);
        slider.addChangeListener(e -> {
            float value = slider.getValue() / 100.0f;
            callback.onValueChanged(value);
            valueLabel.setText(String.format("%.2f", value));
            updateInfo();
        });

        return row + 1;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("1.00");
        label.setFont(new Font("Monospaced", Font.PLAIN, 14));
        label.setPreferredSize(new Dimension(50, 20));
        return label;
    }

    private void addMesh(String type) {
        Mesh mesh;
        String name = type + " " + meshCounter++;

        if (type.equals("Cube")) {
            mesh = new CubeMesh(name);
        } else if (type.equals("Cylinder")) {
            mesh = new CylinderMesh(name);
        } else if (type.equals("Donut")) {
            mesh = new DonutMesh(name);
        } else {
            mesh = new TriangleMesh(name);
        }

        scene.addMesh(mesh);
        meshListModel.addElement(mesh);
        meshList.setSelectedValue(mesh, true);
    }

    private void duplicateMesh() {
        Mesh selected = meshList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a mesh to duplicate",
                    "No Mesh Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newName = selected.getType() + " " + meshCounter++;
        Mesh duplicate = selected.duplicate(newName);

        scene.addMesh(duplicate);
        meshListModel.addElement(duplicate);
        meshList.setSelectedValue(duplicate, true);
    }

    private void removeMesh() {
        Mesh selected = meshList.getSelectedValue();
        if (selected != null) {
            scene.removeMesh(selected);
            meshListModel.removeElement(selected);

            if (!meshListModel.isEmpty()) {
                meshList.setSelectedIndex(0);
            } else {
                updateControlsForSelectedMesh(null);
            }
        }
    }

    private void loadFromJSON() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load GLX JSON File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files (*.json)", "json"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            try {
                // Load meshes from JSON file
                List<Mesh> loadedMeshes = GLXReader.loadFromFile(selectedFile.getAbsolutePath());

                if (loadedMeshes.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No meshes found in the JSON file.",
                            "Empty File",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Ask if user wants to clear existing meshes
                int clearOption = JOptionPane.showConfirmDialog(this,
                        "Clear existing meshes before loading?\n" +
                                "Yes = Replace all\nNo = Add to existing\nCancel = Abort",
                        "Load Options",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (clearOption == JOptionPane.CANCEL_OPTION) {
                    return;
                }

                if (clearOption == JOptionPane.YES_OPTION) {
                    // Clear existing meshes
                    meshListModel.clear();
                    List<Mesh> currentMeshes = scene.getMeshes();
                    for (Mesh mesh : currentMeshes) {
                        scene.removeMesh(mesh);
                    }
                    meshCounter = 1;
                }

                // Add loaded meshes to scene and list
                for (Mesh mesh : loadedMeshes) {
                    // Rename mesh if needed
                    mesh.setName(mesh.getType() + " " + meshCounter++);
                    scene.addMesh(mesh);
                    meshListModel.addElement(mesh);
                }

                // Select first loaded mesh
                if (!meshListModel.isEmpty()) {
                    meshList.setSelectedIndex(clearOption == JOptionPane.YES_OPTION ? 0 : meshListModel.getSize() - loadedMeshes.size());
                }

                // Show success message
                JOptionPane.showMessageDialog(this,
                        "Successfully loaded " + loadedMeshes.size() + " mesh(es) from:\n" +
                                selectedFile.getName(),
                        "Load Successful",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error loading file:\n" + e.getMessage(),
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error parsing JSON:\n" + e.getMessage(),
                        "Parse Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void resetSelected() {
        Mesh selected = scene.getSelectedMesh();
        if (selected != null) {
            functions.resetAll();
            if (selected instanceof DonutMesh) {
                ((DonutMesh)selected).setInnerRadius(0.3f);
                ((DonutMesh)selected).setOuterRadius(0.7f);
            } else if (selected instanceof TriangleMesh) {
                ((TriangleMesh)selected).setSlopeFactor(1.0f);
            }
            updateControlsForSelectedMesh(selected);
        }
    }

    private void updateControlsForSelectedMesh(Mesh mesh) {
        if (mesh == null) {
            meshTypeLabel.setText("No mesh selected");
            volumeLabel.setText("Volume: -");
            surfaceAreaLabel.setText("Surface Area: -");
            dimensionPanel.removeAll();
            dimensionPanel.revalidate();
            dimensionPanel.repaint();
            return;
        }

        meshTypeLabel.setText(mesh.getName() + " (" + mesh.getType() + ")");

        removeAllListeners();

        xSlider.setValue((int)(mesh.getPositionX() * 100));
        ySlider.setValue((int)(mesh.getPositionY() * 100));
        zSlider.setValue((int)(mesh.getPositionZ() * 100));
        rxSlider.setValue((int)mesh.getRotationX());
        rySlider.setValue((int)mesh.getRotationY());
        rzSlider.setValue((int)mesh.getRotationZ());

        xValueLabel.setText(String.format("%.2f", mesh.getPositionX()));
        yValueLabel.setText(String.format("%.2f", mesh.getPositionY()));
        zValueLabel.setText(String.format("%.2f", mesh.getPositionZ()));
        rxValueLabel.setText(String.format("%.2f", mesh.getRotationX()));
        ryValueLabel.setText(String.format("%.2f", mesh.getRotationY()));
        rzValueLabel.setText(String.format("%.2f", mesh.getRotationZ()));

        if (mesh instanceof CubeMesh || mesh instanceof CylinderMesh || mesh instanceof TriangleMesh) {
            widthSlider.setValue((int)(mesh.getWidth() * 100));
            lengthSlider.setValue((int)(mesh.getLength() * 100));
            heightSlider.setValue((int)(mesh.getHeight() * 100));

            widthValueLabel.setText(String.format("%.2f", mesh.getWidth()));
            lengthValueLabel.setText(String.format("%.2f", mesh.getLength()));
            heightValueLabel.setText(String.format("%.2f", mesh.getHeight()));

            if (mesh instanceof TriangleMesh) {
                TriangleMesh triangle = (TriangleMesh) mesh;
                slopeFactorSlider.setValue((int)(triangle.getSlopeFactor() * 100));
                slopeFactorValueLabel.setText(String.format("%.2f", triangle.getSlopeFactor()));
            }
        } else if (mesh instanceof DonutMesh) {
            DonutMesh donut = (DonutMesh) mesh;
            innerRadiusSlider.setValue((int)(donut.getInnerRadius() * 100));
            outerRadiusSlider.setValue((int)(donut.getOuterRadius() * 100));

            innerRadiusValueLabel.setText(String.format("%.2f", donut.getInnerRadius()));
            outerRadiusValueLabel.setText(String.format("%.2f", donut.getOuterRadius()));
        }

        updateDimensionControls(mesh);
        addAllListeners();
        updateInfo();
    }

    private void removeAllListeners() {
        for (var listener : widthSlider.getChangeListeners()) widthSlider.removeChangeListener(listener);
        for (var listener : lengthSlider.getChangeListeners()) lengthSlider.removeChangeListener(listener);
        for (var listener : heightSlider.getChangeListeners()) heightSlider.removeChangeListener(listener);
        for (var listener : xSlider.getChangeListeners()) xSlider.removeChangeListener(listener);
        for (var listener : ySlider.getChangeListeners()) ySlider.removeChangeListener(listener);
        for (var listener : zSlider.getChangeListeners()) zSlider.removeChangeListener(listener);
        for (var listener : rxSlider.getChangeListeners()) rxSlider.removeChangeListener(listener);
        for (var listener : rySlider.getChangeListeners()) rySlider.removeChangeListener(listener);
        for (var listener : rzSlider.getChangeListeners()) rzSlider.removeChangeListener(listener);
    }

    private void addAllListeners() {
        widthSlider.addChangeListener(e -> {
            float value = widthSlider.getValue() / 100.0f;
            functions.updateWidth(value);
            widthValueLabel.setText(String.format("%.2f", value));
            updateInfo();
        });
        lengthSlider.addChangeListener(e -> {
            float value = lengthSlider.getValue() / 100.0f;
            functions.updateLength(value);
            lengthValueLabel.setText(String.format("%.2f", value));
            updateInfo();
        });
        heightSlider.addChangeListener(e -> {
            float value = heightSlider.getValue() / 100.0f;
            functions.updateHeight(value);
            heightValueLabel.setText(String.format("%.2f", value));
            updateInfo();
        });
        xSlider.addChangeListener(e -> {
            float value = xSlider.getValue() / 100.0f;
            functions.updatePositionX(value);
            xValueLabel.setText(String.format("%.2f", value));
        });
        ySlider.addChangeListener(e -> {
            float value = ySlider.getValue() / 100.0f;
            functions.updatePositionY(value);
            yValueLabel.setText(String.format("%.2f", value));
        });
        zSlider.addChangeListener(e -> {
            float value = zSlider.getValue() / 100.0f;
            functions.updatePositionZ(value);
            zValueLabel.setText(String.format("%.2f", value));
        });
        rxSlider.addChangeListener(e -> {
            float value = rxSlider.getValue();
            functions.updateRotationX(value);
            rxValueLabel.setText(String.format("%.2f", value));
        });
        rySlider.addChangeListener(e -> {
            float value = rySlider.getValue();
            functions.updateRotationY(value);
            ryValueLabel.setText(String.format("%.2f", value));
        });
        rzSlider.addChangeListener(e -> {
            float value = rzSlider.getValue();
            functions.updateRotationZ(value);
            rzValueLabel.setText(String.format("%.2f", value));
        });
    }

    private void updateInfo() {
        Mesh selected = scene.getSelectedMesh();
        if (selected != null) {
            volumeLabel.setText(String.format("Volume: %.2f", selected.getVolume()));
            surfaceAreaLabel.setText(String.format("Surface Area: %.2f", selected.getSurfaceArea()));
        }
    }

    @FunctionalInterface
    interface SliderCallback {
        void onValueChanged(float value);
    }
}