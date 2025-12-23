package glx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
        JsonObject root = new JsonObject();
        JsonArray nodes = new JsonArray();

        for (int i = 0; i < meshes.size(); i++) {
            Mesh mesh = meshes.get(i);

            // Add main mesh node
            JsonObject meshNode = null;
            if (mesh instanceof CubeMesh) {
                meshNode = generateCubeJSON(mesh);
            } else if (mesh instanceof CylinderMesh) {
                meshNode = generateCylinderJSON(mesh);
            } else if (mesh instanceof DonutMesh) {
                meshNode = generateDonutJSON(mesh);
            } else if (mesh instanceof TriangleMesh) {
                meshNode = generateTriangleJSON(mesh);
            }

            if (meshNode != null) {
                nodes.add(meshNode);
            }

            // Add shape nodes
            List<PlaneShape> shapes = mesh.getShapes();
            for (PlaneShape shape : shapes) {
                if (shape.intruded && shape.depth > 0) {
                    nodes.add(generateIntrudedShapeJSON(shape, i + 1));
                } else if (shape.extruded && shape.depth > 0) {
                    nodes.add(generateExtrudedShapeJSON(shape, i + 1));
                } else if (!shape.intruded && !shape.extruded) {
                    nodes.add(generatePlaneShapeJSON(shape, i + 1));
                }
            }
        }

        root.add("nodes", nodes);
        return gson.toJson(root);
    }

    private static JsonObject generateCubeJSON(Mesh mesh) {
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
        rotation.addProperty("xRot", formatNumber(mesh.getRotationX()));
        rotation.addProperty("yRot", formatNumber(mesh.getRotationY()));
        rotation.addProperty("zRot", formatNumber(mesh.getRotationZ()));

        cube.add("coordinates", coordinates);
        cube.add("size", size);
        cube.add("rotation", rotation);

        JsonObject wrapper = new JsonObject();
        wrapper.add("type", gson.toJsonTree("Cube"));
        wrapper.add("data", cube);

        return wrapper;
    }

    private static JsonObject generateCylinderJSON(Mesh mesh) {
        JsonObject cylinder = new JsonObject();

        JsonObject coordinates = new JsonObject();
        coordinates.addProperty("x", formatNumber(mesh.getPositionX()));
        coordinates.addProperty("y", formatNumber(mesh.getPositionY()));
        coordinates.addProperty("z", formatNumber(mesh.getPositionZ()));

        JsonObject size = new JsonObject();
        size.addProperty("r", formatNumber(mesh.getWidth() / 2.0f));

        JsonObject rotation = new JsonObject();
        rotation.addProperty("xRot", formatNumber(mesh.getRotationX()));
        rotation.addProperty("yRot", formatNumber(mesh.getRotationY()));
        rotation.addProperty("zRot", formatNumber(mesh.getRotationZ()));

        cylinder.add("coordinates", coordinates);
        cylinder.add("size", size);
        cylinder.add("rotation", rotation);

        JsonObject wrapper = new JsonObject();
        wrapper.add("type", gson.toJsonTree("Cylinder"));
        wrapper.add("data", cylinder);

        return wrapper;
    }

    private static JsonObject generateDonutJSON(Mesh mesh) {
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
        rotation.addProperty("xRot", formatNumber(mesh.getRotationX()));
        rotation.addProperty("yRot", formatNumber(mesh.getRotationY()));
        rotation.addProperty("zRot", formatNumber(mesh.getRotationZ()));

        donutObj.add("coordinates", coordinates);
        donutObj.add("size", size);
        donutObj.add("rotation", rotation);

        JsonObject wrapper = new JsonObject();
        wrapper.add("type", gson.toJsonTree("Donut"));
        wrapper.add("data", donutObj);

        return wrapper;
    }

    private static JsonObject generateTriangleJSON(Mesh mesh) {
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
        rotation.addProperty("xRot", formatNumber(mesh.getRotationX()));
        rotation.addProperty("yRot", formatNumber(mesh.getRotationY()));
        rotation.addProperty("zRot", formatNumber(mesh.getRotationZ()));

        triangleObj.add("coordinates", coordinates);
        triangleObj.add("size", size);
        triangleObj.add("rotation", rotation);

        JsonObject wrapper = new JsonObject();
        wrapper.add("type", gson.toJsonTree("Triangle"));
        wrapper.add("data", triangleObj);

        return wrapper;
    }

    private static JsonObject generateIntrudedShapeJSON(PlaneShape shape, int nodeNumber) {
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

        cutIntrude.add("coordinates", coordinates);
        cutIntrude.add("size", size);
        cutIntrude.add("intrude", intrude);

        JsonObject wrapper = new JsonObject();
        wrapper.add("type", gson.toJsonTree(typeName));
        wrapper.addProperty("node", nodeNumber);
        wrapper.addProperty("plane", convertPlaneNotation(shape.plane));
        wrapper.add("data", cutIntrude);

        return wrapper;
    }

    private static JsonObject generateExtrudedShapeJSON(PlaneShape shape, int nodeNumber) {
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

        cutExtrude.add("coordinates", coordinates);
        cutExtrude.add("size", size);
        cutExtrude.add("extrude", extrude);

        JsonObject wrapper = new JsonObject();
        wrapper.add("type", gson.toJsonTree(typeName));
        wrapper.addProperty("node", nodeNumber);
        wrapper.addProperty("plane", convertPlaneNotation(shape.plane));
        wrapper.add("data", cutExtrude);

        return wrapper;
    }

    private static JsonObject generatePlaneShapeJSON(PlaneShape shape, int nodeNumber) {
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

        planeShape.add("coordinates", coordinates);
        planeShape.add("size", size);

        JsonObject wrapper = new JsonObject();
        wrapper.add("type", gson.toJsonTree(typeName));
        wrapper.addProperty("node", nodeNumber);
        wrapper.addProperty("plane", convertPlaneNotation(shape.plane));
        wrapper.add("data", planeShape);

        return wrapper;
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

    private static float formatNumber(float value) {
        // Round to 2 decimal places
        return Math.round(value * 100.0f) / 100.0f;
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