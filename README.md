# XanoStoreKotlin

Aplicación Android nativa (Kotlin) que consume APIs de Xano para:
- Autenticación de usuario (login y obtención de perfil)
- Listado de productos con búsqueda y detalle
- Creación de productos con subida de imágenes (multipart) y preview con Coil

Enfoque didáctico: el código está minuciosamente comentado para que un estudiante pueda comprender arquitectura, flujos, librerías y buenas prácticas básicas.

## Objetivos de Aprendizaje
- Entender cómo se integra Retrofit/OkHttp/Gson con corrutinas.
- Usar ViewBinding en Activities/Fragments/Adapters.
- Implementar un `Interceptor` para encabezados de autenticación.
- Gestionar estado de sesión con `SharedPreferences`.
- Manejar selección y subida de imágenes (multipart/form-data) y su visualización con Coil.

## Estructura de Proyecto
```
XanoStoreKotlin/
├─ app/
│  ├─ src/main/java/com/miapp/xanostorekotlin
│  │  ├─ api/                      # Configuración y servicios HTTP
│  │  │  ├─ ApiConfig.kt          # Lee base URLs desde BuildConfig
│  │  │  ├─ RetrofitClient.kt     # Fábricas de Retrofit/OkHttp
│  │  │  ├─ AuthInterceptor.kt    # Inserta Authorization: Bearer <token>
│  │  │  ├─ TokenManager.kt       # Persistencia de token/usuario
│  │  │  ├─ AuthService.kt        # POST auth/login, GET auth/me
│  │  │  ├─ ProductService.kt     # GET/POST product
│  │  │  └─ UploadService.kt      # POST upload (multipart)
│  │  ├─ model/                   # Data classes (requests/responses/entidades)
│  │  │  ├─ User.kt
│  │  │  ├─ AuthResponse.kt
│  │  │  ├─ LoginRequest.kt
│  │  │  ├─ Product.kt
│  │  │  ├─ ProductImage.kt
│  │  │  ├─ CreateProductRequest.kt
│  │  │  └─ CreateProductResponse.kt
│  │  ├─ ui/
│  │  │  ├─ MainActivity.kt       # Login
│  │  │  ├─ HomeActivity.kt       # BottomNav: Perfil/Productos/Agregar
│  │  │  ├─ ProductDetailActivity.kt # Detalle de producto con carrusel
│  │  │  ├─ fragments/
│  │  │  │  ├─ ProductsFragment.kt # Lista + búsqueda
│  │  │  │  ├─ AddProductFragment.kt # Form + upload imágenes
│  │  │  │  └─ ProfileFragment.kt  # Perfil y logout
│  │  │  └─ adapter/
│  │  │     ├─ ProductAdapter.kt   # RecyclerView de productos
│  │  │     └─ ImagePreviewAdapter.kt # Previsualización selección de imágenes
│  ├─ src/main/res
│  │  ├─ layout/
│  │  │  ├─ activity_main.xml
│  │  │  ├─ activity_home.xml
│  │  │  ├─ activity_product_detail.xml
│  │  │  ├─ fragment_products.xml
│  │  │  ├─ fragment_add_product.xml
│  │  │  ├─ fragment_profile.xml
│  │  │  ├─ item_product.xml
│  │  │  ├─ item_image_preview.xml
│  │  │  └─ item_image_slider.xml
│  │  ├─ menu/bottom_nav_menu.xml  # Menú de navegación inferior
│  │  ├─ values/strings.xml, colors.xml, themes.xml
│  │  └─ values-night/themes.xml
│  ├─ build.gradle.kts             # Configuración del módulo app
│  └─ proguard-rules.pro
├─ docs/ENDPOINTS.md               # Endpoints y ejemplos cURL
├─ gradle/libs.versions.toml       # Catálogo de versiones y librerías
├─ settings.gradle.kts             # Módulos incluidos
└─ build.gradle.kts                # Plugins a nivel raíz
```

## Configuración de Android y Gradle
- `namespace`: `com.miapp.xanostorekotlin`
- `compileSdk`: 36, `minSdk`: 24, `targetSdk`: 36
- `compileOptions`: `JavaVersion.VERSION_11`
- `kotlinOptions`: `jvmTarget = "11"`
- `buildFeatures`: `viewBinding = true`, `buildConfig = true`

Plugins (raíz y módulo `app`):
- `com.android.application`
- `org.jetbrains.kotlin.android`

Librerías principales (de `gradle/libs.versions.toml`):
- AndroidX: `core-ktx`, `appcompat`, `recyclerview`, `constraintlayout`, `lifecycle-runtime-ktx`, `activity-ktx`
- Material: `material`
- Networking: `retrofit`, `converter-gson`, `okhttp`, `okhttp-logging`
- Corrutinas: `kotlinx-coroutines-android`
- Imágenes: `coil`
- Testing: `junit`, `androidx-junit`, `espresso-core`

## BuildConfig y URLs de la API
En `app/build.gradle.kts` se definen campos de BuildConfig:
```kts
defaultConfig {
    buildConfigField("String", "XANO_STORE_BASE", "\"https://x8ki-letl-twmt.n7.xano.io/api:3Xncgo9I\"")
    buildConfigField("String", "XANO_AUTH_BASE",  "\"https://x8ki-letl-twmt.n7.xano.io/api:PDQSRKQT\"")
    buildConfigField("int", "XANO_TOKEN_TTL_SEC", "86400")
}
```
Estas constantes se exponen en `BuildConfig` y se leen en `ApiConfig.kt`.
`BuildConfig.java` generado (debug): `app/build/generated/source/buildConfig/debug/com/miapp/xanostorekotlin/BuildConfig.java`.

## Endpoints Utilizados
- Base Auth: `https://x8ki-letl-twmt.n7.xano.io/api:PDQSRKQT`
  - `POST /auth/login` → `AuthResponse`
  - `GET /auth/me` → `User`
- Base Store: `https://x8ki-letl-twmt.n7.xano.io/api:3Xncgo9I`
  - `GET /product` → `List<Product>`
  - `POST /product` → `CreateProductResponse`
  - `POST /upload` (multipart) → `UploadResponse`/`ProductImage`

Ver `docs/ENDPOINTS.md` para cURL y ejemplos.

## Detalle de Módulos y Clases

### api/
- `ApiConfig`: Centraliza `storeBaseUrl`, `authBaseUrl`, `tokenTtlSec` desde `BuildConfig`.
- `RetrofitClient`: Provee `createAuthService`, `createProductService`, `createUploadService`.
  - Configura `OkHttpClient` con `HttpLoggingInterceptor` y timeouts.
  - Aplica `AuthInterceptor` para servicios que requieren token.
- `AuthInterceptor`: Añade `Authorization: Bearer <token>` si existe token.
- `TokenManager`: Persistencia simple con `SharedPreferences` (token, nombre, email); métodos `saveAuth`, `getToken`, `isLoggedIn`, `clear`.
- `AuthService`: `POST auth/login` y `GET auth/me`.
- `ProductService`: `GET/POST product`.
- `UploadService`: `POST upload` (multipart con `@Part image`).

### model/
- `User`: id, name, email, createdAt (mapa con `@SerializedName`).
- `AuthResponse`: contiene `authToken` (ajusta según tu API).
- `LoginRequest`: email y password.
- `Product`: entidad de producto (ajusta campos según API); integra imágenes si el backend las provée.
- `ProductImage`: modelo completo para imágenes devuelto/esperado por Xano.
- `CreateProductRequest`: payload para crear producto, incluye lista `images: List<ProductImage>`.
- `CreateProductResponse`: respuesta de creación (ajustar según API).

### ui/
- `MainActivity`: pantalla de login; uso de `lifecycleScope` para corrutinas; guarda token y navega a `HomeActivity`.
- `HomeActivity`: `BottomNavigationView` que navega entre `ProfileFragment`, `ProductsFragment` y `AddProductFragment`.
- `ProductDetailActivity`: muestra detalle del producto, con carrusel de imágenes (usa `item_image_slider.xml`).

### ui/fragments/
- `ProductsFragment`: carga productos con corrutinas, muestra en `RecyclerView` con `ProductAdapter`, barra de búsqueda local.
- `AddProductFragment`: formulario para crear producto, selección de una o varias imágenes, preview (`ImagePreviewAdapter`), subida (multipart) y luego creación del producto con referencia a las imágenes.
- `ProfileFragment`: muestra datos del usuario, y permite cerrar sesión (`TokenManager.clear()`).

### ui/adapter/
- `ProductAdapter`: muestra nombre, precio y primera imagen con Coil; clic abre `ProductDetailActivity`.
- `ImagePreviewAdapter`: lista de URIs de imágenes seleccionadas, renderiza con Coil.

## Layouts y ViewBinding
- `activity_main.xml` ↔ `ActivityMainBinding`: campos de email/password y botón de login.
- `activity_home.xml` ↔ `ActivityHomeBinding`: texto de bienvenida y `BottomNavigationView`.
- `activity_product_detail.xml` ↔ `ActivityProductDetailBinding`: carrusel y datos del producto.
- `fragment_products.xml` ↔ `FragmentProductsBinding`: `RecyclerView` y barra de búsqueda.
- `fragment_add_product.xml` ↔ `FragmentAddProductBinding`: inputs de nombre/desc/precio, botón seleccionar imagen, preview y botón enviar.
- `fragment_profile.xml` ↔ `FragmentProfileBinding`: datos del usuario y botón logout.
- `item_product.xml` ↔ `ItemProductBinding`: item del listado.
- `item_image_preview.xml` ↔ `ItemImagePreviewBinding`: preview de imagen seleccionada.
- `item_image_slider.xml` ↔ `ItemImageSliderBinding`: item para carrusel en detalle.

## Flujo de Autenticación y Sesión
1. Usuario ingresa email/password en `MainActivity`.
2. Se llama `AuthService.login` mediante `RetrofitClient.createAuthService`.
3. Se guarda `authToken` y datos del usuario con `TokenManager.saveAuth`.
4. Se navega a `HomeActivity` y se usa `AuthInterceptor` para llamadas autenticadas.

## Flujo de Productos
- `ProductsFragment` carga `/product` con token, renderiza lista y permite búsqueda local.
- `ProductDetailActivity` muestra información ampliada (imágenes en carrusel).

## Flujo de Creación y Subida de Imágenes
1. En `AddProductFragment`, el usuario selecciona imágenes (URIs locales).
2. Se suben vía `UploadService.uploadImage` como `multipart/form-data`.
3. El backend devuelve metadatos (`ProductImage`/`UploadResponse`), que se agregan al `CreateProductRequest`.
4. Se llama `ProductService.createProduct` para persistir producto con imágenes.
5. Se muestra preview con Coil en tiempo real.

## Compilación y Ejecución
- Windows: `gradlew.bat assembleDebug` o desde Android Studio (Build > Make Project).
- Instalar en dispositivo/emulador (Run > Run 'app').
- `BuildConfig` se regenera tras compilar; valida `app/build/generated/source/buildConfig/...`.

## Pruebas
- Unitarias: `app/src/test/...` con JUnit.
- Instrumentadas: `app/src/androidTest/...` con AndroidX JUnit y Espresso.

## Seguridad y Buenas Prácticas
- `SharedPreferences` no es seguro en producción: considera `EncryptedSharedPreferences` o `DataStore` + cifrado.
- Añade validaciones de formularios y manejo de errores HTTP (por simplicidad se redujeron).
- Usa `DiffUtil` en `RecyclerView` si manejas listas grandes.

## Contribución
- Ajusta modelos/endpoints según tu instancia de Xano.
- Actualiza `build.gradle.kts` si cambias las bases.
- Ejecuta `./gradlew lint` y considera CI para validaciones automáticas.

## Licencia
Proyecto sin licencia explícita. Añade una si planeas distribuirlo.

---
Sugerencia pedagógica: lee primero `ApiConfig`, `RetrofitClient` y los `Service`. Luego recorre `MainActivity` → `HomeActivity` → Fragments y Adapters. Observa cómo el token fluye con `AuthInterceptor` y cómo `ViewBinding` simplifica el acceso a vistas.