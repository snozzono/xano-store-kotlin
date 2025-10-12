package com.miapp.xanostorekotlin.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.databinding.FragmentAddProductBinding
import com.miapp.xanostorekotlin.model.CreateProductRequest
import com.miapp.xanostorekotlin.model.ProductImage
import com.miapp.xanostorekotlin.ui.adapter.ImagePreviewAdapter
import com.miapp.xanostorekotlin.ui.admin.HomeAdminActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private val selectedImageUris = mutableListOf<Uri>()
    // lateinit no es necesario si inicializamos el adapter de inmediato.
    private lateinit var imagePreviewAdapter: ImagePreviewAdapter

    // Se registra el lanzador para obtener imágenes.
    private val pickImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            selectedImageUris.clear()
            selectedImageUris.addAll(uris)
            // La línea donde estaba tu cursor. Es seguro llamar a notifyDataSetChanged aquí
            // porque el adapter ya está inicializado y conectado.
            imagePreviewAdapter.notifyDataSetChanged()
            binding.rvImagePreview.visibility = View.VISIBLE
        }
    }

    // --- MEJORA: Inicializar el adapter en onCreate ---
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializamos el adapter aquí. Esto sobrevive a la destrucción/recreación de la vista.
        imagePreviewAdapter = ImagePreviewAdapter(selectedImageUris)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // --- MEJORA: Conectar el adapter y la UI en onViewCreated ---
        setupUI()
    }

    // Renombramos la función para que sea más claro lo que hace.
    private fun setupUI() {
        // Asignamos el adapter (ya inicializado) al RecyclerView.
        binding.rvImagePreview.adapter = imagePreviewAdapter
        // Configuramos los listeners de los botones.
        binding.btnSelectImage.setOnClickListener {
            pickImages.launch("image/*")
        }
        binding.btnSubmit.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        val name = binding.etName.text?.toString()?.trim().orEmpty()
        val description = binding.etDescription.text?.toString()?.trim()
        val price = binding.etPrice.text?.toString()?.trim()?.toIntOrNull()

        if (name.isBlank()) {
            Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progress.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val uploadedImages = mutableListOf<ProductImage>()
                if (selectedImageUris.isNotEmpty()) {
                    val uploadTasks = selectedImageUris.map { uri ->
                        async(Dispatchers.IO) { uploadImage(uri) }
                    }
                    val results = uploadTasks.awaitAll()
                    uploadedImages.addAll(results.filterNotNull())
                }

                val service = RetrofitClient.createProductService(requireContext())
                val imageObjects = if (uploadedImages.isNotEmpty()) uploadedImages else null

                val req = CreateProductRequest(
                    name = name,
                    description = description,
                    price = price,
                    images = imageObjects
                )

                Log.d("AddProductFragment", "Enviando petición para crear producto: $req")
                val resp = withContext(Dispatchers.IO) { service.createProduct(req) }

                Toast.makeText(requireContext(), "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                navigateToProducts()
            } catch (e: Exception) {
                Log.e("AddProductFragment", "Error al crear el producto", e)
                Toast.makeText(requireContext(), "Error al crear el producto: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.progress.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
            }
        }
    }

    private suspend fun uploadImage(uri: Uri): ProductImage? = withContext(Dispatchers.IO) {
        try {
            val contentResolver = requireContext().contentResolver
            val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IOException("No se pudo abrir el stream para la URI: $uri")

            val requestBody = bytes.toRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("content", "image.jpg", requestBody)
            val uploadService = RetrofitClient.createUploadService(requireContext())
            val imageList: List<ProductImage> = uploadService.uploadImage(part)

            imageList.firstOrNull()
        } catch (e: Exception) {
            Log.e("AddProductFragment", "Falló la subida de la imagen: $uri", e)
            null
        }
    }

    private fun navigateToProducts() {
        if (activity is HomeAdminActivity) {
            (activity as HomeAdminActivity).navigateTo(R.id.nav_products)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Importante: Desvinculamos el adapter del RecyclerView para evitar memory leaks.
        binding.rvImagePreview.adapter = null
        _binding = null
    }
}
