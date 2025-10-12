package com.miapp.xanostorekotlin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.miapp.xanostorekotlin.databinding.FragmentUsersBinding

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Este fragmento mostrará la lista de usuarios para el administrador.
 * TODO: Implementar la lógica para obtener y mostrar los usuarios desde la API.
 */
class UsersFragment : Fragment() {

    // 2. CORRECCIÓN: Cambiado "FragmentUsersBind4ing" a "FragmentUsersBinding".
    private var _binding: FragmentUsersBinding? = null

    // Esta propiedad solo es válida entre onCreateView y onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Infla el layout para este fragmento usando ViewBinding
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Aquí puedes empezar a configurar tus vistas, como un RecyclerView.
        // Por ejemplo: binding.recyclerViewUsers.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpia la referencia al binding para evitar fugas de memoria.
        _binding = null
    }
}