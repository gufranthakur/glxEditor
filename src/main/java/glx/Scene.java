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

    public void addMesh(Mesh mesh) {
        meshes.add(mesh);
        if (selectedMesh == null) {
            selectedMesh = mesh;
        }
    }

    public void removeMesh(Mesh mesh) {
        meshes.remove(mesh);
        if (selectedMesh == mesh) {
            selectedMesh = meshes.isEmpty() ? null : meshes.get(0);
        }
    }

    public List<Mesh> getMeshes() {
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

    // Legacy methods for backward compatibility with glx.Functions
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

        for (Mesh mesh : meshes) {
            glPushMatrix();
            glTranslatef(mesh.getPositionX(), mesh.getPositionY(), mesh.getPositionZ());

            if (mesh == selectedMesh) {
                drawSelectionBox(mesh);
            }

            mesh.draw();
            glPopMatrix();
        }
    }

    private void drawSelectionBox(Mesh mesh) {
        float w = mesh.getWidth() / 2.0f + 0.1f;
        float l = mesh.getLength() / 2.0f + 0.1f;
        float h = mesh.getHeight() / 2.0f + 0.1f;

        glColor3f(1, 1, 0);
        glLineWidth(3);
        glBegin(GL_LINES);

        // Bottom edges
        glVertex3f(-w, -h, -l); glVertex3f(w, -h, -l);
        glVertex3f(w, -h, -l); glVertex3f(w, -h, l);
        glVertex3f(w, -h, l); glVertex3f(-w, -h, l);
        glVertex3f(-w, -h, l); glVertex3f(-w, -h, -l);

        // Top edges
        glVertex3f(-w, h, -l); glVertex3f(w, h, -l);
        glVertex3f(w, h, -l); glVertex3f(w, h, l);
        glVertex3f(w, h, l); glVertex3f(-w, h, l);
        glVertex3f(-w, h, l); glVertex3f(-w, h, -l);

        // Vertical edges
        glVertex3f(-w, -h, -l); glVertex3f(-w, h, -l);
        glVertex3f(w, -h, -l); glVertex3f(w, h, -l);
        glVertex3f(w, -h, l); glVertex3f(w, h, l);
        glVertex3f(-w, -h, l); glVertex3f(-w, h, l);

        glEnd();
    }
}