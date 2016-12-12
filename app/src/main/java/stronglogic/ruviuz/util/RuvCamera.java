package stronglogic.ruviuz.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.util.Size;

/**
 * Created by logicp on 12/11/16.
 * This is our Camera Helper class
 */

public class RuvCamera {
    private static final String TAG = "RuviuzRUVCAMERA";
    private Activity mActivity;

    private Size jpegSizes[]=null;

    private CameraDevice cameraDevice;
    private CaptureRequest.Builder previewBuilder;
    private CameraCaptureSession previewSession;


    public RuvCamera(Activity mActivity) {
        this.mActivity = mActivity;
    }


    public void getCamera() {
        if (cameraDevice == null) {
            return;
        }
        CameraManager cManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);

        try {
            CameraCharacteristics characteristics = cManager.getCameraCharacteristics(cameraDevice.getId());

            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
        } catch (android.hardware.camera2.CameraAccessException e) {
            e.printStackTrace();
        }
    }

}
