package glx.shape;

import glx.mesh.Mesh;
import static org.lwjgl.opengl.GL11.*;

public class SquareShape extends PlaneShape {

    public SquareShape(String name, String plane) {
        super(name, plane);
    }

    @Override
    public void draw(Mesh mesh) {
        if (!intruded && !extruded) {
            float w = width / 2.0f;
            float h = height / 2.0f;

            glColor3f(0.3f, 0.3f, 0.35f);
            glDisable(GL_DEPTH_TEST);
            glBegin(GL_QUADS);

            switch (plane) {
                case "Front":
                    drawRotatedQuad(x, y, mesh.length / 2.0f + 0.02f, w, h, rotation, 0, 0, 1);
                    break;
                case "Back":
                    drawRotatedQuad(x, y, -mesh.length / 2.0f - 0.02f, w, h, rotation, 0, 0, -1);
                    break;
                case "Top":
                    drawRotatedQuad(x, mesh.height / 2.0f + 0.02f, y, w, h, rotation, 0, 1, 0);
                    break;
                case "Bottom":
                    drawRotatedQuad(x, -mesh.height / 2.0f - 0.02f, y, w, h, rotation, 0, -1, 0);
                    break;
                case "Right":
                    drawRotatedQuad(mesh.width / 2.0f + 0.02f, y, x, w, h, rotation, 1, 0, 0);
                    break;
                case "Left":
                    drawRotatedQuad(-mesh.width / 2.0f - 0.02f, y, x, w, h, rotation, -1, 0, 0);
                    break;
            }

            glEnd();
            glEnable(GL_DEPTH_TEST);
        }

        if (depth > 0 && intruded) {
            drawIntrusionBox(mesh);
        }

        if (depth > 0 && extruded) {
            drawExtrusionBox(mesh);
        }
    }

    private void drawRotatedQuad(float cx, float cy, float cz, float w, float h, float angle, int axisX, int axisY, int axisZ) {
        float rad = (float) Math.toRadians(angle);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float[][] corners = {
                {-w, -h}, {w, -h}, {w, h}, {-w, h}
        };

        for (float[] corner : corners) {
            float rx = corner[0] * cos - corner[1] * sin;
            float ry = corner[0] * sin + corner[1] * cos;

            if (axisX != 0) {
                glVertex3f(cx, cy + ry, cz + rx);
            } else if (axisY != 0) {
                glVertex3f(cx + rx, cy, cz + ry);
            } else {
                glVertex3f(cx + rx, cy + ry, cz);
            }
        }
    }

    private void drawIntrusionBox(Mesh mesh) {
        float w = width / 2.0f;
        float h = height / 2.0f;
        float d = depth;

        glDisable(GL_DEPTH_TEST);
        glColor3f(0.2f, 0.2f, 0.2f);

        switch (plane) {
            case "Front":
                drawBox(x, y, mesh.length / 2.0f, w, h, -d, rotation, 0, 0, 1);
                break;
            case "Back":
                drawBox(x, y, -mesh.length / 2.0f, w, h, d, rotation, 0, 0, -1);
                break;
            case "Top":
                drawBox(x, mesh.height / 2.0f, y, w, -d, h, rotation, 0, 1, 0);
                break;
            case "Bottom":
                drawBox(x, -mesh.height / 2.0f, y, w, d, h, rotation, 0, -1, 0);
                break;
            case "Right":
                drawBox(mesh.width / 2.0f, y, x, -d, h, w, rotation, 1, 0, 0);
                break;
            case "Left":
                drawBox(-mesh.width / 2.0f, y, x, d, h, w, rotation, -1, 0, 0);
                break;
        }

        glEnable(GL_DEPTH_TEST);
    }

    private void drawExtrusionBox(Mesh mesh) {
        float w = width / 2.0f;
        float h = height / 2.0f;
        float d = depth;

        glDisable(GL_DEPTH_TEST);
        glColor3f(0.2f, 0.2f, 0.2f);

        switch (plane) {
            case "Front":
                drawBox(x, y, mesh.length / 2.0f, w, h, d, rotation, 0, 0, 1);
                break;
            case "Back":
                drawBox(x, y, -mesh.length / 2.0f, w, h, -d, rotation, 0, 0, -1);
                break;
            case "Top":
                drawBox(x, mesh.height / 2.0f, y, w, d, h, rotation, 0, 1, 0);
                break;
            case "Bottom":
                drawBox(x, -mesh.height / 2.0f, y, w, -d, h, rotation, 0, -1, 0);
                break;
            case "Right":
                drawBox(mesh.width / 2.0f, y, x, d, h, w, rotation, 1, 0, 0);
                break;
            case "Left":
                drawBox(-mesh.width / 2.0f, y, x, -d, h, w, rotation, -1, 0, 0);
                break;
        }

        glEnable(GL_DEPTH_TEST);
    }

    private void drawBox(float cx, float cy, float cz, float w, float h, float d, float angle, int axisX, int axisY, int axisZ) {
        float rad = (float) Math.toRadians(angle);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);

        float[][] corners = {
                {-w, -h}, {w, -h}, {w, h}, {-w, h}
        };

        float[][] rotatedCorners = new float[4][2];
        for (int i = 0; i < 4; i++) {
            rotatedCorners[i][0] = corners[i][0] * cos - corners[i][1] * sin;
            rotatedCorners[i][1] = corners[i][0] * sin + corners[i][1] * cos;
        }

        glBegin(GL_QUADS);

        for (int i = 0; i < 4; i++) {
            int next = (i + 1) % 4;
            float rx1 = rotatedCorners[i][0];
            float ry1 = rotatedCorners[i][1];
            float rx2 = rotatedCorners[next][0];
            float ry2 = rotatedCorners[next][1];

            if (axisX != 0) {
                glVertex3f(cx, cy + ry1, cz + rx1);
                glVertex3f(cx + d, cy + ry1, cz + rx1);
                glVertex3f(cx + d, cy + ry2, cz + rx2);
                glVertex3f(cx, cy + ry2, cz + rx2);
            } else if (axisY != 0) {
                glVertex3f(cx + rx1, cy, cz + ry1);
                glVertex3f(cx + rx1, cy + d, cz + ry1);
                glVertex3f(cx + rx2, cy + d, cz + ry2);
                glVertex3f(cx + rx2, cy, cz + ry2);
            } else {
                glVertex3f(cx + rx1, cy + ry1, cz);
                glVertex3f(cx + rx1, cy + ry1, cz + d);
                glVertex3f(cx + rx2, cy + ry2, cz + d);
                glVertex3f(cx + rx2, cy + ry2, cz);
            }
        }

        for (int i = 0; i < 4; i++) {
            float rx = rotatedCorners[i][0];
            float ry = rotatedCorners[i][1];

            if (axisX != 0) {
                glVertex3f(cx + d, cy + ry, cz + rx);
            } else if (axisY != 0) {
                glVertex3f(cx + rx, cy + d, cz + ry);
            } else {
                glVertex3f(cx + rx, cy + ry, cz + d);
            }
        }

        glEnd();
    }
}