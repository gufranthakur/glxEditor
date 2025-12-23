package glx.mesh;

import glx.shape.PlaneShape;
import static org.lwjgl.opengl.GL11.*;

public class CylinderMesh extends Mesh {
    private static final int SEGMENTS = 32;

    public CylinderMesh(String name) {
        super(name);
    }

    @Override
    public void draw() {
        float radius = width / 2.0f;
        float h = height / 2.0f;

        glColor3f(0.6f, 0.6f, 0.6f);

        glBegin(GL_TRIANGLE_FAN);
        glVertex3f(0, h, 0);
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = (float) (2 * Math.PI * i / SEGMENTS);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            glVertex3f(x, h, z);
        }
        glEnd();

        glBegin(GL_TRIANGLE_FAN);
        glVertex3f(0, -h, 0);
        for (int i = SEGMENTS; i >= 0; i--) {
            float angle = (float) (2 * Math.PI * i / SEGMENTS);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            glVertex3f(x, -h, z);
        }
        glEnd();

        glBegin(GL_QUAD_STRIP);
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = (float) (2 * Math.PI * i / SEGMENTS);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);

            glVertex3f(x, h, z);
            glVertex3f(x, -h, z);
        }
        glEnd();

        drawEdges(radius, h);

        for (PlaneShape shape : shapes) {
            shape.draw(this);
        }
    }

    private void drawEdges(float radius, float h) {
        glColor3f(0, 0, 0);
        glLineWidth(2);

        glBegin(GL_LINE_LOOP);
        for (int i = 0; i < SEGMENTS; i++) {
            float angle = (float) (2 * Math.PI * i / SEGMENTS);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            glVertex3f(x, h, z);
        }
        glEnd();

        glBegin(GL_LINE_LOOP);
        for (int i = 0; i < SEGMENTS; i++) {
            float angle = (float) (2 * Math.PI * i / SEGMENTS);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            glVertex3f(x, -h, z);
        }
        glEnd();

        glBegin(GL_LINES);
        for (int i = 0; i < 4; i++) {
            float angle = (float) (2 * Math.PI * i / 4);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            glVertex3f(x, h, z);
            glVertex3f(x, -h, z);
        }
        glEnd();
    }

    @Override
    public Mesh duplicate(String newName) {
        CylinderMesh copy = new CylinderMesh(newName);
        copy.width = this.width;
        copy.length = this.length;
        copy.height = this.height;
        copy.positionX = this.positionX;
        copy.positionY = this.positionY;
        copy.positionZ = this.positionZ;
        copy.rotationX = this.rotationX;
        copy.rotationY = this.rotationY;
        copy.rotationZ = this.rotationZ;
        return copy;
    }

    @Override
    public float getVolume() {
        float radius = width / 2.0f;
        return (float) (Math.PI * radius * radius * height);
    }

    @Override
    public float getSurfaceArea() {
        float radius = width / 2.0f;
        return (float) (2 * Math.PI * radius * height + 2 * Math.PI * radius * radius);
    }

    @Override
    public String getType() {
        return "Cylinder";
    }
}