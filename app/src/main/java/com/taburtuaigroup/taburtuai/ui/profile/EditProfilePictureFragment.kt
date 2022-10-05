package com.taburtuaigroup.taburtuai.ui.profile

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.ViewModelFactory
import com.taburtuaigroup.taburtuai.util.Event
import com.taburtuaigroup.taburtuai.util.PROFILE_URL
import com.taburtuaigroup.taburtuai.util.ToastUtil
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class EditProfilePictureFragment : DialogFragment() {
    private lateinit var dialogView: View
    private var profileUrl: String = ""
    private lateinit var uid: String
    private var getFile: File? = null
    private var filePath: Uri? = null

    private lateinit var img: ImageView
    private lateinit var btnClose: Button
    private lateinit var btnPilihFoto: Button
    private lateinit var btnSimpan: Button


    private lateinit var viewModel: EditProfileViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            uid = FirebaseAuth.getInstance().currentUser!!.uid
        } catch (e: Exception) {

        }

        if (profileUrl != "") {
            Glide.with(dialogView)
                .load(profileUrl)
                .placeholder(
                    AppCompatResources.getDrawable(
                        dialogView.context,
                        R.drawable.placeholder
                    )
                )
                .into(img)
        }

        btnClose.setOnClickListener {
            viewModel.isDialogOpen = false
            dismiss()
        }
        btnPilihFoto.setOnClickListener {
            startGallery()
        }

        btnSimpan.setOnClickListener {
            if (filePath != null) {
                viewModel.uploadProfilePic(filePath!!)
            }
        }

        viewModel.message.observe(this) {
            if (isAdded) {
                val msg = it.second.getContentIfNotHandled()
                if (msg != null) {
                    val x = Pair(it.first, Event(msg))
                    ToastUtil.makeToast(requireActivity(), x)
                    if (!x.first) dismiss().also { viewModel.isDialogOpen = false }
                }
            }
        }

    }



    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(uid, ".jpg", storageDir)
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }


    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            filePath = result.data?.data as Uri
            if (filePath != null) {
                val temp: Uri = filePath!!
                val myFile = uriToFile(temp, requireActivity())
                getFile = myFile
                btnSimpan.visibility=View.VISIBLE
                img.setImageURI(filePath)

            }
        }
    }


    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.pilih_foto))
        launcherIntentGallery.launch(chooser)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = layoutInflater
        dialogView = inflater.inflate(R.layout.fragment_edit_profile_picture, null)
        builder.setView(dialogView)
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory.getInstance(requireActivity().application)
            )[EditProfileViewModel::class.java]

        img = dialogView.findViewById(R.id.img_profile_picture)
        btnClose = dialogView.findViewById(R.id.btn_close)
        btnPilihFoto = dialogView.findViewById(R.id.btn_pilih_foto)
        btnSimpan = dialogView.findViewById(R.id.btn_simpan)

        arguments.let {
            profileUrl = it?.getString(PROFILE_URL) ?: ""
        }
        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_edit_profile_picture, null)
    }

}