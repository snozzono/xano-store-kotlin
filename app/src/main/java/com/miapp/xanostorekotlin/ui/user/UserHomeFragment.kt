package com.miapp.xanostorekotlin.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.miapp.xanostorekotlin.databinding.FragmentUserHomeBinding
import com.miapp.xanostorekotlin.ui.product.adapter.ProductAdapter
import com.miapp.xanostorekotlin.ui.viewmodel.ProductViewModel
import com.miapp.xanostorekotlin.ui.viewmodel.ProductViewModelFactory

class UserHomeFragment : Fragment() {

    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup ViewModel
        val factory = ProductViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(ProductViewModel::class.java)

        // Setup RecyclerView
        binding.recyclerViewProducts.layoutManager = GridLayoutManager(context, 2)

        // Observe LiveData
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter = ProductAdapter(products)
            binding.recyclerViewProducts.adapter = productAdapter
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }

        // Fetch products
        viewModel.fetchProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
