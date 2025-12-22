package glx.json;

public class CubeSizeData {
    private float h;
    private float w;
    private float l;

    public CubeSizeData(float height, float width, float length) {
        this.h = height;
        this.w = width;
        this.l = length;
    }

    public float getH() { return h; }
    public float getW() { return w; }
    public float getL() { return l; }
}