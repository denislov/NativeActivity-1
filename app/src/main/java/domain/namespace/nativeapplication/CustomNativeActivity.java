package domain.namespace.nativeapplication;

import android.app.NativeActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import androidx.core.content.ContextCompat;

public class CustomNativeActivity extends NativeActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService();
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
}
