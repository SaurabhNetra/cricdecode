package co.acjs.cricdecode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class FileIO {
private static final int BUFFER_SIZE = 1024 * 2;

public FileIO() {
    // Utility class.
}

public int copy(InputStream input, OutputStream output) throws Exception, IOException {
    byte[] buffer = new byte[BUFFER_SIZE];

    BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
    BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
    int count = 0, n = 0;
    try {
        while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
            out.write(buffer, 0, n);
            count += n;
        }
        out.flush();
    } finally {
        try {
            out.close();
        } catch (IOException e) {
            Log.e(e.getMessage(), null);
        }
        try {
            in.close();
        } catch (IOException e) {
            Log.e(e.getMessage(), null);
        }
    }
    return count;
}

}