package capehorn.cadmium.io;

import capehorn.cadmium.core.VecBuffer;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public class Obj {

    public ObjFormatWriter writer(Writer writer) {
        return new ObjFormatWriter(writer);
    }

    public static class ObjFormatWriter extends Writer {
        private final Writer writer;

        public ObjFormatWriter(Writer writer) {
            this.writer = writer;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            Objects.checkFromIndexSize(off, len, cbuf.length);
            if (len == 0) {
                return;
            }
            writer.append(new String(cbuf));
        }

        @Override
        public void flush() throws IOException {
            writer.flush();
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }

        public void writeVertex(double x, double y, double z) throws IOException {
            writer.append(String.format("v %f %f %f", x, y, z));
        }

        public void writeVertex(double x, double y, double z, double w) throws IOException {
            writer.append(String.format("v %f %f %f %f", x, y, z, w));
        }

        public void writeVertexNormal(double x, double y, double z) throws IOException {
            writer.append(String.format("vn %f %f %f", x, y, z));
        }

        public void writeFace(int... faceIndices) throws IOException {
            StringBuilder sb = new StringBuilder();
            writeFaceAccordingly(faceIndices, null, null);
        }

        public void writeFaceWithTexture(int[] faceIndices, int[] textureIndices) throws IOException {
            // TODO verify input (equal length)
            writeFace(faceIndices, textureIndices, null);
        }

        public void writeFaceWithNormal(int[] faceIndices, int[] normalIndices) throws IOException {
            // TODO verify input (equal length)
            writeFace(faceIndices, null, normalIndices);
        }

        public void writeFace(int[] faceIndices, int[] textureIndices, int[] normalIndices) throws IOException {
            // TODO verify input (equal length)
            writeFaceAccordingly(faceIndices, textureIndices, normalIndices);
        }

        private void writeFaceAccordingly(int[] fs, int[] ts, int[] ns) throws IOException {
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
            writer.append("f").append(sb.toString());
        }
    }


}
