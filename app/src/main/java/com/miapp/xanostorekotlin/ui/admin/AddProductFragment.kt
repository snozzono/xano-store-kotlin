package com.miapp.xanostorekotlin.ui.admin

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.xanostorekotlin.databinding.FragmentAddProductBinding
import com.miapp.xanostorekotlin.ui.admin.adapter.ImagePreviewAdapter
import com.miapp.xanostorekotlin.ui.viewmodel.AddProductViewModel

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddProductViewModel by viewModels()
    private val imageList = mutableListOf<Any>() // Solo contendrá Uris
    private lateinit var imageAdapter: ImagePreviewAdapter

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        uris?.let {
            imageList.addAll(it)
            imageAdapter.notifyDataSetChanged()
            updateRecyclerViewVisibility()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        imageAdapter = ImagePreviewAdapter(imageList) { position ->
            imageList.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
            updateRecyclerViewVisibility()
        }
        binding.recyclerViewImagePreview.apply {
            adapter = imageAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupClickListeners() {
        binding.buttonSaveProduct.setOnClickListener {
            saveProduct()
        }
        binding.buttonAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun observeViewModel() {
        viewModel.operationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Producto creado con éxito", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }
    }

    private fun updateRecyclerViewVisibility() {
        binding.recyclerViewImagePreview.isVisible = imageList.isNotEmpty()
    }

    private fun saveProduct() {
        val name = binding.editTextProductName.text.toString()
        val description = binding.editTextProductDescription.text.toString()
        val price = binding.editTextProductPrice.text.toString().toDoubleOrNull() ?: 0.0
        val stock = binding.editTextProductStock.text.toString().toIntOrNull() ?: 0
        val brand = binding.editTextProductBrand.text.toString()
        val category = binding.editTextProductCategory.text.toString()

        if (name.isBlank() || brand.isBlank() || category.isBlank()) {
            Toast.makeText(requireContext(), "Nombre, Marca y Categoría son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Siempre pasamos `null` para el primer argumento porque estamos creando, no editando.
        viewModel.saveOrUpdateProduct(
            null, name, description, price, stock, brand, category, imageList
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
