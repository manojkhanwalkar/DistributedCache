# DistributedCache

A small distributed key/value cache. Each node is a socket server configured by a
Spring XML file (see `src/main/java/config/server1.xml` and `server2.xml`); clients
(`client.DCClient`) speak a newline-delimited JSON protocol and pick a node by key hash.

## Build and run

```
mvn package
java -cp "target/classes:<dependency classpath>" server.Server src/main/java/config/server1.xml
```

## Security notes

- The protocol has **no authentication and no encryption**. Every client that can reach a
  node can read and overwrite any key. Listeners therefore bind to `127.0.0.1` by default
  (`bindAddress` property of `socket.ListenerService`). Bind to another interface only on a
  trusted, firewalled network.
- Limits (properties of `socket.ListenerService`): `maxConnections` (default 100; extra
  connections are refused), `readTimeoutMillis` (default 300000; idle connections are
  closed). A request line may be at most 1 MiB; invalid or oversized requests close the
  connection.
- The data directory (`dirName` of the persistence manager) is created with mode 0700 and
  must be owned by the user running the server, otherwise startup fails. Data files are
  created with mode 0600. Prefer a directory outside `/tmp`.
- The off-heap store rejects records larger than its file size (`length`, default 500 bytes).
