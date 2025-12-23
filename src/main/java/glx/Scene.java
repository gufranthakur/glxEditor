package glx;

import glx.mesh.Mesh;

import static org.lwjgl.opengl.GL11.*;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private List<Mesh> meshes = new ArrayList<>();
    private Mesh selectedMesh = null;
    public float cameraRotationY = 45.0f;
    public float cameraRotationX = 30.0f;
    public float cameraDistance = 6.0f;

    public int draggedAxis = -1;
    public float dragStartX = 0;
    public float dragStartY = 0;
    public float meshStartX = 0;
    public float meshStartY = 0;
    public float meshStartZ = 0;

    public synchronized void addMesh(Mesh mesh) {
        meshes.add(mesh);
        if (selectedMesh == null) {
            selectedMesh = mesh;
        }
    }

    public synchronized void removeMesh(Mesh mesh) {
        meshes.remove(mesh);
        if (selectedMesh == mesh) {
            selectedMesh = meshes.isEmpty() ? null : meshes.get(0);
        }
    }

    public synchronized List<Mesh> getMeshes() {
        return new ArrayList<>(meshes);
    }

    public Mesh getSelectedMesh() {
        return selectedMesh;
    }

    public void setSelectedMesh(Mesh mesh) {
        if (meshes.contains(mesh)) {
            this.selectedMesh = mesh;
        }
    }

    public void setWidth(float width) {
        if (selectedMesh != null) selectedMesh.setWidth(width);
    }

    public void setLength(float length) {
        if (selectedMesh != null) selectedMesh.setLength(length);
    }

    public void setHeight(float height) {
        if (selectedMesh != null) selectedMesh.setHeight(height);
    }

    public void setPositionX(float x) {
        if (selectedMesh != null) selectedMesh.setPositionX(x);
    }

    public void setPositionY(float y) {
        if (selectedMesh != null) selectedMesh.setPositionY(y);
    }

    public void setPositionZ(float z) {
        if (selectedMesh != null) selectedMesh.setPositionZ(z);
    }

    public void setRotationX(float rx) {
        if (selectedMesh != null) selectedMesh.setRotationX(rx);
    }

    public void setRotationY(float ry) {
        if (selectedMesh != null) selectedMesh.setRotationY(ry);
    }

    public void setRotationZ(float rz) {
        if (selectedMesh != null) selectedMesh.setRotationZ(rz);
    }

    public float getWidth() {
        return selectedMesh != null ? selectedMesh.getWidth() : 1.0f;
    }

    public float getLength() {
        return selectedMesh != null ? selectedMesh.getLength() : 1.0f;
    }

    public float getHeight() {
        return selectedMesh != null ? selectedMesh.getHeight() : 1.0f;
    }

    public float getPositionX() {
        return selectedMesh != null ? selectedMesh.getPositionX() : 0.0f;
    }

    public float getPositionY() {
        return selectedMesh != null ? selectedMesh.getPositionY() : 0.0f;
    }

    public float getPositionZ() {
        return selectedMesh != null ? selectedMesh.getPositionZ() : 0.0f;
    }

    public float getRotationX() {
        return selectedMesh != null ? selectedMesh.getRotationX() : 0.0f;
    }

    public float getRotationY() {
        return selectedMesh != null ? selectedMesh.getRotationY() : 0.0f;
    }

    public float getRotationZ() {
        return selectedMesh != null ? selectedMesh.getRotationZ() : 0.0f;
    }

    public void render(int windowWidth, int windowHeight) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glViewport(0, 0, windowWidth, windowHeight);
        float aspect = (float) windowWidth / windowHeight;
        glFrustum(-aspect, aspect, -1, 1, 2, 20);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0.0f, 0.0f, -cameraDistance);

        glRotatef(cameraRotationX, 1.0f, 0.0f, 0.0f);
        glRotatef(cameraRotationY, 0.0f, 1.0f, 0.0f);

        List<Mesh> meshesToRender;
        synchronized (this) {
            meshesToRender = new ArrayList<>(meshes);
        }

        for (Mesh mesh : meshesToRender) {
            glPushMatrix();
            glTranslatef(mesh.getPositionX(), mesh.getPositionY(), mesh.getPositionZ());

            glRotatef(mesh.getRotationX(), 1.0f, 0.0f, 0.0f);
            glRotatef(mesh.getRotationY(), 0.0f, 1.0f, 0.0f);
            glRotatef(mesh.getRotationZ(), 0.0f, 0.0f, 1.0f);

            if (mesh == selectedMesh) {
                drawSelectionBox(mesh);
            }

            mesh.draw();
            glPopMatrix();
        }

        if (selectedMesh != null) {
            drawGizmo(selectedMesh);
        }
    }

    private void drawSelectionBox(Mesh mesh) {
        float w = mesh.getWidth() / 2.0f + 0.1f;
        float l = mesh.getLength() / 2.0f + 0.1f;
        float h = mesh.getHeight() / 2.0f + 0.1f;

        glColor3f(1, 1, 0);
        glLineWidth(3);
        glBegin(GL_LINES);

        glVertex3f(-w, -h, -l); glVertex3f(w, -h, -l);
        glVertex3f(w, -h, -l); glVertex3f(w, -h, l);
        glVertex3f(w, -h, l); glVertex3f(-w, -h, l);
        glVertex3f(-w, -h, l); glVertex3f(-w, -h, -l);

        glVertex3f(-w, h, -l); glVertex3f(w, h, -l);
        glVertex3f(w, h, -l); glVertex3f(w, h, l);
        glVertex3f(w, h, l); glVertex3f(-w, h, l);
        glVertex3f(-w, h, l); glVertex3f(-w, h, -l);

        glVertex3f(-w, -h, -l); glVertex3f(-w, h, -l);
        glVertex3f(w, -h, -l); glVertex3f(w, h, -l);
        glVertex3f(w, -h, l); glVertex3f(w, h, l);
        glVertex3f(-w, -h, l); glVertex3f(-w, h, l);

        glEnd();
    }

    private void drawGizmo(Mesh mesh) {
        glPushMatrix();
        glTranslatef(mesh.getPositionX(), mesh.getPositionY(), mesh.getPositionZ());

        float axisLength = 1.0f;
        float arrowSize = 0.15f;

        glDisable(GL_DEPTH_TEST);
        glLineWidth(4);

        glBegin(GL_LINES);
        if (draggedAxis == 0) {
            glColor3f(1, 1, 0);
        } else {
            glColor3f(1, 0, 0);
        }
        glVertex3f(0, 0, 0);
        glVertex3f(axisLength, 0, 0);

        if (draggedAxis == 1) {
            glColor3f(1, 1, 0);
        } else {
            glColor3f(0, 1, 0);
        }
        glVertex3f(0, 0, 0);
        glVertex3f(0, axisLength, 0);

        if (draggedAxis == 2) {
            glColor3f(1, 1, 0);
        } else {
            glColor3f(0, 0, 1);
        }
        glVertex3f(0, 0, 0);
        glVertex3f(0, 0, axisLength);
        glEnd();

        glBegin(GL_TRIANGLES);
        if (draggedAxis == 0) {
            glColor3f(1, 1, 0);
        } else {
            glColor3f(1, 0, 0);
        }
        glVertex3f(axisLength, 0, 0);
        glVertex3f(axisLength - arrowSize, arrowSize / 2, 0);
        glVertex3f(axisLength - arrowSize, -arrowSize / 2, 0);

        glVertex3f(axisLength, 0, 0);
        glVertex3f(axisLength - arrowSize, 0, arrowSize / 2);
        glVertex3f(axisLength - arrowSize, 0, -arrowSize / 2);

        if (draggedAxis == 1) {
            glColor3f(1, 1, 0);
        } else {
            glColor3f(0, 1, 0);
        }
        glVertex3f(0, axisLength, 0);
        glVertex3f(arrowSize / 2, axisLength - arrowSize, 0);
        glVertex3f(-arrowSize / 2, axisLength - arrowSize, 0);

        glVertex3f(0, axisLength, 0);
        glVertex3f(0, axisLength - arrowSize, arrowSize / 2);
        glVertex3f(0, axisLength - arrowSize, -arrowSize / 2);

        if (draggedAxis == 2) {
            glColor3f(1, 1, 0);
        } else {
            glColor3f(0, 0, 1);
        }
        glVertex3f(0, 0, axisLength);
        glVertex3f(arrowSize / 2, 0, axisLength - arrowSize);
        glVertex3f(-arrowSize / 2, 0, axisLength - arrowSize);

        glVertex3f(0, 0, axisLength);
        glVertex3f(0, arrowSize / 2, axisLength - arrowSize);
        glVertex3f(0, -arrowSize / 2, axisLength - arrowSize);
        glEnd();

        glEnable(GL_DEPTH_TEST);
        glPopMatrix();
    }

    public int checkGizmoHit(double mouseX, double mouseY, int windowWidth, int windowHeight) {
        if (selectedMesh == null) return -1;

        float[] viewport = new float[4];
        viewport[0] = 0;
        viewport[1] = 0;
        viewport[2] = windowWidth;
        viewport[3] = windowHeight;

        float[] origin = worldToScreen(selectedMesh.getPositionX(), selectedMesh.getPositionY(), selectedMesh.getPositionZ(), viewport);
        float[] xEnd = worldToScreen(selectedMesh.getPositionX() + 1.0f, selectedMesh.getPositionY(), selectedMesh.getPositionZ(), viewport);
        float[] yEnd = worldToScreen(selectedMesh.getPositionX(), selectedMesh.getPositionY() + 1.0f, selectedMesh.getPositionZ(), viewport);
        float[] zEnd = worldToScreen(selectedMesh.getPositionX(), selectedMesh.getPositionY(), selectedMesh.getPositionZ() + 1.0f, viewport);

        float threshold = 15.0f;

        if (distanceToLine(mouseX, mouseY, origin[0], origin[1], xEnd[0], xEnd[1]) < threshold) {
            return 0;
        }
        if (distanceToLine(mouseX, mouseY, origin[0], origin[1], yEnd[0], yEnd[1]) < threshold) {
            return 1;
        }
        if (distanceToLine(mouseX, mouseY, origin[0], origin[1], zEnd[0], zEnd[1]) < threshold) {
            return 2;
        }

        return -1;
    }

    private float[] worldToScreen(float x, float y, float z, float[] viewport) {
        float[] model = new float[16];
        float[] proj = new float[16];

        glGetFloatv(GL_MODELVIEW_MATRIX, model);
        glGetFloatv(GL_PROJECTION_MATRIX, proj);

        float[] point = new float[4];
        point[0] = x;
        point[1] = y;
        point[2] = z;
        point[3] = 1.0f;

        float[] temp = new float[4];
        for (int i = 0; i < 4; i++) {
            temp[i] = model[i] * point[0] + model[4 + i] * point[1] +
                    model[8 + i] * point[2] + model[12 + i] * point[3];
        }

        float[] clip = new float[4];
        for (int i = 0; i < 4; i++) {
            clip[i] = proj[i] * temp[0] + proj[4 + i] * temp[1] +
                    proj[8 + i] * temp[2] + proj[12 + i] * temp[3];
        }

        if (clip[3] == 0.0f) clip[3] = 1.0f;

        float[] ndc = new float[3];
        ndc[0] = clip[0] / clip[3];
        ndc[1] = clip[1] / clip[3];
        ndc[2] = clip[2] / clip[3];

        float[] screen = new float[3];
        screen[0] = viewport[0] + viewport[2] * (ndc[0] + 1.0f) / 2.0f;
        screen[1] = viewport[1] + viewport[3] * (1.0f - (ndc[1] + 1.0f) / 2.0f);
        screen[2] = (ndc[2] + 1.0f) / 2.0f;

        return screen;
    }

    private float distanceToLine(double px, double py, float x1, float y1, float x2, float y2) {
        float A = (float)px - x1;
        float B = (float)py - y1;
        float C = x2 - x1;
        float D = y2 - y1;

        float dot = A * C + B * D;
        float lenSq = C * C + D * D;
        float param = -1;

        if (lenSq != 0) {
            param = dot / lenSq;
        }

        float xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        float dx = (float)px - xx;
        float dy = (float)py - yy;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public void updateMeshPosition(double mouseX, double mouseY, int windowWidth, int windowHeight) {
        if (selectedMesh == null || draggedAxis == -1) return;

        float deltaX = (float)(mouseX - dragStartX);
        float deltaY = (float)(mouseY - dragStartY);

        float sensitivity = 0.01f;

        switch (draggedAxis) {
            case 0:
                selectedMesh.setPositionX(meshStartX + deltaX * sensitivity);
                break;
            case 1:
                selectedMesh.setPositionY(meshStartY - deltaY * sensitivity);
                break;
            case 2:
                selectedMesh.setPositionZ(meshStartZ + deltaX * sensitivity);
                break;
        }
    }
}