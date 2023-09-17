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

import java.net.SocketTimeoutException

import garbage.tls.G
import garbage.tls.Server

private const val PORT = 12012

private const val BR = "\r\n"
private val DATA = "${G.NAME} ${G.VERSION} says Hi!\n"
private val RESPONSE = "HTTP/1.1 200${BR}content-length: ${DATA.length}${BR}content-type: text/plain$BR$BR$DATA"

fun main() {
    println("${G.NAME}: ${G.VERSION}")

    val server_socket = Server("/key.p12", "pPdu7-rMORqXMet5AdSHp8y_A-MhgWiZXFfd_XISGpM".toCharArray()).bind(PORT)
    println("Bound at: $PORT")
    while (Thread.currentThread().isInterrupted() == false) {
        try {
            server_socket.accept().use {
                println("New client: ${it.getRemoteSocketAddress()}")
                it.getOutputStream().use {
                    it.write(RESPONSE.toByteArray())
                    it.flush()
                }
            }
        } catch (e: SocketTimeoutException) {
            continue
        } catch (t: Throwable) {
            System.err.println("[error]: $t")
        }
    }
}
