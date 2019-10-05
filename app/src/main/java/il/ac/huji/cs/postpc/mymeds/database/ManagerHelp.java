package il.ac.huji.cs.postpc.mymeds.database;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

abstract class ManagerHelp {
    private Context context;

    ManagerHelp(Context context) {
        this.context = context;
    }

    String readJSONFromAsset(String fileName) throws IOException {
        InputStream inputStream = context.getAssets().open(fileName);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}
