package glx;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import glx.mesh.CubeMesh;
import glx.mesh.CylinderMesh;
import glx.mesh.DonutMesh;
import glx.mesh.Mesh;
import glx.mesh.TriangleMesh;
import glx.shape.CircleShape;
import glx.shape.PlaneShape;
import glx.shape.SquareShape;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads GLX JSON format and reconstructs mesh objects
 */
public class GLXReader {
    private static final Gson gson = new Gson();

    /**
     * Load meshes from a JSON file
     */
    public static List<Mesh> loadFromFile(String filename) throws IOException {
        try (FileReader reader = new FileReader(filename)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            return parseJSON(root);
        }
    }

    /**
     * Load meshes from a JSON string
     */
    public static List<Mesh> loadFromString(String jsonString) {
        JsonObject root = gson.fromJson(jsonString, JsonObject.class);
        return parseJSON(root);
    }

    /**
     * Parse the JSON structure and create mesh objects
     */
    private static List<Mesh> parseJSON(JsonObject root) {
        List<Mesh> meshes = new ArrayList<>();

        if (!root.has("nodes")) {
            throw new IllegalArgumentException("Invalid GLX JSON: missing 'nodes' array");
        }

        JsonArray nodes = root.getAsJsonArray("nodes");

        // First pass: create all meshes
        for (JsonElement nodeElement : nodes) {
            JsonObject node = nodeElement.getAsJsonObject();
            String type = node.get("type").getAsString();

            // Only create meshes, not shapes
            if (isMeshType(type)) {
                Mesh mesh = parseMesh(node, meshes.size() + 1);
                meshes.add(mesh);
            }
        }

        // Second pass: add shapes to meshes
        for (JsonElement nodeElement : nodes) {
            JsonObject node = nodeElement.getAsJsonObject();
            String type = node.get("type").getAsString();

            // Only process shapes
            if (isShapeType(type)) {
                int nodeNumber = node.get("node").getAsInt();
                if (nodeNumber > 0 && nodeNumber <= meshes.size()) {
                    Mesh targetMesh = meshes.get(nodeNumber - 1);
                    PlaneShape shape = parseShape(node);
                    targetMesh.addShape(shape);
                }
            }
        }

        return meshes;
    }

    /**
     * Check if type is a mesh type
     */
    private static boolean isMeshType(String type) {
        return type.equals("Cube") ||
                type.equals("Cylinder") ||
                type.equals("Donut") ||
                type.equals("Triangle");
    }

    /**
     * Check if type is a shape type
     */
    private static boolean isShapeType(String type) {
        return type.startsWith("Cut-Intrude-") ||
                type.startsWith("Cut-Extrude-") ||
                type.startsWith("Plane-");
    }

    /**
     * Parse a mesh node
     */
    private static Mesh parseMesh(JsonObject node, int defaultId) {
        String type = node.get("type").getAsString();
        JsonObject data = node.getAsJsonObject("data");

        Mesh mesh = null;
        String name = type + "_" + defaultId;

        // Create the appropriate mesh type
        switch (type) {
            case "Cube":
                mesh = new CubeMesh(name);
                break;
            case "Cylinder":
                mesh = new CylinderMesh(name);
                break;
            case "Donut":
                mesh = new DonutMesh(name);
                break;
            case "Triangle":
                mesh = new TriangleMesh(name);
                break;
            default:
                throw new IllegalArgumentException("Unknown mesh type: " + type);
        }

        // Parse coordinates
        if (data.has("coordinates")) {
            JsonObject coords = data.getAsJsonObject("coordinates");
            mesh.setPositionX(getFloat(coords, "x", 0));
            mesh.setPositionY(getFloat(coords, "y", 0));
            mesh.setPositionZ(getFloat(coords, "z", 0));
        }

        // Parse size
        if (data.has("size")) {
            JsonObject size = data.getAsJsonObject("size");

            if (mesh instanceof CubeMesh || mesh instanceof TriangleMesh) {
                mesh.setHeight(getFloat(size, "h", 1));
                mesh.setWidth(getFloat(size, "w", 1));
                mesh.setLength(getFloat(size, "l", 1));

                if (mesh instanceof TriangleMesh) {
                    TriangleMesh triangle = (TriangleMesh) mesh;
                    triangle.setSlopeFactor(getFloat(size, "slopeFactor", 1.0f));
                }
            } else if (mesh instanceof CylinderMesh) {
                float radius = getFloat(size, "r", 0.5f);
                mesh.setWidth(radius * 2);
                mesh.setLength(radius * 2);
            } else if (mesh instanceof DonutMesh) {
                DonutMesh donut = (DonutMesh) mesh;
                donut.setInnerRadius(getFloat(size, "innerR", 0.3f));
                donut.setOuterRadius(getFloat(size, "outerR", 0.7f));
            }
        }

        // Parse rotation
        if (data.has("rotation")) {
            JsonObject rotation = data.getAsJsonObject("rotation");
            mesh.setRotationX(getFloat(rotation, "xRot", 0));
            mesh.setRotationY(getFloat(rotation, "yRot", 0));
            mesh.setRotationZ(getFloat(rotation, "zRot", 0));
        }

        return mesh;
    }

    /**
     * Parse a shape node
     */
    private static PlaneShape parseShape(JsonObject node) {
        String type = node.get("type").getAsString();
        String plane = node.get("plane").getAsString();
        JsonObject data = node.getAsJsonObject("data");

        // Convert plane notation back to readable format
        String planeName = convertPlaneNotation(plane);

        // Determine shape type
        boolean isCircle = type.contains("Circle");
        boolean isIntrude = type.contains("Intrude");
        boolean isExtrude = type.contains("Extrude");

        PlaneShape shape;
        String shapeName = type + "_shape";

        if (isCircle) {
            shape = new CircleShape(shapeName, planeName);
        } else {
            shape = new SquareShape(shapeName, planeName);
        }

        // Parse coordinates
        if (data.has("coordinates")) {
            JsonObject coords = data.getAsJsonObject("coordinates");
            shape.x = getFloat(coords, "x", 0);
            shape.y = getFloat(coords, "y", 0);
        }

        // Parse size
        if (data.has("size")) {
            JsonObject size = data.getAsJsonObject("size");

            if (isCircle) {
                shape.radius = getFloat(size, "r", 0.15f);
            } else {
                shape.width = getFloat(size, "w", 0.3f);
                shape.height = getFloat(size, "h", 0.3f);
            }
        }

        // Parse depth and set intrude/extrude flags
        if (isIntrude && data.has("intrude")) {
            JsonObject intrude = data.getAsJsonObject("intrude");
            float depth = getFloat(intrude, "depth", 0);
            shape.depth = Math.abs(depth); // Store as positive
            shape.intruded = true;
        } else if (isExtrude && data.has("extrude")) {
            JsonObject extrude = data.getAsJsonObject("extrude");
            shape.depth = getFloat(extrude, "depth", 0);
            shape.extruded = true;
        }

        return shape;
    }

    /**
     * Convert plane notation from JSON format to internal format
     */
    private static String convertPlaneNotation(String plane) {
        switch (plane) {
            case "+Z": return "Front";
            case "-Z": return "Back";
            case "+Y": return "Top";
            case "-Y": return "Bottom";
            case "+X": return "Right";
            case "-X": return "Left";
            default: return plane;
        }
    }

    /**
     * Safely get a float value from JSON object
     */
    private static float getFloat(JsonObject obj, String key, float defaultValue) {
        if (obj.has(key)) {
            JsonElement element = obj.get(key);
            if (element.isJsonPrimitive()) {
                return element.getAsFloat();
            }
        }
        return defaultValue;
    }

    /**
     * Validate a GLX JSON string
     */
    public static boolean validateJSON(String jsonString) {
        try {
            JsonObject root = gson.fromJson(jsonString, JsonObject.class);

            // Check for required root structure
            if (!root.has("nodes")) {
                return false;
            }

            JsonArray nodes = root.getAsJsonArray("nodes");

            // Validate each node
            for (JsonElement nodeElement : nodes) {
                if (!nodeElement.isJsonObject()) {
                    return false;
                }

                JsonObject node = nodeElement.getAsJsonObject();

                // Check required fields
                if (!node.has("type") || !node.has("data")) {
                    return false;
                }

                String type = node.get("type").getAsString();

                // Shape nodes need additional fields
                if (isShapeType(type)) {
                    if (!node.has("node") || !node.has("plane")) {
                        return false;
                    }
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get information about a GLX JSON file without fully loading it
     */
    public static GLXInfo getInfo(String jsonString) {
        try {
            JsonObject root = gson.fromJson(jsonString, JsonObject.class);
            JsonArray nodes = root.getAsJsonArray("nodes");

            int meshCount = 0;
            int shapeCount = 0;
            List<String> meshTypes = new ArrayList<>();

            for (JsonElement nodeElement : nodes) {
                JsonObject node = nodeElement.getAsJsonObject();
                String type = node.get("type").getAsString();

                if (isMeshType(type)) {
                    meshCount++;
                    meshTypes.add(type);
                } else if (isShapeType(type)) {
                    shapeCount++;
                }
            }

            return new GLXInfo(meshCount, shapeCount, meshTypes);
        } catch (Exception e) {
            return new GLXInfo(0, 0, new ArrayList<>());
        }
    }

    /**
     * Information about a GLX file
     */
    public static class GLXInfo {
        public final int meshCount;
        public final int shapeCount;
        public final List<String> meshTypes;

        public GLXInfo(int meshCount, int shapeCount, List<String> meshTypes) {
            this.meshCount = meshCount;
            this.shapeCount = shapeCount;
            this.meshTypes = meshTypes;
        }

        @Override
        public String toString() {
            return String.format("GLX Info: %d meshes (%s), %d shapes",
                    meshCount, String.join(", ", meshTypes), shapeCount);
        }
    }

    /**
     * Example usage
     */
    public static void main(String[] args) {
        try {
            // Example: Load from file
            List<Mesh> meshes = GLXReader.loadFromFile("output.json");
            System.out.println("Loaded " + meshes.size() + " meshes");

            for (Mesh mesh : meshes) {
                System.out.println("- " + mesh.getName() + " (" + mesh.getType() + ")");
                System.out.println("  Position: (" + mesh.getPositionX() + ", " +
                        mesh.getPositionY() + ", " + mesh.getPositionZ() + ")");
                System.out.println("  Shapes: " + mesh.getShapes().size());
            }

        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
        }

        // Example: Validate JSON
        String testJson = "{\"nodes\":[{\"type\":\"Cube\",\"data\":{\"coordinates\":{\"x\":0,\"y\":0,\"z\":0}}}]}";
        boolean isValid = GLXReader.validateJSON(testJson);
        System.out.println("JSON valid: " + isValid);

        // Example: Get info
        GLXInfo info = GLXReader.getInfo(testJson);
        System.out.println(info);
    }
}