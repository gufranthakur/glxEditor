package glx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import glx.mesh.CubeMesh;
import glx.mesh.CylinderMesh;
import glx.mesh.DonutMesh;
import glx.mesh.TriangleMesh;
import glx.mesh.Mesh;
import glx.shape.CircleShape;
import glx.shape.PlaneShape;
import glx.shape.SquareShape;

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
            } else if (mesh instanceof DonutMesh) {
                result.append(generateDonutJSON(mesh));
            } else if (mesh instanceof TriangleMesh) {
                result.append(generateTriangleJSON(mesh));
            }

            List<PlaneShape> shapes = mesh.getShapes();
            for (PlaneShape shape : shapes) {
                if (shape.intruded && shape.depth > 0) {
                    result.append("\n\n");
                    result.append(generateIntrudedShapeJSON(shape, i + 1));
                }
                if (shape.extruded && shape.depth > 0) {
                    result.append("\n\n");
                    result.append(generateExtrudedShapeJSON(shape, i + 1));
                }
                if (!shape.intruded && !shape.extruded) {
                    result.append("\n\n");
                    result.append(generatePlaneShapeJSON(shape, i + 1));
                }
            }

            if (i < meshes.size() - 1) {
                result.append("\n\n");
            }
        }

        return result.toString();
    }

    private static String generateCubeJSON(Mesh mesh) {
        JsonObject cube = new JsonObject();

        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("x", formatNumber(mesh.getPositionX()));
        coordinates.addProperty("y", formatNumber(mesh.getPositionY()));
        coordinates.addProperty("z", formatNumber(mesh.getPositionZ()));

        JsonObject size = new JsonObject();
        size.addProperty("h", formatNumber(mesh.getHeight()));
        size.addProperty("w", formatNumber(mesh.getWidth()));
        size.addProperty("l", formatNumber(mesh.getLength()));

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

        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("x", formatNumber(mesh.getPositionX()));
        coordinates.addProperty("y", formatNumber(mesh.getPositionY()));
        coordinates.addProperty("z", formatNumber(mesh.getPositionZ()));

        JsonObject size = new JsonObject();
        size.addProperty("r", formatNumber(mesh.getWidth() / 2.0f));

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

    private static String generateDonutJSON(Mesh mesh) {
        DonutMesh donut = (DonutMesh) mesh;
        JsonObject donutObj = new JsonObject();

        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("x", formatNumber(mesh.getPositionX()));
        coordinates.addProperty("y", formatNumber(mesh.getPositionY()));
        coordinates.addProperty("z", formatNumber(mesh.getPositionZ()));

        JsonObject size = new JsonObject();
        size.addProperty("innerR", formatNumber(donut.getInnerRadius()));
        size.addProperty("outerR", formatNumber(donut.getOuterRadius()));

        JsonObject rotation = new JsonObject();
        rotation.addProperty("xRot", formatRotation(mesh.getRotationX()));
        rotation.addProperty("yRot", formatRotation(mesh.getRotationY()));
        rotation.addProperty("zRot", formatRotation(mesh.getRotationZ()));

        donutObj.add("Co-ordinates", coordinates);
        donutObj.add("size", size);
        donutObj.add("rotation", rotation);

        JsonObject wrapper = new JsonObject();
        wrapper.add("Donut", donutObj);

        return formatOutput(gson.toJson(wrapper));
    }

    private static String generateTriangleJSON(Mesh mesh) {
        TriangleMesh triangle = (TriangleMesh) mesh;
        JsonObject triangleObj = new JsonObject();

        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("x", formatNumber(mesh.getPositionX()));
        coordinates.addProperty("y", formatNumber(mesh.getPositionY()));
        coordinates.addProperty("z", formatNumber(mesh.getPositionZ()));

        JsonObject size = new JsonObject();
        size.addProperty("w", formatNumber(mesh.getWidth()));
        size.addProperty("l", formatNumber(mesh.getLength()));
        size.addProperty("h", formatNumber(mesh.getHeight()));
        size.addProperty("slopeFactor", formatNumber(triangle.getSlopeFactor()));

        JsonObject rotation = new JsonObject();
        rotation.addProperty("xRot", formatRotation(mesh.getRotationX()));
        rotation.addProperty("yRot", formatRotation(mesh.getRotationY()));
        rotation.addProperty("zRot", formatRotation(mesh.getRotationZ()));

        triangleObj.add("Co-ordinates", coordinates);
        triangleObj.add("size", size);
        triangleObj.add("rotation", rotation);

        JsonObject wrapper = new JsonObject();
        wrapper.add("Triangle", triangleObj);

        return formatOutput(gson.toJson(wrapper));
    }

    private static String generateIntrudedShapeJSON(PlaneShape shape, int nodeNumber) {
        String shapeType = shape instanceof CircleShape ? "Circle" : "Square";
        String typeName = "Cut-Intrude-" + shapeType;

        JsonObject cutIntrude = new JsonObject();

        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("x", formatNumber(shape.x));
        coordinates.addProperty("y", formatNumber(shape.y));

        JsonObject size = new JsonObject();
        if (shape instanceof CircleShape) {
            size.addProperty("r", formatNumber(shape.radius));
        } else {
            size.addProperty("w", formatNumber(shape.width));
            size.addProperty("h", formatNumber(shape.height));
        }

        JsonObject intrude = new JsonObject();
        intrude.addProperty("depth", formatNumber(-shape.depth));

        cutIntrude.add("co-ordinates", coordinates);
        cutIntrude.add("size", size);
        cutIntrude.add("intrude", intrude);

        JsonObject cutIntrudeWrapper = new JsonObject();
        cutIntrudeWrapper.addProperty("plane", convertPlaneNotation(shape.plane));
        cutIntrudeWrapper.add("cut-intrude", cutIntrude);

        JsonObject typeWrapper = new JsonObject();
        typeWrapper.add(typeName, cutIntrudeWrapper);

        JsonObject nodeWrapper = new JsonObject();
        nodeWrapper.add("node " + nodeNumber, typeWrapper);

        return formatOutput(gson.toJson(nodeWrapper));
    }

    private static String convertPlaneNotation(String plane) {
        switch (plane) {
            case "Front": return "+Z";
            case "Back": return "-Z";
            case "Top": return "+Y";
            case "Bottom": return "-Y";
            case "Right": return "+X";
            case "Left": return "-X";
            default: return plane;
        }
    }

    private static String formatNumber(float value) {
        String formatted = String.format("%.2f", value);
        if (formatted.endsWith(".00")) {
            return formatted.substring(0, formatted.length() - 3);
        }
        return formatted;
    }

    private static String formatRotation(float value) {
        return formatNumber(value) + "*";
    }

    private static String formatOutput(String json) {
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1).trim();
        }

        json = json.replaceFirst("\"(Cube|Cylinder|Donut|Triangle|node \\d+)\":", "$1 :");
        json = json.replaceAll("\"(Cut-Intrude-Circle|Cut-Intrude-Square|Cut-Extrude-Circle|Cut-Extrude-Square|Plane-Circle|Plane-Square)\":", "$1");
        json = json.replaceAll("\"(Co-ordinates|Size|size|rotation|plane|cut-intrude|cut-extrude|plane-shape|co-ordinates|intrude|extrude)\":", "$1 :");
        json = json.replaceAll("\"(x|y|z|h|w|l|r|innerR|outerR|slopeFactor|xRot|yRot|zRot|depth)\":", "$1 :");

        json = json.replaceAll(": \"(\\d+\\.?\\d*)\"", ": $1");
        json = json.replaceAll(": \"(\\d+\\.?\\d*\\*)\"", ": $1");
        json = json.replaceAll(": \"([+-][XYZ])\"", ": $1");

        return json;
    }

    private static String generateExtrudedShapeJSON(PlaneShape shape, int nodeNumber) {
        String shapeType = shape instanceof CircleShape ? "Circle" : "Square";
        String typeName = "Cut-Extrude-" + shapeType;

        JsonObject cutExtrude = new JsonObject();

        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("x", formatNumber(shape.x));
        coordinates.addProperty("y", formatNumber(shape.y));

        JsonObject size = new JsonObject();
        if (shape instanceof CircleShape) {
            size.addProperty("r", formatNumber(shape.radius));
        } else {
            size.addProperty("w", formatNumber(shape.width));
            size.addProperty("h", formatNumber(shape.height));
        }

        JsonObject extrude = new JsonObject();
        extrude.addProperty("depth", formatNumber(shape.depth));

        cutExtrude.add("co-ordinates", coordinates);
        cutExtrude.add("size", size);
        cutExtrude.add("extrude", extrude);

        JsonObject cutExtrudeWrapper = new JsonObject();
        cutExtrudeWrapper.addProperty("plane", convertPlaneNotation(shape.plane));
        cutExtrudeWrapper.add("cut-extrude", cutExtrude);

        JsonObject typeWrapper = new JsonObject();
        typeWrapper.add(typeName, cutExtrudeWrapper);

        JsonObject nodeWrapper = new JsonObject();
        nodeWrapper.add("node " + nodeNumber, typeWrapper);

        return formatOutput(gson.toJson(nodeWrapper));
    }

    private static String generatePlaneShapeJSON(PlaneShape shape, int nodeNumber) {
        String shapeType = shape instanceof CircleShape ? "Circle" : "Square";
        String typeName = "Plane-" + shapeType;

        JsonObject planeShape = new JsonObject();

        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("x", formatNumber(shape.x));
        coordinates.addProperty("y", formatNumber(shape.y));

        JsonObject size = new JsonObject();
        if (shape instanceof CircleShape) {
            size.addProperty("r", formatNumber(shape.radius));
        } else {
            size.addProperty("w", formatNumber(shape.width));
            size.addProperty("h", formatNumber(shape.height));
        }

        planeShape.add("co-ordinates", coordinates);
        planeShape.add("size", size);

        JsonObject planeShapeWrapper = new JsonObject();
        planeShapeWrapper.addProperty("plane", convertPlaneNotation(shape.plane));
        planeShapeWrapper.add("plane-shape", planeShape);

        JsonObject typeWrapper = new JsonObject();
        typeWrapper.add(typeName, planeShapeWrapper);

        JsonObject nodeWrapper = new JsonObject();
        nodeWrapper.add("node " + nodeNumber, typeWrapper);

        return formatOutput(gson.toJson(nodeWrapper));
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