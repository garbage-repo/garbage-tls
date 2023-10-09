/*
==--==--==--==--==--==--==--==--==--==--==--==--==--==--==--==--

garbage-tls

Copyright (C) 2023  Anonymous



This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

::--::--::--::--::--::--::--::--::--::--::--::--::--::--::--::--
*/

package garbage.tls

import java.io.BufferedInputStream
import java.io.InputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory

import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocketFactory
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

private const val SOCKET_TIMEOUT = 30_000

private fun <T> load_res(path: String, f: (InputStream) -> T): T {
    return Server::class.java.getResourceAsStream(path)!!.use {
        f(it)
    }
}

private fun new_ssl_context() = SSLContext.getInstance("TLSv1.2")

// Do NOT use KeyStore.getDefaultType() -- because some tool like bouncy-castle will mess things up
private fun new_pkcs12() = KeyStore.getInstance("PKCS12")

class Client(private val x509_res_path: String, private val socket_timeout: Int = SOCKET_TIMEOUT) {

    val x509_trust_manager: X509TrustManager by lazy {
        val key_store = new_pkcs12()
        key_store.load(null)
        load_res(x509_res_path) {
            for (certificate in CertificateFactory.getInstance("X.509").generateCertificates(it)) {
                key_store.setCertificateEntry(certificate.hashCode().toString(), certificate)
            }
        }

        val trust_manager_factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trust_manager_factory.init(key_store)
        trust_manager_factory.getTrustManagers()[0] as X509TrustManager
    }

    val ssl_socket_factory: SSLSocketFactory by lazy {
        val ssl_context = new_ssl_context()
        ssl_context.init(null, arrayOf(x509_trust_manager), SecureRandom())
        ssl_context.getSocketFactory()
    }

    fun connect(host: String, port: Int): Socket {
        val result = ssl_socket_factory.createSocket(Socket(host, port), host, port, true)
        result.setSoTimeout(socket_timeout)
        return result
    }

}

sealed abstract class BaseServer protected constructor(
    private val key_store_password: CharArray, private val socket_timeout: Int = SOCKET_TIMEOUT,
) {

    abstract fun load_key_store(): KeyStore

    protected val server_socket_factory: SSLServerSocketFactory by lazy {
        val key_manager_factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        key_manager_factory.init(load_key_store(), key_store_password)

        val ssl_context = new_ssl_context()
        ssl_context.init(key_manager_factory.getKeyManagers(), null, SecureRandom())
        ssl_context.getServerSocketFactory()
    }

    fun bind(port: Int, address: InetAddress? = null): ServerSocket {
        val result =
            if (address == null) server_socket_factory.createServerSocket(port)
            else server_socket_factory.createServerSocket(port, 0, address)
        result.setSoTimeout(socket_timeout)
        return result
    }

}

class Server(private val pkcs12_res_path: String, private val password: CharArray, private val socket_timeout: Int = SOCKET_TIMEOUT):
BaseServer(password, socket_timeout) {

    override fun load_key_store(): KeyStore {
        val result = new_pkcs12()
        load_res(pkcs12_res_path) {
            result.load(it, password)
        }
        return result
    }

}

class ServerFromKeyStore(private val key_store: KeyStore, private val password: CharArray, private val socket_timeout: Int = SOCKET_TIMEOUT):
BaseServer(password, socket_timeout) {

    override fun load_key_store() = key_store

}
