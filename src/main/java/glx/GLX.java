package glx;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import glx.stage.Stage1Panel;
import glx.stage.Stage2Panel;
import glx.stage.Stage3Panel;
import glx.stage.Stage4Panel;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import javax.swing.*;
import java.awt.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class GLX {
    private long window;
    private Scene scene;
    private Functions functions;
    private JFrame controlFrame;
    private Frame glfwFrame;
    private double lastMouseX;
    private double lastMouseY;
    private boolean isDragging = false;
    private boolean isGizmoDragging = false;

    public void run() {
        scene = new Scene();
        functions = new Functions(scene);

        SwingUtilities.invokeLater(this::createControlPanel);

        init();
        loop();
        cleanup();
    }

    private void createControlPanel() {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        controlFrame = new JFrame("Mesh Controls");
        controlFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Stage 1", new Stage1Panel(functions, scene));
        tabbedPane.addTab("Stage 2", new Stage2Panel(scene));
        tabbedPane.addTab("Stage 3", new Stage3Panel(scene));
        tabbedPane.addTab("Stage 4", new Stage4Panel(scene));
        tabbedPane.addTab("Output", new OutputPanel(scene));

        controlFrame.add(tabbedPane);
        controlFrame.pack();

        if (glfwFrame != null) {
            Point glfwLoc = glfwFrame.getLocation();
            int glfwWidth = glfwFrame.getWidth();
            controlFrame.setLocation(glfwLoc.x + glfwWidth + 10, glfwLoc.y);
        }

        controlFrame.setVisible(true);

        controlFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                glfwSetWindowShouldClose(window, true);
            }
        });
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);

        window = glfwCreateWindow(800, 600, "LWJGL3 Mesh Visualizer", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetMouseButtonCallback(window, (win, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                if (action == GLFW_PRESS) {
                    double[] xpos = new double[1];
                    double[] ypos = new double[1];
                    glfwGetCursorPos(window, xpos, ypos);

                    int[] width = new int[1];
                    int[] height = new int[1];
                    glfwGetWindowSize(window, width, height);

                    int hitAxis = scene.checkGizmoHit(xpos[0], ypos[0], width[0], height[0]);

                    if (hitAxis != -1) {
                        isGizmoDragging = true;
                        scene.draggedAxis = hitAxis;
                        scene.dragStartX = (float)xpos[0];
                        scene.dragStartY = (float)ypos[0];

                        if (scene.getSelectedMesh() != null) {
                            scene.meshStartX = scene.getSelectedMesh().getPositionX();
                            scene.meshStartY = scene.getSelectedMesh().getPositionY();
                            scene.meshStartZ = scene.getSelectedMesh().getPositionZ();
                        }
                    } else {
                        isDragging = true;
                        lastMouseX = xpos[0];
                        lastMouseY = ypos[0];
                    }
                } else if (action == GLFW_RELEASE) {
                    isDragging = false;
                    isGizmoDragging = false;
                    scene.draggedAxis = -1;
                }
            }
        });

        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            if (isGizmoDragging) {
                int[] width = new int[1];
                int[] height = new int[1];
                glfwGetWindowSize(window, width, height);

                scene.updateMeshPosition(xpos, ypos, width[0], height[0]);
            } else if (isDragging) {
                double deltaX = xpos - lastMouseX;
                double deltaY = ypos - lastMouseY;

                scene.cameraRotationY += (float) deltaX * 0.5f;
                scene.cameraRotationX += (float) deltaY * 0.5f;

                scene.cameraRotationX = Math.max(-89.0f, Math.min(89.0f, scene.cameraRotationX));

                lastMouseX = xpos;
                lastMouseY = ypos;
            }
        });

        glfwSetScrollCallback(window, (win, xoffset, yoffset) -> {
            scene.cameraDistance -= (float) yoffset * 0.5f;
            scene.cameraDistance = Math.max(3.0f, Math.min(15.0f, scene.cameraDistance));
        });

        glfwShowWindow(window);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glClearColor(0.1f, 0.1f, 0.15f, 1.0f);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            int[] width = new int[1];
            int[] height = new int[1];
            glfwGetFramebufferSize(window, width, height);

            scene.render(width[0], height[0]);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        if (controlFrame != null) {
            SwingUtilities.invokeLater(() -> controlFrame.dispose());
        }

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();

        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }
    }

    public static void main(String[] args) {
        new GLX().run();
    }
}