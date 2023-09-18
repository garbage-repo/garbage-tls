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
                val data_from_server = Client("/key.pem", socket_timeout).connect("localhost", server_port).use {
                    it.getInputStream().readAllBytes()
                }
                assertArrayEquals(data, data_from_server)
                @OptIn(kotlin.ExperimentalStdlibApi::class)
                println("Server sent: ${data_from_server.toHexString()}")
            }
        }
        server_thread.join()
    }

}
