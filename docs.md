# Root `build.gradle`

```gradle
allprojects {
    repositories {
        // ...
        maven {
            url 'https://garbage-repo.github.io/'
        }
    }
}
```

# App's `build.gradle`

```gradle
// ...
dependencies {
    // 0.0.0 should be replaced with actual version numbers
    implementation 'garbage:tls:0.0.0'
}
```

# API

For usage on Android, minimum supported SDK is `9`.

## `garbage.tls.Client`

Constructor:

```kotlin
class Client(
    private val x509_res_path: String,
    private val socket_timeout: Int = 30_000,
)
```

| Parameter         | Description
| ----------------- | -----------
| `x509_res_path`   | Resource path to your X.509 certificate, in [PEM][site:pem] format.
| `socket_timeout`  | Socket timeout, in milliseconds.

### Properties

| Property              | Description
| --------------------- | -----------
| `x509_trust_manager`  | `javax.net.ssl.X509TrustManager`, for use with tool like OkHttp.
| `ssl_socket_factory`  | `javax.net.ssl.SSLSocketFactory`, for use with tool like OkHttp.

### Functions

#### `connect()`

```kotlin
import java.net.Socket

fun connect(host: String, port: Int): Socket
```

Connects to a `host`, at `port`.

## `garbage.tls.Server`

### Constructor #1

```kotlin
class Server(
    private val key_store: Lazy<KeyStore>,
    private val password: CharArray,
    private val socket_timeout: Int = 30_000,
)
```

| Parameter         | Description
| ----------------- | -----------
| `key_store`       | Your keystore.
| `password`        | Password of your keystore.
| `socket_timeout`  | Socket timeout, in milliseconds.

### Constructor #2

```kotlin
class Server(
    private val pkcs12_res_path: String,
    private val password: CharArray,
    private val socket_timeout: Int = 30_000,
)
```

| Parameter         | Description
| ----------------- | -----------
| `pkcs12_res_path` | Resource path to your PKCS #12 keystore.
| `password`        | Password of your keystore.
| `socket_timeout`  | Socket timeout, in milliseconds.

### Constructor #3

```kotlin
import java.security.KeyStore

class Server(
    private val key_store: KeyStore,
    private val password: CharArray,
    private val socket_timeout: Int = 30_000,
)
```

| Parameter         | Description
| ----------------- | -----------
| `key_store`       | Your keystore.
| `password`        | Password of your keystore.
| `socket_timeout`  | Socket timeout, in milliseconds.

### Functions

#### `bind()`

```kotlin
import java.net.InetAddress
import java.net.ServerSocket

fun bind(port: Int, address: InetAddress? = null): ServerSocket
```

Binds to a port.

[site:pem]: https://en.wikipedia.org/wiki/Privacy-Enhanced_Mail
