package csy.com.as_googlerecomend_easypermissions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Android 6.0 谷歌官方推荐的动态权限获取框架
 * https://github.com/googlesamples/easypermissions
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PM_CALL = 1001;
    private static final int PM_CAMERA_AND_WIFI = 1002;

    private Button btCall;
    private Button btCallAndWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btCall = (Button) findViewById(R.id.btCall);
        btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methodRequiresOnePermission();//申请单个权限
            }
        });

        btCallAndWifi = (Button) findViewById(R.id.btCallAndWifi);
        btCallAndWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methodRequiresTwoPermission();//申请多个权限
            }
        });

    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Do something after user returned from app settings screen, like showing a Toast.
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(this, R.string.returned_from_app_settings_to_activity, Toast.LENGTH_SHORT)
                    .show();
            //可根据需要再进一步判断,比如如果是申请拨打电话权限过去的，那么从设置里面返回的时候再判断是否允许了相应权限,如果允许了则执行相应的操作
        }

    }


    @AfterPermissionGranted(PM_CAMERA_AND_WIFI)
    private void methodRequiresTwoPermission() {
        //String[] perms = {Manifest.permission.CAMERA, Manifest.permission.CHANGE_WIFI_STATE};
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.CHANGE_WIFI_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            Toast.makeText(this, R.string.hasPermissions, Toast.LENGTH_SHORT)
                    .show();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_and_wifi_rationale),
                    PM_CAMERA_AND_WIFI, perms);
        }
    }


    @AfterPermissionGranted(PM_CALL)
    private void methodRequiresOnePermission() {
        //String[] perms = {Manifest.permission.CAMERA, Manifest.permission.CHANGE_WIFI_STATE};
        String[] perms = {Manifest.permission.CALL_PHONE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            Toast.makeText(this, R.string.hasPermissions, Toast.LENGTH_SHORT)
                    .show();

            callPhone();

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.call_rationale),
                    PM_CALL, perms);
        }
    }

    /**
     * 拨打电话
     */
    private void callPhone() {

        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "15116197841"));

        //checkSelfPermission检查的是manifest有没有注册某项权限,并不能判断用户是否允许了某项权限
        //如果5.1及以下的系统拨打电话时,弹出窗请求权限,而用户勾选总是,并且拒绝后,虽然不会崩溃,但是操作会失败
        //因为虽然ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
        //
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        startActivity(callIntent);
    }
}
