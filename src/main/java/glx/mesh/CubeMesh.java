package glx.mesh;

import glx.shape.PlaneShape;
import static org.lwjgl.opengl.GL11.*;

public class CubeMesh extends Mesh {

    public CubeMesh(String name) {
        super(name);
    }

    @Override
    public void draw() {
        float w = width / 2.0f;
        float l = length / 2.0f;
        float h = height / 2.0f;

        glColor3f(0.6f, 0.6f, 0.6f);
        glBegin(GL_QUADS);

        glVertex3f(-w, -h, l);
        glVertex3f(w, -h, l);
        glVertex3f(w, h, l);
        glVertex3f(-w, h, l);

        glVertex3f(-w, -h, -l);
        glVertex3f(-w, h, -l);
        glVertex3f(w, h, -l);
        glVertex3f(w, -h, -l);

        glVertex3f(-w, h, -l);
        glVertex3f(-w, h, l);
        glVertex3f(w, h, l);
        glVertex3f(w, h, -l);

        glVertex3f(-w, -h, -l);
        glVertex3f(w, -h, -l);
        glVertex3f(w, -h, l);
        glVertex3f(-w, -h, l);

        glVertex3f(w, -h, -l);
        glVertex3f(w, h, -l);
        glVertex3f(w, h, l);
        glVertex3f(w, -h, l);

        glVertex3f(-w, -h, -l);
        glVertex3f(-w, -h, l);
        glVertex3f(-w, h, l);
        glVertex3f(-w, h, -l);

        glEnd();

        drawEdges(w, l, h);

        for (PlaneShape shape : shapes) {
            shape.draw(this);
        }
    }

    private void drawEdges(float w, float l, float h) {
        glColor3f(0, 0, 0);
        glLineWidth(2);
        glBegin(GL_LINES);

        glVertex3f(-w, -h, -l); glVertex3f(w, -h, -l);
        glVertex3f(w, -h, -l); glVertex3f(w, -h, l);
        glVertex3f(w, -h, l); glVertex3f(-w, -h, l);
        glVertex3f(-w, -h, l); glVertex3f(-w, -h, -l);

        glVertex3f(-w, h, -l); glVertex3f(w, h, -l);
        glVertex3f(w, h, -l); glVertex3f(w, h, l);
        glVertex3f(w, h, l); glVertex3f(-w, h, l);
        glVertex3f(-w, h, l); glVertex3f(-w, h, -l);

        glVertex3f(-w, -h, -l); glVertex3f(-w, h, -l);
        glVertex3f(w, -h, -l); glVertex3f(w, h, -l);
        glVertex3f(w, -h, l); glVertex3f(w, h, l);
        glVertex3f(-w, -h, l); glVertex3f(-w, h, l);

        glEnd();
    }

    @Override
    public float getVolume() {
        return width * length * height;
    }

    @Override
    public float getSurfaceArea() {
        return 2 * (width * length + width * height + length * height);
    }

    @Override
    public String getType() {
        return "Cube";
    }
}