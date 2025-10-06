package com.miapp.xanostorekotlin.ui.adapter // Paquete del adaptador de RecyclerView

import android.content.Intent
import android.view.LayoutInflater // Import para inflar layouts
import android.view.ViewGroup // Import del contenedor padre en RecyclerView
import androidx.recyclerview.widget.RecyclerView // Import de la clase base RecyclerView
import com.miapp.xanostorekotlin.model.Product // Import del modelo Product (que ahora tiene la lista de imágenes)
import com.miapp.xanostorekotlin.databinding.ItemProductBinding // Import del ViewBinding del item_product.xml
import coil.load // Extensión de Coil para cargar imágenes en ImageView
import com.miapp.xanostorekotlin.ui.ProductDetailActivity

/**
 * ProductAdapter
 * Adaptador para mostrar productos en un RecyclerView.
 * ACTUALIZADO para usar la nueva estructura del modelo de datos de Product.
 */
class ProductAdapter(private var items: List<Product> = emptyList()) : // Adaptador que recibe lista de productos
    RecyclerView.Adapter<ProductAdapter.VH>() { // Especificamos el ViewHolder interno

    // ViewHolder interno que contiene una referencia al ViewBinding de un item.
    class VH(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    // Este métodoo se llama cuando el RecyclerView necesita crear un nuevo ViewHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        // Obtenemos el 'inflater' del contexto del ViewGroup padre.
        val inflater = LayoutInflater.from(parent.context)
        // Inflamos el layout de nuestro item (item_product.xml) usando ViewBinding.
        val binding = ItemProductBinding.inflate(inflater, parent, false)
        // Creamos y devolvemos una nueva instancia de nuestro ViewHolder.
        return VH(binding)
    }

    // Este métodoo es llamado por el RecyclerView para mostrar los datos en la posición especificada.
    override fun onBindViewHolder(holder: VH, position: Int) {
        // 1. OBTENER EL DATO
        // Obtenemos el objeto 'Product' correspondiente a esta posición en la lista.
        val product = items[position]

        // 2. MANEJO DE LA IMAGEN (VERSIÓN FINAL Y CORRECTA)
        // Verificamos si la lista de imágenes del producto ('product.images') no es nula Y no está vacía.
        if (!product.images.isNullOrEmpty()) {
            // Si la lista tiene al menos una imagen, procedemos.

            // Obtenemos la URL de la PRIMERA imagen de la lista.
            // La respuesta de la API ya nos da la URL completa, por lo que no necesitamos construirla.
            val imageUrl = product.images[0].url

            // (Opcional pero recomendado) Imprimimos la URL que vamos a cargar en el Logcat para depurar.
            android.util.Log.d("ProductAdapter", "Cargando imagen desde: $imageUrl")

            // Usamos la librería Coil para cargar la imagen desde la URL en nuestro ImageView.
            holder.binding.imgProduct.load(imageUrl) {
                // (Opcional) Mostramos una imagen genérica mientras la imagen real se está descargando.
                placeholder(android.R.drawable.ic_menu_gallery)
                // (Opcional) Mostramos una imagen de error si la descarga falla (p.ej., URL incorrecta, sin internet).
                error(android.R.drawable.ic_dialog_alert)
            }

            // MANEJO DEL CLIC
            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                // Creamos un Intent para abrir la actividad de detalle.
                val intent = Intent(context, ProductDetailActivity::class.java)
                // Añadimos el objeto 'product' completo al intent.
                // Es crucial que 'Product' sea Serializable para que esto funcione.
                intent.putExtra("PRODUCT_EXTRA", product)
                // Iniciamos la nueva actividad.
                context.startActivity(intent)
            }
        } else {
            // Si la lista de imágenes es nula o está vacía, asignamos una imagen por defecto.
            // Esto evita que el ImageView quede vacío o muestre una imagen de un item reciclado.
            holder.binding.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // 3. ASIGNACIÓN DE TEXTOS (Lógica que ya funcionaba)
        // Asignamos el nombre del producto al TextView 'tvTitle'.
        holder.binding.tvTitle.text = product.name

        // El campo 'description' puede ser nulo. Si es nulo, usamos el operador Elvis (?:) para asignar un string vacío "".
        holder.binding.tvDescription.text = product.description ?: ""

        // El campo 'price' también puede ser nulo. Usamos 'let' para formatear el texto solo si el precio no es nulo.
        // Si no es nulo, le damos el formato "S/ [precio]". Si es nulo, usamos el operador Elvis (?:) para asignar "".
        holder.binding.tvPrice.text = product.price?.let { "Precio: $it" } ?: ""
    }

    // Devuelve el número total de items en la lista de datos.
    override fun getItemCount(): Int = items.size

    // Métodoo público para actualizar la lista de productos del adaptador desde fuera (ej. desde la Activity/Fragment).
    fun updateData(newItems: List<Product>) {
        // Reemplazamos la lista de items interna con la nueva lista.
        items = newItems
        // Notificamos al RecyclerView que todos los datos han cambiado para que se redibuje.
        // Nota: Para mejor rendimiento en listas grandes, se usaría DiffUtil en lugar de notifyDataSetChanged().
        notifyDataSetChanged()
    }
}
