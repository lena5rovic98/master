package com.example.master

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.master.ui.SleepRequestsManager
import com.google.android.gms.common.util.CollectionUtils.setOf
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.*
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import android.content.res.AssetFileDescriptor
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter

class MainActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 124
    var image: FirebaseVisionImage? = null
    var detector: FirebaseVisionFaceDetector? = null
    var detector2: FirebaseVisionFaceDetector? = null

    companion object {
        const val MODEL_FILE = "model.tflite"
    }

    private val permissionRequester: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (!isGranted) {
                requestActivityRecognitionPermission()
            } else {
                // Request goes here
                sleepRequestManager.subscribeToSleepUpdates()
            }
        }

    private val sleepRequestManager by lazy{
        SleepRequestsManager(this)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        FirebaseApp.initializeApp(this)

        // loadModelFile()

        val currentTime = LocalDateTime.now()
        val currentTimeFormatted = currentTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))

        val camera = findViewById<Button>(R.id.camera_button)
        camera.setOnClickListener(
            View.OnClickListener {
                // making a new intent for opening camera
                val intent = Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE
                )
                if (intent.resolveActivity(
                        packageManager
                    )
                    != null
                ) {
                    startActivityForResult(
                        intent, REQUEST_IMAGE_CAPTURE
                    )
                } else {
                    // if the image is not captured, set
                    // a toast to display an error image.
                    Toast
                        .makeText(
                            this@MainActivity,
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
            })


        requestActivityRecognitionPermission()

//        sleepRequestManager.requestSleepUpdates(requestPermission = {
//            permissionRequester.launch(ACTIVITY_RECOGNITION)
//        })
        sleepRequestManager.subscribeToSleepUpdates()

        val options = NLClassifier.NLClassifierOptions.builder().build()
        val nlClassifier = NLClassifier.createFromFileAndOptions(
            this, MODEL_FILE, options
        )

        val executor = ScheduledThreadPoolExecutor(1)

        executor.execute {
            val results = nlClassifier.classify("beautiful and positive day today")
            Log.d("classifier negative", results[0].score.toString())
            Log.d("classifier positive", results[1].score.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === REQUEST_IMAGE_CAPTURE && resultCode === RESULT_OK) {
            val extra: Bundle? = data?.extras
            val bitmap = extra?.get("data") as Bitmap?
            detectFace(bitmap!!)
            // detectContours(bitmap!!)
        }
    }

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
                    resultText = "$resultText, FACE NUMBER. $i: \nSmile: ${face.smilingProbability * 100}%" +
                            "\nleft eye open: ${face.leftEyeOpenProbability * 100}%" +
                            "\nright eye open: ${face.rightEyeOpenProbability * 100}%"

                    val leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)
//                    val faceContours = face.getContour(FirebaseVisionFaceContour.FACE).points

                    val smilingProbability = face.smilingProbability
                    val leftEyeOpenProbability = face.leftEyeOpenProbability
                    val rightEyeOpenProbability = face.rightEyeOpenProbability
                    val rightEyeLandmark = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)
                    val bottomMouthLandmark = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM)
                    val faceContour = face.getLandmark(FirebaseVisionFaceContour.FACE)




                    i++
                }
                Toast.makeText(this@MainActivity, resultText, Toast.LENGTH_SHORT).show()
//                if (firebaseVisionFaces.size == 0) {
//                    Toast.makeText(this@MainActivity, "NO FACE DETECT", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this@MainActivity, "FACE DETECT", Toast.LENGTH_SHORT).show()
//                }
            }
            .addOnFailureListener {
                Toast.makeText(this@MainActivity, "Oops, Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun detectContours(bitmap: Bitmap) {
        val options2: FirebaseVisionFaceDetectorOptions = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .build()

        try {
            image = FirebaseVisionImage.fromBitmap(bitmap)
            detector2 = FirebaseVision.getInstance()
                .getVisionFaceDetector(options2)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        detector2!!.detectInImage(image!!)
            .addOnSuccessListener { firebaseVisionFaces ->
                for (face in firebaseVisionFaces) {
                    val leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)
//                    val rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)
//                    val nose = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)
//                    val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
//                    val rightEar = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR)
//                    val leftMouth = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT)
//                    val bottomMouth = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM)
//                    val rightMouth = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT)

//                    val faceContours = face.getContour(FirebaseVisionFaceContour.FACE).points
//                    val leftEyebrowTopContours = face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP).points
//                    val leftEyebrowBottomContours = face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM).points
//                    val rightEyebrowTopContours = face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP).points
//                    val rightEyebrowBottomContours = face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM).points
//                    val leftEyeContours = face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points
//                    val rightEyeContours = face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).points
//                    val upperLipTopContours = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).points
//                    val upperLipBottomContours = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).points
//                    val lowerLipTopContours = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP).points
//                    val lowerLipBottomContours = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).points
//                    val noseBridgeContours = face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE).points
//                    val noseBottomContours = face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM).points

                }
            }
            .addOnFailureListener {
                val a = 2
                Toast.makeText(this@MainActivity, "Oops, Something went wrong", Toast.LENGTH_SHORT).show()
            }

    }

    private fun requestActivityRecognitionPermission() {

        if (ContextCompat.checkSelfPermission(this@MainActivity,
                android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), 101)
        }
    }

    private fun loadModelFile(): MappedByteBuffer? {
        val assetFileDescriptor = this.assets.openFd("classification.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = fileInputStream.getChannel()
        val startOffset = assetFileDescriptor.startOffset
        val len = assetFileDescriptor.length
        // return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, len)

        val interpreter = Interpreter(fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, len), null)

        val row = arrayOf(4000, 2000, 21,22, 3982, 3, 90)
//        val input = FloatArray(1)
//        input[0] = str.toFloat()
        val output = Array(1) {
            IntArray(
                1
            )
        }

        try {
            interpreter.run(row, output)
            val res = output[0][0]
        } catch (e: Exception) {
            val a = 3
        }
        return null
    }
}