package glx.shape;

import glx.mesh.Mesh;

public abstract class PlaneShape {
    protected String name;
    protected String plane;
    protected float x = 0.0f;
    protected float y = 0.0f;
    protected float size = 0.3f;
    protected float depth = 0.0f;
    public boolean intruded = false;

    public PlaneShape(String name, String plane) {
        this.name = name;
        this.plane = plane;
    }

    public abstract void draw(Mesh mesh);

    public String getName() { return name; }
    public String getPlane() { return plane; }
    public void setPlane(String plane) { this.plane = plane; }
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getSize() { return size; }
    public void setSize(float size) { this.size = size; }
    public float getDepth() { return depth; }
    public void setDepth(float depth) { this.depth = depth; }

    @Override
    public String toString() {
        return name + " [" + plane + "]" + (intruded ? " [INTRUDED]" : "");
    }
}