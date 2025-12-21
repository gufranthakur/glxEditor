package glx;

import glx.mesh.Mesh;
import glx.shape.PlaneShape;

public class Functions {
    private Scene scene;

    public Functions(Scene scene) {
        this.scene = scene;
    }

    public void updateWidth(float width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }
        scene.setWidth(width);
    }

    public void updateLength(float length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        scene.setLength(length);
    }

    public void updateHeight(float height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }
        scene.setHeight(height);
    }

    public void updatePositionX(float x) {
        scene.setPositionX(x);
    }

    public void updatePositionY(float y) {
        scene.setPositionY(y);
    }

    public void updatePositionZ(float z) {
        scene.setPositionZ(z);
    }

    public void updateDimensions(float width, float length, float height) {
        updateWidth(width);
        updateLength(length);
        updateHeight(height);
    }

    public void updatePosition(float x, float y, float z) {
        scene.setPositionX(x);
        scene.setPositionY(y);
        scene.setPositionZ(z);
    }

    public void scaleUniform(float scaleFactor) {
        if (scaleFactor <= 0) {
            throw new IllegalArgumentException("Scale factor must be positive");
        }
        scene.setWidth(scene.getWidth() * scaleFactor);
        scene.setLength(scene.getLength() * scaleFactor);
        scene.setHeight(scene.getHeight() * scaleFactor);
    }

    public void resetDimensions() {
        scene.setWidth(1.0f);
        scene.setLength(1.0f);
        scene.setHeight(1.0f);
    }

    public void resetPosition() {
        scene.setPositionX(0.0f);
        scene.setPositionY(0.0f);
        scene.setPositionZ(0.0f);
    }

    public void resetAll() {
        resetDimensions();
        resetPosition();
    }

    public float getWidth() {
        return scene.getWidth();
    }

    public float getLength() {
        return scene.getLength();
    }

    public float getHeight() {
        return scene.getHeight();
    }

    public float getPositionX() {
        return scene.getPositionX();
    }

    public float getPositionY() {
        return scene.getPositionY();
    }

    public float getPositionZ() {
        return scene.getPositionZ();
    }

    public float getVolume() {
        return scene.getWidth() * scene.getLength() * scene.getHeight();
    }

    public float getSurfaceArea() {
        float w = scene.getWidth();
        float l = scene.getLength();
        float h = scene.getHeight();
        return 2 * (w * l + w * h + l * h);
    }

    // Stage 2 functions
    public void addShapeToMesh(Mesh mesh, PlaneShape shape) {
        if (mesh != null) {
            mesh.addShape(shape);
        }
    }

    public void removeShapeFromMesh(Mesh mesh, PlaneShape shape) {
        if (mesh != null) {
            mesh.removeShape(shape);
        }
    }

    public void updateShapeX(PlaneShape shape, float x) {
        if (shape != null) {
            shape.setX(x);
        }
    }

    public void updateShapeY(PlaneShape shape, float y) {
        if (shape != null) {
            shape.setY(y);
        }
    }

    public void updateShapeSize(PlaneShape shape, float size) {
        if (shape != null && size > 0) {
            shape.setSize(size);
        }
    }

    public void updateShapePlane(PlaneShape shape, String plane) {
        if (shape != null) {
            shape.setPlane(plane);
        }
    }
}