package com.example.sudokusolver.camera

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import com.example.sudokusolver.databinding.FragmentCameraBinding
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.example.sudokusolver.R
import kotlinx.android.synthetic.main.camera_ui_container.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/*
How does it work?
onCreateView: inflate the fragment_camera, which contains the viewFinder (PreviewView) inside a constraintLayout.

*/


class CameraFragment : Fragment() {

    private lateinit var container: ConstraintLayout
    private lateinit var viewFinder: PreviewView
    private lateinit var broadcastManager: LocalBroadcastManager
    private lateinit var cameraExecutor: ExecutorService

    //image analyzer
    private lateinit var gridScannerAnalyzer: GridScannerAnalyzer

    private var displayId: Int = -1
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val displayManager by lazy {
        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    /*
    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                CameraFragmentDirections.actionCameraToPermissions()
            )
        }
    }
    */
    override fun onDestroyView() {
        super.onDestroyView()

        // Shut down our background executor
        cameraExecutor.shutdown()

        // Unregister the broadcast receivers and listeners
        //broadcastManager.unregisterReceiver(volumeDownReceiver)
        //displayManager.unregisterDisplayListener(displayListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_camera, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        container = view as ConstraintLayout
        viewFinder = container.findViewById(R.id.view_finder)

        //Initiate image analyzer
        gridScannerAnalyzer = GridScannerAnalyzer()

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        broadcastManager = LocalBroadcastManager.getInstance(view.context)

        // Wait for the views to be properly laid out
        viewFinder.post {

            // Keep track of the display in which this view is attached
            displayId = viewFinder.display.displayId

            // Build UI controls
            updateCameraUi()

            // Set up the camera and its use cases
            setUpCamera()
        }
        //
    }
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            // Enable or disable switching between cameras
//            updateCameraSwitchButton()

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }
    /** Returns true if the device has an available back camera. False otherwise */
    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    /** Returns true if the device has an available front camera. False otherwise */
    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }
    /** Enabled or disabled a button to switch cameras depending on the available cameras */

//    private fun updateCameraSwitchButton() {
//        val switchCamerasButton = container.findViewById<ImageButton>(R.id.camera_switch_button)
//        try {
//            switchCamerasButton.isEnabled = hasBackCamera() && hasFrontCamera()
//        } catch (exception: CameraInfoUnavailableException) {
//            switchCamerasButton.isEnabled = false
//        }
//    }


    /** Declare and bind preview, capture and analysis use cases */
    private fun bindCameraUseCases() {

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        Log.d(TAG, "Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = viewFinder.display.rotation

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        // Preview
        preview = Preview.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation
            .setTargetRotation(rotation)
            .build()

        // ImageAnalysis
        imageAnalyzer = ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
            .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetRotation(rotation)
            .build()

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            Log.d(TAG, "starting camera")
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture, imageAnalyzer)
            Log.d(TAG, "Camera initialized")
            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
            Log.d(TAG, "preview done")
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun updateCameraUi() {

        // Remove previous UI if any
        container.findViewById<ConstraintLayout>(R.id.camera_ui_container)?.let {
            container.removeView(it)
        }

        // Inflate a new view containing all UI for controlling the camera
        val controls = View.inflate(requireContext(), R.layout.camera_ui_container, container)
        controls.camera_capture_button.setOnClickListener { takePhoto() }
//        // Listener for button used to capture photo
//        controls.findViewById<ImageButton>(R.id.camera_capture_button).setOnClickListener {
//
//            // Get a stable reference of the modifiable image capture use case
//            imageCapture?.let { imageCapture ->
//
//                // Create output file to hold the image
//                val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
//
//                // Setup image capture metadata
//                val metadata = ImageCapture.Metadata().apply {
//
//                    // Mirror image when using the front camera
//                    isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
//                }
//
//                // Create output options object which contains file + metadata
//                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
//                    .setMetadata(metadata)
//                    .build()
//
//                // Setup image capture listener which is triggered after photo has been taken
//                imageCapture.takePicture(
//                    outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
//                        override fun onError(exc: ImageCaptureException) {
//                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//                        }
//
//                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                            val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
//                            Log.d(TAG, "Photo capture succeeded: $savedUri")
//
//                            // We can only change the foreground Drawable using API level 23+ API
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                // Update the gallery thumbnail with latest picture taken
//                                setGalleryThumbnail(savedUri)
//                            }
//
//                            // Implicit broadcasts will be ignored for devices running API level >= 24
//                            // so if you only target API level 24+ you can remove this statement
//                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//                                requireActivity().sendBroadcast(
//                                    Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri)
//                                )
//                            }
//
//                            // If the folder selected is an external media directory, this is
//                            // unnecessary but otherwise other apps will not be able to access our
//                            // images unless we scan them using [MediaScannerConnection]
//                            val mimeType = MimeTypeMap.getSingleton()
//                                .getMimeTypeFromExtension(savedUri.toFile().extension)
//                            MediaScannerConnection.scanFile(
//                                context,
//                                arrayOf(savedUri.toFile().absolutePath),
//                                arrayOf(mimeType)
//                            ) { _, uri ->
//                                Log.d(TAG, "Image capture scanned into media store: $uri")
//                            }
//                        }
//                    })
//
//                // We can only change the foreground Drawable using API level 23+ API
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//                    // Display flash animation to indicate that photo was captured
//                    container.postDelayed({
//                        container.foreground = ColorDrawable(Color.WHITE)
//                        container.postDelayed(
//                            { container.foreground = null }, ANIMATION_FAST_MILLIS)
//                    }, ANIMATION_SLOW_MILLIS)
//                }
//            }
//      }
//
//        // Setup for button used to switch cameras
//        controls.findViewById<ImageButton>(R.id.camera_switch_button).let {
//
//            // Disable the button until the camera is set up
//            it.isEnabled = false
//
//            // Listener for button used to switch cameras. Only called if the button is enabled
//            it.setOnClickListener {
//                lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
//                    CameraSelector.LENS_FACING_BACK
//                } else {
//                    CameraSelector.LENS_FACING_FRONT
//                }
//                // Re-bind use cases to update selected camera
//                bindCameraUseCases()
//            }
//        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(cameraExecutor, object: ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val h = image.getHeight()
                Log.d(TAG, "Captured image with height $h")
                gridScannerAnalyzer.analyze(image)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
            }
        })
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    companion object {
        private const val TAG = "CameraApp"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}