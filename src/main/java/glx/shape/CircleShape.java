package glx.shape;

import glx.mesh.Mesh;
import static org.lwjgl.opengl.GL11.*;

public class CircleShape extends PlaneShape {
    private static final int SEGMENTS = 32;

    public CircleShape(String name, String plane) {
        super(name, plane);
    }

    @Override
    public void draw(Mesh mesh) {
        if (!intruded) {
            float r = size / 2.0f;

            glColor3f(0.3f, 0.3f, 0.35f);
            glDisable(GL_DEPTH_TEST);
            glBegin(GL_TRIANGLE_FAN);

            switch (plane) {
                case "Front":
                    glVertex3f(x, y, mesh.getLength() / 2.0f + 0.02f);
                    for (int i = 0; i <= SEGMENTS; i++) {
                        float angle = (float) (2 * Math.PI * i / SEGMENTS);
                        glVertex3f(x + r * (float) Math.cos(angle), y + r * (float) Math.sin(angle), mesh.getLength() / 2.0f + 0.02f);
                    }
                    break;
                case "Back":
                    glVertex3f(x, y, -mesh.getLength() / 2.0f - 0.02f);
                    for (int i = SEGMENTS; i >= 0; i--) {
                        float angle = (float) (2 * Math.PI * i / SEGMENTS);
                        glVertex3f(x + r * (float) Math.cos(angle), y + r * (float) Math.sin(angle), -mesh.getLength() / 2.0f - 0.02f);
                    }
                    break;
                case "Top":
                    glVertex3f(x, mesh.getHeight() / 2.0f + 0.02f, y);
                    for (int i = 0; i <= SEGMENTS; i++) {
                        float angle = (float) (2 * Math.PI * i / SEGMENTS);
                        glVertex3f(x + r * (float) Math.cos(angle), mesh.getHeight() / 2.0f + 0.02f, y + r * (float) Math.sin(angle));
                    }
                    break;
                case "Bottom":
                    glVertex3f(x, -mesh.getHeight() / 2.0f - 0.02f, y);
                    for (int i = SEGMENTS; i >= 0; i--) {
                        float angle = (float) (2 * Math.PI * i / SEGMENTS);
                        glVertex3f(x + r * (float) Math.cos(angle), -mesh.getHeight() / 2.0f - 0.02f, y + r * (float) Math.sin(angle));
                    }
                    break;
                case "Right":
                    glVertex3f(mesh.getWidth() / 2.0f + 0.02f, y, x);
                    for (int i = 0; i <= SEGMENTS; i++) {
                        float angle = (float) (2 * Math.PI * i / SEGMENTS);
                        glVertex3f(mesh.getWidth() / 2.0f + 0.02f, y + r * (float) Math.cos(angle), x + r * (float) Math.sin(angle));
                    }
                    break;
                case "Left":
                    glVertex3f(-mesh.getWidth() / 2.0f - 0.02f, y, x);
                    for (int i = SEGMENTS; i >= 0; i--) {
                        float angle = (float) (2 * Math.PI * i / SEGMENTS);
                        glVertex3f(-mesh.getWidth() / 2.0f - 0.02f, y + r * (float) Math.cos(angle), x + r * (float) Math.sin(angle));
                    }
                    break;
            }

            glEnd();
            glEnable(GL_DEPTH_TEST);
        }

        if (depth > 0 && intruded) {
            drawIntrusionCylinder(mesh);
        }
    }

    private void drawIntrusionCylinder(Mesh mesh) {
        float r = size / 2.0f;
        float d = depth;

        glDisable(GL_DEPTH_TEST);
        glColor3f(0.2f, 0.2f, 0.2f);

        switch (plane) {
            case "Front":
                float zFront = mesh.getLength() / 2.0f;
                glBegin(GL_QUAD_STRIP);
                for (int i = 0; i <= SEGMENTS; i++) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    float cx = x + r * (float) Math.cos(angle);
                    float cy = y + r * (float) Math.sin(angle);
                    glVertex3f(cx, cy, zFront);
                    glVertex3f(cx, cy, zFront - d);
                }
                glEnd();

                glBegin(GL_TRIANGLE_FAN);
                glVertex3f(x, y, zFront - d);
                for (int i = 0; i <= SEGMENTS; i++) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    glVertex3f(x + r * (float) Math.cos(angle), y + r * (float) Math.sin(angle), zFront - d);
                }
                glEnd();
                break;

            case "Back":
                float zBack = -mesh.getLength() / 2.0f;
                glBegin(GL_QUAD_STRIP);
                for (int i = 0; i <= SEGMENTS; i++) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    float cx = x + r * (float) Math.cos(angle);
                    float cy = y + r * (float) Math.sin(angle);
                    glVertex3f(cx, cy, zBack);
                    glVertex3f(cx, cy, zBack + d);
                }
                glEnd();

                glBegin(GL_TRIANGLE_FAN);
                glVertex3f(x, y, zBack + d);
                for (int i = SEGMENTS; i >= 0; i--) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    glVertex3f(x + r * (float) Math.cos(angle), y + r * (float) Math.sin(angle), zBack + d);
                }
                glEnd();
                break;

            case "Top":
                float yTop = mesh.getHeight() / 2.0f;
                glBegin(GL_QUAD_STRIP);
                for (int i = 0; i <= SEGMENTS; i++) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    float cx = x + r * (float) Math.cos(angle);
                    float cz = y + r * (float) Math.sin(angle);
                    glVertex3f(cx, yTop, cz);
                    glVertex3f(cx, yTop - d, cz);
                }
                glEnd();

                glBegin(GL_TRIANGLE_FAN);
                glVertex3f(x, yTop - d, y);
                for (int i = 0; i <= SEGMENTS; i++) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    glVertex3f(x + r * (float) Math.cos(angle), yTop - d, y + r * (float) Math.sin(angle));
                }
                glEnd();
                break;

            case "Bottom":
                float yBottom = -mesh.getHeight() / 2.0f;
                glBegin(GL_QUAD_STRIP);
                for (int i = 0; i <= SEGMENTS; i++) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    float cx = x + r * (float) Math.cos(angle);
                    float cz = y + r * (float) Math.sin(angle);
                    glVertex3f(cx, yBottom, cz);
                    glVertex3f(cx, yBottom + d, cz);
                }
                glEnd();

                glBegin(GL_TRIANGLE_FAN);
                glVertex3f(x, yBottom + d, y);
                for (int i = SEGMENTS; i >= 0; i--) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    glVertex3f(x + r * (float) Math.cos(angle), yBottom + d, y + r * (float) Math.sin(angle));
                }
                glEnd();
                break;

            case "Right":
                float xRight = mesh.getWidth() / 2.0f;
                glBegin(GL_QUAD_STRIP);
                for (int i = 0; i <= SEGMENTS; i++) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    float cy = y + r * (float) Math.cos(angle);
                    float cz = x + r * (float) Math.sin(angle);
                    glVertex3f(xRight, cy, cz);
                    glVertex3f(xRight - d, cy, cz);
                }
                glEnd();

                glBegin(GL_TRIANGLE_FAN);
                glVertex3f(xRight - d, y, x);
                for (int i = 0; i <= SEGMENTS; i++) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    glVertex3f(xRight - d, y + r * (float) Math.cos(angle), x + r * (float) Math.sin(angle));
                }
                glEnd();
                break;

            case "Left":
                float xLeft = -mesh.getWidth() / 2.0f;
                glBegin(GL_QUAD_STRIP);
                for (int i = 0; i <= SEGMENTS; i++) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    float cy = y + r * (float) Math.cos(angle);
                    float cz = x + r * (float) Math.sin(angle);
                    glVertex3f(xLeft, cy, cz);
                    glVertex3f(xLeft + d, cy, cz);
                }
                glEnd();

                glBegin(GL_TRIANGLE_FAN);
                glVertex3f(xLeft + d, y, x);
                for (int i = SEGMENTS; i >= 0; i--) {
                    float angle = (float) (2 * Math.PI * i / SEGMENTS);
                    glVertex3f(xLeft + d, y + r * (float) Math.cos(angle), x + r * (float) Math.sin(angle));
                }
                glEnd();
                break;
        }

        glColor3f(0.15f, 0.15f, 0.15f);
        glLineWidth(1);
        glBegin(GL_LINES);

        for (int i = 0; i < 4; i++) {
            float angle = (float) (2 * Math.PI * i / 4);
            float cx = x + r * (float) Math.cos(angle);
            float cy = y + r * (float) Math.sin(angle);

            switch (plane) {
                case "Front":
                    glVertex3f(cx, cy, mesh.getLength() / 2.0f);
                    glVertex3f(cx, cy, mesh.getLength() / 2.0f - d);
                    break;
                case "Back":
                    glVertex3f(cx, cy, -mesh.getLength() / 2.0f);
                    glVertex3f(cx, cy, -mesh.getLength() / 2.0f + d);
                    break;
                case "Top":
                    glVertex3f(cx, mesh.getHeight() / 2.0f, cy);
                    glVertex3f(cx, mesh.getHeight() / 2.0f - d, cy);
                    break;
                case "Bottom":
                    glVertex3f(cx, -mesh.getHeight() / 2.0f, cy);
                    glVertex3f(cx, -mesh.getHeight() / 2.0f + d, cy);
                    break;
                case "Right":
                    glVertex3f(mesh.getWidth() / 2.0f, cy, cx);
                    glVertex3f(mesh.getWidth() / 2.0f - d, cy, cx);
                    break;
                case "Left":
                    glVertex3f(-mesh.getWidth() / 2.0f, cy, cx);
                    glVertex3f(-mesh.getWidth() / 2.0f + d, cy, cx);
                    break;
            }
        }

        glEnd();
        glEnable(GL_DEPTH_TEST);
    }
}