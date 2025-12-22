package glx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import glx.mesh.CubeMesh;
import glx.mesh.CylinderMesh;
import glx.mesh.Mesh;

import java.util.List;

public class GLXWriter {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static String generateJSON(List<Mesh> meshes) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < meshes.size(); i++) {
            Mesh mesh = meshes.get(i);

            if (mesh instanceof CubeMesh) {
                result.append(generateCubeJSON(mesh));
            } else if (mesh instanceof CylinderMesh) {
                result.append(generateCylinderJSON(mesh));
            }

            if (i < meshes.size() - 1) {
                result.append("\n\n");
            }
        }

        return result.toString();
    }

    private static String generateCubeJSON(Mesh mesh) {
        JsonObject cube = new JsonObject();

        // Co-ordinates
        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("x", formatNumber(mesh.getPositionX()));
        coordinates.addProperty("y", formatNumber(mesh.getPositionY()));
        coordinates.addProperty("z", formatNumber(mesh.getPositionZ()));

        // Size
        JsonObject size = new JsonObject();
        size.addProperty("h", formatNumber(mesh.getHeight()));
        size.addProperty("w", formatNumber(mesh.getWidth()));
        size.addProperty("l", formatNumber(mesh.getLength()));

        // Rotation
        JsonObject rotation = new JsonObject();
        rotation.addProperty("xRot", formatRotation(mesh.getRotationX()));
        rotation.addProperty("yRot", formatRotation(mesh.getRotationY()));
        rotation.addProperty("zRot", formatRotation(mesh.getRotationZ()));

        cube.add("Co-ordinates", coordinates);
        cube.add("Size", size);
        cube.add("rotation", rotation);

        JsonObject wrapper = new JsonObject();
        wrapper.add("Cube", cube);

        return formatOutput(gson.toJson(wrapper));
    }

    private static String generateCylinderJSON(Mesh mesh) {
        JsonObject cylinder = new JsonObject();

        // Co-ordinates
        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("x", formatNumber(mesh.getPositionX()));
        coordinates.addProperty("y", formatNumber(mesh.getPositionY()));
        coordinates.addProperty("z", formatNumber(mesh.getPositionZ()));

        // Size (radius = width / 2)
        JsonObject size = new JsonObject();
        size.addProperty("r", formatNumber(mesh.getWidth() / 2.0f));

        // Rotation
        JsonObject rotation = new JsonObject();
        rotation.addProperty("xRot", formatRotation(mesh.getRotationX()));
        rotation.addProperty("yRot", formatRotation(mesh.getRotationY()));
        rotation.addProperty("zRot", formatRotation(mesh.getRotationZ()));

        cylinder.add("Co-ordinates", coordinates);
        cylinder.add("size", size);
        cylinder.add("rotation", rotation);

        JsonObject wrapper = new JsonObject();
        wrapper.add("Cylinder", cylinder);

        return formatOutput(gson.toJson(wrapper));
    }

    private static String formatNumber(float value) {
        // Format to 2 decimal places, but remove trailing zeros
        String formatted = String.format("%.2f", value);
        // Remove unnecessary .00
        if (formatted.endsWith(".00")) {
            return formatted.substring(0, formatted.length() - 3);
        }
        return formatted;
    }

    private static String formatRotation(float value) {
        return formatNumber(value) + "*";
    }

    private static String formatOutput(String json) {
        // Remove outer braces
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1).trim();
        }

        // Remove quotes around main keys (Cube/Cylinder)
        json = json.replaceFirst("\"(Cube|Cylinder)\":", "$1");

        // Remove quotes around property names but keep structure
        json = json.replaceAll("\"(Co-ordinates|Size|size|rotation)\":", "$1");
        json = json.replaceAll("\"(x|y|z|h|w|l|r|xRot|yRot|zRot)\":", "$1:");

        // Clean up number formatting - remove quotes around numbers and rotation values
        json = json.replaceAll(": \"(\\d+\\.?\\d*)\"", ": $1");
        json = json.replaceAll(": \"(\\d+\\.?\\d*\\*)\"", ": $1");

        return json;
    }

    public static void saveToFile(List<Mesh> meshes, String filename) {
        String json = generateJSON(meshes);
        try (java.io.FileWriter writer = new java.io.FileWriter(filename)) {
            writer.write(json);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}