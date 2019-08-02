package il.ac.huji.cs.postpc.mymeds;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class PermissionChecker {

    private Activity activity;
    private ArrayList<HasPermissionsListener> requests;
    private static final Object LOCK = new Object();

    PermissionChecker(Activity activity) {
        this.activity = activity;
        this.requests = new ArrayList<>();
    }

    public void enqueueIfHasCameraPermissions(HasPermissionsListener listener) {
        enqueueIfHasPermissions(new String[] {Manifest.permission.CAMERA}, listener);
    }

    public void enqueueIfHasPermissions(String[] permissions, HasPermissionsListener listener) {
        if (hasPermissions(permissions)) {
            listener.granted();
        } else {
            synchronized (LOCK) {
                requests.add(listener);
                requestPermissions(permissions, requests.size() - 1);
            }
        }
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions(String[] permissions, int id) {
        ActivityCompat.requestPermissions(activity, permissions, id);
    }

    public void onRequestPermissionsResult(int resultCode, String[] permissions, int[] grantResults) {

        HasPermissionsListener listener = null;
        synchronized (LOCK) {
            if (resultCode >= requests.size()) {
                return;
            }

            listener = requests.get(resultCode);
            if (listener == null) {
                return;
            }

            requests.set(resultCode, null);
        }

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                listener.denied();
                return;
            }
        }

        listener.granted();
    }

    public interface HasPermissionsListener {
        void granted();
        void denied();
    }
}
