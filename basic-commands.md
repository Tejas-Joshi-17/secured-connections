# Secured Connection

---

## 1. Generating a Private Key

```bash
# Generate a 2048-bit RSA private key
openssl genpkey \
  -algorithm RSA \
  -out private.key \
  -pkeyopt rsa_keygen_bits:2048
```

| Parameter                       | Description                                                                    |
|:--------------------------------|--------------------------------------------------------------------------------|
| `openssl`                       | Core binary / OpenSSL CLI utility                                              |
| `genpkey`                       | Subcommand used to generate a private key                                      |
| `-algorithm RSA `               | Specifies the cryptographic algorithm (e.g., RSA, EC, ED25519)                 |
| `-out private.key `             | Target output file path for the generated private key                          |
| `-pkeyopt rsa_keygen_bits:2048` | Sets RSA modulus size to 2048 bits (3072+ recommended for higher security)     |


---

## 2. Generating a Public Key

```bash
# Extract the public key component from an existing private key
openssl rsa \
  -pubout \
  -in private.key \
  -out public.key
```


| Parameter         | Description                                                                 |
|:------------------|-----------------------------------------------------------------------------|
| `openssl`         | Core binary / OpenSSL CLI utility                                           |
| `rsa`             | Subcommand used to manage and process RSA keys                              |
| `-pubout`         | Tells OpenSSL to extract and output the public key (PEM format)             |
| `-in private.key` | Specifies the input file containing the private key                         |
| `-out public.key` | Target output file path where the extracted public key will be saved        |


--- 

## 3. Creating CSR (Certificate Signing Request)

```bash
openssl req \ 
  -new \
  -key private.key \
  -out server.csr 
```


| Parameter         | Description                                                                                       |
|:------------------|---------------------------------------------------------------------------------------------------|
| `openssl`         | Core binary / OpenSSL CLI utility                                                                 |
| `req`             | The OpenSSL utility used for PKCS#10 Certificate Signing Request management and creation          |
| `-new`            | Tells OpenSSL to generate a new CSR                                                               |
| `-in private.key` | Specifies the existing private key file to sign the CSR and extract the corresponding public key. |
| `-out server.csr` | Designates the output filename for the generated CSR file                                         |


---

## 4. Viewing CSR (Certificate Signing Request)

```bash
openssl req \
  -text \
  -noout \
  -verify \
  -in server.csr 
```

| Parameter         | Description                                                                                                                               |
|:------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| `openssl`         | Core binary / OpenSSL CLI utility                                                                                                         |
| `req`             | The CSR management and processing module                                                                                                  |
| `-text`           | Prints the CSR's contents (subject details, public key info, attributes) in human-readable plain text                                     |
| `-noout`          | Suppresses the raw PEM-encoded output so you only see the decoded text                                                                    |
| `-verify`         | Checks the self-signature inside the CSR to ensure the request is not corrupted and was genuinely signed by the corresponding private key |
| `-in server.csr ` | Specifies the input CSR file you want to read                                                                                             |


---

## 5. Generating Self-Signed Certificate


```bash
openssl req \
  -x509 \
  -key private.key \
  -out cert.pem \
  -days 365
```

| Parameter          | Description                                                                                                                                                                                                                                    |
|:-------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `openssl`          | Core binary / OpenSSL CLI utility                                                                                                                                                                                                              |
| `req`              | The sub-command/utility that handles PKCS#10 certificate requests and generation                                                                                                                                                               |
| `-x509`            | Tells OpenSSL to output a self-signed certificate instead of generating a Certificate Signing Request (.csr). Without this flag, OpenSSL defaults to creating a request; with it, OpenSSL signs the certificate itself using the provided key. |
| `-key private.key` | Specifies the path to the private key used to sign the certificate. It also extracts the matching public key to embed directly into cert.pem                                                                                                   |
| `-out cert.pem`    | Defines the destination path and filename for the resulting certificate. The output will be formatted in base64 PEM (-----BEGIN CERTIFICATE-----)                                                                                              |
| `-days 365`        | Sets the certificate's validity duration to 365 days (one year)                                                                                                                                                                                |


---

## 6. Generating Self-Signed Certificate (For Local Development)

```bash
openssl req \
  -x509 \
  -key private.key \
  -out server.crt \
  -days 365 \
  -subj \
  "/CN=localhost" \
  -addext \
  "subjectAltName = DNS:localhost, IP:127.0.0.1"
```

| Parameter                                          | Description                                                                                                                                                                                                                                    |
|:---------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `openssl`                                          | Core binary / OpenSSL CLI utility                                                                                                                                                                                                              |
| `req`                                              | The sub-command/utility that handles PKCS#10 certificate requests and generation                                                                                                                                                               |
| `-x509`                                            | Tells OpenSSL to output a self-signed certificate instead of generating a Certificate Signing Request (.csr). Without this flag, OpenSSL defaults to creating a request; with it, OpenSSL signs the certificate itself using the provided key. |
| `-key private.key`                                 | Specifies the path to the private key used to sign the certificate. It also extracts the matching public key to embed directly into cert.pem                                                                                                   |
| `-out cert.pem`                                    | Defines the destination path and filename for the resulting certificate. The output will be formatted in base64 PEM (-----BEGIN CERTIFICATE-----)                                                                                              |
| `-days 365`                                        | Sets the certificate's validity duration to 365 days (one year)                                                                                                                                                                                |
| `-subj`                                            | Skips terminal prompts for Country, State, Org, etc                                                                                                                                                                                            |
| `/CN=localhost`                                    | Sets the Common Name to localhost                                                                                                                                                                                                              |
| `-addext`                                          | Injects modern X.509 extensions into the certificate on the fly (available in OpenSSL 1.1.1 and newer) without needing a custom .cnf configuration file:                                                                                       |
| `"subjectAltName = DNS:localhost, IP:127.0.0.1"`   | Validates the certificate when accessed via the domain hostname `https://localhost` or accessed directly via the loopback IP `[https://127.0.0.1](https://127.0.0.1)`.                                                                         |


---

```text
CN = Common Name (Used to check url matching)
SAN = Subject Alternative Name (Used to additional urls)

```

---

## 7. Create Keystore

```bash
keytool -genkeypair \
  -alias myapp \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore server-keystore.p12 \
  -validity 3650
```

| Parameter                       | Description                                                                                                |
|---------------------------------|------------------------------------------------------------------------------------------------------------|
| `keytool`                       | Java utility used to manage cryptographic keys, certificates, keystores, and truststores.                  |
| `-genkeypair`                   | Generates a public-private key pair and creates a self-signed X.509 certificate containing the public key. |
| `-alias myapp`                  | Assigns the alias `myapp` to the key entry. The alias identifies this entry inside the keystore.           |
| `-keyalg RSA`                   | Specifies RSA as the cryptographic algorithm for generating the key pair.                                  |
| `-keysize 2048`                 | Sets the RSA key size to 2048 bits.                                                                        |
| `-storetype PKCS12`             | Specifies PKCS#12 as the keystore format. The file commonly uses the `.p12` or `.pfx` extension.           |
| `-keystore server-keystore.p12` | Specifies the keystore file name. If the file does not exist, `keytool` creates it.                        |
| `-validity 3650`                | Sets the certificate validity period to 3650 days (approximately 10 years).                                |


### Note :-  Above keystore contains Private Key + Public Certificate

---

## 8. Add One more entry in keystore

just change **alias name & use same keystore file name**

```bash
keytool -genkeypair \
  -alias myapp2 \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore server-keystore.p12 \
  -validity 3650
```

---

## 9. How to View Keystore

```bash
keytool -list \
  -v \
  -keystore server-keystore.p12
```

| Parameter                       | Description                                                                                                                                                                         |
|---------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `keytool`                       | Java utility used to manage cryptographic keys, certificates, keystores, and truststores.                                                                                           |
| `-list`                         | Lists the entries stored inside the specified keystore, including aliases and entry types.                                                                                          |
| `-v`                            | Stands for verbose. Displays detailed information about each entry, such as certificate owner, issuer, serial number, validity dates, fingerprints, and certificate chain details.  |
| `-keystore server-keystore.p12` | Specifies the keystore file to inspect. In this example, the file is `server-keystore.p12`.                                                                                         |

---

## 10. Export Certificate from keystore

```bash
keytool -exportcert \
    -alias myapp \
    -keystore server-keystore.p12 \
    -file server.crt
```


| Parameter                       | Description                                                                                                                              |
|---------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| `keytool`                       | Java utility used to manage cryptographic keys, certificates, keystores, and truststores.                                                |
| `-exportcert`                   | Exports the certificate associated with the specified alias from the keystore. It exports only the certificate, **not the private key**. |
| `-alias myapp`                  | Specifies the alias of the keystore entry whose certificate you want to export. Here, the alias is myapp.                                |
| `-keystore server-keystore.p12` | Specifies the source keystore file containing the certificate. Here, the file is server-keystore.p12.                                    |
| `-file server.crt`              | Specifies the output filename where the exported certificate will be saved. Here, the file is server.crt.                                |

1. Generated Certificate contains only :-    **Public Key + Certificate Information**
2. **You can safely share it to client**.

---

## 11. View Certificate

```bash
keytool -printcert \
  -file server.crt
```

| Parameter          | Description                                                                                                                                          |
|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------|
| `keytool`          | Java utility used to manage cryptographic keys, certificates, keystores, and truststores.                                                            |
| `-printcert`       | Displays the details of an X.509 certificate, including its owner, issuer, serial number, validity period, public key information, and fingerprints. |
| `-file server.crt` | Specifies the certificate file to read. Here, server.crt is the certificate previously exported from the keystore.                                   |

---

## 12. Create Truststore & Importing Certificate in it

```bash
keytool -importcert \
  -alias myserver \
  -file server.crt \
  -storetype PKCS12 \
  -keystore server-truststore.p12 \
  -storepass password
```

| Parameter                         | Description                                                                                                                                    |
|-----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| `keytool`                         | Java utility used to manage cryptographic keys, certificates, keystores, and truststores.                                                      |
| `-importcert`                     | Imports a certificate or certificate chain into the specified keystore.                                                                        |
| `-alias myserver`                 | Specifies the alias under which the imported certificate will be stored. Here, the alias is myserver.                                          |
| `-file server.crt`                | Specifies the source certificate file to import. Here, the certificate is read from server.crt.                                                |
| `-storetype PKCS12`               | Specifies PKCS#12 as the destination keystore format. The file commonly uses the .p12 or .pfx extension.                                       |
| `-keystore server-truststore.p12` | Specifies the destination truststore file. If the file does not exist, keytool can create it.                                                  |
| `-storepass password`             | Supplies the password used to access the destination truststore. Here, the password is literally password. Avoid weak passwords in production. |

1. Truststore contains only Trusted Certificate, No Private Keys

---

## 13. How to View Truststore

```bash
keytool -list \
  -v \
  -keystore server-truststore.p12
```

| Parameter                         | Description                                                                                                                                                                          |
|-----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `keytool`                         | Java utility used to manage cryptographic keys, certificates, keystores, and truststores.                                                                                            |
| `-list`                           | Lists the entries stored inside the specified keystore, including aliases and entry types.                                                                                           |
| `-v`                              | Stands for verbose. Displays detailed information about each entry, such as certificate owner, issuer, serial number, validity dates, fingerprints, and certificate chain details.   |
| `-keystore server-truststore.p12` | Specifies the keystore file to inspect. In this example, the file is `server-truststore.p12`.                                                                                        |

---