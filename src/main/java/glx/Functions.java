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

    public void updateRotationX(float rx) {
        scene.setRotationX(rx);
    }

    public void updateRotationY(float ry) {
        scene.setRotationY(ry);
    }

    public void updateRotationZ(float rz) {
        scene.setRotationZ(rz);
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

    public void updateRotation(float rx, float ry, float rz) {
        scene.setRotationX(rx);
        scene.setRotationY(ry);
        scene.setRotationZ(rz);
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

    public void resetRotation() {
        scene.setRotationX(0.0f);
        scene.setRotationY(0.0f);
        scene.setRotationZ(0.0f);
    }

    public void resetAll() {
        resetDimensions();
        resetPosition();
        resetRotation();
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

    public float getRotationX() {
        return scene.getRotationX();
    }

    public float getRotationY() {
        return scene.getRotationY();
    }

    public float getRotationZ() {
        return scene.getRotationZ();
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

}