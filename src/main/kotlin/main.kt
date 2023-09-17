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
