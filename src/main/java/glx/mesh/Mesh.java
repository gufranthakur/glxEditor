package glx.mesh;

import glx.shape.PlaneShape;

public abstract class Mesh {
    protected String name;
    protected float width = 1.0f;
    protected float length = 1.0f;
    protected float height = 1.0f;
    protected float positionX = 0.0f;
    protected float positionY = 0.0f;
    protected float positionZ = 0.0f;
    protected java.util.List<PlaneShape> shapes = new java.util.ArrayList<>();

    public Mesh(String name) {
        this.name = name;
    }

    public abstract void draw();
    public abstract float getVolume();
    public abstract float getSurfaceArea();
    public abstract String getType();

    public void addShape(PlaneShape shape) { shapes.add(shape); }
    public void removeShape(PlaneShape shape) { shapes.remove(shape); }
    public java.util.List<PlaneShape> getShapes() { return new java.util.ArrayList<>(shapes); }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }

    public float getLength() { return length; }
    public void setLength(float length) { this.length = length; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public float getPositionX() { return positionX; }
    public void setPositionX(float x) { this.positionX = x; }

    public float getPositionY() { return positionY; }
    public void setPositionY(float y) { this.positionY = y; }

    public float getPositionZ() { return positionZ; }
    public void setPositionZ(float z) { this.positionZ = z; }

    @Override
    public String toString() {
        return name + " (" + getType() + ")";
    }
}