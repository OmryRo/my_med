package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

abstract class ManagerHelp {
    private Context context;
    private static final String TAG = ManagerHelp.class.getSimpleName();

    ManagerHelp(Context context) {
        this.context = context;
    }

    private String convertToString(InputStream is) throws IOException {
        int size = is.available();
        byte[] buffer = new byte[size];
        int totalNumberOfBytesReadIntoTheBuffer = is.read(buffer);
        Log.d(TAG, "convertToString: read number" + totalNumberOfBytesReadIntoTheBuffer);
        is.close();
        return new String(buffer, StandardCharsets.UTF_8);
    }

    String readJSONFromAsset(String fileName) {
        String result;
        try {
            InputStream is = context.getAssets().open(fileName);
            result = convertToString(is);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }
}
