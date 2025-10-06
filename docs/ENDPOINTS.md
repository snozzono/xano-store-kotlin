# Endpoints de la API (XanoStoreKotlin)

Este documento lista los endpoints tal como están definidos en el repositorio. Las URLs base se obtienen de `BuildConfig` (configuradas en `app/build.gradle.kts`) y se consumen a través de `ApiConfig.kt`.

## Base URLs

- `XANO_AUTH_BASE`: `https://x8ki-letl-twmt.n7.xano.io/api:PDQSRKQT` (Autenticación)
- `XANO_STORE_BASE`: `https://x8ki-letl-twmt.n7.xano.io/api:3Xncgo9I` (Tienda/Productos y Upload)

Estas URLs están definidas en `app/build.gradle.kts` mediante `buildConfigField` y se exponen en tiempo de ejecución vía `BuildConfig` y `ApiConfig.kt`.

## Autenticación (AuthService)

- Método: `POST`
- Ruta: `login`
- URL completa: `${XANO_AUTH_BASE}/login` → `https://x8ki-letl-twmt.n7.xano.io/api:PDQSRKQT/login`
- Request body: `LoginRequest { email: String, password: String }`
- Response: `AuthResponse { token: String, user: User, exp?: Long }`

Ejemplo cURL:

```sh
curl -X POST "https://x8ki-letl-twmt.n7.xano.io/api:PDQSRKQT/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"usuario@example.com","password":"secreta"}'
```

## Productos (ProductService)

### Listar productos
- Método: `GET`
- Ruta: `products`
- URL completa: `${XANO_STORE_BASE}/products` → `https://x8ki-letl-twmt.n7.xano.io/api:3Xncgo9I/products`
- Headers: `Authorization: Bearer <token>`
- Response: `List<Product>`

Ejemplo cURL:

```sh
curl "https://x8ki-letl-twmt.n7.xano.io/api:3Xncgo9I/products" \
  -H "Authorization: Bearer $TOKEN"
```

### Crear producto
- Método: `POST`
- Ruta: `products`
- URL completa: `${XANO_STORE_BASE}/products` → `https://x8ki-letl-twmt.n7.xano.io/api:3Xncgo9I/products`
- Headers: `Authorization: Bearer <token>`
- Request body: `CreateProductRequest { name: String, description?: String, price?: Double, image_url?: String }`
- Response: `CreateProductResponse`

Ejemplo cURL:

```sh
curl -X POST "https://x8ki-letl-twmt.n7.xano.io/api:3Xncgo9I/products" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Producto","description":"Desc","price":99.99,"image_url":"https://..."}'
```

## Subida de imágenes (UploadService)

- Método: `POST`
- Ruta: `upload`
- URL completa: `${XANO_STORE_BASE}/upload` → `https://x8ki-letl-twmt.n7.xano.io/api:3Xncgo9I/upload`
- Headers: `Authorization: Bearer <token>`
- Tipo de contenido: `multipart/form-data`
- Parte requerida: `image` (`MultipartBody.Part`)
- Response: `UploadResponse { id?: Int, url?: String }`

Ejemplo cURL:

```sh
curl -X POST "https://x8ki-letl-twmt.n7.xano.io/api:3Xncgo9I/upload" \
  -H "Authorization: Bearer $TOKEN" \
  -F "image=@/ruta/a/archivo.jpg"
```

## Notas de implementación

- El token `Bearer` se inserta automáticamente por `AuthInterceptor` en los clientes de `ProductService` y `UploadService`.
- `AuthService` se construye sin interceptor de `Authorization`.
- Los endpoints se crean con Retrofit en `app/src/main/java/com/miapp/xanostorekotlin/api/*Service.kt` y las URLs base se leen desde `ApiConfig.kt`.

## Cómo actualizar las URLs

1. Edita `app/build.gradle.kts` y actualiza los valores de `buildConfigField` para `XANO_AUTH_BASE` y `XANO_STORE_BASE`.
2. Vuelve a compilar: `gradlew.bat assembleDebug` (o `assembleRelease`).
3. Verifica `BuildConfig` generado en `app/build/generated/source/buildConfig/<variant>/com/miapp/xanostorekotlin/BuildConfig.java`.