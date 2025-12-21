package glx.shape;

import glx.mesh.Mesh;
import static org.lwjgl.opengl.GL11.*;

public class SquareShape extends PlaneShape {

    public SquareShape(String name, String plane) {
        super(name, plane);
    }

    @Override
    public void draw(Mesh mesh) {
        if (!intruded) {
            float s = size / 2.0f;

            glColor3f(0.3f, 0.3f, 0.35f);
            glDisable(GL_DEPTH_TEST);
            glBegin(GL_QUADS);

            switch (plane) {
                case "Front":
                    glVertex3f(x - s, y - s, mesh.getLength() / 2.0f + 0.02f);
                    glVertex3f(x + s, y - s, mesh.getLength() / 2.0f + 0.02f);
                    glVertex3f(x + s, y + s, mesh.getLength() / 2.0f + 0.02f);
                    glVertex3f(x - s, y + s, mesh.getLength() / 2.0f + 0.02f);
                    break;
                case "Back":
                    glVertex3f(x - s, y - s, -mesh.getLength() / 2.0f - 0.02f);
                    glVertex3f(x - s, y + s, -mesh.getLength() / 2.0f - 0.02f);
                    glVertex3f(x + s, y + s, -mesh.getLength() / 2.0f - 0.02f);
                    glVertex3f(x + s, y - s, -mesh.getLength() / 2.0f - 0.02f);
                    break;
                case "Top":
                    glVertex3f(x - s, mesh.getHeight() / 2.0f + 0.02f, y - s);
                    glVertex3f(x - s, mesh.getHeight() / 2.0f + 0.02f, y + s);
                    glVertex3f(x + s, mesh.getHeight() / 2.0f + 0.02f, y + s);
                    glVertex3f(x + s, mesh.getHeight() / 2.0f + 0.02f, y - s);
                    break;
                case "Bottom":
                    glVertex3f(x - s, -mesh.getHeight() / 2.0f - 0.02f, y - s);
                    glVertex3f(x + s, -mesh.getHeight() / 2.0f - 0.02f, y - s);
                    glVertex3f(x + s, -mesh.getHeight() / 2.0f - 0.02f, y + s);
                    glVertex3f(x - s, -mesh.getHeight() / 2.0f - 0.02f, y + s);
                    break;
                case "Right":
                    glVertex3f(mesh.getWidth() / 2.0f + 0.02f, y - s, x - s);
                    glVertex3f(mesh.getWidth() / 2.0f + 0.02f, y + s, x - s);
                    glVertex3f(mesh.getWidth() / 2.0f + 0.02f, y + s, x + s);
                    glVertex3f(mesh.getWidth() / 2.0f + 0.02f, y - s, x + s);
                    break;
                case "Left":
                    glVertex3f(-mesh.getWidth() / 2.0f - 0.02f, y - s, x - s);
                    glVertex3f(-mesh.getWidth() / 2.0f - 0.02f, y - s, x + s);
                    glVertex3f(-mesh.getWidth() / 2.0f - 0.02f, y + s, x + s);
                    glVertex3f(-mesh.getWidth() / 2.0f - 0.02f, y + s, x - s);
                    break;
            }

            glEnd();
            glEnable(GL_DEPTH_TEST);
        }

        if (depth > 0 && intruded) {
            drawIntrusionCube(mesh);
        }
    }

    private void drawIntrusionCube(Mesh mesh) {
        float s = size / 2.0f;
        float d = depth;

        float x1, y1, z1, x2, y2, z2;

        switch (plane) {
            case "Front":
                x1 = x - s;
                x2 = x + s;
                y1 = y - s;
                y2 = y + s;
                z1 = mesh.getLength() / 2.0f;
                z2 = z1 - d;
                break;
            case "Back":
                x1 = x - s;
                x2 = x + s;
                y1 = y - s;
                y2 = y + s;
                z1 = -mesh.getLength() / 2.0f;
                z2 = z1 + d;
                break;
            case "Top":
                x1 = x - s;
                x2 = x + s;
                y1 = mesh.getHeight() / 2.0f;
                y2 = y1 - d;
                z1 = y - s;
                z2 = y + s;
                break;
            case "Bottom":
                x1 = x - s;
                x2 = x + s;
                y1 = -mesh.getHeight() / 2.0f;
                y2 = y1 + d;
                z1 = y - s;
                z2 = y + s;
                break;
            case "Right":
                x1 = mesh.getWidth() / 2.0f;
                x2 = x1 - d;
                y1 = y - s;
                y2 = y + s;
                z1 = x - s;
                z2 = x + s;
                break;
            case "Left":
                x1 = -mesh.getWidth() / 2.0f;
                x2 = x1 + d;
                y1 = y - s;
                y2 = y + s;
                z1 = x - s;
                z2 = x + s;
                break;
            default:
                return;
        }

        glDisable(GL_DEPTH_TEST);
        glColor3f(0.2f, 0.2f, 0.2f);
        glBegin(GL_QUADS);

        glVertex3f(x1, y1, z1);
        glVertex3f(x2, y1, z1);
        glVertex3f(x2, y2, z1);
        glVertex3f(x1, y2, z1);

        glVertex3f(x1, y1, z2);
        glVertex3f(x1, y2, z2);
        glVertex3f(x2, y2, z2);
        glVertex3f(x2, y1, z2);

        glVertex3f(x1, y1, z1);
        glVertex3f(x1, y2, z1);
        glVertex3f(x1, y2, z2);
        glVertex3f(x1, y1, z2);

        glVertex3f(x2, y1, z1);
        glVertex3f(x2, y1, z2);
        glVertex3f(x2, y2, z2);
        glVertex3f(x2, y2, z1);

        glVertex3f(x1, y1, z1);
        glVertex3f(x1, y1, z2);
        glVertex3f(x2, y1, z2);
        glVertex3f(x2, y1, z1);

        glVertex3f(x1, y2, z1);
        glVertex3f(x2, y2, z1);
        glVertex3f(x2, y2, z2);
        glVertex3f(x1, y2, z2);

        glEnd();

        glColor3f(0.15f, 0.15f, 0.15f);
        glLineWidth(2);
        glBegin(GL_LINES);

        glVertex3f(x1, y1, z1); glVertex3f(x2, y1, z1);
        glVertex3f(x2, y1, z1); glVertex3f(x2, y2, z1);
        glVertex3f(x2, y2, z1); glVertex3f(x1, y2, z1);
        glVertex3f(x1, y2, z1); glVertex3f(x1, y1, z1);

        glVertex3f(x1, y1, z2); glVertex3f(x2, y1, z2);
        glVertex3f(x2, y1, z2); glVertex3f(x2, y2, z2);
        glVertex3f(x2, y2, z2); glVertex3f(x1, y2, z2);
        glVertex3f(x1, y2, z2); glVertex3f(x1, y1, z2);

        glVertex3f(x1, y1, z1); glVertex3f(x1, y1, z2);
        glVertex3f(x2, y1, z1); glVertex3f(x2, y1, z2);
        glVertex3f(x2, y2, z1); glVertex3f(x2, y2, z2);
        glVertex3f(x1, y2, z1); glVertex3f(x1, y2, z2);

        glEnd();
        glEnable(GL_DEPTH_TEST);
    }
}