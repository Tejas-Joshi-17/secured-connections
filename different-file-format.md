# Difference between file formats

1. When you configure a secure connection (HTTPS/TLS) between a Spring Boot application and another server, you may encounter files such as `.pem,` `.crt`, `.cer`, `.key`, `.p12`, `.pfx`, `.jks`, and `.truststore`.

---

# File Formats :-

## 1. Private Key - `.key`

* Used to prove the identity of a server or client during TLS authentication. It must remain secret.
* Example :- `server.key`.

## 2. Certificate - `.crt`, `.cer`, `.pem`

* Contains a public key and identity information, digitally signed by a Certificate Authority (CA) or self-signed.
* Example :- `server.crt`.

## 3. Certificate Chain - often `.pem`, or `.crt`

* May contain the server certificate and one or more intermediate certificates needed to establish trust in a CA.
* Example :- `fullchain.pem`.

## 4. Keystore or certificate bundle (Truststore) - `.p12`, `.pfx`, `.jks`

* A container that can store a private key and its certificate chain, trusted certificates, or other certificate entries, depending on the format and contents.
* Example :- `server.p12`, `server.jks`.

---

# Important Difference :-

### 1. `pem` - Readable Text Format

* Example :-  `server.pem`
* A PEM file commonly looks like this when opened in a text editor :-
```text
-----BEGIN CERTIFICATE-----
MIID...
...
-----END CERTIFICATE-----
```
* A private-key PEM might instead start with :-
```text
-----BEGIN PRIVATE KEY-----
...
-----END PRIVATE KEY-----
```

* Human-readable structure, with Base64-encoded content.
* Can contain one or multiple certificates and/or keys.
* Often used with OpenSSL, NGINX, Apache and Linux servers.
* A private key in PEM format may be encrypted or unencrypted.


### 2. `p12` / `.pfx` - PKCS#12 container

* Example :-  `server.p12`
* This is generally a binary file, so opening it in a text editor won't show a readable certificate block.
* It can package together :-
  * A Private Key.
  * The Certificate associated with that key.
  * Intermediate Certificate forming the certificate chain.
  * Optionally, Other Certificate
* It can be password-protected and is widely supported by Java, OpenSSL and other platforms.


### 3. `.jks` - Java Keystore Container

* Example :- `server.jks`
* This is Java's traditional keystore format.
* It can store :-
    * **PrivateKeyEntry** :- A Private Key and its associated certificate chain.
    * **TrustedCertificateEntry** :-  A Trusted Certificate, such as a CA certificate.
* Java Application can load it using Java's KeyStore APIs.
* **JKS** is Java-Specific, whereas **PKCS#12** is a more portable standard.
* **Important** :- Modern Java versions support PKCS#12 natively, so you often do not need to convert a `.p12` file to `.jks` just because you are using Spring Boot.


---

# Keystore vs Truststore

### 1. Keystore :- 

* Contains your application's identity and, when using certificate-based TLS authentication, its private key and certificate chain.
* Example :- your Spring Boot server presents its certificate to an incoming HTTPS client.
* Possible file :- `server.p12`

### 2. Truststore :-

* Contains certificates your application trusts when verifying another party's identity, commonly CA certificates.
* Example :- your Spring Boot client verifies the certificate presented by a third-party HTTPS API.
* Possible file :- `truststore.p12` or `truststore.jks`

**Note** :-  
1. The same physical file format can be used for either purpose.
2. A `.p12` file is not automatically a keystore, and a `.jks` file is not automatically a truststore.
3. **Their contents and how the application loads them determine their role.**


---

# How do you inspect these files ?

### 1. Inspect a PEM Certificate :-

```shell
openssl x509 -in server.crt -text -noout
```

* Shows the certificate's subject, issuer, validity dates, public key information, and extensions.
* If the certificate is DER-encoded binary instead of PEM, add `-inform DER`.


### 2. Inspect a `.p12` file :-

```shell
keytool -list -v \
  -storetype PKCS12 \
  -keystore server.p12
```

* Enter the keystore password when prompted. 
* This display aliases, entry types, and certificate-chain details.

### 3. Inspect a `.jks` file :- 

```shell
keytool -list -v \
  -storetype JKS \
  -keystore server.jks
```

* This displays the entries stored in the Java KeyStore.


### 4. Convert `.p12` to `.jks` :-

```shell
keytool -importkeystore \
  -srckeystore server.p12 \
  -srcstoretype PKCS12 \
  -destkeystore server.jks \
  -deststoretype JKS
```

---

# Comparison Table

| Extension | What it is                               | Can Store a private key ?                | Typical use                            |
|-----------|------------------------------------------|------------------------------------------|----------------------------------------|
| `.pem`    | Text-based encoding/container convention | Yes                                      | Certificates, keys, certificate chains |
| `.crt`    | Usually a certificate file               | Usually no                               | Server or CA certificate               |
| `.key`    | Usually a private or public key file     | Yes, if it contains a private key        | TLS authentication                     |
| `.p12`    | PKCS#12 binary container                 | Yes                                      | Java, servers, client certificates     |
| `.jks`    | Java Keystore format                     | Yes, if it contains a private-key entry  | Java applications                      |

1. `.pem`, `.crt`, and `.cer` are often used interchangeably in filenames, but their meanings are not strictly interchangeable.
2. PEM is an encoding convention, whereas `.crt` and `.cer` commonly indicate certificate files.
3. `PKCS#12 / .p12` and `.jks` are keystore container formats.



| Question                                       | Answer                                                                              |
|------------------------------------------------|-------------------------------------------------------------------------------------|
| Is `.pem` always a certificate                 | No. It can contain a key, certificates, or both.                                    |
| Are `.p12` and `.pfx` different ?              | Both commonly use the PKCS#12 format.                                               |
| Is `.jks` mandatory for Java ?                 | No. Java supports PKCS#12                                                           |
| Does `.crt` contain the private key ?          | Normally not; check the actual contents.                                            |
| Is a truststore always `.jks`                  | No. It can also be `.p12` or another supported format.                              |
| Does importing a certificate make it trusted ? | It depends on where and how it is imported and how the application uses that store. |
| Can I share a `.p12` file publicly ?           | Not if it contains a private key. Treat it as sensitive.                            |

---

# Spring Boot Configuration

### 1. Server-side HTTPs setup using a PKCS#12 keystore :-

```text
server.port=8443

server.ssl.enabled=true
server.ssl.key-store=classpath:server.p12
server.ssl.key-store-type=PKCS12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
server.ssl.key-alias=server
```

Here, 
* `key-store` specifies the file location.
* `key-store-type` specifies the container format.
* `key-store-password` unlocks the keystore.
* `key-alias` selects the private-key entry used for the server identity.