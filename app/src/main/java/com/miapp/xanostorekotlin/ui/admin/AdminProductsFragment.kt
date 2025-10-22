package com.miapp.xanostorekotlin.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.xanostorekotlin.R
import com.miapp.xanostorekotlin.databinding.FragmentAdminProductsBinding
import com.miapp.xanostorekotlin.model.product.Product
import com.miapp.xanostorekotlin.ui.admin.adapter.AdminProductAdapter
import com.miapp.xanostorekotlin.ui.viewmodel.AdminProductsViewModel

class AdminProductsFragment : Fragment(), AdminProductAdapter.OnProductAdminListener {

    private var _binding: FragmentAdminProductsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminProductsViewModel by viewModels()
    private lateinit var adapter: AdminProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        viewModel.fetchProducts()
    }

    private fun setupRecyclerView() {
        adapter = AdminProductAdapter(emptyList(), this)
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProducts.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.fabAddProduct.setOnClickListener {
            // Navega a la pantalla de AÑADIR producto
            findNavController().navigate(R.id.action_productsFragment_to_addProductFragment)
        }
    }



    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.updateProducts(products)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onProductDeleteClicked(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar el producto '${product.name}'?")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                viewModel.deleteProduct(product)
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onProductToggleClicked(product: Product) {
        viewModel.toggleProductStatus(product)
    }

    override fun onProductClicked(product: Product) {
        // Navega a la pantalla de EDITAR producto, pasando el ID del producto
        val action = AdminProductsFragmentDirections.actionProductsFragmentToEditProductFragment(product.id)
        findNavController().navigate(action)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
