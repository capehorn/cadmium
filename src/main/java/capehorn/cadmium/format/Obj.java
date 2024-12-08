package capehorn.cadmium.format;

import capehorn.cadmium.CadmiumRuntimeException;
import capehorn.cadmium.core.Vec3;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public class Obj {

    public ObjFormatWriter writer(Writer writer) {
        return new ObjFormatWriter(writer);
    }

    public static class ObjFormatWriter implements Closeable, Flushable {
        private final Writer writer;
        private int vertexCounter;

        public ObjFormatWriter(Writer writer) {
            this.writer = writer;
        }

        @Override
        public void flush() throws IOException {
            writer.flush();
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }

        public int writeVertex(double x, double y, double z) {
            write(String.format("v %f %f %f\n", x, y, z));
            return ++vertexCounter;
        }

        public int writeVertex(Vec3 v) {
            write(String.format("v %f %f %f\n", v.x(), v.y(), v.z()));
            return ++vertexCounter;
        }

        public int writeVertex(double x, double y, double z, double w) {
            write(String.format("v %f %f %f %f\n", x, y, z, w));
            return ++vertexCounter;
        }

        public void writeVertexNormal(double x, double y, double z) {
            write(String.format("vn %f %f %f\n", x, y, z));
        }

        public void writeFace(int... faceIndices) {
            writeFaceAccordingly(faceIndices, null, null);
        }

        public void writeFaceWithTexture(int[] faceIndices, int[] textureIndices) {
            // TODO verify input (equal length)
            writeFace(faceIndices, textureIndices, null);
        }

        public void writeFaceWithNormal(int[] faceIndices, int[] normalIndices) {
            // TODO verify input (equal length)
            writeFace(faceIndices, null, normalIndices);
        }

        public void writeFace(int[] faceIndices, int[] textureIndices, int[] normalIndices) {
            // TODO verify input (equal length)
            writeFaceAccordingly(faceIndices, textureIndices, normalIndices);
        }

        public void writeLine(int[] vertexIndices) {
            StringBuilder sb = new StringBuilder("l");
            for (int idx: vertexIndices) {
                sb.append(" ").append(idx);
            }
            sb.append("\n");
            write(sb.toString());
        }

        private void writeFaceAccordingly(int[] fs, int[] ts, int[] ns) {
            StringBuilder sb = new StringBuilder("f");
            for (int i = 0; i < fs.length; i++) {
                sb.append(" ").append(fs[i]);
                if (ts == null && ns == null) {
                    continue;
                }
                if (ts != null && ns != null) {
                    sb.append("/").append(ts[i]).append("/").append(ns[i]);
                    continue;
                }
                if (ts != null) {
                    sb.append("/").append(ts[i]);
                    continue;
                }
                sb.append("//").append(ns[i]);
            }
            sb.append("\n");
            write(sb.toString());
        }

        private void write(String str) {
            try {
                writer.write(str);
            } catch (IOException e) {
                throw new CadmiumRuntimeException("Failed to write into obj. format: " + str);
            }
        }
    }


}
