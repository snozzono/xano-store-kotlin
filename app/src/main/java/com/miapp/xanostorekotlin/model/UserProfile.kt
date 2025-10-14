import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("user_role") val role: String? // Mapea "user_role" del JSON a la variable "role"
)
        