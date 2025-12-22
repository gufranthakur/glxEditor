package glx.shape;

import glx.mesh.Mesh;

public abstract class PlaneShape {
    public String name;
    public String plane;
    public float x = 0.0f;
    public float y = 0.0f;
    public float width = 0.3f;
    public float height = 0.3f;
    public float radius = 0.15f;
    public float rotation = 0.0f;
    public float depth = 0.0f;
    public boolean intruded = false;
    public boolean extruded = false;

    public PlaneShape(String name, String plane) {
        this.name = name;
        this.plane = plane;
    }

    public abstract void draw(Mesh mesh);

    @Override
    public String toString() {
        String suffix = "";
        if (intruded) suffix = " [INTRUDED]";
        if (extruded) suffix = " [EXTRUDED]";
        return name + " [" + plane + "]" + suffix;
    }
}