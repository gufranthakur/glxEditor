package glx.mesh;

import glx.shape.PlaneShape;
import static org.lwjgl.opengl.GL11.*;

public class DonutMesh extends Mesh {
    private static final int MAJOR_SEGMENTS = 32;
    private static final int MINOR_SEGMENTS = 16;

    public float innerRadius = 0.3f;
    public float outerRadius = 0.7f;

    public DonutMesh(String name) {
        super(name);
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(float innerRadius) {
        this.innerRadius = innerRadius;
    }

    public float getOuterRadius() {
        return outerRadius;
    }

    public void setOuterRadius(float outerRadius) {
        this.outerRadius = outerRadius;
    }

    @Override
    public void draw() {
        float majorRadius = (outerRadius + innerRadius) / 2.0f;
        float minorRadius = (outerRadius - innerRadius) / 2.0f;

        glColor3f(0.6f, 0.6f, 0.6f);

        for (int i = 0; i < MAJOR_SEGMENTS; i++) {
            glBegin(GL_QUAD_STRIP);
            for (int j = 0; j <= MINOR_SEGMENTS; j++) {
                for (int k = 0; k <= 1; k++) {
                    float u = (float)(i + k) / MAJOR_SEGMENTS * 2.0f * (float)Math.PI;
                    float v = (float)j / MINOR_SEGMENTS * 2.0f * (float)Math.PI;

                    float x = (majorRadius + minorRadius * (float)Math.cos(v)) * (float)Math.cos(u);
                    float y = minorRadius * (float)Math.sin(v);
                    float z = (majorRadius + minorRadius * (float)Math.cos(v)) * (float)Math.sin(u);

                    glVertex3f(x, y, z);
                }
            }
            glEnd();
        }

        drawEdges(majorRadius, minorRadius);

        for (PlaneShape shape : shapes) {
            shape.draw(this);
        }
    }

    private void drawEdges(float majorRadius, float minorRadius) {
        glColor3f(0, 0, 0);
        glLineWidth(2);

        for (int i = 0; i < MAJOR_SEGMENTS; i += 4) {
            glBegin(GL_LINE_LOOP);
            for (int j = 0; j < MINOR_SEGMENTS; j++) {
                float u = (float)i / MAJOR_SEGMENTS * 2.0f * (float)Math.PI;
                float v = (float)j / MINOR_SEGMENTS * 2.0f * (float)Math.PI;

                float x = (majorRadius + minorRadius * (float)Math.cos(v)) * (float)Math.cos(u);
                float y = minorRadius * (float)Math.sin(v);
                float z = (majorRadius + minorRadius * (float)Math.cos(v)) * (float)Math.sin(u);

                glVertex3f(x, y, z);
            }
            glEnd();
        }

        for (int j = 0; j < MINOR_SEGMENTS; j += 4) {
            glBegin(GL_LINE_LOOP);
            for (int i = 0; i < MAJOR_SEGMENTS; i++) {
                float u = (float)i / MAJOR_SEGMENTS * 2.0f * (float)Math.PI;
                float v = (float)j / MINOR_SEGMENTS * 2.0f * (float)Math.PI;

                float x = (majorRadius + minorRadius * (float)Math.cos(v)) * (float)Math.cos(u);
                float y = minorRadius * (float)Math.sin(v);
                float z = (majorRadius + minorRadius * (float)Math.cos(v)) * (float)Math.sin(u);

                glVertex3f(x, y, z);
            }
            glEnd();
        }
    }

    @Override
    public Mesh duplicate(String newName) {
        DonutMesh copy = new DonutMesh(newName);
        copy.width = this.width;
        copy.length = this.length;
        copy.height = this.height;
        copy.positionX = this.positionX;
        copy.positionY = this.positionY;
        copy.positionZ = this.positionZ;
        copy.rotationX = this.rotationX;
        copy.rotationY = this.rotationY;
        copy.rotationZ = this.rotationZ;
        copy.innerRadius = this.innerRadius;
        copy.outerRadius = this.outerRadius;
        return copy;
    }

    @Override
    public float getVolume() {
        float majorRadius = (outerRadius + innerRadius) / 2.0f;
        float minorRadius = (outerRadius - innerRadius) / 2.0f;
        return (float)(2 * Math.PI * Math.PI * majorRadius * minorRadius * minorRadius);
    }

    @Override
    public float getSurfaceArea() {
        float majorRadius = (outerRadius + innerRadius) / 2.0f;
        float minorRadius = (outerRadius - innerRadius) / 2.0f;
        return (float)(4 * Math.PI * Math.PI * majorRadius * minorRadius);
    }

    @Override
    public String getType() {
        return "Donut";
    }
}