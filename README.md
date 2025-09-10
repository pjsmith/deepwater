# Deepwater

A simple roguelike game with a client-server architecture.

## Running the game

### Using Docker

The easiest way to run the game is using Docker and Docker Compose.

#### Combined Server + Client (Default for Local Development)

1.  Make sure you have Docker and Docker Compose installed.
2.  Clone this repository.
3.  From the root of the repository, run:

    ```
    docker-compose up
    ```

4.  Open your browser and navigate to `http://localhost:3000/game` to play the game.

This builds a container with both the server and client resources packaged together.

#### Server-Only Deployment

To run only the server (useful for production with separate client deployment):

```
docker-compose --profile server-only up deepwater-server
```

Or build directly:

```
docker build --target server -t deepwater-server .
docker run -p 3000:3000 deepwater-server
```

#### Docker Build Targets

- `combined` (default): Server with client resources packaged in the JAR
- `server`: Server-only build without client resources

### Running the server and client separately

You can also run the server and client separately.

#### Server

To run the server, you need to have Clojure and the Clojure CLI tools installed.

From the `server` directory, run:

```
clojure -M -m deepwater.server.core
```

#### Client

For the client, you can simply open the `client/index.html` file in your browser. Note that the client expects the server to be running on `localhost:3000`.

## Tools

### Map Generator Tester

There is a tool to test the map generator. To run it, go to the `tools` directory and run:

```
clojure -M -m deepwater.tools.core
```
