package com.miapp.xanostorekotlin.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.xanostorekotlin.api.RetrofitClient
import com.miapp.xanostorekotlin.databinding.FragmentProductsBinding
import com.miapp.xanostorekotlin.model.product.Product
import com.miapp.xanostorekotlin.ui.product.adapter.ProductAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsFragment : Fragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductAdapter
    private var allProducts: List<Product> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupSearch()
        loadProducts()
    }

    private fun setupRecycler() {
        adapter = ProductAdapter()
        binding.recyclerProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerProducts.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter(query)
                return true
            }



            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
    }

    private fun filter(query: String?) {
        val q = query?.trim()?.lowercase().orEmpty()
        if (q.isBlank()) {
            adapter.updateData(allProducts)
        } else {
            adapter.updateData(allProducts.filter { it.name.lowercase().contains(q) })
        }
    }

    private fun loadProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val service = RetrofitClient.createECommerceService(requireContext())
                val products = withContext(Dispatchers.IO) {
                    service.getProducts()
                }
                allProducts = products
                adapter.updateData(products)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
