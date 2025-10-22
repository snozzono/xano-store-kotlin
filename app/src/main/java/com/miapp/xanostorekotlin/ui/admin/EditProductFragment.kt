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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.xanostorekotlin.databinding.FragmentEditProductBinding
import com.miapp.xanostorekotlin.model.product.Product
import com.miapp.xanostorekotlin.ui.admin.adapter.ImagePreviewAdapter
import com.miapp.xanostorekotlin.ui.viewmodel.AddProductViewModel

class EditProductFragment : Fragment() {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!

    // Usaremos el mismo ViewModel, ya que la lógica de negocio subyacente (guardar/actualizar) es la misma.
    private val viewModel: AddProductViewModel by viewModels()
    private val args: EditProductFragmentArgs by navArgs()

    private var productToEdit: Product? = null
    private val imageList = mutableListOf<Any>()
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
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        // Cargamos el producto usando el ID que se nos pasa por argumento de navegación
        viewModel.loadProduct(args.productId)

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
        viewModel.productLoaded.observe(viewLifecycleOwner) { product ->
            if (product == null) {
                // Esto no debería pasar en modo edición, es un error o estado inesperado.
                Toast.makeText(requireContext(), "Error: No se pudo cargar el producto.", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
                return@observe
            }
            productToEdit = product
            populateUI(product)
        }
        viewModel.operationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Producto actualizado con éxito", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }
    }

    private fun populateUI(product: Product) {
        binding.editTextProductName.setText(product.name)
        binding.editTextProductDescription.setText(product.description)
        binding.editTextProductPrice.setText(product.price?.toString() ?: "")
        binding.editTextProductStock.setText(product.stock.toString())
        binding.editTextProductBrand.setText(product.brand)
        binding.editTextProductCategory.setText(product.category)

        imageList.clear()
        product.image?.let { imageList.add(it) } // CORREGIDO: Usar .add() para un solo objeto
        imageAdapter.notifyDataSetChanged()
        updateRecyclerViewVisibility()
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

        viewModel.saveOrUpdateProduct(
            productToEdit, name, description, price, stock, brand, category, imageList
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
