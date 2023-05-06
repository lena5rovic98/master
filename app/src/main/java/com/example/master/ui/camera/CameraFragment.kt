package com.example.master.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.master.databinding.FragmentCameraBinding
import com.example.master.models.DetectedFace
import com.example.master.models.NetworkLocation
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.*
import java.time.LocalDateTime

class CameraFragment: Fragment(), LocationListener {

    private val REQUEST_IMAGE_CAPTURE = 124
    var image: FirebaseVisionImage? = null
    var detector: FirebaseVisionFaceDetector? = null

    private lateinit var locationManager: LocationManager

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraViewModel: CameraViewModel

    private lateinit var firebaseFace: FirebaseVisionFace

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        cameraViewModel = ViewModelProvider(this)[CameraViewModel::class.java]

        // ask for camera permission
        requireCameraPermission()

        // ask for location permission
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // making a new intent for opening camera
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } else {
            // if the image is not captured, set a toast to display an error image.
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === REQUEST_IMAGE_CAPTURE && resultCode === AppCompatActivity.RESULT_OK) {
            val extra: Bundle? = data?.extras
            val bitmap = extra?.get("data") as Bitmap?
            detectFace(bitmap!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun detectFace(bitmap: Bitmap) {
        val options: FirebaseVisionFaceDetectorOptions = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()

        try {
            image = FirebaseVisionImage.fromBitmap(bitmap)
            detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        detector!!.detectInImage(image!!)
            .addOnSuccessListener { firebaseVisionFaces ->
                var resultText: String? = ""
                var i = 1
                for (face in firebaseVisionFaces) {
                    firebaseFace = face
                    resultText = "$resultText, FACE NUMBER. $i: \nSmile: ${face.smilingProbability * 100}%" +
                            "\nleft eye open: ${face.leftEyeOpenProbability * 100}%" +
                            "\nright eye open: ${face.rightEyeOpenProbability * 100}%"

//                    val leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)
//                    val faceContours = face.getContour(FirebaseVisionFaceContour.FACE).points

//                    val smilingProbability = face.smilingProbability
//                    val leftEyeOpenProbability = face.leftEyeOpenProbability
//                    val rightEyeOpenProbability = face.rightEyeOpenProbability
//                    val rightEyeLandmark = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)
//                    val bottomMouthLandmark = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM)
//                    val faceContour = face.getLandmark(FirebaseVisionFaceContour.FACE)

                    i++
                }
                Toast.makeText(context, resultText, Toast.LENGTH_SHORT).show()
                if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)

//                if (firebaseVisionFaces.size == 0) {
//                    Toast.makeText(this@MainActivity, "NO FACE DETECT", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this@MainActivity, "FACE DETECT", Toast.LENGTH_SHORT).show()
//                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Oops, Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onLocationChanged(location: Location) {
        val dateTime = LocalDateTime.now().toString()
        cameraViewModel.writeFaceRecognition(
            DetectedFace(
                firebaseFace.smilingProbability,
                firebaseFace.leftEyeOpenProbability,
                firebaseFace.rightEyeOpenProbability,
                dateTime,
                NetworkLocation(location.latitude, location.longitude, location.altitude)
            )
        )
    }

    private fun requireCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA
                ),
                101
            )
        }
    }
}
