package com.example.testocr


import android.Manifest.permission.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.exifinterface.media.ExifInterface
import androidx.print.PrintHelper
import com.bumptech.glide.Glide
import com.example.testocr.Model.SuperHeroModel
import com.example.testocr.Utils.PDFUtils
import com.example.testocr.databinding.ActivityMainBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.io.File.separator
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {



    var superHeroModelList: List<SuperHeroModel> = ArrayList()
    private val dialog: AlertDialog? = null

    private lateinit var binding: ActivityMainBinding

    private lateinit var textRecognizer: TextRecognizer

    private var QRCODE =
        "https://api.qrserver.com/v1/create-qr-code/?size=150x150&bgcolor=255-255-255&data="

    private var imagePath = ""
    private var imageName = ""
    val filename: String = "test_pdf.pdf"
    private lateinit var scaledbmp: Bitmap

    private var storageRef = Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getpermission = Intent()
                getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getpermission)
            }
        }

        binding.btnCamera.setOnClickListener {
            cameraPermissions()
        }

      /*  binding.btnGallery.setOnClickListener {
            storagePermissions()
        }*/


        /* binding.btnPrint.setOnClickListener {
             print()
         }*/

;

        binding.btnCreatePdf.setOnClickListener {
            createPDFFile(Common.getAppPath(this@MainActivity) + filename)


        }

        /*   binding.btnGenerateBmp.setOnClickListener {
               binding.imgQrcode.drawable?.let {
                   scaledbmp = (it as BitmapDrawable).bitmap
               }
           }*/

    }

    lateinit var mBitmap: Bitmap
    lateinit var textScan: String


    private fun createPDFFile(path: String) {
        if (File(path).exists())
            File(path).delete()

        try {
         /*   val document = Document()

            //save
            PdfWriter.getInstance(document, FileOutputStream(path))
            //Open to write
            document.open()

            //Setting
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("")
            document.addCreator("")

            //Font setting
            val colorAccent = BaseColor(0, 153, 204, 255)
            val HeadingFontSize = 20.0f
            val valueFontSize = 26.0f

            //Custom font
            val fontName =
                BaseFont.createFont("assets/font/bradon_medium.otf" , "UTF-8", BaseFont.EMBEDDED)

            //Add Title to document
            val titleStyle = Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "order Details", Element.ALIGN_CENTER, titleStyle)


            val headingStyle = Font(fontName,HeadingFontSize, Font.NORMAL,colorAccent)
            addNewItem(document,"Order No:", Element.ALIGN_LEFT,headingStyle)


            val valueStyle = Font(fontName,valueFontSize, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, text = " #123123", Element.ALIGN_LEFT,valueStyle)

            addLineSeperator(document)

            addNewItem(document,"order Date:", Element.ALIGN_LEFT,headingStyle)
            addNewItem(document, text = " #03/08/2019", Element.ALIGN_LEFT,valueStyle)

            addLineSeperator(document)

            addNewItem(document,"account Name", Element.ALIGN_LEFT,headingStyle)
            addNewItem(document,"Eddy lee", Element.ALIGN_LEFT,valueStyle)


            addLineSeperator(document)

            //Product Detail
            addLineSpace(document)
            addNewItem(document, "Product Details", Element.ALIGN_CENTER, titleStyle)

            //Item1
            addNewItemWithLeftAndRight(document,"Pizza 25","(0.0%)",titleStyle,valueStyle)
            addNewItemWithLeftAndRight(document,"12.0*1000","12000.0",titleStyle,valueStyle)

            //Item2
            addNewItemWithLeftAndRight(document,"Pizza 26","(0.0%)",titleStyle,valueStyle)
            addNewItemWithLeftAndRight(document,"12.0*1000","(12000.0)",titleStyle,valueStyle)

            addLineSeperator(document)
            //Total
            addLineSpace(document)
            addLineSpace(document)

            addNewItemWithLeftAndRight(document,"Total","24000.0",titleStyle,valueStyle)

            //close
            document.close()

            Toast.makeText(this@MainActivity,"Success",Toast.LENGTH_SHORT).show()

            printPDF()*/
            val daydate: String =
                SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())
            val hour: String =
                SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date())

            var str = binding.tvText.text
            var sexy = ""
            val alpha = StringBuffer ()
            val num = StringBuffer()
            val special = StringBuffer()
            for (i in 0 until str.length) {
                if (Character.isDigit(str[i])) num.append(str[i]) else if (str[i] >= 'a' && str[i] <= 'z' || str[i] >= 'A' && str[i] <= 'Z') alpha.append(str[i])


                else special.append(str.get(i))

            }
            alpha.append(" ")

            if (num.length>=3) sexy = num.substring(0,3)




            binding.imgQrcode.drawable?.let {
                mBitmap = (it as BitmapDrawable).bitmap
                scaledbmp = Bitmap.createScaledBitmap(mBitmap, 350, 350, false)
            }
            val document = Document()

            //save
            PdfWriter.getInstance(document, FileOutputStream(path))
            //Open to write
            document.open()

            //Setting
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("")
            document.addCreator("")

            //Font setting
            val colorAccent = BaseColor(0, 153, 204, 255)
            val HeadingFontSize = 20.0f
            val valueFontSize = 26.0f

            //Custom fontBaseFont.createFont("assets/fonts/bradon_medium.otf", "UTF-8", BaseFont.EMBEDDED)
            val fontName =
                BaseFont.createFont("assets/font/bradon_medium.otf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
            val titleStyle = Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK)
            addNewItem(document, "$alpha"+"$sexy", Element.ALIGN_CENTER, titleStyle)


            val valueStyle = Font(fontName,valueFontSize,Font.NORMAL,BaseColor.BLACK)
            addNewItem(document,"$sexy "+"$num",Element.ALIGN_LEFT,valueStyle)
            addNewItem(document,"$hour",Element.ALIGN_LEFT,valueStyle)

            addNewItem(document, "$daydate",Element.ALIGN_UNDEFINED,valueStyle)


            val stream = ByteArrayOutputStream()
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val myImg = Image.getInstance(stream.toByteArray())
            myImg.alignment= Image.ALIGN_BASELINE
            document.add(myImg)



            //close
            document.close()
            Toast.makeText(this@MainActivity,"Success",Toast.LENGTH_SHORT).show()
            printPDF()
        } catch (e: Exception)
        {
            Log.e("EDMTDev",""+e.message)
        }
    }

    private fun getBitmapFromUrl(
        context: Context,
        model: SuperHeroModel,
        document: Document
    ): Observable<SuperHeroModel>? {
        return Observable.fromCallable {
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(model.image)
                .submit().get()
            model
        }
    }


    private fun getAppPath(): String? {
        val dir = File(
            Environment.getExternalStorageDirectory()
                .toString() + separator
                    + resources.getString(R.string.app_name)
                    + separator
        )
        if (!dir.exists()) dir.mkdir()
        return dir.path + separator
    }


    private fun printPDF() {
        val printManager = getSystemService(PRINT_SERVICE) as PrintManager
        try {
            val printAdapter= pdfDocumentAdapter(this@MainActivity,Common.getAppPath(this@MainActivity)+filename)
            printManager.print("Document",printAdapter, PrintAttributes.Builder().build())

            printManager.print("Document", printAdapter, PrintAttributes.Builder().build())
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(DocumentException::class)
    private fun addNewItemWithLeftAndRight(
        document: Document,
        textLeft: String,
        textRight: String,
        leftStyle: Font,
        rightStyle: Font
    ) {
        val chunkTextLeft = Chunk(textLeft, leftStyle)
        val chunkTextRight = Chunk(textRight, rightStyle)
        val p = Paragraph(chunkTextLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkTextRight)
        document.add(p)


    }

    private fun addLineSeperator(document: Document) {
        val lineSeparator = LineSeparator()
        lineSeparator.lineColor = BaseColor(0, 0, 0, 68)
        addLineSpace(document)
        document.add(Chunk(lineSeparator))
        addLineSpace(document)
    }

    private fun addLineSpace(document: Document) {
        document.add(Paragraph(""))
    }

    @Throws(DocumentException::class)
    private fun addNewItem(document: Document, text: String, align: Int, style: Font) {

        val chunk = Chunk(text, style)
        val p = Paragraph(chunk)
        p.alignment = align
        document.add(p)
    }

    private fun print() {

        binding.imgQrcode.drawable?.let {
            val mBitmap = (it as BitmapDrawable).bitmap
            val photoPrinter = PrintHelper(this)
            photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT
            photoPrinter.printBitmap("print", mBitmap)
        }

    }


    private fun cameraPermissions() {
        if (ActivityCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), 1)
        } else {
            cameraResult.launch(Intent(this, CameraViewActivity::class.java))
        }
    }

    private fun storagePermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                2
            )
        } else {
            galleryResult.launch(
                Intent(
                    Intent.ACTION_SEND,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraResult.launch(Intent(this, CameraViewActivity::class.java))
                } else {
                    Toast.makeText(this, "Camera permission requested", Toast.LENGTH_LONG).show()
                }
            }
            2 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    galleryResult.launch(
                        Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                    )
                } else {
                    Toast.makeText(this, "Storage permission requested", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {

                val text = result.data!!.getStringExtra("text")

                binding.tvText.text = text

                Glide.with(this)
                    .load("$QRCODE$text")
                    .into(binding.imgQrcode)

            }
        }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data!!.data != null) {
                val imageUri = result.data!!.data
                try {
                    val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(imageStream)
                    val inputImage: InputImage =
                        InputImage.fromBitmap(bitmap, fixRotation(imageUri))
                    imageRecognition(inputImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    private fun fixRotation(uri: Uri): Int {
        val ei: ExifInterface
        var fixOrientation = 0
        try {
            val input = contentResolver.openInputStream(uri)
            ei = ExifInterface(input!!)
            val orientation: Int = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            fixOrientation = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 80
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_NORMAL -> 0
                else -> 0
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fixOrientation
    }

    private fun imageRecognition(inputImage: InputImage) {
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        textRecognizer.process(inputImage)
            .addOnSuccessListener {
                binding.tvText.text = it.text

                Glide.with(this)
                    .load("$QRCODE${it.text}")
                    .into(binding.imgQrcode)

            }
            .addOnFailureListener {
                Log.e("error", it.message.toString())
            }
    }

    private fun saveQRCode() {
        binding.imgQrcode.drawable?.let {
            val mBitmap = (it as BitmapDrawable).bitmap
            mBitmap.saveToGallery()
            uploadImage()
        }
    }

    //create file to save image from camera to gallery
    @Throws(IOException::class)
    private fun createImagineFile(): File {
        var path: String =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + separator.toString() + "OCRKotlin"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            path =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + separator.toString() + "OCRKotlin"
        }
        val outputDir = File(path)
        outputDir.mkdir()
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH).format(Date())
        val fileName = "OCRKotlin_$timeStamp.jpg"
        val image = File(path + separator.toString() + fileName)
        imagePath = image.absolutePath
        imageName = fileName
        Log.e("imagePath", imagePath)
        Log.e("imageAbsolute", image.absolutePath.toString())
        return image
    }

    //save bitmap to gallery
    private fun Bitmap.saveToGallery(): Uri? {
        val file = createImagineFile()
        if (Build.VERSION.SDK_INT >= 31) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(MediaStore.Images.Media.RELATIVE_PATH, file.absolutePath)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)

            val uri: Uri? =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(this, contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                contentResolver.update(uri, values, null, null)
                return uri
            }
        } else {
            saveImageToStream(this, FileOutputStream(file))
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, file.absolutePath)
            // .DATA is deprecated in API 29
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            return Uri.fromFile(file)
        }

        return null
    }

    //save image to stream
    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //upload image
    private fun uploadImage() {
        val file = Uri.fromFile(File(imagePath))
        val fileName = imageName
        val imageURL = "/salem/$fileName"
        Log.e("name", imageURL)
        val riversRef = storageRef.child(imageURL)
        val uploadTask = riversRef.putFile(file)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            riversRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e("success", "upload")
                Toast.makeText(this, "success upload", Toast.LENGTH_LONG).show()
            } else {
                Log.e("failed", "upload")
                Toast.makeText(this, "failed upload", Toast.LENGTH_LONG).show()
            }
        }

    }
}


