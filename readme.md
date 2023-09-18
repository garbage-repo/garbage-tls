<!--
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
-->

# `garbage-tls`

_`android:networkSecurityConfig` is garbage_

## X.509 Certificate

So, here's the cert: [`blob/demo/app/src/main/resources/key.pem`][cert-url]

```code
Certificate:
    Data:
        Version: 3 (0x2)
        Serial Number: 1694948696 (0x6506dd58)
        Signature Algorithm: sha512WithRSAEncryption
        Issuer: O = garbage-tls
        Validity
            Not Before: Sep 17 11:04:56 2023 GMT
            Not After : Jan 18 11:04:56 3022 GMT
        Subject: O = hell
        Subject Public Key Info:
            Public Key Algorithm: rsaEncryption
                Public-Key: (8192 bit)
                Modulus:
                    00:86:52:ea:68:01:ff:25:27:b6:3d:2a:15:bb:75:
                    ...
                    97:60:46:b9:4f
                Exponent: 65537 (0x10001)
        X509v3 extensions:
            X509v3 Subject Alternative Name:
                IP Address:10.0.2.2
    Signature Algorithm: sha512WithRSAEncryption
    Signature Value:
        56:e4:db:47:a5:0f:bf:95:5b:02:a9:38:96:d5:8f:93:ad:e8:
        ...
        aa:20:3f:7d:03:38:6f:14:1a:c4:32:38:4e:93:31:56
```

## `android:networkSecurityConfig`

So, it asks you to add this file to your project: `res/xml/network_security_config.xml`

With above cert, the content would be:

```xml
<network-security-config>
    <domain-config>
        <domain includeSubdomains='false'>10.0.2.2</domain>
        <trust-anchors>
            <certificates src='@raw/server_certificate'/>
        </trust-anchors>
        <pin-set>
            <pin digest='SHA-256'>lmPg9zFPhPwrK+PDNWanDWuyt79VJ/dBUiZlurjcyHc=</pin>
        </pin-set>
    </domain-config>
</network-security-config>
```

(`@raw/server_certificate` is that cert)

## Questions

So, there are 2 questions:

-   The server IP address `10.0.2.2` is right there in the cert extension.  Why the ɛf do you have to write it down to the xml file?
-   The cert is right there: `@raw/server_certificate`, its hash can be hashed.  Why the ɛf do you have to hash it by hand, and then write it down to the xml file?

Let's use some Kotlin code to hash the cert, but in Java it doesn't even need Java 1.8:

```kotlin
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory

import kotlin.io.encoding.Base64

BufferedInputStream(FileInputStream("../key.pem")).use {
    for (cert in CertificateFactory.getInstance("X.509").generateCertificates(it)) {
        val md = MessageDigest.getInstance("SHA256")
        md.update(cert.getPublicKey().getEncoded())

        @OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
        val hash = Base64.encode(md.digest())
        println("$hash")
    }
}
```

## Answers

Back in the day, they invented computer machines, programming languages and stuff, to tell the machines to do human favors.

So how the ɛf `android:networkSecurityConfig` dare to ask you to do stuff?

The Rock has an answer:

![It's garbage](../../raw/main/docs/images/its-garbage.png)

## What now?

Tell you what, throw that sh!t out, and bring in `garbage-tls`.  See [`demo`][demo-branch] branch for sample code.  See [docs](docs.md) for documentation.

[cert-url]: ../../blob/demo/app/src/main/resources/key.pem
[demo-branch]: ../../tree/demo
[wiki]: ../../wiki
