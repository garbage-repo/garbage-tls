import java.io.ByteArrayOutputStream
import java.net.InetAddress
import java.security.SecureRandom

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import garbage.tls.Client
import garbage.tls.G
import garbage.tls.Server

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class TlsTests {

    @Test
    fun tests() {
        println(G.TAG)

        val socket_timeout = 2_000
        val server_socket = Server("/key.p12", "E9Xr2YezIJP1gQcBkqif6SwJwkdNmvy3HGS3sP9xpi4".toCharArray(), socket_timeout).bind(0)
        val server_port = server_socket.getLocalPort()
        val data = SecureRandom.getSeed(24)
        val server_thread = Thread({
            println("Bound at: $server_port")
            server_socket.accept().use {
                it.getOutputStream().use {
                    it.write(data)
                    it.flush()
                }
            }
        })
        server_thread.start()
        runBlocking {
            launch {
                val data_from_server = Client("/key.pem", socket_timeout).connect("localhost", server_port).use { socket ->
                    ByteArrayOutputStream().use {
                        socket.getInputStream().copyTo(it)
                        it.toByteArray()
                    }
                }
                assertArrayEquals(data, data_from_server)
                @OptIn(kotlin.ExperimentalStdlibApi::class)
                println("Server sent: ${data_from_server.toHexString()}")
            }
        }
        server_thread.join()
    }

}
