package glx.mesh;

import glx.shape.PlaneShape;
import static org.lwjgl.opengl.GL11.*;

public class TriangleMesh extends Mesh {
    public float slopeFactor = 1.0f;

    public TriangleMesh(String name) {
        super(name);
    }

    public float getSlopeFactor() {
        return slopeFactor;
    }

    public void setSlopeFactor(float slopeFactor) {
        this.slopeFactor = slopeFactor;
    }

    @Override
    public void draw() {
        float w = width / 2.0f;
        float l = length / 2.0f;
        float h = height;
        float offset = (slopeFactor - 0.5f) * width;

        glColor3f(0.6f, 0.6f, 0.6f);
        glBegin(GL_TRIANGLES);

        glVertex3f(-w, 0, l);
        glVertex3f(w, 0, l);
        glVertex3f(offset, h, l);

        glVertex3f(-w, 0, -l);
        glVertex3f(offset, h, -l);
        glVertex3f(w, 0, -l);

        glEnd();

        glBegin(GL_QUADS);

        glVertex3f(-w, 0, -l);
        glVertex3f(w, 0, -l);
        glVertex3f(w, 0, l);
        glVertex3f(-w, 0, l);

        glVertex3f(-w, 0, -l);
        glVertex3f(-w, 0, l);
        glVertex3f(offset, h, l);
        glVertex3f(offset, h, -l);

        glVertex3f(w, 0, -l);
        glVertex3f(offset, h, -l);
        glVertex3f(offset, h, l);
        glVertex3f(w, 0, l);

        glEnd();

        drawEdges(w, l, h, offset);

        for (PlaneShape shape : shapes) {
            shape.draw(this);
        }
    }

    private void drawEdges(float w, float l, float h, float offset) {
        glColor3f(0, 0, 0);
        glLineWidth(2);
        glBegin(GL_LINES);

        glVertex3f(-w, 0, -l);
        glVertex3f(w, 0, -l);

        glVertex3f(w, 0, -l);
        glVertex3f(w, 0, l);

        glVertex3f(w, 0, l);
        glVertex3f(-w, 0, l);

        glVertex3f(-w, 0, l);
        glVertex3f(-w, 0, -l);

        glVertex3f(-w, 0, -l);
        glVertex3f(offset, h, -l);

        glVertex3f(w, 0, -l);
        glVertex3f(offset, h, -l);

        glVertex3f(-w, 0, l);
        glVertex3f(offset, h, l);

        glVertex3f(w, 0, l);
        glVertex3f(offset, h, l);

        glVertex3f(offset, h, -l);
        glVertex3f(offset, h, l);

        glEnd();
    }

    @Override
    public Mesh duplicate(String newName) {
        TriangleMesh copy = new TriangleMesh(newName);
        copy.width = this.width;
        copy.length = this.length;
        copy.height = this.height;
        copy.positionX = this.positionX;
        copy.positionY = this.positionY;
        copy.positionZ = this.positionZ;
        copy.rotationX = this.rotationX;
        copy.rotationY = this.rotationY;
        copy.rotationZ = this.rotationZ;
        copy.slopeFactor = this.slopeFactor;
        return copy;
    }

    @Override
    public float getVolume() {
        return (width * length * height) / 2.0f;
    }

    @Override
    public float getSurfaceArea() {
        float baseArea = width * length;
        float triangleArea = (width * height);

        float leftSlope = (float)Math.sqrt(
                Math.pow((slopeFactor - 0.5f) * width + width / 2.0f, 2) +
                        Math.pow(height, 2)
        );
        float rightSlope = (float)Math.sqrt(
                Math.pow(width / 2.0f - (slopeFactor - 0.5f) * width, 2) +
                        Math.pow(height, 2)
        );

        float slopeArea = (leftSlope + rightSlope) * length;

        return baseArea + triangleArea + slopeArea;
    }

    @Override
    public String getType() {
        return "Triangle";
    }
}